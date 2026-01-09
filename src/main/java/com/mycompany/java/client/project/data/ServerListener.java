package com.mycompany.java.client.project.data;

public interface ServerListener {
    void onMessage(Response response);  
    void onDisconnect();                
}
