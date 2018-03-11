/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.common;

import com.kadirkorkmaz.rabbitmq.common.implementations.MessageType;
import com.kadirkorkmaz.rabbitmq.common.implementations.NodeIdentifier;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Message {

    public MessageType getType();

    public String getBody();

    public String getDebugInfo();

    public NodeIdentifier getSender();

    public NodeIdentifier getReceiver();

}
