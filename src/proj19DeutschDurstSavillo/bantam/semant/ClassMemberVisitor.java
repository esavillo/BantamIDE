/*
 * File: ClassMemberVisitor.java
 * S19 CS461 Project 12
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 2/25/19
 * This file contains the ClassMemberVisitor that adds fields and methods
 * to the symbol table for each class
 */

package proj19DeutschDurstSavillo.bantam.semant;

import proj19DeutschDurstSavillo.bantam.ast.Class_;
import proj19DeutschDurstSavillo.bantam.ast.Field;
import proj19DeutschDurstSavillo.bantam.ast.Method;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.bantam.util.ClassTreeNode;
import proj19DeutschDurstSavillo.bantam.util.Error;
import proj19DeutschDurstSavillo.bantam.util.ErrorHandler;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;


/**
 * Visitor class which is responsible for being given a parse tree (Program astRoot) and
 * class inheritance tree (ClassTreeNode ctnRoot) and adding methods and fields to the
 * symbol tables for each class
 */
public class ClassMemberVisitor extends Visitor
{
    private ClassTreeNode currentClass;
    private ErrorHandler errorHandler;

    /**
     * Add fields and methods to symbol tables
     * @param astRoot the root of the AST
     * @param ctnRoot the root of the ClassTreeNode inheritance tree
     * @param errorHandler the errorHandler to register errors
     */
    public void addMembers(Program astRoot, ClassTreeNode ctnRoot, ErrorHandler errorHandler) {
        this.currentClass = ctnRoot;
        this.errorHandler = errorHandler;
        super.visit(astRoot);
    }

    /**
     * Set current class to class being parsed and initialize symbol tables
     *
     * @param node the class node
     * @return null
     */
    @Override
    public Object visit(Class_ node)
    {
        currentClass = currentClass.lookupClass(node.getName());
        node.getMemberList().accept(this);

        return null;
    }

    /**
     * Check that fields are declared properly and add them to symbol table
     *
     * @param node the field node
     * @return null
     */
    @Override
    public Object visit(Field node)
    {
        if (currentClass.getVarSymbolTable().peek(node.getName()) != null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, currentClass.getASTNode().getFilename(),
                    node.getLineNum(), "Duplicate field name: " + node.getName());
            return null;
        }

        if (SemanticAnalyzer.reservedIdentifiers.contains(node.getName())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, currentClass.getASTNode().getFilename(),
                    node.getLineNum(),
                    "The field name " + node.getName() + " is a reserved word");
        }

        currentClass.getVarSymbolTable().add(node.getName(), node.getType());
        return null;
    }

    /**
     * Check that methods are declared properly and add them to symbol table
     *
     * @param node the method node
     * @return null
     */
    @Override
    public Object visit(Method node)
    {
        if (currentClass.getMethodSymbolTable().peek(node.getName()) != null) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, currentClass.getASTNode().getFilename(),
                    node.getLineNum(), "Duplicate method name: " + node.getName());
            return null;
        }

        if (SemanticAnalyzer.reservedIdentifiers.contains(node.getName())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR, currentClass.getASTNode().getFilename(),
                    node.getLineNum(),
                    "The method name " + node.getName() + " is a reserved word");
        }

        currentClass.getMethodSymbolTable().add(node.getName(), node.getReturnType());

        // add parameter list node to method symbol table for checking argument types later
        this.currentClass.getMethodSymbolTable().add(node.getName() + "()", node.getFormalList());

        return null;
    }
}
