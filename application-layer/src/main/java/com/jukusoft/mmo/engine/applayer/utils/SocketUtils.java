package com.jukusoft.mmo.engine.applayer.utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
* Utility class to check if remote port is open
*/
public class SocketUtils {

    /**
    * protected constructor
    */
    protected SocketUtils() {
        //
    }

    /**
    * check, if remote tcp port is open
     *
     * @param ip remote server ip
     * @param port remote server port
     * @param timeout timeout in milliseconds
     *
     * @return true, if port is open
    */
    public static boolean checkRemoteTCPPort (String ip, int port, int timeout) throws IOException {
        try (Socket s = new Socket()) {
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(ip, port);

            try {
                s.connect(sa, timeout);
            } catch (IOException e) {
                return false;
            }

            return s.isConnected();
        }
    }

    /**
     * list own ip addresses
     *
     * @return list with own ip addresses
     */
    public static List<String> listOwnIPs () throws SocketException {
        //create new empty list with ip addresses
        List<String> ownIPList = new ArrayList<>();

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

        //iterate through network interfaves
        while (networkInterfaces.hasMoreElements()) {
            //get network interface
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            //get ip addresses from network interface
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();

            //iterate through ip address enumeration
            while (inetAddressEnumeration.hasMoreElements()) {
                //get ip address
                InetAddress inetAddress = inetAddressEnumeration.nextElement();

                //add ip address to list
                ownIPList.add(inetAddress.getHostAddress());
            }
        }

        //return list with ip addresses
        return ownIPList;
    }

}
