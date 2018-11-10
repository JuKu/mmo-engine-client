package com.jukusoft.mmo.engine.network;

import com.jukusoft.mmo.engine.shared.client.events.network.*;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.PublicKeyRequest;
import com.jukusoft.mmo.engine.shared.messages.PublicKeyResponse;
import com.jukusoft.mmo.engine.shared.messages.RTTRequest;
import com.jukusoft.mmo.engine.shared.messages.RTTResponse;
import com.jukusoft.mmo.engine.shared.utils.EncryptionUtils;
import com.jukusoft.vertx.connection.clientserver.*;
import com.jukusoft.vertx.serializer.TypeLookup;
import com.jukusoft.vertx.serializer.exceptions.NetworkException;
import io.vertx.core.buffer.Buffer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class NetworkView implements SubSystem {

    protected static final String LOG_TAG = "Network";

    protected Client netClient = null;

    protected int rttInterval = 100;
    protected AtomicBoolean rttMsgReceived = new AtomicBoolean(true);
    protected AtomicLong lastRttTime = new AtomicLong(0);

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

                        //set RTT timer to activate ping detection
                        this.setRttTimer();

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

        //register message types first
        TypeLookup.register(PublicKeyRequest.class);
        TypeLookup.register(PublicKeyResponse.class);
        TypeLookup.register(RTTRequest.class);
        TypeLookup.register(RTTResponse.class);

        //register message listeners
        this.netClient.handlers().register(PublicKeyResponse.class, (MessageHandler<PublicKeyResponse, RemoteConnection>) (msg, conn) -> {
            //initialize EncryptionUtils with received RSA public key
            EncryptionUtils.init(msg.getPublicKey());

            Log.d(LOG_TAG, "received public key response.");
            Log.i(LOG_TAG, "network layer is ready now.");

            //fires events
            Events.queueEvent(Pools.get(PublicKeyReceivedEvent.class));
            Events.queueEvent(Pools.get(ConnectionReadyEvent.class));
        });

        //get rtt interval
        this.rttInterval = Config.getInt("Network", "rttInterval");

        //register message listener for RTT (round trip time) to detect ping
        this.netClient.handlers().register(RTTResponse.class, (MessageHandler<RTTResponse, RemoteConnection>) (msg, conn) -> {
            //get current timestamp
            long now = System.currentTimeMillis();

            //calculate rtt & ping
            long rtt = now - this.lastRttTime.get();
            long ping = rtt / 2;

            //fire event to notify subsystems about ping change
            PingChangedEvent event = Pools.get(PingChangedEvent.class);
            event.ping = (int) ping;
            Events.queueEvent(event);

            //reset flag
            this.rttMsgReceived.set(true);
        });
    }

    /**
     * determine round-trip-time
     */
    protected void executeRTTCheck () {
        //check, if a rtt message was already sended and no response received
        if (!this.rttMsgReceived.get()) {
            return;
        }

        this.rttMsgReceived.set(false);

        //set current timestamp
        lastRttTime.set(System.currentTimeMillis());

        //send rtt request message to proxy server
        this.netClient.send(Pools.get(RTTRequest.class));
    }

    protected void setRttTimer () {
        this.netClient.setPeriodic(this.rttInterval, timerID -> executeRTTCheck());
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
