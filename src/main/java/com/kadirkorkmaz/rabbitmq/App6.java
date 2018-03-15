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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public class App6 {

    private static void connectNodes(Node node1, Node node2) {
        node1.addConnection(node2.getIdentifier());
        node2.addConnection(node1.getIdentifier());
    }

    private static final int SUBNET_SIZE = 5;

    private static void connectSubnetNodes(Node[] subnetNodes) {
        for (int i = 0; i < SUBNET_SIZE; i++) {
            subnetNodes[i].addConnection(subnetNodes[(i + 1) % SUBNET_SIZE].getIdentifier());
            System.out.println(subnetNodes[i].getIdentifier().getNodeId() + " -- > " + subnetNodes[(i + 1) % SUBNET_SIZE].getIdentifier().getNodeId());
            subnetNodes[(i + 1) % SUBNET_SIZE].addConnection(  subnetNodes[i].getIdentifier());
        }
        System.out.println("");
    }

    public static void main(String[] args) throws IOException, TimeoutException {

        Node n11 = new NodeImp(new NodeIdentifier("11"));
        Node n12 = new NodeImp(new NodeIdentifier("12"));
        Node n13 = new NodeImp(new NodeIdentifier("13"));
        Node n14 = new NodeImp(new NodeIdentifier("14"));
        Node n15 = new NodeImp(new NodeIdentifier("15"));

        Node[] subnet1 = {n11, n12, n13, n14, n15};
        connectSubnetNodes(subnet1);

        Node n21 = new NodeImp(new NodeIdentifier("21"));
        Node n22 = new NodeImp(new NodeIdentifier("22"));
        Node n23 = new NodeImp(new NodeIdentifier("23"));
        Node n24 = new NodeImp(new NodeIdentifier("24"));
        Node n25 = new NodeImp(new NodeIdentifier("25"));

        Node[] subnet2 = {n21, n22, n23, n24, n25};
        connectSubnetNodes(subnet2);

        Node n31 = new NodeImp(new NodeIdentifier("31"));
        Node n32 = new NodeImp(new NodeIdentifier("32"));
        Node n33 = new NodeImp(new NodeIdentifier("33"));
        Node n34 = new NodeImp(new NodeIdentifier("34"));
        Node n35 = new NodeImp(new NodeIdentifier("35"));

        Node[] subnet3 = {n31, n32, n33, n34, n35};
        connectSubnetNodes(subnet3);

        connectNodes(n11, n21);
        connectNodes(n11, n31);
        connectNodes(n21, n31);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                n12.sendMessage(n35.getIdentifier(), "Hello Message....");

                RoutingTable table = n12.getRoutinTable();
                System.out.println("=======> Node : " + n12.getIdentifier().getNodeId() + " connections " + table.getEntryList().size());
                int count = 1;
                for (RoutingTableEntry entry : table.getEntryList()) {
                    System.out.println((count++) + " - " + entry);
                    //if (maxCostEntry.getCost() < entry.getCost()) {
                    //    maxCostEntry = entry;
                    //}
                }
                
                System.out.println("\n");

                this.cancel();
            }
        }, 10000, 1000);

    }

}
