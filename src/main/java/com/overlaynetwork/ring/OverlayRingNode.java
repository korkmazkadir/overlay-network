/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.ring;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.overlaynetwork.common.Message;
import com.overlaynetwork.common.MessageListener;
import com.overlaynetwork.common.RemoteOverlayRingNode;
import com.overlaynetwork.implementation.AckStatus;
import com.overlaynetwork.implementation.Acknowledgement;
import com.overlaynetwork.implementation.NodeIdentifier;
import com.overlaynetwork.implementation.ResponseWaiter;
import com.overlaynetwork.implementation.RoutingTable;
import com.overlaynetwork.implementation.RoutingTableEntry;
import com.overlaynetwork.node.NetworkNode;
import com.overlaynetwork.router.DynamicRouter;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Kadir Korkmaz
 */
public class OverlayRingNode implements RemoteOverlayRingNode, MessageListener {

    private static AtomicLong MESSAGE_ID_COUNTER = new AtomicLong(0L);

    private final static Gson gson = new Gson();

    private final NetworkNode node;

    private String nodeId;

    private final RingConnection ringConnections;

    private final Map<Long, ResponseWaiter<Acknowledgement>> reponseWaiterMap;

    public OverlayRingNode(NetworkNode node) {
        this.node = node;
        node.addIncommingMessageListener(this);
        ringConnections = new RingConnection();
        reponseWaiterMap = new LinkedHashMap<>();
    }

    private String getNodeId() {
        if (nodeId == null) {
            nodeId = node.getIdentifier().getNodeId();
        }
        return nodeId;
    }

    @Override
    public Acknowledgement sendLeft(String destinationNodeId, String message) throws RemoteException {
        OverlayRingMessage overlayMessage = createRingMessage(destinationNodeId, message, false);
        return sendLeft(overlayMessage, true);
    }

    private Acknowledgement sendLeft(OverlayRingMessage overlayMessage, boolean waitForResponse) {
        RingConnection connections = getConnectedNodes();
        if (connections.leftConnection == null) {
            return null;
        }

        node.sendMessage(new NodeIdentifier(connections.leftConnection), gson.toJson(overlayMessage));

        if (waitForResponse == false) {
            return null;
        }

        ResponseWaiter<Acknowledgement> waiter = new ResponseWaiter<>();
        reponseWaiterMap.put(overlayMessage.id, waiter);

        waiter.waitForResponse(1, TimeUnit.SECONDS);
        synchronized (reponseWaiterMap) {
            reponseWaiterMap.remove(overlayMessage.id);
        }
        return waiter.getResponse();
    }

    @Override
    public Acknowledgement sendRight(String destinationNodeId, String message) throws RemoteException {
        OverlayRingMessage overlayMessage = createRingMessage(destinationNodeId, message, false);
        return sendRight(overlayMessage, true);
    }

