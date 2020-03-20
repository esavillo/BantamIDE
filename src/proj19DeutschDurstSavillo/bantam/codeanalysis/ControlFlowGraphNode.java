/*
 * File: ControlFlowGraphNode.java
 * S19 CS461 Project 19
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 5/12/19
 * This file contains the representation of a control flow graph node.
 *
 * A Control Flow Graph Node is a single node within the Control Flow
 * Graph. A Control Flow Graph is a subset of the AST, representing the
 * direction of flow between different nodes by way of a directed,
 * potentially cyclical graph. Thus, each node keeps track of its
 * neighbors in a list called outgoing, which represent one way connections
 * from a node to its neighbors.
 */

package proj19DeutschDurstSavillo.bantam.codeanalysis;

import java.util.ArrayList;

import proj19DeutschDurstSavillo.bantam.ast.ASTNode;

/**
 * ControlFlowGraphNode is a node of a directed graph
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class ControlFlowGraphNode {
    // unique num is used to define a unique name, purely for display
    // and debugging purposes
    public static int uniquenum;
    private ArrayList<ControlFlowGraphNode> outgoing;
    private String name;
    /**
     * visit marks a node as "seen", useful for DFS and just simply 
     * pretty printing the graph nodes. Loops can be visited twice,
     * since a path through the loop and a path around the loop should
     * both be considered in dfs algorithms.
     */
    private int visited;
    private CFGType type;
    private boolean hasReturn;
    private boolean hasBreak;
    // must map back to AST Node for optimizations/manipulations of AST
    private ASTNode node;

    /**
     * Clearly not all inclusive, CFGType contains only the most interesting,
     * relevant, and necessary node types for constructing a Control Flow
     * Graph.
     */
    enum CFGType {
        LOOP, CONDITIONAL, RETURN, BREAK, START, END, ELSE;
    }

    ControlFlowGraphNode(String name, CFGType type, ASTNode node) {
        // set a unique name for everything but start type nodes
        if (!(type == CFGType.START)) {
            this.name = name + uniquenum;
            ++uniquenum;
        } else {
            this.name = name;
        }
        this.outgoing = new ArrayList<>();
        this.visited = 0;
        this.type = type;
        this.hasReturn = false;
        this.hasBreak = false;
        this.node = node;
    }

    /**
     * appends the given cfgn to the outgoing list and sets the hasReturn or
     * hasBreak flags if applicable.
     *
     * @param ControlFlowGraphNode
     */
    public void appendOutgoing(ControlFlowGraphNode cfgn) {
        this.outgoing.add(cfgn);
        if (cfgn.getType() == CFGType.RETURN)
            this.hasReturn = true;
        if (cfgn.getType() == CFGType.BREAK)
            this.hasBreak = true;
    } 

    // increments visited
    public void visit() {
        this.visited ++;
    }

     // decrements visited
     public void leave() {
        this.visited --;
    }

    // plethora of getter methods
    public boolean wasVisited() { 
        if (this.type == CFGType.LOOP)
            return this.visited == 2;
        return this.visited == 1;
     }
    public ArrayList<ControlFlowGraphNode> getOutgoing() { return this.outgoing; }
    public String getName() { return this.name; }
    public CFGType getType() { return this.type; }
    public ASTNode getNode() { return this.node; }
    public boolean itHasReturn() { return this.hasReturn; }
    public boolean itHasBreak() { return this.hasBreak; }
}
