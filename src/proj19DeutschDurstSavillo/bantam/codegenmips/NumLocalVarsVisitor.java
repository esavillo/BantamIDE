/*
 * File: NumLocalVarsVisitor.java
 * F18 CS361 Project 11
 * Names: Robert Durst, Evan Savillo
 * Date: 2/12/19
 * This file contains the NumLocalVarsVisitor which generates a mapping of
 * method names to number of local variables.
 */
package proj19DeutschDurstSavillo.bantam.codegenmips;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;
import proj19DeutschDurstSavillo.interfaces.PostParseActor;

import java.util.HashMap;
import java.util.Map;

public class NumLocalVarsVisitor extends Visitor implements PostParseActor {

  private Map<String, Integer> localVars;
  private String currentClass;
  private String currentMethod;
  private int localsCount;

  /**
   * Setter for the local vars map
   * @param localVarsMap mapping of method names to number of local vars
   */
  public void setLocalVarsMap(Map<String, Integer> localVarsMap) {
    this.localVars = localVarsMap;
  }

  /**
   * Setter for the class name
   * @param className class name String
   */
  public void setCurrentClass(String className) {
    this.currentClass = className;
  }

  /**
   * Traverse a parse tree, generating a hashmap where each class's method in
   * the tree is a key with the number of its local variables and parameters as
   * its key.
   *
   * @param ast the root ast node
   * @return Map<String, Integer> mapping of method names to number of local
   *         variables
   */
  public Map<String, Integer> getNumLocalVars(Program ast) {
    // set initial state
    this.localVars = new HashMap<>();
    this.currentClass = "";
    this.currentMethod = "";
    this.localsCount = 0;

    // traverse tree
    ast.accept(this);

    return this.localVars;
  }

  // an implementation of the PostParseActor interface
  @Override
  public Object act(ASTNode node, Map<String, Object> knowledge) {
    StringBuilder retValue = new StringBuilder();
    retValue.append("Calculating local variables for each class...\n\n");
    for (Map.Entry<String, Integer> entry :
         this.getNumLocalVars((Program)node).entrySet()) {
      retValue.append(entry.getKey() + ": " + entry.getValue() + '\n');
    }

    return retValue.toString();
  }

  /**
   * Parse a class, adding a method to the map in the case that the source code
   * for a previous class finished, and then parse the members of the class.
   *
   * @param node the class node
   * @return null
   */
  @Override
  public Object visit(Class_ node) {

    // update state information
    this.currentClass = node.getName();

    // parse the class's members
    node.getMemberList().accept(this);

    return null;
  }

  /**
   * Parse a method, adding a method to the map in the case that the source code
   * for a previous method finished, and then parse the parameters and
   * statements of the method.
   *
   * @param node the method node
   * @return null
   */
  @Override
  public Object visit(Method node) {
    // update state information
    this.currentMethod = node.getName();
    this.localsCount = 0;

    // parse the parameters and the statements of the method
    this.localsCount += node.getFormalList().getSize();
    node.getStmtList().accept(this);

    this.localVars.put(this.currentClass + "." + this.currentMethod,
                       this.localsCount);

    return null;
  }

  /**
   * Parse a declaration statement, incrementing the variable count.
   *
   * @param node the declaration statement node
   * @return null
   */
  @Override
  public Object visit(DeclStmt node) {
    // update state information
    this.localsCount++;

    return null;
  }

  // ------ Optimizing -------- //
  /*
   * In general, the optimization for NumLocalVarsVisitor is the ignoring of
   * expressions since local variables are only initialized by DeclStmt nodes.
   */

  /**
   * Ignore the content of all fields.
   *
   * @param node the field node
   * @return null
   */
  @Override
  public Object visit(Field node) {
    return null;
  }

  /**
   * By ignoring the rest of the tree following an expression statement, we can
   * ensure no extraneous expression parsing will occur without overriding any
   * other expression related visitor methods.
   *
   * @param node the expression statement node
   * @return null
   */
  @Override
  public Object visit(ExprStmt node) {
    return null;
  }

  /**
   * Ignore the PredExpr from the if statement since no pertinent information
   * results from expressions.
   *
   * @param node the if statement node
   * @return null
   */
  @Override
  public Object visit(IfStmt node) {
    node.getThenStmt().accept(this);
    if (node.getElseStmt() != null) {
      node.getElseStmt().accept(this);
    }
    return null;
  }

  /**
   * Ignore the PredExpr from the while statement since no pertinent information
   * results from expressions.
   *
   * @param node the while statement node
   * @return null
   */
  @Override
  public Object visit(WhileStmt node) {
    node.getBodyStmt().accept(this);
    return null;
  }

  /**
   * Ignore the PredExpr from the for statement since no pertinent information
   * results from expressions.
   *
   * @param node the for statement node
   * @return null
   */
  @Override
  public Object visit(ForStmt node) {
    node.getBodyStmt().accept(this);
    return null;
  }

  /**
   * By ignoring the rest of the tree following an expression list, we can
   * ensure no extraneous expression parsing will occur without overriding any
   * other expression related visitor methods.
   *
   * @param node the expression list node
   * @return null
   */
  @Override
  public Object visit(ExprList node) {
    return null;
  }
}
