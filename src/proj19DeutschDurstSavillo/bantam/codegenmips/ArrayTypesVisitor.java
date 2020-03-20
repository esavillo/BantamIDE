/*
 * File: ArrayTypesVisitor.java
 * F18 CS361 Project 18
 * Names: Robert Durst, Evan Savillo
 * Date: 5/4/19
 * This file contains the ArrayTypesVisitor which returns a list of
 * the array types appearing in the program
 */
package proj19DeutschDurstSavillo.bantam.codegenmips;

import proj19DeutschDurstSavillo.bantam.ast.CastExpr;
import proj19DeutschDurstSavillo.bantam.ast.InstanceofExpr;
import proj19DeutschDurstSavillo.bantam.ast.NewArrayExpr;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

/**
 * ArrayTypesVisitor visits an AST and generates a list of the array types which appear
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class ArrayTypesVisitor extends Visitor {

    private List<String> arrayTypes;

    /**
     * Traverse a parse tree, generating a list of array types
     *
     * @param ast the root ast node
     * @return List<String> of array types in form type[]
     */
    public List<String> getArrayTypes(Program ast) {
        this.arrayTypes = new ArrayList<>();

        // traverse tree
        ast.accept(this);

        return this.arrayTypes;
    }

    /**
     * Visit a NewArrayExpr and add its type to the list of array types.
     *
     * @param node the NewArrayExpr node
     * @return null
     */
    @Override
    public Object visit(NewArrayExpr node) {
        String arrayType = node.getType();
        arrayTypes.add(arrayType + "[]");

        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return null
     */
    public Object visit(InstanceofExpr node) {
        String type = node.getType();
        if (type.endsWith("[]") && !arrayTypes.contains(type)) {
            arrayTypes.add(node.getType());
        }
        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return null
     */
    public Object visit(CastExpr node) {
        String type = node.getType();
        if (type.endsWith("[]") && !arrayTypes.contains(type)) {
            arrayTypes.add(node.getType());
        }
        return null;
    }
}
