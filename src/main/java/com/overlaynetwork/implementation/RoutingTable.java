/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.implementation;

import com.google.gson.internal.LinkedHashTreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Kadir Korkmaz
 */
public class RoutingTable {

    private final NodeIdentifier nodeId;

    private final Map<String, RoutingTableEntry> entryList;
    
    private AtomicLong version;

    public RoutingTable(NodeIdentifier nodeId) {
        this.nodeId = nodeId;
        entryList = new LinkedHashTreeMap<>();
        version = new AtomicLong(0L);
    }

    public NodeIdentifier getNodeId() {
        return nodeId;
    }

    public List<RoutingTableEntry> getEntryList() {
        return new ArrayList<>(entryList.values());
    }
    
    public void addEntry(RoutingTableEntry entry){
        entryList.put(entry.getDestinationNodeId().getNodeId(),entry);
        version.incrementAndGet();
    }
    
    public void updateCost(String nodeId, int cost){
        if(entryList.containsKey(nodeId)){
            System.out.println( nodeId + " new cost " + cost);
            entryList.get(nodeId).setCost(cost);
            version.incrementAndGet();
        }
    }
    
    public long getVersion(){
        return version.get();
    }

    public void incremenetVersion(){
        version.incrementAndGet();
    }
    
}
