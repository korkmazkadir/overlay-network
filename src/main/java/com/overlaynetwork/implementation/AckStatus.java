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
public enum AckStatus implements Serializable{
    SUCCESS, UNREACHABLE_NODE;
}
