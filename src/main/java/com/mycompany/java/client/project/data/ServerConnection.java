/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template */ package com.mycompany.java.client.project.data;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerConnection implements Closeable, AutoCloseable {

    private String ipAddress;
    private int portNumber;
    private Socket s;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ServerConnection(String ipAddress, int port) throws IOException {
        this.ipAddress = ipAddress;
        this.portNumber = port;
        connectToServer();
    }

    private void connectToServer() throws IOException {
        System.out.println("Attempting to connect to " + ipAddress + ":" + portNumber);
        s = new Socket(ipAddress, portNumber);
        oos = new ObjectOutputStream(s.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(s.getInputStream());
        System.out.println("Streams initialized successfully.");
    }

    public void sendRequest(Object obj) throws IOException {
        if (oos == null) {
            throw new IOException("ObjectOutputStream is null. Connection failed earlier?");
        }
        oos.writeObject(obj);
        oos.flush();
    }

    public Object readResponse() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    @Override
    public void close() throws IOException {
        if (oos != null) {
            oos.close();
        }
        if (ois != null) {
            ois.close();
        }
        if (s != null) {
            s.close();
        }
    }
}
