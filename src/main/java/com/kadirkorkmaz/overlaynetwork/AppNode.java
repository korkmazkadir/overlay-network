/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork;

import com.kadirkorkmaz.overlaynetwork.common.NodeRegistry;
import com.kadirkorkmaz.overlaynetwork.common.RemoteNode;
import com.kadirkorkmaz.overlaynetwork.node.RemoteNetworkNode;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Kadir Korkmaz
 */
public class AppNode {

    private static String URL = "127.0.0.1";
    private static String PREFIX = "local";

    public static void main(String[] args) {

        try {

            System.out.println("Node is running...");

            Registry registry = LocateRegistry.getRegistry(2020);
            NodeRegistry nodeRegistry = (NodeRegistry) registry.lookup("node-registry");

            RemoteNetworkNode node = new RemoteNetworkNode(PREFIX);
            RemoteNode nodeStub = (RemoteNode) UnicastRemoteObject.exportObject(node, 0);

            nodeRegistry.registerNode(nodeStub);
            node.initilize();
            
            System.out.println("Node registered with id : " + nodeStub.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
