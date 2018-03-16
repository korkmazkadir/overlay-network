/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork;

import com.kadirkorkmaz.overlaynetwork.common.NodeRegistry;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Kadir Korkmaz
 */
public class AppConfigTool {

    private static String URL = "127.0.0.1";

    private static String[] inputTokes = new String[10];
    private static String commandDelimeter = " ";

    private static String[] availableCommands = {"list", "connect", "disconnect", "send", "help", "exit"};

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
    }

    private static String getUserInput() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

    private static void parseUserInput(String userInput) {
        userInput.trim();
        inputTokes = userInput.split(commandDelimeter);
    }

    private static void processUserInput() throws RemoteException {
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
            String nodeId1 = inputTokes[1].trim();
            String nodeId2 = inputTokes[2].trim();

            if (nodeId1.isEmpty() || nodeId2.isEmpty()) {
                System.out.println("Error : node id cannot be empty");
                return;
            }

            boolean result = nodeRegistry.addConnectionBetween(nodeId1, nodeId2);
            System.out.println("Result : " + result);
        } else if (command.equals(availableCommands[3])) {

            String nodeId1 = inputTokes[1].trim();
            String nodeId2 = inputTokes[2].trim();
            String message = inputTokes[3].trim();

            if (nodeId1.isEmpty() || nodeId2.isEmpty() || message.isEmpty()) {
                System.out.println("Error : node id or message cannot be empty");
                return;
            }

            nodeRegistry.sendMessage(nodeId1, nodeId2, message);
            
        } else if (command.equals(availableCommands[4])) {
            printCommands();
        } else if (command.equals(availableCommands[5])) {
            System.out.println("Closing...");
            System.exit(0);
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
