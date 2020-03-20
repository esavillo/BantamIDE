/*
 * File: ConstantPropVisitor.java
 * S19 CS461 Project 17
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 5/12/19
 * This file contains a visitor that propagates constants
 */

package proj19DeutschDurstSavillo.bantam.astopt;

import proj19DeutschDurstSavillo.bantam.ast.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ConstantPropVisitor visits an AST node and propagates constant variables
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class ConstantPropVisitor extends OptimizerVisitor {

    // mapping of variable names to constant values
    private Map<String, String> varMap = new HashMap<>();

    private boolean propagate;
    private boolean modified;

    /**
     * Perform constant folding on given AST
     * @param root the AST to optimize
     * @return the AST with a round of constant folding applied
     */
    public Program constantPropAST(Program root) {
        this.propagate = true;
        this.modified = false;
        return (Program) root.accept(this);
    }

    /**
     * Returns whether the AST has been modified by the propagation visitor
     * @return true if the AST is modified, false if not
     */
    public boolean madeModifications() {
        return this.modified;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        FormalList formalList = (FormalList) node.getFormalList().accept(this);
        // enter basic block
        this.varMap.clear();
        StmtList stmtList = (StmtList) node.getStmtList().accept(this);
        this.varMap.clear();
        return new Method(node.getLineNum(), node.getReturnType(), node.getName(), formalList, stmtList);
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        Expr init = (Expr) node.getInit().accept(this);
        if (init instanceof ConstExpr) {
            ConstExpr constExpr = (ConstExpr) init;
            varMap.put(node.getName(), constExpr.getConstant());
        }
        return new DeclStmt(node.getLineNum(), node.getName(), init);
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        Expr pred = (Expr) node.getPredExpr().accept(this);
        // reset to enter new basic block
        this.varMap.clear();
        Stmt then = (Stmt) node.getThenStmt().accept(this);
        // enter other basic block
        this.varMap.clear();
        Stmt elseStmt = null;
        if (node.getElseStmt() != null) {
            elseStmt = (Stmt) node.getElseStmt().accept(this);
            this.varMap.clear();
        }
        return new IfStmt(node.getLineNum(), pred, then, elseStmt);
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        this.propagate = false;
        Expr pred = (Expr) node.getPredExpr().accept(this);
        this.propagate = true;
        // reset to enter new basic block
        this.varMap.clear();
        Stmt body = (Stmt) node.getBodyStmt().accept(this);
        this.varMap.clear();

        return new WhileStmt(node.getLineNum(), pred, body);
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
        this.propagate = false;
        Expr init = null;
        if (node.getInitExpr() != null) {
            init = (Expr) node.getInitExpr().accept(this);
        }
        Expr pred = null;
        if (node.getPredExpr() != null) {
            pred = (Expr) node.getPredExpr().accept(this);
        }
        Expr update = null;
        if (node.getUpdateExpr() != null) {
            update = (Expr) node.getUpdateExpr().accept(this);
        }
        this.propagate = true;

        // enter new basic block
        this.varMap.clear();
        Stmt body = (Stmt) node.getBodyStmt().accept(this);
        this.varMap.clear();

        return new ForStmt(node.getLineNum(), init, pred, update, body);
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        Expr expr = (Expr) node.getExpr().accept(this);
        if (expr instanceof ConstExpr) {
            ConstExpr constExpr = (ConstExpr) expr;
            varMap.put(node.getName(), constExpr.getConstant());
        }
        else {
            varMap.remove(node.getName());
        }

        AssignExpr assignExpr = new AssignExpr(node.getLineNum(), node.getRefName(), node.getName(), expr);
        assignExpr.setExprType(node.getExprType());
        return assignExpr;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        this.propagate = false;
        Expr expr = (Expr) node.getExpr().accept(this);
        this.propagate = true;

        UnaryIncrExpr unaryIncrExpr = new UnaryIncrExpr(node.getLineNum(), expr, node.isPostfix());
        unaryIncrExpr.setExprType(node.getExprType());
        return unaryIncrExpr;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return result of the visit
     */
    public Object visit(UnaryDecrExpr node) {
        this.propagate = false;
        Expr expr = (Expr) node.getExpr().accept(this);
        this.propagate = true;

        UnaryDecrExpr unaryDecrExpr = new UnaryDecrExpr(node.getLineNum(), expr, node.isPostfix());
        unaryDecrExpr.setExprType(node.getExprType());
        return unaryDecrExpr;
    }

    /**
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return result of the visit
     */
    public Object visit(VarExpr node) {
        Expr ref = null;
        if (node.getRef() != null) {
            ref = (Expr) node.getRef().accept(this);
        }
        else if (this.propagate && varMap.containsKey(node.getName())) {
            this.modified = true;
            String val = varMap.get(node.getName());
            switch(node.getExprType()) {
                case "int":
                    return new ConstIntExpr(node.getLineNum(), val);
                case "boolean":
                    return new ConstBooleanExpr(node.getLineNum(), val);
                case "String":
                    return new ConstStringExpr(node.getLineNum(), val);
            }
        }

        VarExpr varExpr = new VarExpr(node.getLineNum(), ref, node.getName());
        varExpr.setExprType(node.getExprType());
        return varExpr;
    }
}