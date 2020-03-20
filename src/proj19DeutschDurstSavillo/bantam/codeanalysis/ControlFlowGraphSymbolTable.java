/*
 * File: CFGSymbolTable.java
 * S19 CS461 Project 19
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 5/12/19
 * This file contains the symbol table for the control flow graph. While semantically
 * similar to the Symbol Table in the util directory, its purpose and implementation
 * were different enough to warrant not inheritting from that class.
 */

package proj19DeutschDurstSavillo.bantam.codeanalysis;

import proj19DeutschDurstSavillo.bantam.codeanalysis.ControlFlowGraphNode.CFGType;
import java.util.Stack;
import javafx.util.Pair; 

/**
 * CFGSymbolTable is a data structure for determining which nodes to point to
 * which other nodes in the Control Flow Graph. It contains some logical oddities
 * of which are explained in the comments below.
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class ControlFlowGraphSymbolTable {
    /**
     * symbolTable is a very similar data structure to the symbol table in the
     * util directory in that each scope is a stack on the stack of scopes. However,
     * unlike in the symbol table, here we often only really care about certain
     * nodes such as the "top node."
     */
    private Stack<Stack<ControlFlowGraphNode>> symbolTable;
    private int level;
    /**
     * loopStack is used to determine the loop level for cfg's that need to be
     * aware of recursive loops. An example of this is a break statement that
     * must exit its own loop and enter the scope of the next loop up or the
     * first statement after the current loop's scope ends.
     */
    private Stack<Pair<ControlFlowGraphNode, Integer>> loopStack;
    /**
     * breakPromises is a sort of promise based data structure that allows us
     * to deal with the edge case where a break must point to the next
     * statement immediately following the exiting of its loop's scope. This
     * is only needed whenever a break exits a loop and goes back to the main
     * scope.
     *
     * For example, consider:
     *
     *    while {           // A
     *      break;          // B
     *    }
     *
     *    ... more code ... // C
     *
     * Here we will want the following graph:
     *
     * A --> B --> C
     */
    private Stack<ControlFlowGraphNode> breakPromises;
    // flag for letting us know that we just exited a scope
    private boolean justExited;

    ControlFlowGraphSymbolTable() {
        this.symbolTable = new Stack<>();
        this.loopStack = new Stack<>();
        this.breakPromises = new Stack<>();
        this.level = 0;
        this.justExited = false;
    }

    /**
     * Increments the level counter and pushes an empty stack to the top of the stack.
     */
    public void enterScope() {
        this.symbolTable.push(new Stack<>());
        ++this.level;
    }

    /**
     * Implements the various logic necessary to maintain the internal data structures
     * on the exiting of a scope level
     */
    public void exitScope() {
        Stack<ControlFlowGraphNode> topLevel = this.symbolTable.pop();

        /**
         * When exiting the scope of a loop block, we want to direct the last node to point
         * back to the loop node, thus creating a loop.
         * 
         * For example, consider:
         *  
         *      while {      // A
         *          while {} // B
         *          while {} // C
         *      }
         * 
         * We want to end up with a graph like this:
         * 
         *  A --> B --> C
         *  ^           |
         *  |           |
         *  -------------
         * 
         * However, there is an edge case if there was a break or return statement within the scope.
         * 
         * Consider:
         * 
         *      while {      // A
         *          while {} // B
         *          return;  // C
         *      }
         * 
         * For this we want to end up with a graph like:
         * 
         *  A --> B --> C
         */
        if (level > 1 &&  this.symbolTable.peek().size() != 0 && topLevel.size() != 0) {
            ControlFlowGraphNode temp = this.symbolTable.peek().peek();
            if (temp != null && !topLevel.peek().itHasReturn() && !topLevel.peek().itHasBreak())
                topLevel.peek().appendOutgoing(temp);
            
        }

        // since the loopStack is not a stack of stacks, we must manually remove the corresponding
        // scope level
        while(loopStack.size() > 0 && loopStack.peek().getValue() == this.level)
            loopStack.pop();

        --this.level;
        justExited = true;
    }

    /**
     * tryJustExited scope checks to see if a scope was just exited and if so, appends the inputted
     * cfgn to the outgoing list for each break in the promises stack.
     * 
     * @param ControlFlowGraphNode 
     */
    public void tryJustExited(ControlFlowGraphNode cfgn) {
        if (this.justExited) {
            while(breakPromises.size() >  0) {
                ControlFlowGraphNode temp = breakPromises.pop();
                temp.appendOutgoing(cfgn);
            }

            this.justExited = false;
        }
    }

    /**
     * Adds a symbol (ControlFlowGraphNode) to the symbol table.
     *
     * @param ControlFlowGraphNode
     */
    public void add(ControlFlowGraphNode cfgn) {
        this.tryJustExited(cfgn);
        this.symbolTable.peek().push(cfgn);
    }

    /**
     * Adds a ControlFlowGraphNode to the loop stack Logically, it may seem that addLoop and add could
     * be combined into a single method. I think so too. However, the two need to be added to different
     * scope levels, and so to allow for this more easily, we decided to keep these two methods separate. 
     * 
     * @param ControlFlowGraphNode
     */
    public void addLoop(ControlFlowGraphNode cfgn) {
        if (cfgn.getType() == CFGType.LOOP) {
            if (loopStack.size() > 1 && loopStack.peek().getValue() == this.level)
                loopStack.pop();
            loopStack.push(new Pair<ControlFlowGraphNode, Integer>(cfgn, this.level));
        }
    }

    /**
     * peekTopNode returns a reference to the second to top cfgn node on the 
     * stack of stacks.
     * 
     * Unlike a normal symbol table, we want to access the (n-1) level nodes.
     * 
     * Consider the following:
     * 
     *      void foo() { // A
     *          while {} // B
     *          return   // C
     *      }
     * 
     * For this we want a graph like this:
     * 
     *  A --> B --> C
     * 
     * If we were to peek normally, we would end up with:
     * 
     *  A --> B
     *  |
     *  V
     *  C
     * 
     *  @return ControlFlowGraphNode
     */
    public ControlFlowGraphNode peekTopNode() {
        if (this.symbolTable.peek().size() == 0 && this.level > 1) {
            return this.symbolTable.elementAt(this.symbolTable.size() - 2).peek();
        } else if (level > 0) {
            return this.symbolTable.peek().peek();
        } else {
            throw new RuntimeException("No node to peek at."); // should never get here
        }
    }

    /**
     * Only utilized by break statements for now. This attempts to graph the first loop
     * in the scope above the current loop. If there is only one loop on the loop stack,
     * meaning that the break statement's loop is only one scope above the "main" scope,
     * then we return nothing and add this to the break promises list.
     *
     * @param ControlFlowGraphNode
     * @return ControlFlowGraphNode
     */
    public ControlFlowGraphNode peekTopLoopNode(ControlFlowGraphNode cfgn) {
        if (loopStack.size() > 1)
            return this.loopStack.elementAt(loopStack.size() - 2).getKey();
        else {
            breakPromises.push(cfgn);
            return null;
        }
    }

    /**
     * peekTopLevel returns a reference to the top level (stack) in the stack.
     * 
     * @return Stack of ControlFlowGraphNodes
     */
    public Stack<ControlFlowGraphNode> peekTopLevel() {
        return this.symbolTable.peek();
    }
}
