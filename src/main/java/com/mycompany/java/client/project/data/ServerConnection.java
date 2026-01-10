package com.mycompany.java.client.project.data;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import javafx.application.Platform;

public class ServerConnection implements Closeable {

    private static String ipAddress = "127.0.0.1";
    private static int portNumber = 4646;
    private static ServerConnection INSTANCE;
    private static final Object lock = new Object();

    private Socket socket;

    private BufferedReader reader;
    private PrintWriter writer;
    private final Gson gson;
    private ServerListener listener;
    private Thread listenerThread;

    private ServerConnection() {
        gson = new Gson();
        connect();
        startListenerThread();
    }

    public static ServerConnection getConnection() {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = new ServerConnection();
            }
            return INSTANCE;
        }
    }

    private void connect() {
        try {
            socket = new Socket(ipAddress, portNumber);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            if (listener != null) {
                listener.onDisconnect();
            }
        }
    }

    public void setListener(ServerListener listener) {
        this.listener = listener;
    }

    public boolean sendRequest(Request request) {
        if (!isAlive()) {
            if (listener != null) {
                listener.onDisconnect();
                close();
            }
            return false;
        }
        String json = gson.toJson(request);
        writer.println(json);
        return true;
    }
    

    private void startListenerThread() {
        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    Response response = gson.fromJson(line, Response.class);
                    if (listener != null) {
                        Platform.runLater(() -> listener.onMessage(response));
                    }
                }
            } catch (IOException e) {
                if (listener != null) {
                    Platform.runLater(() -> listener.onDisconnect());
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public boolean isAlive() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void reconnect() throws IOException {
        close();
        connect();
        startListenerThread();
    }

    public static void changeServer(String ip, int port) {
        synchronized (lock) {
            ipAddress = ip;
            portNumber = port;
            if (INSTANCE != null && !INSTANCE.isAlive()) {
                INSTANCE = null;
            }
        }
    }

    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException ex) {
                System.getLogger(ServerConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.getLogger(ServerConnection.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }
}
