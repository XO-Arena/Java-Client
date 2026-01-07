package com.mycompany.java.client.project.data;

import java.io.*;
import java.net.Socket;

public class ServerConnection implements AutoCloseable {
    private static ServerConnection instance;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private ServerConnection() throws IOException {
        // اتصل بالسيرفر مرة واحدة فقط عند تشغيل التطبيق
        socket = new Socket("localhost", 12345); 
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public static synchronized ServerConnection getInstance() throws IOException {
        if (instance == null || instance.socket.isClosed()) {
            instance = new ServerConnection();
        }
        return instance;
    }

    public void sendRequest(Object request) throws IOException {
        out.writeObject(request);
        out.flush();
    }

    public Object readResponse() throws IOException, ClassNotFoundException {
        return in.readObject();
    }

    @Override
    public void close() throws IOException {
        if (socket != null) socket.close();
    }
}