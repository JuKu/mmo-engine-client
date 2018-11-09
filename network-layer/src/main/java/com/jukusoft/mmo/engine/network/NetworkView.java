package com.jukusoft.mmo.engine.network;

import com.jukusoft.mmo.engine.shared.client.events.network.ConnectionEstablishedEvent;
import com.jukusoft.mmo.engine.shared.client.events.network.ConnectionFailedEvent;
import com.jukusoft.mmo.engine.shared.client.events.network.ConnectionLostEvent;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.client.events.network.SelectServerEvent;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.PublicKeyRequest;
import com.jukusoft.vertx.connection.clientserver.Client;
import com.jukusoft.vertx.connection.clientserver.ServerData;
import com.jukusoft.vertx.connection.clientserver.TCPClient;
import com.jukusoft.vertx.serializer.exceptions.NetworkException;

public class NetworkView implements SubSystem {

    protected static final String LOG_TAG = "Network";

    protected Client netClient = null;

    @Override
    public void onInit() {
        Log.i(LOG_TAG, "initialize network layer.");

        //create new network client
        Log.i(LOG_TAG, "create tcp client...");
        this.netClient = new TCPClient();
        this.netClient.setThreadPoolSize(Config.getInt(LOG_TAG, "eventThreads"), Config.getInt(LOG_TAG, "workerThreads"));
        this.netClient.setDelay(Config.getInt(LOG_TAG, "sendDelay"), Config.getInt(LOG_TAG, "receiveDelay"));
        this.netClient.init();

        //set endHandler to fire an event if connection to proxy server was lost
        this.netClient.setOnConnectionClosedHandler(() -> Events.queueEvent(Pools.get(ConnectionLostEvent.class)));

        Log.i(LOG_TAG, "register event listeners...");

        //register event listeners
        Events.addListener(Events.NETWORK_THREAD, ClientEvents.SELECT_SERVER, (EventListener<SelectServerEvent>) event -> {
            Log.d(LOG_TAG, "player selected server (ip: " + event.ip + ", port: " + event.port + ").");
            Log.i(LOG_TAG, "try to connect to proxy server now...");

            //open network connection
            try {
                this.netClient.connect(new ServerData(event.ip, event.port), res -> {
                    if (res.succeeded()) {
                        Log.i(LOG_TAG, "proxy connection (ip: " + event.ip + ", port: " + event.port + ") established.");

                        //fire connection established successfully event
                        Events.queueEvent(Pools.get(ConnectionEstablishedEvent.class));

                        //request RSA public key
                        Log.i(LOG_TAG, "request RSA public key for encryption now.");
                        this.netClient.send(Pools.get(PublicKeyRequest.class));
                    } else {
                        Log.i(LOG_TAG, "proxy connection (ip: " + event.ip + ", port: " + event.port + ") failed.");

                        //fire connection failed event
                        Events.queueEvent(Pools.get(ConnectionFailedEvent.class));
                    }
                });
            } catch (NetworkException e) {
                //connecting failed
                Log.w(LOG_TAG, "connection failed.", e);

                //fire connection failed event
                Events.queueEvent(Pools.get(ConnectionFailedEvent.class));
            }
        });
    }

    @Override
    public void onGameloop() {
        //get events and execute listeners
        Events.update(Events.NETWORK_THREAD, 100);
    }

    @Override
    public void onShutdown() {
        //
    }

}
