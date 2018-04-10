/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.connector;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public class RabbitMQConnector {

    private static final ConnectionFactory factory = new ConnectionFactory();

    public static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }

}
