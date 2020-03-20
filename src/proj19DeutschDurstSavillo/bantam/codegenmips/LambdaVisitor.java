/*
 * File: LambdaVisitor.java
 * F18 CS361 Project 18
 * Names: Robert Durst, Evan Savillo
 * Date: 5/4/19
 * This file contains the LambdaVisitor which maps lambdaExpr nodes
 * to unique method names
 */
package proj19DeutschDurstSavillo.bantam.codegenmips;

import proj19DeutschDurstSavillo.bantam.ast.Class_;
import proj19DeutschDurstSavillo.bantam.ast.LambdaExpr;
import proj19DeutschDurstSavillo.bantam.ast.Method;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.bantam.util.ClassTreeNode;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.util.HashMap;
import java.util.Map;

/**
 * LambdaVisitor visits an AST and generates code for lambdas
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class LambdaVisitor extends Visitor {

    /* Map of lambda expression nodes to unique identifiers */
    private Map<LambdaExpr, String> lambdaNameMap;
    /* Map of lambda names to number of local variables */
    private Map<String, Integer> localVarsMap;
    private int lambdaCount;
    private UserDefinedMethodVisitor userDefinedMethodVisitor;
    private ClassTreeNode root;

    /**
     * Returns a map of each lambda node to a unique identifier
     * @param program the root of the AST
     * @return map of lambdaExpr to unique identifier
     */
    public Map<LambdaExpr, String> getLambdaNameMap(Program program) {
        this.lambdaNameMap = new HashMap<>();

        // traverse tree
        program.accept(this);

        return this.lambdaNameMap;
    }

    /**
     * Visits an AST and generates code for lambda expressions
     * Also populates a map of lambda nodes to unique identifiers
     *
     * @param userDefinedMethodVisitor code generator visitor
     * @param root the root of the classtreenode hierarchy
     * @param program the root of the AST
     */
    public void generateLambdaMethods(UserDefinedMethodVisitor userDefinedMethodVisitor,
                                      Program program, ClassTreeNode root) {
        this.lambdaCount = 0;
        this.userDefinedMethodVisitor = userDefinedMethodVisitor;
        this.root = root;
        // create preliminary local vars map
        this.localVarsMap = new HashMap<>();
        this.lambdaNameMap.forEach((lambda, name) -> {
            localVarsMap.put(name, 0);
        });
        userDefinedMethodVisitor.setNumLocalVarsMap(localVarsMap);

        // traverse tree
        program.accept(this);
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node)
    {
        if (userDefinedMethodVisitor != null) {
            // get current class
            ClassTreeNode currentClass = this.root.lookupClass(node.getName());
            // set the current class in the code generation visitor
            userDefinedMethodVisitor.setCurrentClass(currentClass);
        }
        node.getMemberList().accept(this);

        return null;
    }

    /**
     * Visit a lambda expression node
     *
     * @param node the lambda expression node
     * @return null
     */
    @Override
    public Object visit(LambdaExpr node) {
        if (this.userDefinedMethodVisitor == null) {
            String lambdaIdentifier = "Lambda" + this.lambdaCount;
            lambdaCount++;
            lambdaNameMap.put(node, lambdaIdentifier);
            node.getStmtList().accept(this);
        }
        else {
            node.getStmtList().accept(this);

            String lambdaName = lambdaNameMap.get(node);
            // create a method out of this lambda
            Method lambdaMethod = new Method(node.getLineNum(), node.getReturnType(),
                    lambdaNameMap.get(node), node.getFormalList(), node.getStmtList());

            // get the number of local variables in this lambda
            NumLocalVarsVisitor numLocalVarsVisitor = new NumLocalVarsVisitor();
            Map<String, Integer> lambdaVarsMap = new HashMap<>();
            numLocalVarsVisitor.setLocalVarsMap(lambdaVarsMap);
            numLocalVarsVisitor.setCurrentClass("");
            numLocalVarsVisitor.visit(lambdaMethod);
            int numLocalVars = lambdaVarsMap.get("." + lambdaName);

            // set the number of local variables in the map used by the code generator visitor
            this.localVarsMap.put(lambdaName, numLocalVars);

            // generate code for the method
            lambdaMethod.accept(this.userDefinedMethodVisitor);
        }

        return null;
    }
}
