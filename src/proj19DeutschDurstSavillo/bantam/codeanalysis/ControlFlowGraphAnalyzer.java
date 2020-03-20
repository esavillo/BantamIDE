/*
 * File: ControlFlowGraph.java
 * S19 CS461 Project 19
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 5/14/19
 * This file contains the control flow graph and accompanying methods for
 * analyzing.
 */

package proj19DeutschDurstSavillo.bantam.codeanalysis;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.codeanalysis.ControlFlowGraphNode.CFGType;
import proj19DeutschDurstSavillo.bantam.parser.Parser;
import proj19DeutschDurstSavillo.bantam.semant.SemanticAnalyzer;
import proj19DeutschDurstSavillo.bantam.util.*;
import java.util.ArrayList;

public class ControlFlowGraphAnalyzer {   
    private ControlFlowGraphNode start;
    private ArrayList<ArrayList<ControlFlowGraphNode>> paths;
    
    public ControlFlowGraphAnalyzer(ControlFlowGraphNode cfgn) {
        this.start = cfgn;
    }

    /**
     * Based on the following website, determines the complexity of a given function:
     * 
     * https://www.guru99.com/cyclomatic-complexity.html
     * 
     * Simple: <10 paths
     * Moderately Complex: 10-20 paths
     * Complex: 21 - 30 paths
     * Seriously Complex 31 - 40 paths
     * Shivers of Revulsion: >40 paths
     * 
     * @return Complexity Analysis
     */
    private String rateComplexity(int numpaths) {
        if (numpaths > 0 && numpaths < 10) {
            return "Simple";
        } else if (numpaths >= 10 && numpaths < 20) {
            return "Moderately Complex";
        } else if (numpaths >= 20 && numpaths < 30) {
            return "Complex";
        } else if (numpaths >= 30 && numpaths < 40) {
            return "Seriously Complex";
        } else {
            return "Shivers of Revulsion";
        }
    }

    /**
     * Depth first search to determine the number of paths through the code. This is the
     * public interface which inserts the start node and an empty path into the recursive
     * algorithm below.
     * 
     * @return results for output
     */
    public String getCyclomaticComplexityAnalysisResults() {
        this.paths = new ArrayList<>(); // clear the list of paths

        ArrayList<ControlFlowGraphNode> emptypath = new ArrayList<>();
        emptypath.add(start);
        dfsPathFind(start, emptypath);

        // pretty print for console output in terminal and ide
        return String.format("Name: %-20s | Paths: %-5s | Complexity: %-15s\n", start.getName(), paths.size(), rateComplexity(paths.size()));
    }

    /**
     * DFS used to find paths to determine cyclomatic complexity.
     * 
     * @param cfgn
     * @param path
     */
    private void dfsPathFind(ControlFlowGraphNode cfgn,  ArrayList<ControlFlowGraphNode> path) {
        cfgn.visit();

        if (cfgn.getType() == CFGType.RETURN || cfgn.getType() == CFGType.END) {
            // the search ends here
            paths.add(path);
            return;
        } else {
            for (int i = 0; i < cfgn.getOutgoing().size(); i ++)  {
                ControlFlowGraphNode node = cfgn.getOutgoing().get(i);
                if (!node.wasVisited()) {
                    ArrayList<ControlFlowGraphNode> copiedPath = (ArrayList<ControlFlowGraphNode>) path.clone();
                    copiedPath.add(node);
                    dfsPathFind(node, copiedPath);
                    node.leave(); // unvisit so others can visit
                }
            }
        }
    }

    /**
     * public facing interface for exhaustive display
     */
    public void exhaustiveDisplay() {
        this.exhaustiveDisplay(start, 0);
    }

    /**
     * Exhaustive display visits each node, printing out each node with
     * a list of its neighbors. It utilizes an indent parameter to
     * pretty print-ish the various nodes. Each node will be formatted
     * and printed as follows:
     * 
     *      NAME [Neightbor1] [Neighbor2]
     * 
     * This is very useful for debugging.
     *
     * @param cfgn
     * @param n - indent value
     *
     */
    private void exhaustiveDisplay(ControlFlowGraphNode cfgn, int n) {
        // mark this node as visited
        cfgn.visit();
            
        // append an indent to the front
        String disp = "";
        for (int i = 0; i < n; i ++)
            disp += " ";
            
        disp = disp + cfgn.getName();

        // append all neighbors to name
        for (int i = 0; i < cfgn.getOutgoing().size(); i ++) 
            disp +=  " [" + cfgn.getOutgoing().get(i).getName() + "] ";
        System.out.println(disp);

        // traverse all neighbors
        for (int i = 0; i < cfgn.getOutgoing().size(); i ++) 
            if (!cfgn.getOutgoing().get(i).wasVisited()) {
                ControlFlowGraphNode x = cfgn.getOutgoing().get(i);
                exhaustiveDisplay(x, n + 2);
            }
    }

    public static void main(String[] args) {
        ErrorHandler      errorHandler      = new ErrorHandler();
        Parser            parser            = new Parser(errorHandler);
        SemanticAnalyzer  analyzer          = new SemanticAnalyzer(errorHandler);

        String filename = args[0];

        System.out.println("Compiling file: " + filename + "\n");

        // parse and analyze
        try {
            Program program = parser.parse(filename);
            analyzer.analyze(program);
            ControlFlowGraphVisitor cfgv = new ControlFlowGraphVisitor();
            cfgv.visit(program);

            System.out.println("Cyclomatic Complexity Analysis Results Per Method:");
            System.out.println("------------------------------------------------------------");
            for (String name: cfgv.methodCfgs.keySet()){
                ControlFlowGraphAnalyzer cfgna = new ControlFlowGraphAnalyzer(cfgv.methodCfgs.get(name));
                System.out.println(cfgna.getCyclomaticComplexityAnalysisResults());
            }
        } catch (CompilationException e) {
            System.out.println("Parsing error encountered:\n");
            System.out.println(e.getMessage());
        }

        // check for errors
        if (errorHandler.errorsFound()) {
            errorHandler.getErrorList().forEach(error -> System.out.println(error.toString()));
            System.out.println(String.format("\n%d errors found", errorHandler.getErrorList().size()));
        } else {
            System.out.println("\nCompilation successful");
        }

        System.out.println("-----------------------------------------------");

        //clear errors to parse next file
        errorHandler.clear();
    }
}