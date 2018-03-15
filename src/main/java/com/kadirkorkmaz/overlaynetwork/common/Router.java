/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.common;

import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTable;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Router {

    public void routeMessage(Message message);
    
    public RoutingTable getRoutingTable();
    
}
