/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.node;

import com.kadirkorkmaz.rabbitmq.connector.RabbitMQConnector;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class NetworkNode implements Runnable {

    private final int id;
    private final int numberOfNodes;
    private final int nexNodeId;
    private final Connection connection;
    private final Channel channel;
    private boolean isInitiator;

    private final static String NAME_PREFIX = "INPUT_NODE_";

    public NetworkNode(int id, int numberOfNodes, int nexNodeId) throws IOException, TimeoutException {
        System.out.println("id " + id + " next id " + nexNodeId);

        this.id = id;
        this.numberOfNodes = numberOfNodes;
        this.nexNodeId = nexNodeId;
        this.connection = RabbitMQConnector.getConnection();
        this.channel = connection.createChannel();
        this.isInitiator = false;
        createInputQueue();
    }

    public void sendMessage(String message) throws IOException {
        isInitiator = true;
        channel.basicPublish("", getQueueName(nexNodeId), null, message.getBytes());
    }

    public int getId() {
        return id;
    }

    private String getQueueName(int nodeID) {
        return NAME_PREFIX + nodeID;
    }

    private void createInputQueue() throws IOException {
        channel.queueDeclare(getQueueName(this.id), false, false, false, null);
    }

    private void handleMessage(String message) throws IOException {

        System.out.println(" Message Received by " + id + " --> " + message);
        
        if(isInitiator == false){
            channel.basicPublish("", getQueueName(nexNodeId), null, message.getBytes());
        }else{
            this.isInitiator = false;
            System.exit(0);
        }

    }

    private void startListening() throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                handleMessage(message);
            }
        };
        channel.basicConsume(getQueueName(this.id), true, consumer);
    }

    @Override
    public void run() {
        try {
            startListening();
        } catch (IOException ex) {
            Logger.getLogger(NetworkNode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
