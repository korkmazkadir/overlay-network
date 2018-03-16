/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.node;

import com.kadirkorkmaz.overlaynetwork.common.Connection;
import com.kadirkorkmaz.overlaynetwork.common.Message;
import com.kadirkorkmaz.overlaynetwork.common.Node;
import com.kadirkorkmaz.overlaynetwork.implementation.ConnectionType;
import com.kadirkorkmaz.overlaynetwork.implementation.MessageType;
import com.kadirkorkmaz.overlaynetwork.implementation.NetworkConnection;
import com.kadirkorkmaz.overlaynetwork.implementation.NetworkMessage;
import com.kadirkorkmaz.overlaynetwork.implementation.NodeIdentifier;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTable;
import com.kadirkorkmaz.overlaynetwork.implementation.Statistic;
import com.kadirkorkmaz.overlaynetwork.router.DynamicRouter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class NetworkNode implements Node {

    protected NodeIdentifier id;
    protected Connection incommingConnection;
    protected final List<Connection> connections;
    protected DynamicRouter router;
    protected final Statistic statistics;

    protected NetworkNode(){
        this.connections = Collections.synchronizedList(new ArrayList());
        statistics = new Statistic();
    }

    public void initilize() throws IOException, TimeoutException {
        incommingConnection = new NetworkConnection(ConnectionType.INCOMMING_CONNECTION, id);
        router = new DynamicRouter(this);
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
    public void sendMessage(NodeIdentifier destination, String messageBody) {
        Message message = new NetworkMessage(MessageType.USER_DATA, this.id, destination, messageBody);
        router.routeMessage(message);
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
                if (connection.getNodeId().equals(id)) {
                    try {
                        connection.closeConnection();
                    } catch (IOException ex) {
                        Logger.getLogger(NetworkNode.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (TimeoutException ex) {
                        Logger.getLogger(NetworkNode.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    iterator.remove();
                }
            }
        }

    }

    @Override
    public RoutingTable getRoutinTable() {
        return router.getRoutingTable();
    }

}
