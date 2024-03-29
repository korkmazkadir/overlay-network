/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.common;

import com.overlaynetwork.implementation.ConnectionType;
import com.overlaynetwork.implementation.NodeIdentifier;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Connection {
    
    public void openConnection() throws IOException, TimeoutException;
    
    public ConnectionType getType();
    
    public NodeIdentifier getNodeId();
  
    public void sendMessage(Message message) throws IOException;

    public void addIncommingMessageListener(MessageListener listener);
    
    public void addOutgoingMessageListener(MessageListener listener);

    public void closeConnection() throws IOException, TimeoutException;
    
}
