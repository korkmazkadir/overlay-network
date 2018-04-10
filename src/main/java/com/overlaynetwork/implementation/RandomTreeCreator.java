/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.implementation;

import com.overlaynetwork.common.RemoteNode;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Kadir Korkmaz
 */
public class RandomTreeCreator {

    List<RemoteNode> nodes;

    public RandomTreeCreator() {
        nodes = new ArrayList<>();
    }

    public void addNode(RemoteNode newNode) {
        synchronized (nodes) {
            connectNodes(newNode, getRandomNode());
            nodes.add(newNode);
        }
    }

    public void removeAllNodes() {
        synchronized (nodes) {
            nodes.clear();
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
