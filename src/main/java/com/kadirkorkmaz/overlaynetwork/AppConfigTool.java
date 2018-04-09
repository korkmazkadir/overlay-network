/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork;

import com.kadirkorkmaz.overlaynetwork.common.NodeRegistry;
import com.kadirkorkmaz.overlaynetwork.implementation.Acknowledgement;
import com.kadirkorkmaz.overlaynetwork.graph.GraphDrawer;
import com.kadirkorkmaz.overlaynetwork.graph.GraphNode;
import com.kadirkorkmaz.overlaynetwork.topology.imageviewer.ImageViewer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kadir Korkmaz
 */
public class AppConfigTool {

    private static final Logger logger = Logger.getLogger(AppConfigTool.class.getName());
    
    private static String URL = "127.0.0.1";

    private static String[] inputTokes = new String[10];
    private static int tokenSize = 0;
    private static String commandDelimeter = " ";

    private static String[] availableCommands = {"list", "connect", "disconnect", "send", "help", "exit", "topology", "kill", "clear", "sendl", "sendr"};

    private static NodeRegistry nodeRegistry;

    private static void printPrompt() {
        System.out.print("overlay-config >> ");
    }

    private static void printCommands() {
        System.out.println("== Available Commands ==");
        System.out.println("list            :   Lists available nodes");
        System.out.println("connect [nodeId1] [nodeId2] :   Add connection between two noods");
        System.out.println("disconnect [nodeId1] [nodeId2]  :   Remove connection between two noods");
        System.out.println("send [source] [destination] [message] :   Send message from source to destination");
        System.out.println("help :   Provides help");
        System.out.println("exit :   Exit");
        System.out.println("topology  :   Shows topology of the network");
        System.out.println("kill [nodeId] [-all]    :   Shows topology of the network");
        System.out.println("clear  :   clears screen");
    }

    private static String getUserInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    private static void parseUserInput(String userInput) {
        String trimedInput = userInput.trim();
        inputTokes = trimedInput.split(commandDelimeter);
        tokenSize = inputTokes.length;
    }

