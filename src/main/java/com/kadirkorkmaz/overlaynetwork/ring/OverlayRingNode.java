/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.ring;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.kadirkorkmaz.overlaynetwork.common.Message;
import com.kadirkorkmaz.overlaynetwork.common.MessageListener;
import com.kadirkorkmaz.overlaynetwork.common.RemoteOverlayRingNode;
import com.kadirkorkmaz.overlaynetwork.implementation.Acknowledgement;
import com.kadirkorkmaz.overlaynetwork.implementation.NodeIdentifier;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTable;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTableEntry;
import com.kadirkorkmaz.overlaynetwork.node.NetworkNode;
import java.rmi.RemoteException;
import java.util.TreeSet;

/**
 *
 * @author Kadir Korkmaz
 */
public class OverlayRingNode implements RemoteOverlayRingNode, MessageListener {

    private final static Gson gson = new Gson();

    private final NetworkNode node;

    private String nodeId;

    private final RingConnection ringConnections;

    public OverlayRingNode(NetworkNode node) {
        this.node = node;
        node.addIncommingMessageListener(this);
        ringConnections = new RingConnection();
    }

    private String getNodeId() {
        if (nodeId == null) {
            nodeId = node.getIdentifier().getNodeId();
        }
        return nodeId;
    }

    @Override
    public Acknowledgement sendLeft(String destinationNodeId, String message) throws RemoteException {
        RingConnection connections = getConnectedNodes();
        if (connections.leftConnection == null) {
            return null;
        }
        return node.sendMessage(new NodeIdentifier(connections.leftConnection), createRingMessage(destinationNodeId, message));
    }

    @Override
    public Acknowledgement sendRight(String destinationNodeId, String message) throws RemoteException {
        RingConnection connections = getConnectedNodes();
        if (connections.rightConnection == null) {
            return null;
        }
        return node.sendMessage(new NodeIdentifier(connections.rightConnection), createRingMessage(destinationNodeId, message));
    }

    private RingConnection getConnectedNodes() {
        RoutingTable routingTable = node.getRoutinTable();
        synchronized (routingTable) {

            long routingTableVersion = routingTable.getVersion();
            if (routingTableVersion == ringConnections.version) {
                return ringConnections;
            }

            TreeSet<String> connectedNodes = new TreeSet<>();
            for (RoutingTableEntry entry : routingTable.getEntryList()) {
                if (entry.getCost() < 1000) {
                    connectedNodes.add(entry.getDestinationNodeId().getNodeId());
                }
            }
            
            ringConnections.version = routingTableVersion;
            ringConnections.leftConnection = getLeftConnectionId(connectedNodes);
            ringConnections.rightConnection = getRightConnectionId(connectedNodes);
            
            return ringConnections;
        }
    }

    private String getRightConnectionId(TreeSet<String> connectedNodes) {
        String id = connectedNodes.higher(getNodeId());
        if (id == null) {
            // if it is null we have the higher id
            id = connectedNodes.first();
            if (id.equals(getNodeId())) {
                //There is no body return null
                return null;
            }
        }
        System.out.println("Right connection id :  " + id);
        return id;
    }

    private String getLeftConnectionId(TreeSet<String> connectedNodes) {
        String id = connectedNodes.lower(getNodeId());
        if (id == null) {
            // if it is null we have the lowest id
            id = connectedNodes.last();
            if (id.equals(getNodeId())) {
                //There is no body return null
                return null;
            }
        }
        System.out.println("Left connection id :  " + id);
        return id;
    }

    private String createRingMessage(String destinationNodeId, String message) {
        return gson.toJson(new OverlayRingMessage(destinationNodeId, message));
    }

    @Override
    public void notifyMessage(Message message) {

        System.out.println("Overlay Message : " + message.getBody());

        OverlayRingMessage m = null;
        try {
            m = gson.fromJson(message.getBody(), OverlayRingMessage.class);
        } catch (JsonSyntaxException e) {
            System.out.println("No need to process message on this level");
            return;
        }
        
        if (m.destinationId.equals(getNodeId())) {
            System.out.println("Overlay Message Arrived : " + message.getBody());
        } else {

            RingConnection connections = getConnectedNodes();
            String left = connections.leftConnection;
            String right = connections.rightConnection;

            System.out.println("Right node : " + right);
            System.out.println("Left node : " + left);

            if (message.getSender().getNodeId().equals(left)) {
                System.out.println(getNodeId() + " --> Sending Right " + right);
                try {
                    Acknowledgement ack = sendRight(m.destinationId, m.message);
                    System.out.println("ack : " + ack);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            } else if (message.getSender().getNodeId().equals(right)) {
                System.out.println(getNodeId() + " --> Sending Left " + left);
                try {
                    Acknowledgement ack = sendLeft(m.destinationId, m.message);
                    System.out.println("ack : " + ack);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    class OverlayRingMessage {

        public String destinationId;
        public String message;

        public OverlayRingMessage(String destinationId, String message) {
            this.destinationId = destinationId;
            this.message = message;
        }
    }

    class RingConnection {
        public long version = -1L;
        public String leftConnection;
        public String rightConnection;
    }

}
