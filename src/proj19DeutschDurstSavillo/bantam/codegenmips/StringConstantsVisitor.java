/*
 * File: StringConstantsVisitor.java
 * F18 CS361 Project 11
 * Names: Robert Durst, Evan Savillo
 * Date: 2/12/19
 * This file contains the StringConstantsVisitor which generates a mapping of
 * string constants to unique names.
 */
package proj19DeutschDurstSavillo.bantam.codegenmips;

import proj19DeutschDurstSavillo.bantam.ast.ASTNode;
import proj19DeutschDurstSavillo.bantam.ast.ConstStringExpr;
import proj19DeutschDurstSavillo.bantam.ast.Method;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;
import proj19DeutschDurstSavillo.interfaces.PostParseActor;

import java.util.HashMap;
import java.util.Map;

public class StringConstantsVisitor extends Visitor implements PostParseActor {

  private HashMap<String, String> map;
  private int uniqueInt;

  /**
   * Traverse a parse tree, generating a hashmap where each string in the tree
   * is a key with a unique value.
   *
   * @param ast the root ast node
   * @return Map<String, String> unique mapping of string constants to
   *         StringConst_#
   */
  public Map<String, String> getStringConstants(Program ast) {
    // set initial state
    this.map = new HashMap<>();
    this.uniqueInt = 0;

    // traverse tree
    ast.accept(this);

    return this.map;
  }

  // an implementation of the PostParseActor interface
  @Override
  public Object act(ASTNode node, Map<String, Object> knowledge) {
    StringBuilder retValue = new StringBuilder();
    retValue.append("Finding all unique string constants...\n\n");
    for (Map.Entry<String, String> entry :
         this.getStringConstants((Program)node).entrySet()) {
      retValue.append(entry.getKey() + ": " + entry.getValue() + '\n');
    }

    return retValue.toString();
  }

  /**
   * Parse a constant string expression, adding it to the map with an unique
   * name if it has not been seen before.
   *
   * @param node the constant string expression node
   * @return null
   */
  @Override
  public Object visit(ConstStringExpr node) {
    String currentString = node.getConstant();
    if (!this.map.containsKey(currentString)) {
      this.map.put(currentString, "StringConst_" + uniqueInt);
      this.uniqueInt++;
    }
    return null;
  }

  // ------ Optimizing -------- //
  /*
   * In general, the optimization for StringConstantsVisitor is the ignoring of
   * fields since string constants won't be in the fields and then the ignoring
   * of some getIndex methods.
   */

  /**
   * Parse a method's statements but ignore the parameters (formal list).
   *
   * @param node the method node
   * @return null
   */
  @Override
  public Object visit(Method node) {
    node.getStmtList().accept(this);
    return null;
  }
}
