/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.rabbitmq.common.implementations;

import com.google.gson.Gson;
import com.kadirkorkmaz.rabbitmq.common.Connection;
import com.kadirkorkmaz.rabbitmq.common.Message;
import com.kadirkorkmaz.rabbitmq.common.MessageListener;
import com.kadirkorkmaz.rabbitmq.connector.RabbitMQConnector;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Kadir Korkmaz
 */
public class NetworkConnection implements Connection {

    private final ConnectionType type;
    private final NodeIdentifier nodeId;
    private com.rabbitmq.client.Connection rabbitmqConnection;
    private Channel channel;
    private final List<MessageListener> incommingMessagelisteners;
    private final List<MessageListener> outgoingMessagelisteners;

    private static final Gson gson = new Gson();

    public NetworkConnection(ConnectionType type, NodeIdentifier nodeId) {
        this.type = type;
        this.nodeId = nodeId;
        incommingMessagelisteners = new ArrayList<>();
        outgoingMessagelisteners = new ArrayList<>();
    }

    private void startListening() throws IOException {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                    AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                String messageJson = new String(body, "UTF-8");
                
                //System.out.println( nodeId + " --> " + messageJson);
                
                Message message = gson.fromJson(messageJson, NetworkMessage.class);
                for (MessageListener listener : incommingMessagelisteners) {
                    listener.notifyMessage(message);
                }

            }
        };
        channel.basicConsume(nodeId.getNodeId(), true, consumer);
    }

    @Override
    public void openConnection() throws IOException, TimeoutException {
        rabbitmqConnection = RabbitMQConnector.getConnection();
        channel = rabbitmqConnection.createChannel();
        if (type == ConnectionType.INCOMMING_CONNECTION) {
            channel.queueDeclare(nodeId.getNodeId(), false, false, false, null);
            startListening();
        }
    }

    @Override
    public ConnectionType getType() {
        return type;
    }

    @Override
    public void sendMessage(Message message) throws IOException {
        if (type == ConnectionType.OUT_GOING_CONNECTION) {
            channel.queueDeclare(nodeId.getNodeId(), false, false, false, null);
            channel.basicPublish("", nodeId.getNodeId(), null, gson.toJson(message).getBytes());
        }
    }

    @Override
    public void AddIncommingMessageListener(MessageListener listener) {
        incommingMessagelisteners.add(listener);
    }

    @Override
    public void AddOutgoingMessageListener(MessageListener listener) {
        outgoingMessagelisteners.add(listener);
    }

    @Override
    public void closeConnection() throws IOException, TimeoutException {
        channel.close();
        rabbitmqConnection.close();
    }

    @Override
    public NodeIdentifier getNodeId() {
        return nodeId;
    }

}