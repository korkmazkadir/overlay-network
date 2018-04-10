/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.node;

import com.overlaynetwork.common.Connection;
import com.overlaynetwork.common.Message;
import com.overlaynetwork.common.MessageListener;
import com.overlaynetwork.common.Node;
import com.overlaynetwork.implementation.Acknowledgement;
import com.overlaynetwork.implementation.ConnectionType;
import com.overlaynetwork.implementation.MessageType;
import com.overlaynetwork.implementation.NetworkConnection;
import com.overlaynetwork.implementation.NetworkMessage;
import com.overlaynetwork.implementation.NodeIdentifier;
import com.overlaynetwork.implementation.RoutingTable;
import com.overlaynetwork.implementation.Statistic;
import com.overlaynetwork.router.DynamicRouter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class NetworkNode implements Node, MessageListener {

    protected NodeIdentifier id;
    protected Connection incommingConnection;
    protected final List<Connection> connections;
    protected DynamicRouter router;
    protected final Statistic statistics;
    private final List<MessageListener> messageListeners;

    protected NetworkNode() {
        this.connections = Collections.synchronizedList(new ArrayList());
        statistics = new Statistic();
        messageListeners = new LinkedList<>();
    }

    public void initilize() throws IOException, TimeoutException {
        incommingConnection = new NetworkConnection(ConnectionType.INCOMMING_CONNECTION, id);
        router = new DynamicRouter(this);
        router.addIncommingMessageListener(this);
        incommingConnection.openConnection();
        incommingConnection.addIncommingMessageListener(router);
    }

    public NetworkNode(NodeIdentifier id) throws IOException, TimeoutException {
        this.id = id;
        incommingConnection = new NetworkConnection(ConnectionType.INCOMMING_CONNECTION, id);
        incommingConnection.openConnection();
        this.connections = Collections.synchronizedList(new ArrayList());
        router = new DynamicRouter(this);
        incommingConnection.addIncommingMessageListener(router);
        statistics = new Statistic();
        messageListeners = new LinkedList<>();
        System.out.println("Constructor-2");
    }

    @Override
    public NodeIdentifier getIdentifier() {
        return id;
    }

    @Override
    public List<Connection> getConnections() {
        return connections;
    }

    @Override
    public void handleMessage(Message message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Acknowledgement sendMessage(NodeIdentifier destination, String messageBody) {
        Message message = new NetworkMessage(MessageType.USER_DATA, this.id, destination, messageBody);
        Acknowledgement ack = router.routeMessage(message);
        return ack;
    }

    @Override
    public void addConnection(NodeIdentifier nodeId) {
        Connection connection = new NetworkConnection(ConnectionType.OUT_GOING_CONNECTION, nodeId);
        try {
            connection.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(NetworkNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(NetworkNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        connections.add(connection);
    }

    @Override
    public void removeConnection(NodeIdentifier nodeId) {
        synchronized (connections) {
            for (Iterator<Connection> iterator = connections.iterator(); iterator.hasNext();) {
                Connection connection = iterator.next();
                if (connection.getNodeId().equals(nodeId)) {
                    try {
                        connection.closeConnection();
                    } catch (IOException | TimeoutException ex) {
                        ex.printStackTrace();
                    }
                    iterator.remove();
                    System.out.println("Connection removed with node : " + nodeId.getNodeId());
                }
            }
        }

    }

    @Override
    public RoutingTable getRoutinTable() {
        return router.getRoutingTable();
    }

    @Override
    public void addIncommingMessageListener(MessageListener listener) {
        messageListeners.add(listener);
    }

    private void notifyListeners(Message message) {
        System.out.println("Node listener count " + messageListeners.size());
        for (MessageListener messageListener : messageListeners) {
            System.out.println("Network node is notifiying...");
            messageListener.notifyMessage(message);
        }
    }

    @Override
    public void notifyMessage(Message message) {
        notifyListeners(message);
    }

}
