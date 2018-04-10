/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.implementation;

import java.io.Serializable;

/**
 *
 * @author Kadir Korkmaz
 */
public class Acknowledgement implements Serializable{

    private long id;
    private AckStatus status;
    private String senderId;
    private String[] messagePath;

    public Acknowledgement(long id, AckStatus status, String senderId, String[] messagePath) {
        this.id = id;
        this.status = status;
        this.senderId = senderId;
        this.messagePath = messagePath;
    }

    public long getId() {
        return id;
    }

    public AckStatus getStatus() {
        return status;
    }

    public String getSenderId() {
        return senderId;
    }

    public String[] getMessagePath() {
        return messagePath;
    }

    @Override
    public String toString() {
        return "Acknowledgement{" + "status=" + status + ", senderId=" + senderId + ", messagePath=" + String.join(",", messagePath) + '}';
    }
}
