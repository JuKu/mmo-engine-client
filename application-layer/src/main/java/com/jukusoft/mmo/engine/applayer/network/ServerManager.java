package com.jukusoft.mmo.engine.applayer.network;

import com.jukusoft.mmo.engine.applayer.utils.FileUtils;
import com.jukusoft.mmo.engine.applayer.utils.SocketUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    protected static final ServerManager instance = new ServerManager();

    protected List<Server> list = new ArrayList<>();

    public ServerManager() {
        //
    }

    public void loadFromConfig (File config) throws IOException {
        //check, if config file exists
        if (!config.exists()) {
            throw new IllegalStateException("server config file doesnt exists: " + config.getAbsolutePath());
        }

        list.clear();

        String content = FileUtils.readFile(config.getAbsolutePath(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(content);

        JSONArray servers = json.getJSONArray("servers");

        for (int i = 0; i < servers.length(); i++) {
            JSONObject server = servers.getJSONObject(i);

            String ip = server.getString("ip");
            int port = server.getInt("port");
            String title = server.getString("title");
            String description = server.getString("description");

            //check, if server is online
            boolean online = SocketUtils.checkRemoteTCPPort(ip, port, 500);

            Server obj = new Server(ip, port, title, description, online);
            list.add(obj);
        }
    }

    public static ServerManager getInstance() {
        return instance;
    }

    public List<Server> listServers () {
        return this.list;
    }

    protected static Server createServer (String ip, int port, String title, String description, boolean online) {
        return new Server(ip, port, title, description, online);
    }

    public static class Server {

        public final String ip;
        public final int port;
        public final String title;
        public final String description;
        public final boolean online;

        public Server (String ip, int port, String title, String description, boolean online) {
            this.ip = ip;
            this.port = port;
            this.title = title;
            this.description = description;
            this.online = online;
        }

    }

}
