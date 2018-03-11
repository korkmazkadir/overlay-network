/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.node;

import com.kadirkorkmaz.rabbitmq.common.Connection;
import com.kadirkorkmaz.rabbitmq.common.Message;
import com.kadirkorkmaz.rabbitmq.common.Node;
import com.kadirkorkmaz.rabbitmq.common.implementations.ConnectionType;
import com.kadirkorkmaz.rabbitmq.common.implementations.MessageType;
import com.kadirkorkmaz.rabbitmq.common.implementations.NetworkConnection;
import com.kadirkorkmaz.rabbitmq.common.implementations.NetworkMessage;
import com.kadirkorkmaz.rabbitmq.common.implementations.NodeIdentifier;
import com.kadirkorkmaz.rabbitmq.router.DynamicRouter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class NodeImp implements Node {

    private final NodeIdentifier id;
    private final Connection incommingConnection;
    private final List<Connection> connections;
    private final DynamicRouter router;

    public NodeImp(NodeIdentifier id) throws IOException, TimeoutException {
        this.id = id;
        incommingConnection = new NetworkConnection(ConnectionType.INCOMMING_CONNECTION, id);
        incommingConnection.openConnection();
        this.connections = new ArrayList<>();
        router = new DynamicRouter(this);
        incommingConnection.AddIncommingMessageListener(router);
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
            Logger.getLogger(NodeImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(NodeImp.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        connections.add(connection);
    }

    
    @Override
    public void removeConnection(NodeIdentifier nodeId) {
        
        for (Iterator<Connection> iterator = connections.iterator(); iterator.hasNext();) {
            Connection connection = iterator.next();
            if (connection.getNodeId().equals(id)) {
                try {
                    connection.closeConnection();
                } catch (IOException ex) {
                    Logger.getLogger(NodeImp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (TimeoutException ex) {
                    Logger.getLogger(NodeImp.class.getName()).log(Level.SEVERE, null, ex);
                }
                iterator.remove();
            }
        }   
    }

}
