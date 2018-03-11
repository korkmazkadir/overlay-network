/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.common.implementations;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kadir Korkmaz
 */
public class RoutingTable {

    private final NodeIdentifier nodeId;

    private final List<RoutingTableEntry> entryList;

    public RoutingTable(NodeIdentifier nodeId) {
        this.nodeId = nodeId;
        entryList = new ArrayList<>();
    }

    public NodeIdentifier getNodeId() {
        return nodeId;
    }

    public List<RoutingTableEntry> getEntryList() {
        return entryList;
    }
    
    public void addEntry(RoutingTableEntry entry){
        entryList.add(entry);
    }

}
