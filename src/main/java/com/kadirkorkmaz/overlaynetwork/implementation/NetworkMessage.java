/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.implementation;

import com.kadirkorkmaz.overlaynetwork.common.Message;

/**
 *
 * @author Kadir Korkmaz
 */
public class NetworkMessage implements Message {

    private final MessageType type;
    private final NodeIdentifier sender;
    private final NodeIdentifier receiver;
    private final String messageBody;
    private String debugInfo;

    public NetworkMessage(MessageType type, NodeIdentifier sender, NodeIdentifier receiver, String messageBody) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.messageBody = messageBody;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getBody() {
        return messageBody;
    }

    @Override
    public String getDebugInfo() {
        return debugInfo;
    }

    @Override
    public NodeIdentifier getSender() {
        return sender;
    }

    @Override
    public NodeIdentifier getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "NetworkMessage{" + "type=" + type + ", sender=" + sender + ", receiver=" + receiver + ", messageBody=" + messageBody + ", debugInfo=" + debugInfo + '}';
    }

}
