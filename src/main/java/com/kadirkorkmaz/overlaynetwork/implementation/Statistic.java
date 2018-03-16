/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.implementation;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Kadir Korkmaz
 */
public class Statistic {

    private final AtomicLong acceptedMessageCount;

    private final AtomicLong routedMessageCount;

    private final AtomicLong unroutedMessageCount;

    public Statistic() {
        acceptedMessageCount = new AtomicLong(0);
        routedMessageCount = new AtomicLong(0);
        unroutedMessageCount = new AtomicLong(0);
    }

    public void incrementAccptedMessageCount() {
        acceptedMessageCount.incrementAndGet();
    }

    public void incrementRoutedMessageCount() {
        acceptedMessageCount.incrementAndGet();
    }

    public void incrementUnroutedMessageCount() {
        unroutedMessageCount.incrementAndGet();
    }

    public long getAcceptedMessageCount() {
        return acceptedMessageCount.get();
    }

    public long getRoutedMessageCount() {
        return routedMessageCount.get();
    }

    public long getUnroutedMessageCount() {
        return unroutedMessageCount.get();
    }

}
