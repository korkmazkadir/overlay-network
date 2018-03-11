/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.common;

/**
 *
 * @author Kadir Korkmaz
 */
public interface MessageListener {

    public void notifyMessage(Message message);
    
}
