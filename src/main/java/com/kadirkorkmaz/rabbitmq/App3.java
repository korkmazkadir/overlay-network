/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq;

import com.kadirkorkmaz.rabbitmq.common.Node;
import com.kadirkorkmaz.rabbitmq.common.implementations.NodeIdentifier;
import com.kadirkorkmaz.rabbitmq.node.NodeImp;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public class App3 {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Integer numberOfNodes = 31;
        Integer i;

        List<Node> nodes = new LinkedList<>();
        for (i = 0; i < numberOfNodes; i++) {
            nodes.add(new NodeImp(new NodeIdentifier(i.toString())));
        }

        for (i = 0; i < numberOfNodes; i++) {
            Node node = nodes.get(i);
            Node nextNode = nodes.get((i + 1) % numberOfNodes);
            
            node.addConnection(nextNode.getIdentifier());
            nextNode.addConnection(node.getIdentifier());
            Thread.sleep(2000);
        }
        
        Thread.sleep(20000);
        Random rand = new Random();
        Integer sender;
        Integer receiver;
        
        while(true){
            System.out.println("\n\n");
            sender = rand.nextInt(numberOfNodes - 1);
            receiver = rand.nextInt(numberOfNodes - 1);
            
            System.out.println("Message from " + sender + " to " + receiver);
            nodes.get(sender).sendMessage(nodes.get(receiver).getIdentifier(), "Hello There :)");
            Thread.sleep(2000);
        }

        
    }
}
