package com.altiplaconsulting.arbextractor;

import com.google.javascript.jscomp.JsMessage;

public class AltiplaJsMessage {

    private final JsMessage message;
    private final String soyId;

    public AltiplaJsMessage(String soyId, JsMessage message) {
        this.soyId = soyId;
        this.message = message;
    }

    public JsMessage getMessage() {
        return message;
    }

    public String getSoyId() {
        return soyId;
    }

}
