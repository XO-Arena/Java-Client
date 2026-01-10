package com.mycompany.java.client.project.data;

import com.google.gson.JsonElement;
import enums.RequestType;

public class Request {
    private RequestType type; 
    private JsonElement  payload;

    public Request(RequestType type, JsonElement data) {
        this.type = type;
        this.payload = data;
    }
    
    public Request(RequestType type) {
        this.type = type;
    }

    public RequestType getType() { return type; }
    public JsonElement getPayload() { return payload; }
}


