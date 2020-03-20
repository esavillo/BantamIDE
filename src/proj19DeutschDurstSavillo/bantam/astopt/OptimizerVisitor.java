/*
 * File: OptimizerVisitor.java
 * S19 CS461 Project 19
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 5/16/19
 * This file contains a visitor that generates a new AST from a given AST
 */

package proj19DeutschDurstSavillo.bantam.astopt;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.util.Iterator;

/**
 * OptimizerVisitor generates a new AST from a given AST
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class OptimizerVisitor extends Visitor {

    /**
     * Visit a program node
     *
     * @param node the program node
     * @return result of the visit
     */
    public Object visit(Program node) {
        ClassList classList = (ClassList) node.getClassList().accept(this);
        return new Program(node.getLineNum(), classList);
    }

    /**
     * Visit a list node of classes
     *
     * @param node the class list node
     * @return result of the visit
     */
    public Object visit(ClassList node) {
        ClassList classList = new ClassList(node.getLineNum());
        for (ASTNode aNode : node)
            classList.addElement((ASTNode) aNode.accept(this));

        return classList;
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return result of the visit
     */
    public Object visit(Class_ node) {
        MemberList memberList = (MemberList) node.getMemberList().accept(this);
        return new Class_(node.getLineNum(), node.getFilename(), node.getName(), node.getParent(), memberList);
    }

    /**
     * Visit a list node of members
     *
     * @param node the member list node
     * @return result of the visit
     */
    public Object visit(MemberList node) {
        MemberList memberList = new MemberList(node.getLineNum());
        for (ASTNode child : node)
            memberList.addElement((ASTNode) child.accept(this));

        return memberList;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return result of the visit
     */
    public Object visit(Field node) {
        Field field;
        if (node.getInit() != null) {
            Expr init = (Expr) node.getInit().accept(this);
            field = new Field(node.getLineNum(), node.getType(), node.getName(), init);
        } else {
            field = new Field(node.getLineNum(), node.getType(), node.getName(), null);
        }
        return field;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return result of the visit
     */
    public Object visit(Method node) {
        FormalList formalList = (FormalList) node.getFormalList().accept(this);
        StmtList stmtList = (StmtList) node.getStmtList().accept(this);
        return new Method(node.getLineNum(), node.getReturnType(), node.getName(), formalList, stmtList);
    }

    /**
     * Visit a list node of formals
     *
     * @param node the formal list node
     * @return result of the visit
     */
    public Object visit(FormalList node) {
        FormalList formalList = new FormalList(node.getLineNum());
        for (Iterator it = node.iterator(); it.hasNext(); ) {
            formalList.addElement((Formal) ((Formal) it.next()).accept(this));
        }
        return formalList;
    }

    /**
     * Visit a formal node
     *
     * @param node the formal node
     * @return result of the visit
     */
    public Object visit(Formal node) {
        return new Formal(node.getLineNum(), node.getType(), node.getName());
    }

    /**
     * Visit a list node of statements
     *
     * @param node the statement list node
     * @return result of the visit
     */
    public Object visit(StmtList node) {
        StmtList stmtList = new StmtList(node.getLineNum());
        for (Iterator it = node.iterator(); it.hasNext(); ) {
            Stmt stmt = (Stmt) ((Stmt) it.next()).accept(this);
            stmtList.addElement(stmt);
        }
        return stmtList;
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return result of the visit
     */
    public Object visit(DeclStmt node) {
        Expr init = (Expr) node.getInit().accept(this);
        return new DeclStmt(node.getLineNum(), node.getName(), init);
    }

    /**
     * Visit an expression statement node
     *
     * @param node the expression statement node
     * @return result of the visit
     */
    public Object visit(ExprStmt node) {
        Expr expr = (Expr) node.getExpr().accept(this);
        return new ExprStmt(node.getLineNum(), expr);
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node) {
        Expr pred = (Expr) node.getPredExpr().accept(this);
        Stmt then = (Stmt) node.getThenStmt().accept(this);
        Stmt elseStmt = null;
        if (node.getElseStmt() != null) {
            elseStmt = (Stmt) node.getElseStmt().accept(this);
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
        Expr pred = (Expr) node.getPredExpr().accept(this);
        Stmt body = (Stmt) node.getBodyStmt().accept(this);
        return new WhileStmt(node.getLineNum(), pred, body);
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node) {
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
        Stmt body = (Stmt) node.getBodyStmt().accept(this);

        return new ForStmt(node.getLineNum(), init, pred, update, body);
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return result of the visit
     */
    public Object visit(BreakStmt node) {
        return new BreakStmt(node.getLineNum());
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return result of the visit
     */
    public Object visit(BlockStmt node) {
        StmtList stmtList = (StmtList) node.getStmtList().accept(this);
        return new BlockStmt(node.getLineNum(), stmtList);
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return result of the visit
     */
    public Object visit(ReturnStmt node) {
        Expr expr = null;
        if (node.getExpr() != null) {
            expr = (Expr) node.getExpr().accept(this);
        }
        return new ReturnStmt(node.getLineNum(), expr);
    }

    /**
     * Visit a list node of expressions
     *
     * @param node the expression list node
     * @return result of the visit
     */
    public Object visit(ExprList node) {
        ExprList exprList = new ExprList(node.getLineNum());
        for (Iterator it = node.iterator(); it.hasNext(); )
            exprList.addElement((Expr) ((Expr) it.next()).accept(this));
        return exprList;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return result of the visit
     */
    public Object visit(DispatchExpr node) {
        Expr refExpr = null;
        if (node.getRefExpr() != null)
            refExpr = (Expr) node.getRefExpr().accept(this);
        ExprList exprList = (ExprList) node.getActualList().accept(this);
        DispatchExpr dispatchExpr = new DispatchExpr(node.getLineNum(),
                refExpr, node.getMethodName(), exprList);
        dispatchExpr.setExprType(node.getExprType());
        return dispatchExpr;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return result of the visit
     */
    public Object visit(NewExpr node) {
        NewExpr newExpr = new NewExpr(node.getLineNum(), node.getType());
        newExpr.setExprType(node.getExprType());
        return newExpr;
    }

    /**
     * Visit a new array expression node
     *
     * @param node the new array expression node
     * @return result of the visit
     */
    public Object visit(NewArrayExpr node) {
        Expr size = (Expr) node.getSize().accept(this);
        NewArrayExpr newArrayExpr = new NewArrayExpr(node.getLineNum(), node.getType(), size);
        newArrayExpr.setExprType(node.getExprType());
        return newArrayExpr;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return result of the visit
     */
    public Object visit(InstanceofExpr node) {
        Expr expr = (Expr) node.getExpr().accept(this);
        InstanceofExpr instanceofExpr = new InstanceofExpr(node.getLineNum(), expr, node.getType());
        instanceofExpr.setExprType(node.getExprType());
        instanceofExpr.setUpCheck(node.getUpCheck());
        return instanceofExpr;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node) {
        Expr expr = (Expr) node.getExpr().accept(this);
        CastExpr castExpr = new CastExpr(node.getLineNum(), node.getType(), expr);
        castExpr.setExprType(node.getExprType());
        castExpr.setUpCast(node.getUpCast());
        return castExpr;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return result of the visit
     */
    public Object visit(AssignExpr node) {
        Expr expr = (Expr) node.getExpr().accept(this);
        AssignExpr assignExpr = new AssignExpr(node.getLineNum(), node.getRefName(), node.getName(), expr);
        assignExpr.setExprType(node.getExprType());
        return assignExpr;
    }

    /**
     * Visit an array assignment expression node
     *
     * @param node the array assignment expression node
     * @return result of the visit
     */
    public Object visit(ArrayAssignExpr node) {
        Expr index = (Expr) node.getIndex().accept(this);
        Expr expr = (Expr) node.getExpr().accept(this);
        ArrayAssignExpr arrayAssignExpr = new ArrayAssignExpr(node.getLineNum(),
                node.getRefName(), node.getName(), index, expr);
        arrayAssignExpr.setExprType(node.getExprType());
        return arrayAssignExpr;
    }

    /**
     * Visit a lambda node
     *
     * @param node the list node
     * @return result of the visit
     */
    public Object visit(LambdaExpr node) {
        Closure closure = (Closure) node.getClosure().accept(this);
        FormalList formalList = (FormalList) node.getFormalList().accept(this);
        StmtList stmtList = (StmtList) node.getStmtList().accept(this);
        LambdaExpr lambdaExpr = new LambdaExpr(node.getLineNum(), node.getReturnType(),
                formalList, closure, stmtList);
        lambdaExpr.setExprType(node.getExprType());
        return lambdaExpr;
    }

    /**
     * Visit a closure node
     *
     * @param node the list node
     * @return result of the visit
     */
    public Object visit(Closure node) {
        Closure closure = new Closure(node.getLineNum());
        for (Iterator it = node.iterator(); it.hasNext(); )
            closure.addElement((VarExpr) ((VarExpr) it.next()).accept(this));
        return closure;
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompEqExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryCompEqExpr binaryCompEqExpr = new BinaryCompEqExpr(node.getLineNum(), left, right);
        binaryCompEqExpr.setExprType(node.getExprType());
        return binaryCompEqExpr;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompNeExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryCompNeExpr binaryCompNeExpr = new BinaryCompNeExpr(node.getLineNum(), left, right);
        binaryCompNeExpr.setExprType(node.getExprType());
        return binaryCompNeExpr;
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLtExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryCompLtExpr binaryCompLtExpr = new BinaryCompLtExpr(node.getLineNum(), left, right);
        binaryCompLtExpr.setExprType(node.getExprType());
        return binaryCompLtExpr;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompLeqExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryCompLeqExpr binaryCompLeqExpr = new BinaryCompLeqExpr(node.getLineNum(), left, right);
        binaryCompLeqExpr.setExprType(node.getExprType());
        return binaryCompLeqExpr;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGtExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryCompGtExpr binaryCompGtExpr = new BinaryCompGtExpr(node.getLineNum(), left, right);
        binaryCompGtExpr.setExprType(node.getExprType());
        return binaryCompGtExpr;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return result of the visit
     */
    public Object visit(BinaryCompGeqExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryCompGeqExpr binaryCompGeqExpr = new BinaryCompGeqExpr(node.getLineNum(), left, right);
        binaryCompGeqExpr.setExprType(node.getExprType());
        return binaryCompGeqExpr;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithPlusExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryArithPlusExpr binaryArithPlusExpr = new BinaryArithPlusExpr(node.getLineNum(), left, right);
        binaryArithPlusExpr.setExprType(node.getExprType());
        return binaryArithPlusExpr;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithMinusExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryArithMinusExpr binaryArithMinusExpr = new BinaryArithMinusExpr(node.getLineNum(), left, right);
        binaryArithMinusExpr.setExprType(node.getExprType());
        return binaryArithMinusExpr;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithTimesExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryArithTimesExpr binaryArithTimesExpr = new BinaryArithTimesExpr(node.getLineNum(), left, right);
        binaryArithTimesExpr.setExprType(node.getExprType());
        return binaryArithTimesExpr;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithDivideExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryArithDivideExpr binaryArithDivideExpr = new BinaryArithDivideExpr(node.getLineNum(), left, right);
        binaryArithDivideExpr.setExprType(node.getExprType());
        return binaryArithDivideExpr;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return result of the visit
     */
    public Object visit(BinaryArithModulusExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryArithModulusExpr binaryArithModulusExpr = new BinaryArithModulusExpr(node.getLineNum(), left, right);
        binaryArithModulusExpr.setExprType(node.getExprType());
        return binaryArithModulusExpr;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicAndExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryLogicAndExpr binaryLogicAndExpr = new BinaryLogicAndExpr(node.getLineNum(), left, right);
        binaryLogicAndExpr.setExprType(node.getExprType());
        return binaryLogicAndExpr;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node) {
        Expr left = (Expr) node.getLeftExpr().accept(this);
        Expr right = (Expr) node.getRightExpr().accept(this);
        BinaryLogicOrExpr binaryLogicOrExpr = new BinaryLogicOrExpr(node.getLineNum(), left, right);
        binaryLogicOrExpr.setExprType(node.getExprType());
        return binaryLogicOrExpr;
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return result of the visit
     */
    public Object visit(UnaryNegExpr node) {
        UnaryNegExpr unaryNegExpr = new UnaryNegExpr(node.getLineNum(), node.getExpr());
        unaryNegExpr.setExprType(node.getExprType());
        return unaryNegExpr;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    public Object visit(UnaryNotExpr node) {
        Expr bool = (Expr) node.getExpr().accept(this);
        UnaryNotExpr unaryNotExpr = new UnaryNotExpr(node.getLineNum(), bool);
        unaryNotExpr.setExprType(node.getExprType());
        return unaryNotExpr;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return result of the visit
     */
    public Object visit(UnaryIncrExpr node) {
        Expr expr = (Expr) node.getExpr().accept(this);
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
        Expr expr = (Expr) node.getExpr().accept(this);
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
        VarExpr varExpr = new VarExpr(node.getLineNum(), ref, node.getName());
        varExpr.setExprType(node.getExprType());
        return varExpr;
    }

    /**
     * Visit an array expression node
     *
     * @param node the array expression node
     * @return result of the visit
     */
    public Object visit(ArrayExpr node) {
        Expr ref = null;
        if (node.getRef() != null) {
            ref = (Expr) node.getRef().accept(this);
        }
        Expr index = (Expr) node.getIndex().accept(this);
        ArrayExpr arrayExpr = new ArrayExpr(node.getLineNum(), ref, node.getName(), index);
        arrayExpr.setExprType(node.getExprType());
        return arrayExpr;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return result of the visit
     */
    public Object visit(ConstIntExpr node) {
        return node;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return result of the visit
     */
    public Object visit(ConstBooleanExpr node) {
        return node;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return result of the visit
     */
    public Object visit(ConstStringExpr node) {
        return node;
    }
}