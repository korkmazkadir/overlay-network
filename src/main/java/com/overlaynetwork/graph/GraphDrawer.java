/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.overlaynetwork.graph;

import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Label;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.Node;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Kadir Korkmaz
 */
public class GraphDrawer {

    private GraphDrawer() {
    }

    private static GraphNode findByName(List<GraphNode> graphNodes, String nameToSearch) {
        for (GraphNode graphNode : graphNodes) {
            if (graphNode.getName().equals(nameToSearch)) {
                return graphNode;
            }
        }
        return null;
    }

    private static void removeDublicates(List<GraphNode> graphNodes) {
        for (GraphNode node : graphNodes) {
            Set<String> connections = node.getConnections();
            for (String connection : connections) {
                GraphNode connectedNode = findByName(graphNodes, connection);
                if (connectedNode != null) {
                    connectedNode.removeConnection(node.getName());
                }
            }
        }
    }

    private static Graph[] listToArray(List<Graph> graphList) {
        Graph[] graphArray = new Graph[graphList.size()];
        for (int i = 0; i < graphList.size(); i++) {
            graphArray[i] = graphList.get(i);
        }
        return graphArray;
    }

    private static String getFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String Draw(List<GraphNode> graphNodes) throws IOException {
        List<Graph> graphs = new LinkedList<>();
        removeDublicates(graphNodes);

        for (GraphNode n : graphNodes) {
            Set<String> connections = n.getConnections();
            int size = connections.size();
            Node[] nodes = new Node[size + 1];
            nodes[0] = node(n.getName()).with(Shape.CIRCLE);
            int index = 1;
            for (String connection : connections) {
                nodes[index] = node(connection).with(Shape.CIRCLE);
                index++;
            }
            graphs.add(graph().with(nodes[0].link(nodes)));
        }

        Graph global = graph().with(listToArray(graphs));
        //Graph global = graph().with(node("a").link(node("b")));

        String outputFilePath = "topology/" + getFileName() + ".png";
        Graphviz.fromGraph(global).width(1000).height(700).render(Format.PNG).toFile(new File(outputFilePath));
        return outputFilePath;
    }

    private static boolean isInArray(String[] elements, String elementToLook) {
        for (String element : elements) {
            if (elementToLook.equals(element)) {
                return true;
            }
        }
        return false;
    }

    public static String DrawPath(List<GraphNode> graphNodes, String[] messagePath) throws IOException {

        List<Graph> graphs = new LinkedList<>();
        removeDublicates(graphNodes);

        int messagePathLength = messagePath.length;
        Node[] messageNodes = new Node[messagePath.length];
        for (int i = 0; i < messagePathLength; i++) {
            String nodeName = messagePath[i];
            messageNodes[i] = node(nodeName).with(Shape.CIRCLE);
        }

        List<Graph> pathGraps = new LinkedList<>();
        for (int i = 0; i < messagePathLength; i++) {
            String nodeName = messagePath[i];
            Node n1 = node(nodeName).with(Shape.CIRCLE);
            if (i + 1 < messagePathLength) {
                String nodeName2 = messagePath[i + 1];
                Node n2 = node(nodeName2).with(Shape.CIRCLE);
                pathGraps.add(graph().directed().with(n1.link(Link.to(n2).with(Label.of(Integer.toString(i + 1)), guru.nidi.graphviz.attribute.Color.RED))));
            }
        }

        for (GraphNode n : graphNodes) {
            Set<String> connections = n.getConnections();
            int size = connections.size();
            Node[] nodes = new Node[size + 1];
            String nodeName = n.getName();
            nodes[0] = node(nodeName).with(Shape.CIRCLE);
            int index = 1;

            boolean isInPath = isInArray(messagePath, nodeName);

            for (String connection : connections) {
                if (isInPath && isInArray(messagePath, connection)) {
                    continue;
                }
                nodes[index] = node(connection).with(Shape.CIRCLE);
                index++;
            }

            Node[] newNodes = new Node[index];
            for (int i = 0; i < index; i++) {
                newNodes[i] = nodes[i];
            }

            graphs.add(graph().with(nodes[0].link(newNodes)));

        }

        graphs.addAll(0, pathGraps);

        Graph global = graph().with(listToArray(graphs));
        //Graph global = graph().with(node("a").link(node("b")));

        String outputFilePath = "topology/" + getFileName() + ".png";
        Graphviz.fromGraph(global).width(1000).height(700).render(Format.PNG).toFile(new File(outputFilePath));
        return outputFilePath;
    }

}
