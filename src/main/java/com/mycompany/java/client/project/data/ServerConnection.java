/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.java.client.project.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.stream.Collectors;

public class ServerConnection {
    private static String ipAddress;
    private static int portNumber;
    private Socket s;
    private BufferedReader br;
    private PrintStream ps;
    
    private static ServerConnection INSTANCE;
    private static final Object lock = new Object();
    
    private ServerConnection(String address, int port) throws IOException {
        ipAddress = address;
        portNumber = port;
        connectToServer();
    }
    
    public static ServerConnection getConnection() throws IOException {        
        synchronized(lock) {
            if (ipAddress == null) {
                ipAddress = "127.0.0.1";
            }
            if (portNumber == 0) {
                portNumber = 4646;
            }
            INSTANCE = new ServerConnection(ipAddress, portNumber);
        }
        return INSTANCE;
    }
    
    private void connectToServer() throws IOException {
        s = new Socket(ipAddress, portNumber);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        ps = new PrintStream(s.getOutputStream());
    }
    
    public void reconnect() throws IOException {
        close();
        connectToServer();
    }
    
    public boolean isAlive() {
        return s.isConnected();
    }
    
    public void changeServer(String ipAddress, Integer port) throws IOException {
        if (ipAddress != null) this.ipAddress = ipAddress;
        if (port != null) portNumber = port;
        reconnect();
    }
    
    public String getMessage() throws IOException {
        return br.lines().collect(Collectors.joining(System.lineSeparator()));
    }
    
    public void sendMessage(String message) throws IOException {
        ps.write(message.getBytes());
    }

    public void close() throws IOException {
        if (s != null) s.close();
        if (br != null) br.close();
        if (ps != null) ps.close();
    }
}
