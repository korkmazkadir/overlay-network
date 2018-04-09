/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.registry;

import com.kadirkorkmaz.overlaynetwork.common.NodeRegistry;
import com.kadirkorkmaz.overlaynetwork.common.RemoteNode;
import com.kadirkorkmaz.overlaynetwork.implementation.Acknowledgement;
import com.kadirkorkmaz.overlaynetwork.implementation.RandomTreeCreator;
import com.kadirkorkmaz.overlaynetwork.implementation.Statistic;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
    private final RandomTreeCreator randomTreeCreator;

    public NodeRegistryService() {
        this.nodeIdMap = new ConcurrentHashMap<>();
        this.nodeMap = new ConcurrentHashMap<>();
        this.randomTreeCreator = new RandomTreeCreator();
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
            randomTreeCreator.addNode(node);
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
    public Acknowledgement sendMessage(String sourceNodeId, String destinationNodeId, String message) throws RemoteException {
        RemoteNode source;
        synchronized (nodeMap) {
            source = nodeMap.get(sourceNodeId);
        }
        if (source != null) {
            return source.sendMessage(destinationNodeId, message);
        }
        System.out.println("Send message node is null : " + sourceNodeId);
        return null;
    }

    @Override
    public Statistic getStatistics(String nodeId) throws RemoteException {
        RemoteNode node;
        synchronized (nodeMap) {
            node = nodeMap.get(nodeId);
        }
        return node.getStatistics();
    }

    @Override
    public Map<String, List<String>> getTopology() throws RemoteException {
        Map<String, List<String>> topology = new LinkedHashMap<>();
        synchronized (nodeMap) {
            Set<String> nodes = nodeMap.keySet();
            for (String node : nodes) {
                topology.put(node, Arrays.asList(nodeMap.get(node).getConnectedNodeIds()));
            }
        }
        return topology;
    }

    @Override
    public boolean killNode(String nodeId) throws RemoteException {
        synchronized (nodeMap) {
            RemoteNode node = nodeMap.get(nodeId);
            if (node != null) {
                node.kill();
                nodeMap.remove(nodeId);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean killAll() throws RemoteException {
        synchronized (nodeMap) {
            Set<String> nodes = nodeMap.keySet();
            for (String node : nodes) {
                nodeMap.get(node).kill();
            }
            nodeMap.clear();
            nodeIdMap.clear();
            randomTreeCreator.removeAllNodes();
        }
        return true;
    }

    @Override
    public Acknowledgement ringSendLeft(String sourceNodeId, String destinationNodeId, String message) throws RemoteException {
        RemoteNode source;
        synchronized (nodeMap) {
            source = nodeMap.get(sourceNodeId);
        }
        if (source != null) {
            return source.getRingOverlayNode().sendLeft(destinationNodeId, message);
        }
        System.out.println("Send left no node with name : " + sourceNodeId);
        return null;
    }

    @Override
    public Acknowledgement ringSendRight(String sourceNodeId, String destinationNodeId, String message) throws RemoteException {
        RemoteNode source;
        synchronized (nodeMap) {
            source = nodeMap.get(sourceNodeId);
        }
        if (source != null) {
            return source.getRingOverlayNode().sendRight(destinationNodeId, message);
        }
        System.out.println("Send right no node with name : " + sourceNodeId);
        return null;
    }

    @Override
    public Map<String, List<String>> getOverlayRingTopology() throws RemoteException {
        Map<String, List<String>> topology = new LinkedHashMap<>();
        synchronized (nodeMap) {
            Set<String> nodes = nodeMap.keySet();
            for (String node : nodes) {
                topology.put(node, Arrays.asList(nodeMap.get(node).getRingOverlayNode().getConnectedNodeIds()));
            }
        }
        return topology;
    }

}
