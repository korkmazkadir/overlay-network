/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.implementation;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Kadir Korkmaz
 */
public class MessageIdProvider {

    private static AtomicLong id = new AtomicLong(0);

    private MessageIdProvider() {
    }

    public static long GetNewId() {
        return id.incrementAndGet();
    }
    
}
