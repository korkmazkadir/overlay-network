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
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public class App2 {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Node n1 = new NodeImp(new NodeIdentifier("1"));
        Node n2 = new NodeImp(new NodeIdentifier("2"));
        Node n3 = new NodeImp(new NodeIdentifier("3"));
        Node n4 = new NodeImp(new NodeIdentifier("4"));

        n1.addConnection(n2.getIdentifier());
        n2.addConnection(n1.getIdentifier());

        n2.addConnection(n3.getIdentifier());
        n3.addConnection(n2.getIdentifier());

        n3.addConnection(n4.getIdentifier());
        n4.addConnection(n3.getIdentifier());

        n4.addConnection(n1.getIdentifier());
        n1.addConnection(n4.getIdentifier());

        Thread.sleep(3000);

        Node n5 = new NodeImp(new NodeIdentifier("5"));
        Node n6 = new NodeImp(new NodeIdentifier("6"));

        Node n7 = new NodeImp(new NodeIdentifier("7"));
        Node n8 = new NodeImp(new NodeIdentifier("8"));
        

        n5.addConnection(n4.getIdentifier());
        n4.addConnection(n5.getIdentifier());

        n2.addConnection(n6.getIdentifier());
        n6.addConnection(n2.getIdentifier());

        n7.addConnection(n1.getIdentifier());
        n1.addConnection(n7.getIdentifier());

        n8.addConnection(n3.getIdentifier());
        n3.addConnection(n8.getIdentifier());
        
        Thread.sleep(3000);

        System.out.println("\n==========> Sending");
        n5.sendMessage(n6.getIdentifier(), "Hello There :)");

        Thread.sleep(1000);
        
        System.out.println("");
        
        n7.sendMessage(n8.getIdentifier(), "Hello There :)");

        //System.out.println("End of main");

    }

}
