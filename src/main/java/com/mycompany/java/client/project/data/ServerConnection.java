package com.mycompany.java.client.project.data;

import java.io.*;
import java.net.Socket;

public class ServerConnection implements Closeable {

    private static String ipAddress = "127.0.0.1";
    private static int portNumber = 4646;
    private static ServerConnection INSTANCE;
    private static final Object lock = new Object();

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private ServerConnection() throws IOException {
        connect();
    }

    public static ServerConnection getInstance() throws IOException {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = new ServerConnection();
            }
            return INSTANCE;
        }
    }

    private void connect() throws IOException {
        socket = new Socket(ipAddress, portNumber);

        // IMPORTANT ORDER
        oos = new ObjectOutputStream(socket.getOutputStream());
        oos.flush();
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void sendRequest(Object obj) throws IOException {
        oos.writeObject(obj);
        oos.flush();
    }

    public Object readResponse() throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    public boolean isAlive() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void reconnect() throws IOException {
        close();
        connect();
    }

    public void changeServer(String ip, int port) throws IOException {
        ipAddress = ip;
        portNumber = port;
        reconnect();
    }

    @Override
    public void close() throws IOException {
        if (oos != null) {
            oos.close();
        }
        if (ois != null) {
            ois.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
}
