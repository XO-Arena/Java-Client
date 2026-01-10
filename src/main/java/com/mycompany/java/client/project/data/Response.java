package com.mycompany.java.client.project.data;

import com.google.gson.JsonElement;
import enums.ResponseType;

public class Response {
    private ResponseType type;
    private JsonElement payload;

    public Response(ResponseType type, JsonElement data) {
        this.type = type;
        this.payload = data;
    }

    public ResponseType getType() { return type; }
    public JsonElement getPayload() { return payload; }
}