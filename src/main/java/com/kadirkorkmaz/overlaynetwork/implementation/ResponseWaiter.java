/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.implementation;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Kadir Korkmaz
 */
public class ResponseWaiter<T> {
    
    private T response = null;
    private final Semaphore semaphore = new Semaphore(0);
    
    public T waitForResponse(int duration, TimeUnit timeUnit) {
        
        while(true){
            try {
                semaphore.tryAcquire(duration,timeUnit);
            } catch (InterruptedException ex) {
                continue;
            }        
            break;
        }
        
        return response;
    }
    
    public void setResponse(T reponseObject){
        this.response = reponseObject;
        semaphore.release();
    }
    
    public T getResponse(){
        return response;
    }

}