    private Acknowledgement sendRight(OverlayRingMessage overlayMessage, boolean waitForResponse) {
        RingConnection connections = getConnectedNodes();
        if (connections.rightConnection == null) {
            return null;
        }

        node.sendMessage(new NodeIdentifier(connections.rightConnection), gson.toJson(overlayMessage));

        if (waitForResponse == false) {
            return null;
        }

        ResponseWaiter<Acknowledgement> waiter = new ResponseWaiter<>();
        reponseWaiterMap.put(overlayMessage.id, waiter);

        waiter.waitForResponse(30, TimeUnit.SECONDS);
        synchronized (reponseWaiterMap) {
            reponseWaiterMap.remove(overlayMessage.id);
        }
        return waiter.getResponse();
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
                if (entry.getCost() < DynamicRouter.MAX_LINK_COST) {
                    connectedNodes.add(entry.getDestinationNodeId().getNodeId());
                }else{
                    System.out.println("---> Discarting : " + entry.getDestinationNodeId().getNodeId());
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

    private OverlayRingMessage createRingMessage(String destinationNodeId, String message, boolean isAck) {
        OverlayRingMessage m = new OverlayRingMessage(MESSAGE_ID_COUNTER.incrementAndGet(), getNodeId(), destinationNodeId, message, false);
        m.path.add(getNodeId());
        return m;
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

        //Adding message path to node id
        if (m.isAckMessage == false) {
            m.addToPath(getNodeId());
        }

        if (m.destinationId.equals(getNodeId())) {
            System.out.println("Overlay Message Arrived : " + message.getBody());

            if (m.isAckMessage == true) {
                System.out.println( getCurrentTimeStamp() + "Ack received from " + m.sourceId);
                String[] messagePath = m.path.toArray(new String[m.path.size()]);
                Acknowledgement ack = new Acknowledgement(m.id, AckStatus.SUCCESS, nodeId, messagePath);
                ResponseWaiter<Acknowledgement> waiter = reponseWaiterMap.get(ack.getId());
                synchronized (reponseWaiterMap) {
                    if (waiter != null) {
                        waiter.setResponse(ack);
                    } else {
                        System.out.println("waiter is null");
                    }
                }
            } else {
                sendAck(m, message.getSender().getNodeId());
            }

        } else {

            RingConnection connections = getConnectedNodes();
            String left = connections.leftConnection;
            String right = connections.rightConnection;

            //System.out.println("Right node : " + right);
            //System.out.println("Left node : " + left);

            System.out.println( getCurrentTimeStamp() + "Routing message received from " + m.message);
            
            if (message.getSender().getNodeId().equals(left)) {
                System.out.println(getNodeId() + " --> Sending Right " + right);
                sendRight(m, false);
            } else if (message.getSender().getNodeId().equals(right)) {
                System.out.println(getNodeId() + " --> Sending Left " + left);
                sendLeft(m, false);
            }

        }
    }

    @Override
    public String[] getConnectedNodeIds() throws RemoteException {
        RingConnection ringConnection = getConnectedNodes();
        List<String> connections = new ArrayList<>();
        if (ringConnection.leftConnection != null) {
            connections.add(ringConnection.leftConnection);
        }
        if (ringConnection.rightConnection != null) {
            connections.add(ringConnection.rightConnection);
        }

        return connections.toArray(new String[connections.size()]);
    }

    private void sendAck(OverlayRingMessage message, String physicalSourceId) {

        if (message.isAckMessage == true) {
            return;
        }

        RingConnection connections = getConnectedNodes();

        OverlayRingMessage ackMessage = new OverlayRingMessage(message.id, getNodeId(), message.sourceId, "", true);
        ackMessage.path = message.path;

        System.out.println( getCurrentTimeStamp() + " Sending ack to " + physicalSourceId);
        
        if (physicalSourceId.equals(connections.leftConnection)) {
            node.sendMessage(new NodeIdentifier(connections.leftConnection), gson.toJson(ackMessage));
        } else if (physicalSourceId.equals(connections.rightConnection)) {
            node.sendMessage(new NodeIdentifier(connections.rightConnection), gson.toJson(ackMessage));
        } else {
            System.out.println("Source : " + physicalSourceId + " Overlay node no path for ack : " + connections);
        }

        System.out.println( getCurrentTimeStamp() + " end of ack send " + physicalSourceId);
        
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    class OverlayRingMessage {

        public long id;
        public String sourceId;
        public String destinationId;
        public String message;
        public List<String> path;
        public boolean isAckMessage;

        public OverlayRingMessage() {
        }

        public OverlayRingMessage(long id, String sourceId, String destinationId, String message, boolean isAckMessage) {
            this.id = id;
            this.sourceId = sourceId;
            this.destinationId = destinationId;
            this.message = message;
            this.isAckMessage = isAckMessage;
            this.path = new LinkedList<>();
        }

        public void addToPath(String pathElement) {
            path.add(pathElement);
        }

    }

    class RingConnection {

        public long version = -1L;
        public String leftConnection;
        public String rightConnection;

        @Override
        public String toString() {
            return "RingConnection{" + "version=" + version + ", leftConnection=" + leftConnection + ", rightConnection=" + rightConnection + '}';
        }

    }

}
