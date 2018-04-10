/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.common;

import com.overlaynetwork.implementation.Acknowledgement;
import com.overlaynetwork.implementation.RoutingTable;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Router {

    public Acknowledgement routeMessage(Message message);
    
    public RoutingTable getRoutingTable();
    
    public void addIncommingMessageListener(MessageListener listener);
    
}
