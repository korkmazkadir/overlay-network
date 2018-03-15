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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class App5 {

    private static void connectNodes(Node node1, Node node2) {
        node1.addConnection(node2.getIdentifier());
        node2.addConnection(node1.getIdentifier());
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Node n1 = new NodeImp(new NodeIdentifier("1"));
        Node n2 = new NodeImp(new NodeIdentifier("2"));
        Node n3 = new NodeImp(new NodeIdentifier("3"));
        Node n4 = new NodeImp(new NodeIdentifier("4"));
        Node n5 = new NodeImp(new NodeIdentifier("5"));

        connectNodes(n1, n2);
        connectNodes(n2, n3);
        connectNodes(n3, n4);
        connectNodes(n4, n5);
        connectNodes(n3, n5);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                
                n1.sendMessage(n5.getIdentifier(), "Hello Message....");
                this.cancel();
            }
        }, 3000, 1000);

        Timer t2 = new Timer();
        t2.schedule(new TimerTask() {
            @Override
            public void run() {
                
                Node n5 = null;
                try {
                    n5 = new NodeImp(new NodeIdentifier("5"));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                
                connectNodes(n1, n5);
                
                this.cancel();
            }
        }, 10000, 1000);

    }

}
