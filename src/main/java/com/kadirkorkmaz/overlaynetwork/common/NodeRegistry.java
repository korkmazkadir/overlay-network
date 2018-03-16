/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.common;

import com.kadirkorkmaz.overlaynetwork.implementation.Statistic;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Kadir Korkmaz
 */
public interface NodeRegistry extends Remote {

    /*Registeres a node*/
    public void registerNode(RemoteNode node) throws RemoteException;

    /*Unregisters a registered node*/
    public void unregisterNode(RemoteNode node) throws RemoteException;

    /*Returns ID's of registered nodes*/
    public String[] getRegisteredNodes() throws RemoteException;

    /*Adds a uni directional connection between two node*/
    public boolean addConnectionBetween(String nodeId1, String nodeId2) throws RemoteException;

    /*Removes connection between two node*/
    public boolean removeConnectionBetween(String nodeId1, String nodeId2) throws RemoteException;

    /*Sends message from source node to destination node over the overlay network*/
    public void sendMessage(String sourceNodeId, String destinationNodeId, String message) throws RemoteException;

    /*Returns Statistics of a node*/
    public Statistic getStatistics(String nodeId) throws RemoteException;

}
