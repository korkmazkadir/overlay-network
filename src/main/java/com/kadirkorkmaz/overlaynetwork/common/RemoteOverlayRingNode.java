/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.common;

import com.kadirkorkmaz.overlaynetwork.implementation.Acknowledgement;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Kadir Korkmaz
 */
public interface RemoteOverlayRingNode extends Remote {

    public Acknowledgement sendLeft(String destinationNodeId, String message) throws RemoteException;

    public Acknowledgement sendRight(String destinationNodeId, String message) throws RemoteException;

    public String[] getConnectedNodeIds() throws RemoteException;
}
