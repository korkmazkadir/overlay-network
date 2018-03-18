/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.common;

import com.kadirkorkmaz.overlaynetwork.implementation.Acknowledgement;
import com.kadirkorkmaz.overlaynetwork.implementation.RoutingTable;
import com.kadirkorkmaz.overlaynetwork.implementation.Statistic;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Kadir Korkmaz
 */
public interface RemoteNode extends Remote {

    public void setId(String id) throws RemoteException;
    
    public String getId() throws RemoteException;

    public String getPrefix() throws RemoteException;

    public RoutingTable getRoutingTable() throws RemoteException;

    public Statistic getStatistics() throws RemoteException;

    public String[] getConnectedNodeIds() throws RemoteException;

    public void addConnection(String nodeId) throws RemoteException;

    public void removeConnection(String nodeId) throws RemoteException;
    
    public Acknowledgement sendMessage(String destinationNodeId, String message) throws RemoteException;

}
