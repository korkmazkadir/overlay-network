/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.common;

import com.kadirkorkmaz.rabbitmq.common.implementations.RoutingTable;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Router {

    public void routeMessage(Message message);
    
    public RoutingTable getRoutingTable();
    
}
