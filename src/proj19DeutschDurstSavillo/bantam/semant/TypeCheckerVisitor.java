/*
 * File: TypeCheckerVisitor.java
 * S19 CS461 Project 12
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 2/25/19
 * This file contains a visitor to check all the types in
 * the given AST.
 */

package proj19DeutschDurstSavillo.bantam.semant;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.util.Error;
import proj19DeutschDurstSavillo.bantam.util.*;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * Visitor class for checking expression types in an AST
 *
 * @author Martin Deutsch
 * @author Evan Savillo
 * @author Robert Durst
 */
public class TypeCheckerVisitor extends Visitor
{
    private ClassTreeNode currentClass;
    private SymbolTable   currentSymbolTable;
    private ErrorHandler  errorHandler;

    private boolean insideLoop;

    //    private boolean lastStmtIsReturn;
    private Stack<Boolean> lastStmtIsRetStack;
    //    private String methodReturnType;
    private Stack<String>  methodTypeStack;

    private Stack<LambdaListNode> lambdaStack;


    /**
     * Checks the types of every expression in the AST
     *
     * @param root         the ClassTreeNode at the root of the inheritance tree
     * @param program      the root node of the AST
     * @param errorHandler the errorHandler to register any errors with
     */
    public void typeCheck(ClassTreeNode root, Program program, ErrorHandler errorHandler)
    {
        this.currentClass = root;
        this.currentSymbolTable = root.getVarSymbolTable();
        this.errorHandler = errorHandler;
        this.insideLoop = false;

        this.lastStmtIsRetStack = new Stack<>();
        this.methodTypeStack = new Stack<>();

        this.lambdaStack = new Stack<>();

        super.visit(program);
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node)
    {
        this.currentClass = this.currentClass.lookupClass(node.getName());
        this.currentSymbolTable = this.currentClass.getVarSymbolTable();
        node.getMemberList().accept(this);

        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return null
     */
    public Object visit(Field node)
    {
        // The fields should have already been added to the symbol table by the
        // SemanticAnalyzer so the only thing to check is the compatibility of the init
        // expr's type with the field's type.
        if (!isDefinedType(node.getType())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The declared type " + node.getType() + " of the field "
                                          + node.getName() + " is undefined.");
        }
        Expr initExpr = node.getInit();
        if (initExpr != null) {
            initExpr.accept(this);
            if (node.getType().startsWith("_")) {
                String fulltype = "_" + ((LambdaExpr) initExpr).getReturnType() + "(";
                for (ASTNode p : ((LambdaExpr) initExpr).getFormalList()) {
                    fulltype += ((Formal) p).getType() + ",";
                }
                fulltype = fulltype.substring(0, fulltype.length() - 1) + ")";

                if (!node.getType().equals(fulltype)) {
                    errorHandler.register(Error.Kind.SEMANT_ERROR,
                                          currentClass.getASTNode().getFilename(), node.getLineNum(),
                                          "The type of the initializer is " + fulltype
                                                  + " which is not compatible with the " + node.getName() +
                                                  " field's type " + node.getType());
                }
            } else
                // throw exception if the initExpr's type is not a subtype of the node's type
                if (!isSubtype(initExpr.getExprType(), node.getType(), true)) {
                    errorHandler.register(Error.Kind.SEMANT_ERROR,
                                          currentClass.getASTNode().getFilename(), node.getLineNum(),
                                          "The type of the initializer is " + initExpr.getExprType()
                                                  + " which is not compatible with the " + node.getName() +
                                                  " field's type " + node.getType());
                }
        }
        //Note: if there is no initExpr, then leave it to the Code Generator to
        //      initialize it to the default value since it is irrelevant to the
        //      SemanticAnalyzer.
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the Method node to visit
     * @return null
     */
    public Object visit(Method node)
    {
        // throw exception if the node's return type is not a defined type and not "void"
        if (!isDefinedType(node.getReturnType()) && !node.getReturnType().equals("void")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The return type " + node.getReturnType() + " of the method "
                                          + node.getName() + " is undefined.");
        }

        //create a new scope for the method body
        currentSymbolTable.enterScope();

//        this.methodReturnType = node.getReturnType();
//        this.lastStmtIsReturn = false;
        this.methodTypeStack.push(node.getReturnType());
        this.lastStmtIsRetStack.push(false);

        node.getFormalList().accept(this);
        node.getStmtList().accept(this);

        boolean lastStmtIsReturn = this.lastStmtIsRetStack.pop();
        String  returnType       = this.methodTypeStack.pop();

        // last statement must be return in non-void method
        if (!lastStmtIsReturn && !returnType.equals("void")) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       this.currentClass.getASTNode().getFilename(),
                                       node.getLineNum(),
                                       "missing required return statement at end of method body.");
        }

        currentSymbolTable.exitScope();

        return null;
    }

    /**
     * Visit a formal parameter node
     *
     * @param node the Formal node
     * @return null
     */
    public Object visit(Formal node)
    {
        // throw exception if the node's type is not a defined type
        if (!isDefinedType(node.getType())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The declared type " + node.getType() + " of the formal" +
                                          " parameter " + node.getName() + " is undefined.");
        }
        // add it to the current scope
        this.currentSymbolTable.add(node.getName(), node.getType());
        return null;
    }

    /**
     * Visit a list node of statements
     *
     * @param node the statement list node
     * @return a vector of return statements for type checking
     */
    public Object visit(StmtList node)
    {
        for (int i = 0; i < node.getSize(); i++) {
            Stmt stmt = (Stmt) node.get(i);
            stmt.accept(this);
            if (i == node.getSize() - 1) {
//                this.lastStmtIsReturn = stmt instanceof ReturnStmt;
                this.lastStmtIsRetStack.poke(stmt instanceof ReturnStmt);
            }
        }

        return null;
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return null
     */
    public Object visit(DeclStmt node)
    {
        // throw exception if variable name is a reserved word
        if (SemanticAnalyzer.reservedIdentifiers.contains(node.getName())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The variable name " + node.getName() + " is a reserved word.");
        }

        node.getInit().accept(this);

        String type = node.getInit().getExprType();
        // throw exception if init expression is null
        if (type.equals("null")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The initialization expression for " + node.getName() + " is null.");
        }

        // add variable to the current scope
        currentSymbolTable.add(node.getName(), type);

        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return null
     */
    public Object visit(IfStmt node)
    {
        // traverse predicate
        node.getPredExpr().accept(this);

        // throw exception if the predExpr's type is not "boolean"
        if (!node.getPredExpr().getExprType().equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The type of the predicate is " + node.getPredExpr().getExprType()
                                          + " which is not boolean.");
        }

        //	new scope for if's body
        currentSymbolTable.enterScope();
        node.getThenStmt().accept(this);
        currentSymbolTable.exitScope();

        // optional else stmt
        if (node.getElseStmt() != null) {
            // new scope for else's body
            currentSymbolTable.enterScope();
            node.getElseStmt().accept(this);
            currentSymbolTable.exitScope();
        }

        return null;
    }

    /**
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return null
     */
    public Object visit(WhileStmt node)
    {
        node.getPredExpr().accept(this);
        // throw exception if the predExpr's type is not "boolean"
        if (!node.getPredExpr().getExprType().equals("boolean")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The type of the predicate is " + node.getPredExpr().getExprType()
                                          + " which is not boolean.");
        }
        currentSymbolTable.enterScope();
        if (this.insideLoop) {
            node.getBodyStmt().accept(this);
        } else {
            this.insideLoop = true;
            node.getBodyStmt().accept(this);
            this.insideLoop = false;
        }
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return null
     */
    public Object visit(ForStmt node)
    {
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }
        // check that predicate expression has boolean type
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
            if (!node.getPredExpr().getExprType().equals("boolean")) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                                      currentClass.getASTNode().getFilename(), node.getLineNum(),
                                      "The type of the predicate is " + node.getPredExpr().getExprType()
                                              + " which is not boolean.");
            }
        }
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }
        currentSymbolTable.enterScope();
        if (this.insideLoop) {
            node.getBodyStmt().accept(this);
        } else {
            this.insideLoop = true;
            node.getBodyStmt().accept(this);
            this.insideLoop = !this.insideLoop;
        }
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return null
     */
    public Object visit(BreakStmt node)
    {
        if (!this.insideLoop) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "A break statement cannot be used outside of a loop.");
        }
        return null;
    }

    /**
     * Visit a block statement node
     *
     * @param node the block statement node
     * @return null
     */
    public Object visit(BlockStmt node)
    {
        currentSymbolTable.enterScope();
        node.getStmtList().accept(this);
        currentSymbolTable.exitScope();
        return null;
    }

    /**
     * Visit a return statement node
     *
     * @param node the return statement node
     * @return null
     */
    public Object visit(ReturnStmt node)
    {
        String methodReturnType = this.methodTypeStack.peek();

        // if there is a return type, must match method type
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
            String returnType = node.getExpr().getExprType();

            if (!isSubtype(returnType, methodReturnType, true)) {
                this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                           this.currentClass.getASTNode().getFilename(),
                                           node.getLineNum(),
                                           "Type '" + returnType + "' of return statement " +
                                                   "does not match required type '" + methodReturnType + "'");
            }
        }
        // if return type is null, method type must be void
        else {

            if (!"void".equals(methodReturnType)) {
                this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                           this.currentClass.getASTNode().getFilename(),
                                           node.getLineNum(),
                                           "Type 'void' of return statement " +
                                                   "does not match required type '" + methodReturnType + "'");
            }
        }

        return null;
    }

    /**
     * Returns whether a type is equal to or a subtype of another type
     *
     * @param subtype the possible subtype
     * @param type    the possible parent type
     * @param strict  whether or not to care about [] when evaluating type
     * @return true if subtype is equal to or a child of type, false otherwise
     */
    private boolean isSubtype(String subtype, String type, boolean strict)
    {
        if (type == null) {
            return false;
        }

        // lambda shenanigans. Make sure the appropriate parts of the types get compared
        if (subtype.startsWith("_") && !type.startsWith("_")) {
            subtype = subtype.substring(1, subtype.indexOf("("));
        }
        if (!subtype.startsWith("_") && type.startsWith("_")) {
            type = type.substring(1, type.indexOf("("));
        }

        // "null" is a subtype of any Object
        if (subtype.equals("null") && !type.equals("int") && !type.equals("boolean")) {
            return true;
        }

        if (!strict) {
            // strip brackets to just test types
            if (type.endsWith("[]")) {
                type = type.substring(0, type.indexOf('['));
            }
            if (subtype.endsWith("[]")) {
                subtype = subtype.substring(0, subtype.indexOf('['));
            }
        }

        if (subtype.equals(type)) {
            return true;
        }

        for (ClassTreeNode ctn = currentClass.lookupClass(subtype); ctn != null; ctn = ctn.getParent()) {
            // return true if type is an ancestor of subtype
            if (ctn.getName().equals(type)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return null
     */
    public Object visit(DispatchExpr node)
    {
        ClassTreeNode refClass = this.currentClass;
        Expr          refExpr  = node.getRefExpr();

        // set refClass to value of reference
        if (refExpr != null) {
            // check to see if
            node.getRefExpr().accept(this);
            if (invalidThisOrSuper(refExpr)) {
                this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                           this.currentClass.getASTNode().getFilename(),
                                           node.getLineNum(),
                                           ((VarExpr) refExpr).getName() + " must be first term in reference.");
                node.setExprType("null");
                return null;
            }

            String refType = node.getRefExpr().getExprType();

            // if this is an array, the method "clone" is allowed
            if (refType.endsWith("[]")) {
                if (node.getMethodName().equals("clone") && node.getActualList().getSize() == 0) {
                    node.setExprType(refType);
                    return null;
                } else {
                    errorHandler.register(Error.Kind.SEMANT_ERROR,
                                          currentClass.getASTNode().getFilename(), node.getLineNum(),
                                          "Reference is of type " + refType + " so clone() is " +
                                                  "the only allowed method.");
                    node.setExprType("null");
                    return null;
                }

            } else if (!isDefinedClass(refType)) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                                      currentClass.getASTNode().getFilename(), node.getLineNum(),
                                      "Reference is of type " + refType + " which cannot be dereferenced");
                node.setExprType("null");
                return null;
            }
            refClass = this.currentClass.lookupClass(refType);
        }

        // get the return type, which is all that's stored in the methodSymbolTable
        String methodType;
        methodType = (String) refClass.getMethodSymbolTable().lookup(node.getMethodName());

        // horrendous approach ToDo
        if (methodType == null) {
            // if nothing turned up, perhaps its a lambda i.e. is in the varsymtable
            String tentativeMethodType = (String) refClass.getVarSymbolTable().lookup(node.getMethodName());
            if (tentativeMethodType != null)
                methodType = (tentativeMethodType.startsWith("_")) ? tentativeMethodType : methodType;
        }

        if (methodType == null) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       this.currentClass.getASTNode().getFilename(),
                                       node.getLineNum(),
                                       "method '" + node.getMethodName() + "' could not be found " +
                                               "in class " + refClass.getName());

            node.setExprType("null");

            return null;
        }

        // we can be sure now what type we are returning
        node.setExprType(methodType);

        // get the parameters inputted by the user
        ExprList givenParameters = node.getActualList();
        givenParameters.accept(this);

        // givenParameters must match requiredParameters
        FormalList requiredParameters;
        requiredParameters = (FormalList) refClass.getMethodSymbolTable().lookup(node.getMethodName() + "()");

        // continuation of a horrendous approach to lambdas:
        if (methodType.startsWith("_")) {
            int openP  = methodType.indexOf("(");
            int closeP = methodType.indexOf(")");

            Iterator<ASTNode> givenIt = givenParameters.iterator();
            ArrayList<String> requiredParam =
                    new ArrayList<>(Arrays.asList(methodType.substring(openP + 1, closeP).split(",")));
            Iterator<String> requiredIt = requiredParam.iterator();

            if (givenParameters.getSize() != requiredParam.size()) {
                this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                           this.currentClass.getASTNode().getFilename(),
                                           node.getLineNum(),
                                           "'" + node.getMethodName() + "' expects " +
                                                   requiredParam.size() + " " + "parameters.");
                return null;
            }

            // Check each given parameter against the ones required
            while (givenIt.hasNext()) {
                Expr givenParam = (Expr) givenIt.next();

                String givenType = givenParam.getExprType();
                String requiredType = requiredIt.next();

                if (!this.isSubtype(givenType, requiredType, true)) {
                    this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                               this.currentClass.getASTNode().getFilename(),
                                               node.getLineNum(),
                                               "Expected parameter of type '" +
                                                       requiredType +
                                                       "' but instead received parameter of type '" +
                                                       givenType + "'.");
                }
            }

            return null;
        }

        if (givenParameters.getSize() != requiredParameters.getSize()) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       this.currentClass.getASTNode().getFilename(),
                                       node.getLineNum(),
                                       "'" + node.getMethodName() + "' expects " +
                                               requiredParameters.getSize() + " " + "parameters.");

        } else {
            Iterator<ASTNode> givenIt    = givenParameters.iterator();
            Iterator<ASTNode> requiredIt = requiredParameters.iterator();

            // Check each given parameter against the ones required
            while (givenIt.hasNext()) {
                Expr   givenParam    = (Expr) givenIt.next();
                Formal requiredParam = (Formal) requiredIt.next();

                String givenType = givenParam.getExprType();
                String requiredType = requiredParam.getType();

                if (!this.isSubtype(givenType, requiredType, true)) {
                    this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                               this.currentClass.getASTNode().getFilename(),
                                               node.getLineNum(),
                                               "Expected parameter of type '" +
                                                       requiredType +
                                                       "' but instead received parameter of type '" +
                                                       givenType + "'.");
                }
            }

        }

        return null;
    }

    /**
     * Returns whether the given className is a defined class
     *
     * @param className the String name of the class to check for
     * @return true if className is part of the inheritance tree, false if not
     */
    private boolean isDefinedClass(String className)
    {
        if (className.endsWith("[]")) {
            className = className.substring(0, className.indexOf('['));
            if ("int".equals(className) || "boolean".equals(className)) {
                return true;
            }
        }
        return currentClass.lookupClass(className) != null;
    }

    /**
     * Checks if there is a "this" or "super" after another identifier
     * in the given ref Expr, which is illegal
     *
     * @param refExpr the Expr to check
     * @return true if the Expr is invalid, false if not
     */
    private boolean invalidThisOrSuper(Expr refExpr)
    {
        if (refExpr instanceof VarExpr) {
            String refName = ((VarExpr) refExpr).getName();

            boolean refNamedEitherThisOrSuper = (refName.equals("this") ||
                    refName.equals("super"));
            boolean refOfRefNotNull = ((VarExpr) refExpr).getRef() != null;

            return refNamedEitherThisOrSuper && refOfRefNotNull;
        }
        return false;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return null
     */
    public Object visit(NewExpr node)
    {
        // throw exception if the node's type is not a defined class type
        if (!isDefinedClass(node.getType())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The type " + node.getType() + " does not exist.");
            node.setExprType("Object"); // to allow analysis to continue
        } else {
            node.setExprType(node.getType());
        }
        return null;
    }

    /**
     * Visit a new array expression node
     *
     * @param node the new array expression node
     * @return null
     */
    public Object visit(NewArrayExpr node)
    {
        // throw exception if the node's type is not a defined type
        if (!isDefinedType(node.getType())) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The type " + node.getType() + " does not exist.");
            node.setExprType("Object[]"); // to allow analysis to continue
        } else {
            node.setExprType(node.getType() + "[]");
        }

        // throw exception if size is not an int
        node.getSize().accept(this);
        String sizeType = node.getSize().getExprType();
        if (!sizeType.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The type of the array index is " + sizeType + " which is not int.");
        }
        return null;
    }

    /**
     * Visit an instanceof expression node
     *
     * @param node the instanceof expression node
     * @return null
     */
    public Object visit(InstanceofExpr node)
    {
        node.getExpr().accept(this);
        String exprType = node.getExpr().getExprType();
        if (!isDefinedClass(exprType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The left expression must be an object type." +
                                          "Received expression of type '" + exprType + "'.");
        }
        String type = node.getType();
        if (!isDefinedClass(type) && !type.endsWith("[]")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The right type must be a valid classname or array type." +
                                          "Received expression of type '" + type + "'.");
        }

        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return result of the visit
     */
    public Object visit(CastExpr node)
    {
        // traverse expr
        node.getExpr().accept(this);

        // capture vars for later use
        String targetType     = node.getType();
        String castedExprType = node.getExpr().getExprType();

        // ensure not a lambda
        if (targetType.substring(0, 1).equals("_")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The target type of " + targetType + " cannot be a lambda.");
        }

        // ensure not a lambda
        if (castedExprType.substring(0, 1).equals("_")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The cast expression type of " + targetType + " cannot be a lambda.");
        }

        // ensure not a primitive
        if (isPrimitiveType(targetType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The target type of " + targetType + " cannot be a primitive.");
        }

        // ensure not a primitive
        else if (isPrimitiveType(castedExprType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The casted expression type of " + castedExprType + " cannot be a primitive.");
        }

        // ensure class exists
        else if (!isDefinedClass(targetType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The target type of " + targetType + " is undefined.");
        }
        // ensure class exists
        else if (!isDefinedClass(castedExprType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The casted expression type of " + castedExprType + " is undefined.");
        }

        // ensure the types are subtypes
        else if (!isSubtype(targetType, castedExprType, true) && !isSubtype(castedExprType, targetType, true)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The casted expression type of " + castedExprType +
                                          " is not a subclass or a superclass of the target type: " + targetType);
        } else {
            node.setExprType(targetType);
            return null;
        }

        // to allow analysis to continue
        node.setExprType("Object");

        return null;
    }

    /**
     * Returns whether the given type is a primitive type
     *
     * @param type the type name to check
     * @return "int," or "boolean," false if not
     */
    private boolean isPrimitiveType(String type)
    {
        if (type.endsWith("[]")) {
            type = type.substring(0, type.indexOf('['));
        }

        return "int".equals(type) || "boolean".equals(type);
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return null
     */
    public Object visit(AssignExpr node)
    {
        node.getExpr().accept(this);

        String refName = node.getRefName();
        String varName = node.getName();
        Expr   expr    = node.getExpr();

        // check that the variable is legal
//        if (this.currentClass.getVarSymbolTable().lookup(varName) == null) {
        if (performLookup(varName) == null) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       currentClass.getASTNode().getFilename(), node.getLineNum(),
                                       "'" + varName + "' cannot be resolved.");
            node.setExprType("null");
            return null;
        }

        if (refName == null) {
            refName = "this";
        }

        ClassTreeNode refClass = this.currentClass;

        if (refName.equals("super")) {
            refClass = currentClass.getParent();

        } else if (!refName.equals("this")) {
//            String refType = (String) this.currentClass.getVarSymbolTable().lookup(refName);
            String refType = (String) performLookup(refName);

            if (refType == null) {
                this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                           this.currentClass.getASTNode().getFilename(),
                                           node.getLineNum(),
                                           "Could not find '" + refName + "' in class '" +
                                                   this.currentClass.getName() + "'.");
                node.setExprType("null");
                return null;
            }

            refClass = refClass.lookupClass(refType);
        }

        String varType = (String) refClass.getVarSymbolTable().lookup(node.getName());

        if (!isSubtype(expr.getExprType(), varType, true)) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       this.currentClass.getASTNode().getFilename(),
                                       node.getLineNum(),
                                       "type '" + expr.getExprType() +
                                               "' cannot be assigned to variable of type '" + varType + "'");
            node.setExprType("null");
            return null;
        } else {
            node.setExprType(varType);
        }

        refClass.getVarSymbolTable().set(node.getName(), node.getExprType());

        return null;
    }

    /**
     * Performs a lookup in the class symbol table, and if appropriate, goes through
     * a LambdaListNode to do so
     */
    private Object performLookup(String s)
    {
        if (this.lambdaStack.isEmpty()) {
            return this.currentClass.getVarSymbolTable().lookup(s);
        } else {
            return this.lambdaStack.peek().lookup(s);
        }
    }

    /**
     * Visit an array assignment expression node
     *
     * @param node the array assignment expression node
     * @return null
     */
    public Object visit(ArrayAssignExpr node)
    {
        node.getExpr().accept(this);

        String refName = node.getRefName();
        String varName = node.getName();
        Expr   expr    = node.getExpr();

        // check that the variable is legal
        if (performLookup(varName) == null) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       currentClass.getASTNode().getFilename(), node.getLineNum(),
                                       "'" + varName + "' cannot be resolved. (Array)");
            node.setExprType("null");
            return null;
        }

        if (refName == null) {
            refName = "this";
        }

        // get the class referred to in the reference
        ClassTreeNode refClass = this.currentClass;

        if (refName.equals("super")) {
            refClass = currentClass.getParent();

        } else if (!refName.equals("this")) {
            String refType = (String) performLookup(refName);

            if (refType == null) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                                           this.currentClass.getASTNode().getFilename(),
                                           node.getLineNum(),
                                           "Could not find '" + refName + "' in class '" +
                                                   this.currentClass.getName() + "'.");
                node.setExprType("null");
                return null;
            }

            refClass = refClass.lookupClass(refType);
        }

        String varType = (String) refClass.getVarSymbolTable().lookup(node.getName());

        if (!isSubtype(expr.getExprType(), varType, false)) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       this.currentClass.getASTNode().getFilename(),
                                       node.getLineNum(),
                                       "type '" + expr.getExprType() +
                                               "' cannot be assigned to variable of type '" + varType + "'");
            node.setExprType("null[]");
        } else {
            node.setExprType(varType.substring(0, varType.indexOf("[")));
        }

        // Check the index is of type int
        node.getIndex().accept(this);
        String indexType = node.getIndex().getExprType();
        if (!indexType.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The type of the array index is " + indexType + "which is not int.");
        }

        refClass.getVarSymbolTable().set(node.getName(), node.getExprType() + "[]");

        return null;
    }

    /**
     * Visit a lambda node
     *
     * @param node the list node
     * @return result of the visit
     */
    public Object visit(LambdaExpr node)
    {
        this.lambdaStack.push(new LambdaListNode(node)); // we are entering a lambda

        this.methodTypeStack.push(node.getReturnType());
        this.lastStmtIsRetStack.push(false);

        // throw exception if the node's return type is not a defined type and not "void"
        if (!isDefinedType(this.methodTypeStack.peek()) && !this.methodTypeStack.peek().equals("void")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The return type " + node.getReturnType() + "of the lambda is undefined.");
        }

        this.currentClass.getVarSymbolTable().enterScope();

        node.getClosure().accept(this);
        node.getFormalList().accept(this);
        node.getStmtList().accept(this); // Where there can potentially be other lambdas

        this.currentClass.getVarSymbolTable().exitScope();

        boolean lastStmtIsReturn = this.lastStmtIsRetStack.pop();
        String  returnType       = this.methodTypeStack.pop();

        // last statement must be return in non-void method
        if (!lastStmtIsReturn && !returnType.equals("void")) {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       this.currentClass.getASTNode().getFilename(),
                                       node.getLineNum(),
                                       "missing required return statement at end of method body.");
        }

        this.lambdaStack.pop(); // we have left the lambda

        return null;
    }

    /**
     * Visit a closure node
     *
     * @param node the list node
     * @return result of the visit
     */
    public Object visit(Closure node)
    {
        if (node.getSize() == 0) {
            this.lambdaStack.peek().addClosure(this.currentClass.getVarSymbolTable());
            this.lambdaStack.peek().setEmptyClosure(true);
            return null;
        }

        VarExpr varExpr = (VarExpr) node.get(0);

        if (varExpr.getName().equals("this")) {
            this.lambdaStack.peek().addClosure(this.currentClass.getVarSymbolTable());
        } else if (varExpr.getName().equals("super")) {
            this.lambdaStack.peek().addClosure(this.currentClass.getParent().getVarSymbolTable());
        } else {
            this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                       this.currentClass.getASTNode().getFilename(),
                                       varExpr.getLineNum(),
                                       "Error in closure supplied: " + varExpr.getName());
        }

        return null;
    }

    /**
     * Visit a binary comparison equals expression node
     *
     * @param node the binary comparison equals expression node
     * @return null
     */
    public Object visit(BinaryCompEqExpr node)
    {
        this.visitBinaryEqExpr(node, "equality (==)");

        return null;
    }

    private void visitBinaryEqExpr(BinaryExpr node, String op)
    {
        // traverse left and right expression
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);

        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();
        // throw exception if neither type1 nor type2 is a subtype of the other
        if (!isSubtype(type1, type2, true) && !isSubtype(type2, type1, true)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The two expressions in this " + op + " comparison are different subtypes.");
        }

        node.setExprType("boolean");
    }

    /**
     * Visit a binary comparison does not equal expression node
     *
     * @param node the binary comparison not equals expression node
     * @return null
     */
    public Object visit(BinaryCompNeExpr node)
    {
        this.visitBinaryEqExpr(node, "inequality (!=)");

        return null;
    }

    /**
     * Visit a binary comparison less than expression node
     *
     * @param node the binary comparison less than expression node
     * @return null
     */
    public Object visit(BinaryCompLtExpr node)
    {
        this.visitBinaryGeneralExpr(node, "<", "boolean", "int");

        return null;
    }

    /**
     * Visit a binary comparison less than or equals expression node
     *
     * @param node the binary comparison less than or equals expression node
     * @return null
     */
    public Object visit(BinaryCompLeqExpr node)
    {
        this.visitBinaryGeneralExpr(node, "<=", "boolean", "int");

        return null;
    }

    /**
     * Visit a binary comparison greater than expression node
     *
     * @param node the binary comparison greater than expression node
     * @return null
     */
    public Object visit(BinaryCompGtExpr node)
    {
        this.visitBinaryGeneralExpr(node, ">", "boolean", "int");

        return null;
    }

    /**
     * Visit a binary comparison greater than or equals expression node
     *
     * @param node the binary comparison greater than or equals expression node
     * @return null
     */
    public Object visit(BinaryCompGeqExpr node)
    {
        this.visitBinaryGeneralExpr(node, ">=", "boolean", "int");

        return null;
    }

    /**
     * Visit a binary arithmetic plus expression node
     *
     * @param node the binary arithmetic plus expression node
     * @return null
     */
    public Object visit(BinaryArithPlusExpr node)
    {
        this.visitBinaryGeneralExpr(node, "+", "int", "int");

        return null;
    }

    /**
     * Visit a binary arithmetic minus expression node
     *
     * @param node the binary arithmetic minus expression node
     * @return null
     */
    public Object visit(BinaryArithMinusExpr node)
    {
        this.visitBinaryGeneralExpr(node, "-", "int", "int");

        return null;
    }

    /**
     * Visit a binary arithmetic times expression node
     *
     * @param node the binary arithmetic times expression node
     * @return null
     */
    public Object visit(BinaryArithTimesExpr node)
    {
        this.visitBinaryGeneralExpr(node, "*", "int", "int");

        return null;
    }

    /**
     * Visit a binary arithmetic divide expression node
     *
     * @param node the binary arithmetic divide expression node
     * @return null
     */
    public Object visit(BinaryArithDivideExpr node)
    {
        this.visitBinaryGeneralExpr(node, "/", "int", "int");

        return null;
    }

    /**
     * Visit a binary arithmetic modulus expression node
     *
     * @param node the binary arithmetic modulus expression node
     * @return null
     */
    public Object visit(BinaryArithModulusExpr node)
    {
        this.visitBinaryGeneralExpr(node, "%", "int", "int");

        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return null
     */
    public Object visit(BinaryLogicAndExpr node)
    {
        this.visitBinaryGeneralExpr(node, "&&", "boolean", "boolean");

        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return null
     */
    public Object visit(BinaryLogicOrExpr node)
    {
        this.visitBinaryGeneralExpr(node, "||", "boolean", "boolean");

        return null;
    }

    /**
     * Visit a unary negation expression node
     *
     * @param node the unary negation expression node
     * @return null
     */
    public Object visit(UnaryNegExpr node)
    {
        checkUnaryType(node, "int", "negation (-)");
        return null;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return null
     */
    public Object visit(UnaryNotExpr node)
    {
        checkUnaryType(node, "boolean", "not (!)");
        return null;
    }

    /**
     * Visit a unary increment expression node
     *
     * @param node the unary increment expression node
     * @return null
     */
    public Object visit(UnaryIncrExpr node)
    {
        if (!(node.getExpr() instanceof VarExpr) && !(node.getExpr() instanceof ArrayExpr)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "A unary increment operation can only be applied to variable or array.");
        }
        checkUnaryType(node, "int", "increment (++)");
        return null;
    }

    /**
     * Visit a unary decrement expression node
     *
     * @param node the unary decrement expression node
     * @return null
     */
    public Object visit(UnaryDecrExpr node)
    {
        if (!(node.getExpr() instanceof VarExpr) && !(node.getExpr() instanceof ArrayExpr)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "A unary increment operation can only be applied to a variable or array.");
        }
        checkUnaryType(node, "int", "decrement (--)");
        return null;
    }

    /**
     * Visit a variable expression node
     *
     * @param node the variable expression node
     * @return null
     */
    public Object visit(VarExpr node)
    {
        String exprType = "null";
        Expr   refExpr  = node.getRef();

        if (refExpr != null) {
            refExpr.accept(this);

            if (invalidThisOrSuper(refExpr)) {
                this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                           this.currentClass.getASTNode().getFilename(),
                                           node.getLineNum(),
                                           ((VarExpr) refExpr).getName() + " must be first term in reference.");
                node.setExprType("null");
                return null;
            }

            String refType = node.getRef().getExprType();

            if (isDefinedClass(refType) && this.currentClass.lookupClass(refType) != null) {
                // if reference is to an object, check the class definition of the object for the name
                ClassTreeNode refClass   = this.currentClass.lookupClass(refType);
                int           scopeLevel = refClass.getVarSymbolTable().getCurrScopeLevel() - 1;
                if (refClass == currentClass) {
                    scopeLevel = currentClass.getParent().getVarSymbolTable().getCurrScopeLevel();
                }
                if (refClass.getVarSymbolTable().lookup(node.getName(), scopeLevel) == null) {
                    errorHandler.register(Error.Kind.SEMANT_ERROR,
                                          currentClass.getASTNode().getFilename(), node.getLineNum(),
                                          "Reference is of type " + refType +
                                                  " which does not have a field " + node.getName());
                } else {
                    // otherwise check the symbol table
                    exprType = (String) refClass.getVarSymbolTable().lookup(node.getName(), scopeLevel);
                }
            } else {
                // if this is an array, the length field is allowed
                if (refType.endsWith("[]")) {
                    if (node.getName().equals("length")) {
                        node.setExprType("int");
                        return null;
                    } else {
                        errorHandler.register(Error.Kind.SEMANT_ERROR,
                                              currentClass.getASTNode().getFilename(), node.getLineNum(),
                                              "Reference is of type " + refType + " so length is " +
                                                      "the only accessible field.");
                        node.setExprType("null");
                        return null;
                    }
                }
                // if reference is to primitive, throw an exception
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                                      currentClass.getASTNode().getFilename(), node.getLineNum(),
                                      "Reference is of type " + refType + " which cannot be dereferenced");
            }
        } else {

            switch (node.getName()) {
                case "super":
                    exprType = this.currentClass.getParent().getName();
                    break;
                case "this":
                    exprType = this.currentClass.getName();
                    break;
                case "null":
                    exprType = "null";
                    break;
                default:
//                    if (this.currentSymbolTable.lookup(node.getName()) == null) {
                    if (performLookup(node.getName()) == null) {
                            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                              currentClass.getASTNode().getFilename(), node.getLineNum(),
                                              "No variable of name " + node.getName() + " found in scope.");
                    } else {
//                        exprType = (String) currentSymbolTable.lookup(node.getName());
                        exprType = (String) performLookup(node.getName());
                    }
            }
        }

        node.setExprType(exprType);
        return null;
    }

    /**
     * Visit an array expression node
     *
     * @param node the array expression node
     * @return null
     */
    public Object visit(ArrayExpr node)
    {
        String nodeType = "null";
        if (node.getRef() != null) {

            node.getRef().accept(this);

            if (node.getRef() instanceof VarExpr) {
                String refName = ((VarExpr) node.getRef()).getName();

                boolean refNamedEitherThisOrSuper = (refName.equals("this") ||
                        refName.equals("super"));
                boolean refOfRefNotNull = ((VarExpr) node.getRef()).getRef() != null;

                if (refNamedEitherThisOrSuper && refOfRefNotNull) {
                    this.errorHandler.register(Error.Kind.SEMANT_ERROR,
                                               currentClass.getASTNode().getFilename(),
                                               node.getLineNum(),
                                               refName + " must be first term in reference.");

                    node.setExprType("null");

                    return null;
                }
            }

            String refType = node.getRef().getExprType();

            // if reference is to an object, check the class definition of the object for the name
            if (isDefinedClass(refType)) {
                ClassTreeNode refClass   = currentClass.lookupClass(refType);
                int           scopeLevel = refClass.getVarSymbolTable().getCurrScopeLevel() - 1;
                if (refClass == currentClass) {
                    scopeLevel = currentClass.getParent().getVarSymbolTable().getCurrScopeLevel();
                }
                if (refClass.getVarSymbolTable().lookup(node.getName(), scopeLevel) == null) {
                    errorHandler.register(Error.Kind.SEMANT_ERROR,
                                          currentClass.getASTNode().getFilename(), node.getLineNum(),
                                          "Reference is of type " + refType +
                                                  " which does not have a field " + node.getName());
                } else {
                    nodeType = (String) refClass.getVarSymbolTable().lookup(node.getName(), scopeLevel);
                }
            }
            // if reference is to primitive, throw exception as primitive has no fields
            else {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                                      currentClass.getASTNode().getFilename(), node.getLineNum(),
                                      "Reference is of type " + refType + " which cannot be dereferenced");
            }
        }
        // if no reference, look for local variable
        else {
            if (currentSymbolTable.lookup(node.getName()) == null) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                                      currentClass.getASTNode().getFilename(), node.getLineNum(),
                                      "Variable " + node.getName() + " not defined in local scope.");
            } else {
                nodeType = (String) currentSymbolTable.lookup(node.getName());
            }
        }

        // check that the type is an array
        if (!nodeType.endsWith("[]")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The expression is of type " + nodeType + " which is not an array.");
        } else {
            // set element type to non-array
            nodeType = nodeType.substring(0, nodeType.length() - 2);
        }

        // check that the index is an int
        node.getIndex().accept(this);
        String indexType = node.getIndex().getExprType();
        if (!indexType.equals("int")) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The type of the array index is " + indexType + " which is not int.");
        }

        node.setExprType(nodeType);
        return null;
    }

    /**
     * Visit an int constant expression node
     *
     * @param node the int constant expression node
     * @return null
     */
    public Object visit(ConstIntExpr node)
    {
        node.setExprType("int");
        return null;
    }

    /**
     * Visit a boolean constant expression node
     *
     * @param node the boolean constant expression node
     * @return null
     */
    public Object visit(ConstBooleanExpr node)
    {
        node.setExprType("boolean");
        return null;
    }

    /**
     * Visit a string constant expression node
     *
     * @param node the string constant expression node
     * @return null
     */
    public Object visit(ConstStringExpr node)
    {
        node.setExprType("String");
        return null;
    }

    /**
     * Checks if the given node matches the expected type
     * Registers an error if not, then sets the expression type to the expected type
     *
     * @param node         the UnaryExpr node being visited
     * @param expectedType the String representing the expected type
     * @param operator     the String representing the unary operation in this expression
     */
    private void checkUnaryType(UnaryExpr node, String expectedType, String operator)
    {
        node.getExpr().accept(this);
        String type = node.getExpr().getExprType();
        if (!type.equals(expectedType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The " + operator + " operator applies only to " + expectedType
                                          + " expressions," + " not " + type + " expressions.");
        }
        node.setExprType(expectedType);
    }

    private void visitBinaryGeneralExpr(BinaryExpr node, String op, String setType, String requiredType)
    {
        // traverse left and right expression
        node.getLeftExpr().accept(this);
        node.getRightExpr().accept(this);

        // must both be boolean
        String type1 = node.getLeftExpr().getExprType();
        String type2 = node.getRightExpr().getExprType();

        if (!type1.equals(requiredType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The " + op + " operator applies only to " + setType + " expressions. " +
                                          "Received left expression of type: " + type1 + ".");
        } else if (!type2.equals(requiredType)) {
            errorHandler.register(Error.Kind.SEMANT_ERROR,
                                  currentClass.getASTNode().getFilename(), node.getLineNum(),
                                  "The " + op + " operator applies only to " + setType + " expressions. " +
                                          "Received right expression of type: " + type2 + ".");
        }

        node.setExprType(setType);
    }

    /**
     * Returns whether the given type is a defined class or primitive type
     *
     * @param type the type name to check
     * @return true if type is a class name, "int," or "boolean," false if not
     */
    private boolean isDefinedType(String type)
    {
        // in the case the type is a lambda
        if (type.startsWith("_")) {
            return checkLambdaType(type);
        }
        if (type.endsWith("[]")) {
            type = type.substring(0, type.indexOf('['));
        }
        if (isDefinedClass(type)) {
            return true;
        }
        return isPrimitiveType(type);
    }

    /**
     * Checks and returns whether or not the Lambda type is composed of valid types.
     */
    private boolean checkLambdaType(String type)
    {
        int openParen  = type.indexOf("(");
        int closeParen = type.indexOf(")");

        String   returnType = type.substring(1, openParen);
        String[] params     = type.substring(openParen + 1, closeParen).split(",");

        boolean validReturn = isDefinedType(returnType) || returnType.equals("void");
        boolean validParams = true;
        for (int i = 0; i < params.length; ++i) {
            validParams = (validParams && isDefinedType(params[i]));
        }

        return (validReturn && validParams);
    }

    /**
     * Get the contents of the current symbol table as a list
     *
     * @return list of variable names in current symbol table
     */
    public List<String> getCurrentSymbolTableList()
    {
        return this.currentSymbolTable.dumpIntoList();
    }

    /**
     * A private little stack class to help us out.
     * @param <T>
     */
    private class Stack<T>
    {
        private ArrayList<T> stack;

        public Stack()
        {
            this.stack = new ArrayList<>();
        }

        public T peek()
        {
            return (this.isEmpty()) ? null : this.stack.get(0);
        }

        public boolean isEmpty()
        {
            return this.stack.size() <= 0;
        }

        public T peek(int i)
        {
            return (i >= 0 && i < this.stack.size()) ? this.stack.get(i) : null;
        }

        /**
         * Modifies the top of the stack
         * <p>
         * poking a stack suggests an image of a finger encountering the top-most item and interacting with it
         * (in this case of this method, updating it). It also begins with 'p'.
         */
        public void poke(T node)
        {
            T top = this.pop();
            if (top != null) {
                this.push(node);
            }
        }

        public void push(T node)
        {
            this.stack.add(0, node);
        }

        public T pop()
        {
            return (this.isEmpty()) ? null : this.stack.remove(0);
        }

        public Iterator<T> iterator()
        {
            return this.stack.iterator();
        }
    }
}
