/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq;

import com.kadirkorkmaz.rabbitmq.common.Node;
import com.kadirkorkmaz.rabbitmq.common.implementations.NodeIdentifier;
import com.kadirkorkmaz.rabbitmq.common.implementations.RoutingTable;
import com.kadirkorkmaz.rabbitmq.common.implementations.RoutingTableEntry;
import com.kadirkorkmaz.rabbitmq.node.NodeImp;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public class App3 {

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

        Integer numberOfNodes = 41;
        Integer i;

        List<Node> nodes = new LinkedList<>();
        for (i = 0; i < numberOfNodes; i++) {
            nodes.add(new NodeImp(new NodeIdentifier(i.toString())));
        }

        for (i = 0; i < numberOfNodes; i++) {

            Node node = nodes.get(i);
            Node nextNode = nodes.get((i + 1) % numberOfNodes);

            System.out.println(node.getIdentifier().getNodeId() + " -- " + nextNode.getIdentifier().getNodeId());

            node.addConnection(nextNode.getIdentifier());
            nextNode.addConnection(node.getIdentifier());
            //Thread.sleep(2000);
        }

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                RoutingTableEntry maxCostEntry = new RoutingTableEntry(null, null, 0);

                for (Node node : nodes) {

                    RoutingTable table = node.getRoutinTable();
                    System.out.println("=======> Node : " + node.getIdentifier().getNodeId() + " connections " + table.getEntryList().size());
                    int count = 1;
                    for (RoutingTableEntry entry : table.getEntryList()) {
                        System.out.println((count++) + " - " + entry);
                        if (maxCostEntry.getCost() < entry.getCost()) {
                            maxCostEntry = entry;
                        }
                    }
                    System.out.println("\n");

                }

                System.out.println("Max cost entry : " + maxCostEntry);
                this.cancel();
            }
        }, 15000, 1000);

        Random rand = new Random();
        Integer sender;
        Integer receiver;

//        while(true){
//            System.out.println("\n\n");
//            sender = rand.nextInt(numberOfNodes - 1);
//            receiver = rand.nextInt(numberOfNodes - 1);
//            
//            System.out.println("Message from " + sender + " to " + receiver);
//            nodes.get(sender).sendMessage(nodes.get(receiver).getIdentifier(), "Hello There :)");
//            Thread.sleep(2000);
//        }
    }
}
