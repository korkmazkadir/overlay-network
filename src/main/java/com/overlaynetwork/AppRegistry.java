/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork;

import com.overlaynetwork.common.NodeRegistry;
import com.overlaynetwork.registry.NodeRegistryService;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Kadir Korkmaz
 */
public class AppRegistry {

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().exec("rmiregistry 2020");
        LocateRegistry.createRegistry(2020);

        try {
            NodeRegistryService registryService = new NodeRegistryService();
            NodeRegistry nodeRegistryStub = (NodeRegistry) UnicastRemoteObject.exportObject(registryService, 0);

            Registry registry = LocateRegistry.getRegistry(2020);
            registry.bind("node-registry", nodeRegistryStub);

            System.out.println("Registry Up and Running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
