/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.implementation;

import com.overlaynetwork.common.RemoteNode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Kadir Korkmaz
 */
public class RandomTreeCreator {

    Map<String, RemoteNode> nameNodeMap;
    List<RemoteNode> nodes;

    public RandomTreeCreator() {
        nodes = new ArrayList<>();
        nameNodeMap = new LinkedHashMap<>();
    }

    public void addNode(RemoteNode newNode) {
        synchronized (nodes) {

            connectNodes(newNode, getRandomNode());
            nodes.add(newNode);
            
            try {
                nameNodeMap.put(newNode.getId(), newNode);
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
            
        }
    }

    public void removeAllNodes() {
        synchronized (nodes) {
            nodes.clear();
            nameNodeMap.clear();
        }
    }

    public void removeNode(String nodeId) {
        synchronized (nodes) {
            RemoteNode node = nameNodeMap.get(nodeId);
            if(node != null){
                boolean removeResult = nodes.remove(node);
                System.out.println("Removing node from random tree creator : " + nodeId + " result : " + removeResult);
                nameNodeMap.remove(node);
            }
        }
    }

    private RemoteNode getRandomNode() {
        int size = nodes.size();

        if (size == 0) {
            return null;
        }

        int minimum = 0;
        Random rand = new Random();
        int randomIndex = minimum + rand.nextInt((size - minimum));
        return nodes.get(randomIndex);
    }

    private void connectNodes(RemoteNode node1, RemoteNode node2) {

        if (node1 == null || node2 == null) {
            return;
        }

        try {
            node1.addConnection(node2.getId());
            node2.addConnection(node1.getId());
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

}