    private static void processUserInput() throws RemoteException, IOException {
        String command = inputTokes[0].trim().toLowerCase();
        if (command.isEmpty()) {
            return;
        }

        if (command.equals(availableCommands[0])) {
            String[] registeredNodes = nodeRegistry.getRegisteredNodes();
            System.out.println(registeredNodes.length + " node found. Node ids :");
            for (String registeredNodeId : registeredNodes) {
                System.out.println(registeredNodeId);
            }
        } else if (command.equals(availableCommands[1])) {

            if (tokenSize != 3) {
                System.err.println("Error : connect accepts 2 parameter");
                return;
            }

            String nodeId1 = inputTokes[1].trim();
            String nodeId2 = inputTokes[2].trim();

            if (nodeId1.isEmpty() || nodeId2.isEmpty()) {
                System.out.println("Error : node id cannot be empty");
                return;
            }

            boolean result = nodeRegistry.addConnectionBetween(nodeId1, nodeId2);
            System.out.println("Result : " + result);
        } else if (command.equals(availableCommands[2])) {

            if (tokenSize != 3) {
                System.err.println("disconnect : connect accepts 2 parameter");
                return;
            }

            String nodeId1 = inputTokes[1].trim();
            String nodeId2 = inputTokes[2].trim();

            if (nodeId1.isEmpty() || nodeId2.isEmpty()) {
                System.out.println("Error : node id cannot be empty");
                return;
            }

            boolean result = nodeRegistry.removeConnectionBetween(nodeId1, nodeId2);
            System.out.println("Result : " + result);
        } else if (command.equals(availableCommands[3])) {

            if (tokenSize < 4) {
                System.err.println("Error : send accepts 4 parameter");
                return;
            }

            String nodeId1 = inputTokes[1].trim();
            String nodeId2 = inputTokes[2].trim();
            String message = "";

            for (int i = 3; i < tokenSize; i++) {
                message = message + " " + inputTokes[i];
            }

            if (nodeId1.isEmpty() || nodeId2.isEmpty() || message.isEmpty()) {
                System.out.println("Error : node id or message cannot be empty");
                return;
            }

            Acknowledgement ack = nodeRegistry.sendMessage(nodeId1, nodeId2, message);
            System.out.println("Ack : " + ack);

            Map<String, List<String>> topology = nodeRegistry.getTopology();
            Set<String> nodes = topology.keySet();
            List<GraphNode> graphTopology = new ArrayList<>(topology.size());
            for (String node : nodes) {
                graphTopology.add(new GraphNode(node, new LinkedHashSet<>(topology.get(node))));
            }

            if(ack == null){
                logger.log(Level.INFO, "ACK is null, no need to draw message path");
                return;
            }
            
            String outputFilePath = GraphDrawer.DrawPath(graphTopology, ack.getMessagePath());
            ImageViewer viewer = new ImageViewer();
            viewer.view(outputFilePath);

        } else if (command.equals(availableCommands[4])) {
            printCommands();
        } else if (command.equals(availableCommands[5])) {
            System.out.println("Closing...");
            System.exit(0);
        } else if (command.equals(availableCommands[6])) {
            System.out.println("Creating topology");
            Map<String, List<String>> topology = nodeRegistry.getTopology();
            Set<String> nodes = topology.keySet();
            List<GraphNode> graphTopology = new ArrayList<>(topology.size());
            for (String node : nodes) {
                graphTopology.add(new GraphNode(node, new LinkedHashSet<>(topology.get(node))));
            }
            String outputFilePath = GraphDrawer.Draw(graphTopology);
            ImageViewer viewer = new ImageViewer();
            viewer.view(outputFilePath);
        } else if (command.equals(availableCommands[7])) {
            String nodeId = inputTokes[1].trim();
            if (nodeId.equals("-all")) {
                boolean result = nodeRegistry.killAll();
                System.out.println("Killing all nodes, result = " + result);
            } else {
                boolean result = nodeRegistry.killNode(nodeId);
                System.out.println("Killing " + nodeId + ", result = " + result);
            }
        } else if (command.equals(availableCommands[8])) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } else if (command.equals(availableCommands[9])) {
            //sendl

            if (tokenSize < 4) {
                System.err.println("Error : sendl accepts 4 parameter");
                return;
            }

            String nodeId1 = inputTokes[1].trim();
            String nodeId2 = inputTokes[2].trim();
            String message = "";

            for (int i = 3; i < tokenSize; i++) {
                message = message + " " + inputTokes[i];
            }

            if (nodeId1.isEmpty() || nodeId2.isEmpty() || message.isEmpty()) {
                System.out.println("Error : node id or message cannot be empty");
                return;
            }

            Acknowledgement ack = nodeRegistry.ringSendLeft(nodeId1, nodeId2, message);
            System.out.println("Ack : " + ack);

        } else if (command.equals(availableCommands[10])) {
            if (tokenSize < 4) {
                System.err.println("Error : sendr accepts 4 parameter");
                return;
            }

            String nodeId1 = inputTokes[1].trim();
            String nodeId2 = inputTokes[2].trim();
            String message = "";

            for (int i = 3; i < tokenSize; i++) {
                message = message + " " + inputTokes[i];
            }

            if (nodeId1.isEmpty() || nodeId2.isEmpty() || message.isEmpty()) {
                System.out.println("Error : node id or message cannot be empty");
                return;
            }

            Acknowledgement ack = nodeRegistry.ringSendRight(nodeId1, nodeId2, message);
            System.out.println("Ack : " + ack);
            
        }

    }

    public static void main(String[] args) {

        try {

            Registry registry = LocateRegistry.getRegistry(2020);
            nodeRegistry = (NodeRegistry) registry.lookup("node-registry");

            while (true) {
                printPrompt();
                String userInput = getUserInput();
                parseUserInput(userInput);
                processUserInput();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
