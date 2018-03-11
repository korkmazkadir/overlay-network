/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq;

import com.kadirkorkmaz.rabbitmq.node.NetworkNode;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public class App {
   
    public static void main(String[] args) throws IOException, TimeoutException {
        
        List<NetworkNode> nodes = new LinkedList<>();
        int numberOfNodes = 15;
                
        for (int i = 0; i < numberOfNodes; i++) {
            nodes.add(new NetworkNode(i, numberOfNodes, (i+1) % numberOfNodes ));
        }
        
        for (NetworkNode node : nodes) {
            new Thread(node).start();
        }
        
        //nodes.get(3).sendMessage("Hello There :) ");
        
    }
    
}
