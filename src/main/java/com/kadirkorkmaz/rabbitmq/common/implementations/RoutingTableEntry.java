/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.common.implementations;

/**
 *
 * @author Kadir Korkmaz
 */
public class RoutingTableEntry {

    private NodeIdentifier destinationNodeId;
    private NodeIdentifier overLinkNodeId;
    private int cost;

    public RoutingTableEntry(NodeIdentifier destinationNodeId, NodeIdentifier overLinkNodeId, int cost) {
        this.destinationNodeId = destinationNodeId;
        this.overLinkNodeId = overLinkNodeId;
        this.cost = cost;
    }

    public NodeIdentifier getDestinationNodeId() {
        return destinationNodeId;
    }

    public void setDestinationNodeId(NodeIdentifier destinationNodeId) {
        this.destinationNodeId = destinationNodeId;
    }

    public NodeIdentifier getOverLinkNodeId() {
        return overLinkNodeId;
    }

    public void setOverLinkNodeId(NodeIdentifier overLinkNodeId) {
        this.overLinkNodeId = overLinkNodeId;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj.getClass().equals(this.getClass()) == false) {
            return false;
        }

        RoutingTableEntry e = (RoutingTableEntry) obj;
        if (e.destinationNodeId == destinationNodeId && e.overLinkNodeId == overLinkNodeId && e.cost == cost) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return destinationNodeId.getNodeId().hashCode();
    }

    @Override
    public String toString() {
        return "RoutingTableEntry{" + "destinationNodeId=" + destinationNodeId + ", overLinkNodeId=" + overLinkNodeId + ", cost=" + cost + '}';
    }

}
