/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.graph;

import java.util.Set;

/**
 *
 * @author Kadir Korkmaz
 */
public class GraphNode {
    
    private final String name;
    private final Set<String> connections;

    public GraphNode(String nodeName, Set<String> connections) {
        this.name = nodeName;
        this.connections = connections;
    }
    
    public String getName(){
        return name;
    }
    
    public void addConnection(){
        connections.add(name);
    }
    
    public void removeConnection(String connectionName){
        connections.remove(connectionName);
    }
    
    public Set<String> getConnections(){
        return connections;
    }

    @Override
    public String toString() {
        return "GraphNode{" + "name=" + name + ", connections=" + connections + '}';
    }
    
}
