package com.jukusoft.mmo.engine.network;

import com.jukusoft.mmo.engine.applayer.config.Config;
import com.jukusoft.mmo.engine.applayer.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.client.events.network.SelectServerEvent;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.vertx.connection.clientserver.Client;
import com.jukusoft.vertx.connection.clientserver.TCPClient;

public class NetworkView implements SubSystem {

    protected static final String LOG_TAG = "Network";

    protected Client netClient = null;

    @Override
    public void onInit() {
        //create new network client
        Log.i(LOG_TAG, "create tcp client...");
        this.netClient = new TCPClient();
        this.netClient.setThreadPoolSize(Config.getInt("Network", "eventThreads"), Config.getInt("Network", "workerThreads"));
        this.netClient.setDelay(Config.getInt("Network", "sendDelay"), Config.getInt("Network", "receiveDelay"));
        this.netClient.init();

        Log.i(LOG_TAG, "register event listeners...");

        //register event listeners
        Events.addListener(Events.NETWORK_THREAD, ClientEvents.SELECT_SERVER, (EventListener<SelectServerEvent>) event -> {
            Log.d("Network", "player selected server (ip: " + event.ip + ", port: " + event.port + ").");
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
