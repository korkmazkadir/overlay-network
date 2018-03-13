/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.common;

import com.kadirkorkmaz.rabbitmq.common.implementations.NodeIdentifier;
import com.kadirkorkmaz.rabbitmq.common.implementations.RoutingTable;
import java.util.List;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Node {
    
    public NodeIdentifier getIdentifier();
    
    public List<Connection> getConnections();

    public void handleMessage(Message message);
    
    public void sendMessage(NodeIdentifier destination, String messageBody);
    
    public void addConnection(NodeIdentifier nodeId);
    
    public void removeConnection(NodeIdentifier nodeId);
 
    public RoutingTable getRoutinTable();
    
}
