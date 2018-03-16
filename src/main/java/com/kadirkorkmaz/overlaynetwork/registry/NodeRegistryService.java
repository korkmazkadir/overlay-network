/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.registry;

import com.kadirkorkmaz.overlaynetwork.common.NodeRegistry;
import com.kadirkorkmaz.overlaynetwork.common.RemoteNode;
import com.kadirkorkmaz.overlaynetwork.implementation.Statistic;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Kadir Korkmaz
 */
public class NodeRegistryService implements NodeRegistry {

    private final Map<String, AtomicInteger> nodeIdMap;
    private final Map<String, RemoteNode> nodeMap;

    public NodeRegistryService() {
        this.nodeIdMap = new ConcurrentHashMap<>();
        this.nodeMap = new ConcurrentHashMap<>();
    }

    @Override
    public void registerNode(RemoteNode node) throws RemoteException {
        synchronized (nodeIdMap) {
            String prefix = node.getPrefix();
            if (nodeIdMap.containsKey(prefix) == false) {
                nodeIdMap.put(prefix, new AtomicInteger(0));
            }
            int idNumber = nodeIdMap.get(prefix).incrementAndGet();
            String nodeId = prefix + "-" + idNumber;
            node.setId(nodeId);
            nodeMap.put(nodeId, node);
        }
    }

    @Override
    public void unregisterNode(RemoteNode node) throws RemoteException {
        nodeMap.remove(node.getId());
    }

    @Override
    public String[] getRegisteredNodes() throws RemoteException {
        Set<String> nodeIdSet;
        synchronized (nodeMap) {
            nodeIdSet = nodeMap.keySet();
        }
        return nodeIdSet.toArray(new String[nodeIdSet.size()]);
    }

    @Override
    public boolean addConnectionBetween(String nodeId1, String nodeId2) throws RemoteException {
        synchronized (nodeMap) {
            RemoteNode n1 = nodeMap.get(nodeId1);
            RemoteNode n2 = nodeMap.get(nodeId2);
            if (n1 == null || n2 == null) {
                return false;
            }
            n1.addConnection(nodeId2);
            n2.addConnection(nodeId1);
        }
        return true;
    }

    @Override
    public boolean removeConnectionBetween(String nodeId1, String nodeId2) throws RemoteException {
        synchronized (nodeMap) {
            RemoteNode n1 = nodeMap.get(nodeId1);
            RemoteNode n2 = nodeMap.get(nodeId2);
            if (n1 == null || n2 == null) {
                return false;
            }
            n1.removeConnection(nodeId2);
            n2.removeConnection(nodeId1);
        }
        return true;
    }

    @Override
    public void sendMessage(String sourceNodeId, String destinationNodeId, String message) throws RemoteException {
        RemoteNode source;
        synchronized (nodeMap) {
            source = nodeMap.get(sourceNodeId);
        }
        source.sendMessage(destinationNodeId, message);
    }

    @Override
    public Statistic getStatistics(String nodeId) throws RemoteException {
        RemoteNode node;
        synchronized (nodeMap) {
            node = nodeMap.get(nodeId);
        }
        return node.getStatistics();
    }

}
