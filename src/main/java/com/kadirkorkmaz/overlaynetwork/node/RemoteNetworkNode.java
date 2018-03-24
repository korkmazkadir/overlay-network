/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.node;

import com.kadirkorkmaz.overlaynetwork.common.Connection;
import com.kadirkorkmaz.overlaynetwork.common.RemoteNode;
import com.kadirkorkmaz.overlaynetwork.implementation.Acknowledgement;
import com.kadirkorkmaz.overlaynetwork.implementation.NodeIdentifier;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTable;
import com.kadirkorkmaz.overlaynetwork.implementation.Statistic;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class RemoteNetworkNode extends NetworkNode implements RemoteNode {

    private final String prefix;

    public RemoteNetworkNode() throws IOException, TimeoutException {
        super();
        this.prefix = "";
    }

    public RemoteNetworkNode(String prefix) throws IOException, TimeoutException {
        super();
        this.prefix = prefix;
    }

    @Override
    public void setId(String id) throws RemoteException {
        this.id = new NodeIdentifier(id);
    }

    @Override
    public String getId() throws RemoteException {
        return id.getNodeId();
    }

    @Override
    public String getPrefix() throws RemoteException {
        return this.prefix;
    }

    @Override
    public RoutingTable getRoutingTable() throws RemoteException {
        return this.getRoutinTable();
    }

    @Override
    public Statistic getStatistics() throws RemoteException {
        return statistics;
    }

    @Override
    public String[] getConnectedNodeIds() throws RemoteException {
        String[] nodeIds;
        synchronized (connections) {
            nodeIds = new String[connections.size()];
            int index = 0;
            for (Connection conn : connections) {
                nodeIds[index] = conn.getNodeId().getNodeId();
                index++;
            }
        }
        return nodeIds;
    }

    @Override
    public void addConnection(String nodeId) throws RemoteException {
        this.addConnection(new NodeIdentifier(nodeId));
    }

    @Override
    public void removeConnection(String nodeId) throws RemoteException {
        System.out.println("Removing connectin with " + nodeId);
        super.removeConnection(new NodeIdentifier(nodeId));
    }

    @Override
    public Acknowledgement sendMessage(String destinationNodeId, String message) throws RemoteException {
        return this.sendMessage(new NodeIdentifier(destinationNodeId), message);
    }

    @Override
    public void kill() throws RemoteException {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    incommingConnection.closeConnection();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                System.out.println("Closing node " + id.getNodeId());
                synchronized (connections) {
                    for (Connection conn : connections) {
                        try {
                            conn.closeConnection();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                System.exit(0);
            }
        }, 1000);
    }

}
