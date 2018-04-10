/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.implementation;

import com.overlaynetwork.common.Message;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Kadir Korkmaz
 */
public class NetworkMessage implements Message {

    private final long id;
    private final MessageType type;
    private final NodeIdentifier sender;
    private final NodeIdentifier receiver;
    private final String messageBody;
    private String debugInfo;
    private List<String> path = new LinkedList<>();

    public NetworkMessage(MessageType type, NodeIdentifier sender, NodeIdentifier receiver, String messageBody) {
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.messageBody = messageBody;
        this.id = MessageIdProvider.GetNewId();
    }

    @Override
    public long getId() {
        return id;
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
    public List<String> getPath() {
        return path;
    }

    @Override
    public void addToPath(String pathElement) {
        path.add(pathElement);
    }

    @Override
    public String toString() {
        return "NetworkMessage{" + "id=" + id + ", type=" + type + ", sender=" + sender + ", receiver=" + receiver + ", messageBody=" + messageBody + ", debugInfo=" + debugInfo + ", path=" + path + '}';
    }

}
