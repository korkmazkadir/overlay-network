/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.common;

import com.kadirkorkmaz.overlaynetwork.implementation.MessageType;
import com.kadirkorkmaz.overlaynetwork.implementation.NodeIdentifier;
import java.util.List;

/**
 *
 * @author Kadir Korkmaz
 */
public interface Message {

    public long getId();
    
    public MessageType getType();

    public String getBody();

    public String getDebugInfo();

    public NodeIdentifier getSender();

    public NodeIdentifier getReceiver();
    
    public void addToPath(String pathElement);
    
    public List<String> getPath();

}
