package com.jukusoft.mmo.engine.network;

import com.jukusoft.mmo.engine.shared.client.events.init.*;
import com.jukusoft.mmo.engine.shared.client.events.network.*;
import com.jukusoft.mmo.engine.shared.config.Config;
import com.jukusoft.mmo.engine.shared.data.CharacterSlot;
import com.jukusoft.mmo.engine.shared.logger.Log;
import com.jukusoft.mmo.engine.applayer.subsystem.SubSystem;
import com.jukusoft.mmo.engine.shared.client.ClientEvents;
import com.jukusoft.mmo.engine.shared.events.EventListener;
import com.jukusoft.mmo.engine.shared.events.Events;
import com.jukusoft.mmo.engine.shared.memory.Pools;
import com.jukusoft.mmo.engine.shared.messages.*;
import com.jukusoft.mmo.engine.shared.utils.EncryptionUtils;
import com.jukusoft.mmo.engine.shared.version.Version;
import com.jukusoft.vertx.connection.clientserver.*;
import com.jukusoft.vertx.serializer.TypeLookup;
import com.jukusoft.vertx.serializer.exceptions.NetworkException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class NetworkView implements SubSystem {

    protected static final String LOG_TAG = "Network";
    protected static final String LOGIN_TAG = "Login";

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

        //register event listener for login request events
        Events.addListener(Events.NETWORK_THREAD, ClientEvents.LOGIN_REQUEST, (EventListener<LoginRequestEvent>) event -> {
            Log.i(LOG_TAG, "login request event received from UI.");

            JsonObject json = new JsonObject();
            json.put("username", event.username);
            json.put("password", event.password);

            Version version = Version.getInstance();
            Objects.requireNonNull(version);

            //add version information
            JsonObject versionJson = new JsonObject();
            versionJson.put("version", version.getVersion());
            versionJson.put("revision", version.getRevision());
            versionJson.put("build_time", version.getBuildTime());
            versionJson.put("vendor", version.getVendor());
            versionJson.put("vendorID", version.getVendorID());
            versionJson.put("full_version", version.getFullVersion());
            json.put("version", versionJson);

            LoginRequest loginRequest = Pools.get(LoginRequest.class);

            //encrypt message
            try {
                Log.i(LOGIN_TAG, "encrypt login credentials.");

                EncryptionUtils.encrypt(json.encode());
                loginRequest.encryptedData = EncryptionUtils.encrypt(json.encode());

                //send login credentials to server
                Log.i(LOGIN_TAG, "send login request to server...");

                long startTime = System.currentTimeMillis();

                this.netClient.send(loginRequest);

                long endTime = System.currentTimeMillis();
                long diffTime = endTime - startTime;
                Log.v(LOGIN_TAG, "login request serialization takes " + diffTime + "ms");
            } catch (Exception e) {
                Log.e(LOGIN_TAG, "encryption of login credentials failed!", e);

                //fire event with login response, so that UI can show this error on HUD
                LoginResponseEvent event1 = Pools.get(LoginResponseEvent.class);
                event1.loginResponse = LoginResponseEvent.LOGIN_RESPONSE.CLIENT_ERROR;
                Events.queueEvent(event1);
            }
        });

        //register event listener for create character events
        Events.addListener(Events.NETWORK_THREAD, ClientEvents.CREATE_CHARACTER, (EventListener<CreateCharacterEvent>) event -> {
            Log.i(LOGIN_TAG, "received create character event.");

            Objects.requireNonNull(event.character);

            Log.i(LOGIN_TAG, "send create character request to proxy server...");

            //create and send request to proxy server
            CreateCharacterRequest request = Pools.get(CreateCharacterRequest.class);
            request.jsonStr = event.character.toJson().encode();
            this.netClient.send(request);
        });

        //register event listener for create character events
        Events.addListener(Events.NETWORK_THREAD, ClientEvents.ENTER_GAME_WORLD, (EventListener<EnterGameWorldRequestEvent>) event -> {
            Log.i(LOGIN_TAG, "send request to enter game world with character ID " + event.cid + "...");

            //create and send request to proxy server
            EnterGameWorldRequest request = Pools.get(EnterGameWorldRequest.class);
            request.cid = event.cid;
            this.netClient.send(request);
        });

        //register message types first
        TypeLookup.register(PublicKeyRequest.class);
        TypeLookup.register(PublicKeyResponse.class);
        TypeLookup.register(RTTRequest.class);
        TypeLookup.register(RTTResponse.class);
        TypeLookup.register(LoginRequest.class);
        TypeLookup.register(LoginResponse.class);
        TypeLookup.register(CharacterListRequest.class);
        TypeLookup.register(CharacterListResponse.class);
        TypeLookup.register(CreateCharacterRequest.class);
        TypeLookup.register(CreateCharacterResponse.class);
        TypeLookup.register(EnterGameWorldRequest.class);
        TypeLookup.register(EnterGameWorldResponse.class);

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

        //register message listener for login response
        this.netClient.handlers().register(LoginResponse.class, (MessageHandler<LoginResponse, RemoteConnection>) (msg, conn) -> {
            Log.i(LOGIN_TAG, "login response received.");

            int userID = msg.userID;

            if (userID > 0) {
                //login was successfully
                Log.i(LOGIN_TAG, "login successfully!");

                //fire an event to notify subsystems
                LoginResponseEvent event = Pools.get(LoginResponseEvent.class);
                event.loginResponse = LoginResponseEvent.LOGIN_RESPONSE.SUCCESSFUL;
                Events.queueEvent(event);

                //request character list
                CharacterListRequest req = Pools.get(CharacterListRequest.class);
                this.netClient.send(req);
            } else if (userID == 0) {
                //login failed because credentials are wrong
                Log.i(LOGIN_TAG, "login failed, user credentials are wrong.");

                //fire an event to notify subsystems
                LoginResponseEvent event = Pools.get(LoginResponseEvent.class);
                event.loginResponse = LoginResponseEvent.LOGIN_RESPONSE.WRONG_CREDENTIALS;
                Events.queueEvent(event);
            } else {
                //internal server error
                Log.i(LOGIN_TAG, "login failed, caused by internal server error.");

                //fire an event to notify subsystems
                LoginResponseEvent event = Pools.get(LoginResponseEvent.class);
                event.loginResponse = LoginResponseEvent.LOGIN_RESPONSE.INTERNAL_SERVER_ERROR;
                Events.queueEvent(event);
            }
        });

        //register message listener for character list response
        this.netClient.handlers().register(CharacterListResponse.class, (MessageHandler<CharacterListResponse, RemoteConnection>) (msg, conn) -> {
            Log.i(LOGIN_TAG, "character list received.");

            //get json
            JsonObject json = new JsonObject(msg.jsonStr);
            JsonArray array = json.getJsonArray("slots");

            //create event
            CharacterListReceivedEvent event = Pools.get(CharacterListReceivedEvent.class);
            event.slots.clear();

            for (int i = 0; i < array.size(); i++) {
                JsonObject json1 = array.getJsonObject(i);

                //convert to character slot
                CharacterSlot slot = CharacterSlot.createFromJson(json1.getInteger("cid"), json1.getString("name"), json1);

                //add slot to list
                event.slots.add(slot);
            }

            //fire event
            Events.queueEvent(event);
        });

        this.netClient.handlers().register(CreateCharacterResponse.class, (MessageHandler<CreateCharacterResponse, RemoteConnection>) (msg, conn) -> {
            Log.i(LOGIN_TAG, "create character response received.");

            //fire event
            CreateCharacterResponseEvent event = Pools.get(CreateCharacterResponseEvent.class);
            event.resultCode = msg.getResult();
            Events.queueEvent(event);

            if (msg.getResult() == CreateCharacterResponse.CREATE_CHARACTER_RESULT.SUCCESS) {
                this.netClient.setTimer(50l, event1 -> {
                    Log.v("Network", "request character list.");

                    //request character list
                    CharacterListRequest req = Pools.get(CharacterListRequest.class);
                    netClient.send(req);
                });
            }
        });

        this.netClient.handlers().register(EnterGameWorldResponse.class, (MessageHandler<EnterGameWorldResponse, RemoteConnection>) (msg, conn) -> {
            Log.i(LOGIN_TAG, "received EnterGameWorldResponse.");

            EnterGameWorldResponseEvent event = Pools.get(EnterGameWorldResponseEvent.class);

            //TODO: fire event
            Events.queueEvent(event);

            throw new UnsupportedOperationException("method isn't implemented yet.");
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
