/*
 * File: ControlFlowGraphVisitor.java
 * S19 CS461 Project 19
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 5/12/19
 * This file contains a visitor that generates a control flow graph
 */

package proj19DeutschDurstSavillo.bantam.codeanalysis;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.codeanalysis.ControlFlowGraphNode.CFGType;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.util.HashMap;

/**
 * ControlFlowGraphVisitor visits an AST and generates a control flow graph
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class ControlFlowGraphVisitor extends Visitor {
    public HashMap<String, ControlFlowGraphNode> methodCfgs;
    private String currentMethod;
    // all returns point to the same end
    private ControlFlowGraphNode currentCfgEnd;
    private ControlFlowGraphSymbolTable symbolTable;

    public ControlFlowGraphVisitor() {
        this.methodCfgs = new HashMap<>();
        this.currentMethod = "";
        this.symbolTable = new ControlFlowGraphSymbolTable();
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        // enter a new method
        this.currentMethod = node.getName();
        ControlFlowGraphNode start = new ControlFlowGraphNode(this.currentMethod, CFGType.START, node);
        this.methodCfgs.put(this.currentMethod, start);
        this.symbolTable.enterScope();
        this.symbolTable.add(start);
        ControlFlowGraphNode end = new ControlFlowGraphNode("END: " + this.currentMethod, CFGType.END, node); // doesn't matter what this node's ASTNode is
        this.currentCfgEnd = end;

        // node.getFormalList().accept(this);
        node.getStmtList().accept(this);

        // exit method
        this.currentMethod = "";
        // in case of no return add an end node
        if (this.symbolTable.peekTopNode().getType() != CFGType.RETURN)
            this.symbolTable.peekTopNode().appendOutgoing(end);
        this.symbolTable.exitScope();

        return null;
    }

    /**
     * Visit an if statement node
     *
     * An if statement has two cases to consider:
     *  1. If and Else
     *  2. Just an If
     *
     * If and Else:
     *  ConditionalHeader
     *    |         |
     *    V         V
     *   If        Else
     *   .          .
     *   .          .
     *   v          V
     *  code       code
     *   .          .
     *    .        .
     *     .      .        
     *       .   .
     *        Glue
     *
     * If and Else:
     *  ConditionalHeader
     *    |         |
     *    V         |
     *   If         |
     *   .          |
     *   .          |
     *   v          |
     *  code        |
     *   .          .
     *    .        .
     *     .      .        
     *       .   .
     *        Glue
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        ControlFlowGraphNode top = this.symbolTable.peekTopNode();
        ControlFlowGraphNode temp = new ControlFlowGraphNode("ConditionalHeader", CFGType.ELSE, node); // node that captures conditional

        // conncect top to temp, or the last node to the header
        top.appendOutgoing(temp);

        if (node.getElseStmt() != null) {
            // new nodes
            ControlFlowGraphNode cfgnIf = new ControlFlowGraphNode("If", CFGType.CONDITIONAL, node.getThenStmt());
            ControlFlowGraphNode cfgnElse = new ControlFlowGraphNode("Else", CFGType.CONDITIONAL, node.getElseStmt());
            ControlFlowGraphNode cfgnGlue = new ControlFlowGraphNode("Glue", CFGType.ELSE, null);

            // add if and else to temp
            temp.appendOutgoing(cfgnIf);
            temp.appendOutgoing(cfgnElse);

            // add glue to top scope
            this.symbolTable.add(cfgnGlue);

            // if stuff
            node.getPredExpr().accept(this);
            this.symbolTable.enterScope();
            this.symbolTable.add(cfgnIf);
            node.getThenStmt().accept(this);
            this.symbolTable.exitScope();

            // else stuff
            this.symbolTable.enterScope();
            this.symbolTable.add(cfgnElse);
            node.getElseStmt().accept(this);
            this.symbolTable.exitScope();
        } else {
            // new nodes
            ControlFlowGraphNode cfgnIf = new ControlFlowGraphNode("If", CFGType.CONDITIONAL, node.getThenStmt());
            ControlFlowGraphNode cfgnGlue = new ControlFlowGraphNode("Glue", CFGType.ELSE, null);

            // add if and glue to temp
            temp.appendOutgoing(cfgnGlue);
            temp.appendOutgoing(cfgnIf);

            // add glue to top scope
            this.symbolTable.add(cfgnGlue);

            // if stuff
            node.getPredExpr().accept(this);
            this.symbolTable.enterScope();
            this.symbolTable.add(cfgnIf);
            node.getThenStmt().accept(this);
            this.symbolTable.exitScope();
        }
        return null;
    }


    /**
     * Visit a while statement node
     *
     * A While will have two different paths, one for the true case and
     * one for the false case.
     * 
     * Basic structure:
     *     [ Loop  ]--- false ---> outside
     *      |    |
     *      |    |
     *      V    |
     *    inside |
     *      +----+
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        // get top value item from scope stack
        ControlFlowGraphNode temp = this.symbolTable.peekTopNode();

        // construct new while node
        // this points to first thing immediately outside scopestack
        // and first hing immediately inside its scope stack
        ControlFlowGraphNode cfgnWhile = new ControlFlowGraphNode("While", CFGType.LOOP, node);

        // always point temp to "the following node"
        temp.appendOutgoing(cfgnWhile);

        node.getPredExpr().accept(this);

        this.symbolTable.add(cfgnWhile);
        this.symbolTable.enterScope();
        this.symbolTable.addLoop(cfgnWhile);
        node.getBodyStmt().accept(this);
        this.symbolTable.exitScope();

        return null;
    }

    /**
     * Visit a for statement node
     *
     * A For will have two paths, one for the true case and one for the false case.
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
        // get top value item from scope stack
        ControlFlowGraphNode temp = this.symbolTable.peekTopNode();

        // construct new for node
        // this points to first thing immediately outside scopestack
        // and first hing immediately inside its scope stack
        ControlFlowGraphNode cfgnFor = new ControlFlowGraphNode("For", CFGType.LOOP, node);

        // always point temp to "the following node"
        temp.appendOutgoing(cfgnFor);

        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
        }
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }

        this.symbolTable.add(cfgnFor);
        this.symbolTable.enterScope();
        this.symbolTable.addLoop(cfgnFor);
        node.getBodyStmt().accept(this);
        this.symbolTable.exitScope();

        return null;
    }

     /**
     * Visit a return statement node
     *
     * Marks the end of control flow, points to the End.
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
        }
        // get top value item from scope stack
        ControlFlowGraphNode temp = this.symbolTable.peekTopNode();

        // construct new return node
        ControlFlowGraphNode cfgnReturn = new ControlFlowGraphNode("Return", CFGType.RETURN, node);

        temp.appendOutgoing(cfgnReturn);
        cfgnReturn.appendOutgoing(this.currentCfgEnd);

        this.symbolTable.tryJustExited(cfgnReturn);
        this.symbolTable.add(cfgnReturn);

        return null;
    }

    /**
     * Visit a break statement node
     *
     * Either jumps to the loop before the current loop, or if the current loop
     * is only a single scope beyond the main scope, jumps to the next statement
     * in the main scope.
     *
     * @param node the break statement node
     * @return result of the visit
     */
    public Object visit(BreakStmt node) {
        // get top value item from scope stack
        ControlFlowGraphNode temp = this.symbolTable.peekTopNode();

        // construct new break node
        ControlFlowGraphNode cfgnBreak = new ControlFlowGraphNode("Break", CFGType.BREAK, node);

        // get the loop which break should point back to
        ControlFlowGraphNode loop = this.symbolTable.peekTopLoopNode(cfgnBreak);

        temp.appendOutgoing(cfgnBreak);

        if (loop != null)
            cfgnBreak.appendOutgoing(loop);

        return null;
    }
}
