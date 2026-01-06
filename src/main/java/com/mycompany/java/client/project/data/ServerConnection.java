/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.java.client.project.data;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServerConnection implements Closeable, AutoCloseable {
    private String ipAddress;
    private int portNumber;
    private Socket s;
    private BufferedReader br;
    private PrintStream ps;
    
    public ServerConnection(String ipAddress, int port) throws IOException {
        this.ipAddress = ipAddress;
        portNumber = port;
        connectToServer();
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
        return br.readLine();
    }
    
    public void sendMessage(String message) throws IOException {
        ps.write(message.getBytes());
    }

    @Override
    public void close() throws IOException {
        if (s != null) s.close();
        if (br != null) br.close();
        if (ps != null) ps.close();
    }
}
