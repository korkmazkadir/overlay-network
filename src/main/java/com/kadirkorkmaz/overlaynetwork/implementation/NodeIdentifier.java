/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.implementation;

/**
 *
 * @author Kadir Korkmaz
 */
public class NodeIdentifier {

    private final String nodeId;

    public NodeIdentifier(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeId() {
        return nodeId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj.getClass().equals(this.getClass())) {
            if (((NodeIdentifier) obj).getNodeId().equals(this.getNodeId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "NodeIdentifier{" + "nodeId=" + nodeId + '}';
    }

}
