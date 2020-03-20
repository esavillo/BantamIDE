/*
 * File: MainMainVisitor.java
 * S19 CS341 Project 11
 * Names: Evan Savillo and Robert Durst
 * Date: 2/13/19
 * This file contains the MainMainVisitor which checks for a Main.main method.
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
 * Visitor class which is responsible for being given a parse tree (Program root), and
 * determining whether the given program contains a Main class containing a main method
 * that has void return type and no parameters.
 */
public class MainMainVisitor
        extends Visitor
{
    private ClassTreeNode classTree;
    private ErrorHandler  errorHandler;

    private boolean       hasMainClass;
    private boolean       hasMainMethod;
    private String        soughtClassName;
    private ClassTreeNode currentNode;

    public void mainMainCheck(ClassTreeNode root, Program program, ErrorHandler errorHandler)
    {
        this.classTree = root;
        this.errorHandler = errorHandler;

        this.resetFields();

        if (!this.hasMain(program)) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       "main method not found - void main () must be " +
                                               "declared in either Main class or parent");
        }
    }

    /**
     * Returns true if the given program contains a Main class with a main method in it
     * that has void return type and has no parameters, otherwise false.
     *
     * @param ast the root ast node
     * @return boolean whether or not a Main.main method was found
     */
    private boolean hasMain(Program ast)
    {
        // soughtClassName is initially "Main"
        this.currentNode = this.classTree.lookupClass(this.soughtClassName);

        if (this.currentNode == null) {
            return false;
        }

        this.currentNode.getASTNode().accept(this);

        /* Main class is potentially inheriting a valid main method */
        while (hasMainClass && !hasMainMethod) {
            /* So set the sought node to the Parent's name and try again */
            this.currentNode = this.currentNode.getParent();
            if (this.currentNode == null) {
                break;
            }

            this.currentNode.getASTNode().accept(this);
        }

        return hasMainMethod;
    }

    private void resetFields()
    {
        this.hasMainClass = false;
        this.hasMainMethod = false;
        this.soughtClassName = "Main";
        this.currentNode = null;
    }

    /**
     * For class node with the name 'Main', parse its methods.
     *
     * @param node the class node
     * @return null
     */
    @Override
    public Object visit(Class_ node)
    {
        this.hasMainClass = true;

        node.getMemberList().accept(this);

        return null;
    }

    /**
     * Don't bother visiting fields.
     */
    @Override
    public Object visit(Field node)
    {
        return null;
    }

    /**
     * Determines if name is main, return type is void, and
     * if parameter list is empty, i.e. ascertain (and assign) the truth value of
     * hasMainMethod.
     *
     * @param node the method node
     * @return null
     */
    @Override
    public Object visit(Method node)
    {
        boolean nameIsMain     = node.getName().equals("main");
        boolean returnIsVoid   = node.getReturnType().equals("void");
        boolean paramsAreEmpty = (node.getFormalList().getSize() == 0);

        // We cross check with || so we don't overwrite the correct truth value.
        this.hasMainMethod = this.hasMainMethod ||
                (nameIsMain && returnIsVoid && paramsAreEmpty);

        return null;
    }
}
