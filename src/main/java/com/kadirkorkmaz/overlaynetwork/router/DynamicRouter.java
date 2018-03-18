/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.router;

import com.google.gson.Gson;
import com.kadirkorkmaz.overlaynetwork.common.Connection;
import com.kadirkorkmaz.overlaynetwork.common.Message;
import com.kadirkorkmaz.overlaynetwork.common.MessageListener;
import com.kadirkorkmaz.overlaynetwork.common.Node;
import com.kadirkorkmaz.overlaynetwork.common.Router;
import com.kadirkorkmaz.overlaynetwork.implementation.MessageType;
import com.kadirkorkmaz.overlaynetwork.implementation.NetworkMessage;
import com.kadirkorkmaz.overlaynetwork.implementation.NodeIdentifier;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTable;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTableEntry;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class DynamicRouter extends TimerTask implements Router, MessageListener {

    private final Node node;
    private final RoutingTable routingTable;
    private final Map<String, RoutingTableEntry> nodeIdToTableEntryMap;

    private final static long TIMER_PERIOD_MS = 1000;
    private final static long DELAY_MS = 1000;
    private final Timer timer;

    private final static int LINK_COST = 1;

    private final Message healthCheckMessage;

    private final static Gson gson = new Gson();

    private final Lock routingTableLock;

    public DynamicRouter(Node node) {
        this.node = node;
        routingTable = new RoutingTable(node.getIdentifier());
        routingTable.addEntry(new RoutingTableEntry(node.getIdentifier(), node.getIdentifier(), 0));
        timer = new Timer();
        timer.schedule(this, DELAY_MS, TIMER_PERIOD_MS);
        nodeIdToTableEntryMap = new ConcurrentHashMap<>(); // LinkedHashMap<>();
        healthCheckMessage = new NetworkMessage(MessageType.HEALT_CHECK, node.getIdentifier(), null, "I am here");
        routingTableLock = new ReentrantLock();
    }

    @Override
    public void routeMessage(Message message) {
        notifyMessage(message);
    }

    @Override
    public RoutingTable getRoutingTable() {
        return this.routingTable;
    }

    public void sendMessageToOtherConnections(Message message) {
        routingTableLock.lock();
        try {

            List<Connection> connections = node.getConnections();
            synchronized (connections) {
                for (Connection connection : connections) {
                    try {
                        connection.sendMessage(message);
                    } catch (IOException ex) {
                        Logger.getLogger(DynamicRouter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } finally {
            routingTableLock.unlock();
        }
    }

    private void sendRoutingTableToOthers() {
        Message routingTableMessage = new NetworkMessage(MessageType.ROUTING_TABLE, node.getIdentifier(), null, gson.toJson(routingTable));
        sendMessageToOtherConnections(routingTableMessage);
    }

    private Connection findConnectionById(NodeIdentifier id) {

        List<Connection> connections = node.getConnections();
        synchronized (connections) {
            for (Connection connection : connections) {
                if (connection.getNodeId().equals(id)) {
                    return connection;
                }
            }
        }

        return null;
    }

    
    @Override
    public void notifyMessage(Message message) {

        //Health Check message means that node directly connected to current node
        if (message.getType() == MessageType.HEALT_CHECK) {
            String key = message.getSender().getNodeId();
            //If directly connected host sending firstime data or it connected recently to this node
            if (nodeIdToTableEntryMap.containsKey(key) == false || nodeIdToTableEntryMap.get(key).getCost() > LINK_COST ) {
                RoutingTableEntry routingEntry = new RoutingTableEntry(message.getSender(), message.getSender(), LINK_COST);
                nodeIdToTableEntryMap.put(key, routingEntry);

                routingTableLock.lock();
                try {
                    routingTable.addEntry(routingEntry);
                } finally {
                    routingTableLock.unlock();
                }

                System.out.println(node.getIdentifier() + " --> " + routingEntry);
                sendRoutingTableToOthers();
            }
        }

        if (message.getType() == MessageType.ROUTING_TABLE) {
            final RoutingTable table = gson.fromJson(message.getBody(), RoutingTable.class);
            boolean sendOther = false;
            for (RoutingTableEntry entry : table.getEntryList()) {

                //If this node accessable over me skip it
                if (entry.getOverLinkNodeId().getNodeId().equals(node.getIdentifier().getNodeId())) {
                    //System.out.println(entry.getOverLinkNodeId()."");
                    continue;
                }

                if (nodeIdToTableEntryMap.containsKey(entry.getDestinationNodeId().getNodeId())) {
                    RoutingTableEntry routingTableEntry = nodeIdToTableEntryMap.get(entry.getDestinationNodeId().getNodeId());
                    //If we have already this node but new way is better than use the new way
                    if (routingTableEntry.getCost() <= (entry.getCost() + LINK_COST)) {
                        //System.out.println("Cost is smaller " + routingTableEntry.getCost() + " -- " + (entry.getCost() + LINK_COST));
                        continue;
                    }
                }

                //Cost smaller or no entry on table so add this entry to table
                entry.setOverLinkNodeId(message.getSender());
                entry.setCost(entry.getCost() + LINK_COST);

                String nodeId = entry.getDestinationNodeId().getNodeId();
                nodeIdToTableEntryMap.put(nodeId, entry);

                routingTableLock.lock();
                try {
                    routingTable.addEntry(entry);
                } finally {
                    routingTableLock.unlock();
                }

                System.out.println("(TableUpdate) " + node.getIdentifier() + " --> " + entry);
                sendOther = true;
            }

            if (sendOther) {
                sendRoutingTableToOthers();
            }

        }

        if (message.getType() == MessageType.USER_DATA) {
            if (message.getReceiver().equals(node.getIdentifier()) == false) {

                System.out.println("(Routing) " + node.getIdentifier() + " --> " + message);
                RoutingTableEntry entry = nodeIdToTableEntryMap.get(message.getReceiver().getNodeId());

                if (entry == null) {
                    System.out.println("Opps table entry is null :(");
                    return;
                }

                Connection connection = findConnectionById(entry.getOverLinkNodeId());
                if (connection != null) {
                    try {
                        connection.sendMessage(message);
                    } catch (IOException ex) {
                        Logger.getLogger(DynamicRouter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println("Opss, No connection :( " + entry.getOverLinkNodeId());
                }

            } else {
                System.out.println("*(Arrived) " + node.getIdentifier() + " --> " + message.getBody());
            }
        }

    }

    @Override
    public void run() {
        sendMessageToOtherConnections(healthCheckMessage);
    }

}
