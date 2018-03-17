/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kadirkorkmaz.overlaynetwork.topology;

import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import guru.nidi.graphviz.model.Graph;
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
        
        for (GraphNode graphNode : graphNodes) {
            System.out.println(graphNode);
        }
        
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
        Graphviz.fromGraph(global).width(1000).height(700).render(Format.PNG).toFile(new File(  outputFilePath ));
        return outputFilePath;
    }

}
