/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.common;

import com.kadirkorkmaz.overlaynetwork.implementation.Acknowledgement;
import com.kadirkorkmaz.overlaynetwork.implementation.NodeIdentifier;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTable;
import java.util.List;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Node {
    
    public NodeIdentifier getIdentifier();
    
    public List<Connection> getConnections();

    public void handleMessage(Message message);
    
    public Acknowledgement sendMessage(NodeIdentifier destination, String messageBody);
    
    public void addConnection(NodeIdentifier nodeId);
    
    public void removeConnection(NodeIdentifier nodeId);
 
    public RoutingTable getRoutinTable();
    
}
