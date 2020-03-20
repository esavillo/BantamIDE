/*
 * File: UserDefinedMethodVisitor.java
 * S19 CS461 Project 17
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 4/22/19
 * This file contains a visitor that generates MIPS code for user-defined methods
 */

package proj19DeutschDurstSavillo.bantam.codegenmips;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.util.ClassTreeNode;
import proj19DeutschDurstSavillo.bantam.util.Location;
import proj19DeutschDurstSavillo.bantam.util.SymbolTable;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.util.Map;
import java.util.Stack;


/**
 * UserDefinedMethodVisitor visits an AST and generates MIPS code for user-defined methods
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class UserDefinedMethodVisitor extends Visitor
{
    private OurMipsSupport          support;
    private ClassTreeNode           root;
    private ClassTreeNode           currentClass;
    private String                  currentEpilog;
    private int                     currentNumParents;
    private Stack<String>           loopEndStack;
    private boolean                 gc = false;
    /* Maps string constants to unique identifiers */
    private Map<String, String>     stringsMap;
    /* Maps method names to number of local variables in method */
    private Map<String, Integer>    numLocalVarsMap;
    /* Maps class names to unique id numbers */
    private Map<String, String>     classEnumeration;
    /* Maps lambda nodes to their unique method names */
    private Map<LambdaExpr, String> lambdaNameMap;

    private boolean shouldUnroll;

    public UserDefinedMethodVisitor(boolean shouldUnroll)
    {
        this.loopEndStack = new Stack<>();
        this.shouldUnroll = shouldUnroll;
    }

    /**
     * Setter for MipsSupport object used to generate MIPS program
     *
     * @param assemblySupport the MipsSupport object
     */
    public void setAssemblySupport(OurMipsSupport assemblySupport)
    {
        this.support = assemblySupport;
    }

    /**
     * Setter for string constants map
     *
     * @param stringsMap the string constants mapping
     */
    public void setStringsMap(Map<String, String> stringsMap)
    {
        this.stringsMap = stringsMap;
    }

    /**
     * Setter for map of method name to num local vars
     *
     * @param numLocalVarsMap the map of methods to number of local vars
     */
    public void setNumLocalVarsMap(Map<String, Integer> numLocalVarsMap)
    {
        this.numLocalVarsMap = numLocalVarsMap;
    }

    /**
     * Set the root of the class tree node hierarchy
     *
     * @param root the class tree node for Object
     */
    public void setRoot(ClassTreeNode root)
    {
        this.root = root;
        this.currentClass = root;
    }

    /**
     * Setter for the current class being visited
     *
     * @param currentClass the class tree node for the current class
     */
    public void setCurrentClass(ClassTreeNode currentClass)
    {
        this.currentClass = currentClass;
    }

    /**
     * Set the map of classes to id numbers
     *
     * @param classEnumeration the class enumeration mapping
     */
    public void setClassEnumeration(Map<String, String> classEnumeration)
    {
        this.classEnumeration = classEnumeration;
    }

    /**
     * Setter for the lambda name map
     *
     * @param map mapping of lambda nodes to method names
     */
    public void setLambdaNameMap(Map<LambdaExpr, String> map)
    {
        this.lambdaNameMap = map;
    }

    /**
     * Turn on garbage collection
     */
    public void enableGC()
    {
        this.gc = true;
    }

    /**
     * Generate the MIPS code for user-defined methods
     *
     * @param root            the root of the ClassTreeNode hierarchy
     * @param program         the root of the AST
     * @param assemblySupport the MipsSupport object used to generate the MIPS program
     */
    public void genUserDefinedMethods(ClassTreeNode root, Program program,
                                      OurMipsSupport assemblySupport, Map<String, String> stringsMap,
                                      Map<String, Integer> numLocalVarsMap,
                                      Map<String, String> classEnumeration,
                                      Map<LambdaExpr, String> lambdaNameMap)
    {
        this.support = assemblySupport;
        this.root = root;
        this.currentClass = this.root;
        this.stringsMap = stringsMap;

        this.numLocalVarsMap = numLocalVarsMap;
        this.classEnumeration = classEnumeration;
        this.lambdaNameMap = lambdaNameMap;

        // visit the AST
        program.accept(this);

        this.loopEndStack.clear();
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node)
    {
        // set current class
        this.currentClass = this.root.lookupClass(node.getName());
        this.currentNumParents = currentClass.getVarSymbolTable().getCurrScopeLevel() - 1;

        // visit the class if it is user-defined
        if (!this.currentClass.isBuiltIn()) {
            node.getMemberList().accept(this);
        }

        return null;
    }

    /**
     * Visit only methods of each class
     *
     * @param node the member list node
     * @return null
     */
    public Object visit(MemberList node)
    {
        for (ASTNode child : node) {
            if (child instanceof Method) {
                child.accept(this);
            }
        }
        return null;
    }

    /**
     * Visit a method node
     *
     * @param node the method node
     * @return null
     */
    public Object visit(Method node)
    {
        this.currentClass.getVarSymbolTable().enterScope();
        // create label for method
        if (node.getName().startsWith("Lambda")) {
            this.support.genLabel(node.getName());
        } else {
            this.support.genLabel(this.currentClass.getName() + "." + node.getName());
        }
        // create label for epilog of method (for use in returns)
        this.currentEpilog = this.support.getLabel();
        // visit parameters
        node.getFormalList().accept(this);
        // generate prolog of method
        this.generateProlog(node);
        // visit statements
        node.getStmtList().accept(this);
        // generate epilog of method
        this.generateEpilog(node, this.currentEpilog);
        this.currentClass.getVarSymbolTable().exitScope();
        return null;
    }

    /**
     * Generate the MIPS code that begins every method
     */
    public void generateProlog(Method node)
    {
        this.support.genComment("");
        this.support.genComment("Prolog Begin", 1);
        this.genPush("$ra");
        this.genPush("$fp");
        // get number of local variables if needed
        int numVars = 0;
        if (this.numLocalVarsMap != null) {
            if (node.getName().startsWith("Lambda")) {
                numVars = numLocalVarsMap.get(node.getName());
            } else {
                numVars = numLocalVarsMap.get(this.currentClass.getName() + "." + node.getName());
            }
        }

        this.support.genAdd("$fp", "$sp", -4 * numVars);
        this.support.setLastInstrComment("Make space for " + numVars + " local vars");

        // copy parameters
        if (numVars > 0) {
            int numParams = node.getFormalList().getSize();
            for (int i = 0; i < numParams; i++) {
                Formal param = (Formal) node.getFormalList().get(i);
                this.support.genComment("Copy parameter " + param.getName() +
                                                " to local vars area of stack");

                // parameter is located after local vars, $ra and $fp, at ith position in params stack
                int paramLocation = (numVars + 2 + (numParams - i - 1)) * 4;
                this.support.genLoadWord("$t0", paramLocation, "$fp");

                // parameter is stored at bottom of local vars area of stack
                int offset = (numVars - (i + 1)) * 4;
                this.support.genStoreWord("$t0", offset, "$fp");

                // store location of parameter in symbol table
                this.currentClass.getVarSymbolTable().add(param.getName(), new Location("$fp", offset));
            }
        }
        this.support.genMove("$sp", "$fp");
        this.support.setLastInstrComment("Initialize stack pointer");
        // reset offsets
        this.support.setNextAvailStackOffset(0);
        this.support.genComment("Prolog End", 1);
        this.support.genComment("");
    }

    /**
     * Generate the MIPS code that ends every method
     */
    public void generateEpilog(Method node, String epilogLabel)
    {
        this.support.genComment("");
        this.support.genComment("Epilog Begin", 1);
        this.support.genLabel(epilogLabel);
        // get number of local variables if needed
        int numVars = 0;
        if (this.numLocalVarsMap != null) {
            if (node.getName().startsWith("Lambda")) {
                numVars = numLocalVarsMap.get(node.getName());
            } else {
                numVars = numLocalVarsMap.get(this.currentClass.getName() + "." + node.getName());
            }
        }

        this.support.genAdd("$sp", "$fp", 4 * numVars);
        this.support.setLastInstrComment("Remove space for " + numVars + " local vars");
        this.genPop("$fp");
        this.genPop("$ra");
        // remove parameters if necessary
        if (this.numLocalVarsMap != null) {
            int numParams = node.getFormalList().getSize();
            this.support.genAdd("$sp", "$sp", 4 * numParams);
        }
        // reset offsets
        this.support.setNextAvailStackOffset(0);
        this.support.genRetn();
        this.support.setLastInstrComment("Return to caller");
        this.support.genComment("Epilog End", 1);
        this.support.genComment("");
    }

    /**
     * Visit a declaration statement node
     *
     * @param node the declaration statement node
     * @return null
     */
    public Object visit(DeclStmt node)
    {
        if (this.declared) {
            String nodeName = node.getName();
//            String ref      = node.getRefName();

            Location loc = this.getLocation(null, nodeName);
            node.getInit().accept(this);

            support.genStoreWord("$v0", loc.getOffset(), loc.getBaseReg());
            support.setLastInstrComment("Assign " + loc.getOffset() + "(" + loc.getBaseReg() + ") = $v0");

            return null;
        }

        support.genComment("Declaration statement begin", 1);
        node.getInit().accept(this);

        // store the variable's value in the stack
        int offset = this.support.getNextAvailStackOffset();
        this.support.genStoreWord("$v0", offset, "$fp");
        this.support.setLastInstrComment("Store the variable \"" + node.getName() + "\"");
        // store variable's location in symbol table
        this.support.setNextAvailStackOffset(offset + this.support.getWordSize());
        this.currentClass.getVarSymbolTable().add(node.getName(), new Location("$fp", offset));

        support.genComment("Declaration statement end", 1);
        return null;
    }

    /**
     * Visit an if statement node
     *
     * @param node the if statement node
     * @return result of the visit
     */
    public Object visit(IfStmt node)
    {
        String bodyEnd  = support.getLabel();
        String elseBody = support.getLabel();
        this.currentClass.getVarSymbolTable().enterScope();

        node.getPredExpr().accept(this);

        // test if conditional
        this.support.genCondBeq("$v0", "$zero", elseBody);
        this.support.setLastInstrComment("jump over if body if false and go to else_body");

        // if body
        node.getThenStmt().accept(this);

        if (node.getElseStmt() != null) {
            this.support.genUncondBr(bodyEnd);
            this.support.setLastInstrComment("skip else and go to if_statement_body_end");

            this.support.genLabel(elseBody);
            this.support.setLastInstrComment("else_body");
            node.getElseStmt().accept(this);
        } else {
            this.support.genLabel(elseBody);
            this.support.setLastInstrComment("else_body");
        }

        this.support.genLabel(bodyEnd);
        this.support.setLastInstrComment("if_statement_body_end");
        this.currentClass.getVarSymbolTable().exitScope();

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
        if (shouldUnroll) {
            optimizedWhileVisit(node);
        } else {
            regularWhileVisit(node);
        }

        return null;
    }

    private Object regularWhileVisit(WhileStmt node)
    {
        String whileBegin = OurMipsSupport.getLabel();
        String bodyEnd    = OurMipsSupport.getLabel();
        this.loopEndStack.push(bodyEnd);
        this.currentClass.getVarSymbolTable().enterScope();

        this.support.genLabel(whileBegin);
        this.support.setLastInstrComment("while_begin");

        node.getPredExpr().accept(this);

        this.support.genCondBeq("$v0", "$zero", bodyEnd);
        this.support.setLastInstrComment("skip body if predicate evaluated to false");

        node.getBodyStmt().accept(this);

        this.support.genUncondBr(whileBegin);
        this.support.setLastInstrComment("go to re-evaluate predicate");
        this.support.genLabel(bodyEnd);
        this.support.setLastInstrComment("while_end");

        this.loopEndStack.pop();
        this.currentClass.getVarSymbolTable().exitScope();

        return null;
    }

    /**
     * The optimized version is done in the loop-inversion fashion.
     */
    private Object optimizedWhileVisit(WhileStmt node)
    {
        String whileBegin = OurMipsSupport.getLabel();
        String bodyEnd    = OurMipsSupport.getLabel();

        this.loopEndStack.push(bodyEnd);
        this.currentClass.getVarSymbolTable().enterScope();

        this.support.genComment("while begin");

        node.getPredExpr().accept(this);

        this.support.genCondBeq("$v0", "$zero", bodyEnd);
        this.support.setLastInstrComment("skip to end if predicate evaluated to false");

        this.support.genLabel(whileBegin);
        this.support.setLastInstrComment("while_begin");


        node.getBodyStmt().accept(this);
        node.getPredExpr().accept(this);

        this.support.genCondBeq("$v0", "$zero", whileBegin);

        this.support.genLabel(bodyEnd);
        this.support.setLastInstrComment("while_end");

        this.loopEndStack.pop();
        this.currentClass.getVarSymbolTable().exitScope();

        return null;
    }

    /**
     * Visit a for statement node
     *
     * @param node the for statement node
     * @return result of the visit
     */
    public Object visit(ForStmt node)
    {
        if (shouldUnroll) {
            optimizedForVisit(node);
        } else {
            regularForVisit(node);
        }

        return null;
    }

    private Object regularForVisit(ForStmt node)
    {
        String forBegin = OurMipsSupport.getLabel();
        String bodyEnd  = OurMipsSupport.getLabel();
        this.loopEndStack.push(bodyEnd);
        this.currentClass.getVarSymbolTable().enterScope();

        // for init
        if (node.getInitExpr() != null) {
            node.getInitExpr().accept(this);
        }

        // for condition
        this.support.genLabel(forBegin);
        this.support.setLastInstrComment("for_statement_begin");
        if (node.getPredExpr() != null) {
            node.getPredExpr().accept(this);
            this.support.genCondBeq("$v0", "$zero", bodyEnd);
            this.support.setLastInstrComment("go to body end if for conditional check fails");
        }

        // for body
        node.getBodyStmt().accept(this);

        // for increment/update expr
        if (node.getUpdateExpr() != null) {
            node.getUpdateExpr().accept(this);
        }

        // for repeat
        this.support.genUncondBr(forBegin);
        this.support.setLastInstrComment("jump back to beginning of for loop");

        //for body end label
        this.support.genLabel(bodyEnd);
        this.support.setLastInstrComment("for_statement_body_end");
        this.loopEndStack.pop();
        this.currentClass.getVarSymbolTable().exitScope();

        return null;
    }

    private Object optimizedForVisit(ForStmt node)
    {
        Expr init = node.getInitExpr();
        Expr pred = node.getPredExpr();
        Expr update = node.getUpdateExpr();
        Stmt body = node.getBodyStmt();

        /* If there's no way to ensure correct optimization, or in the last case,
         * if optimization is useless, don't bother optimizing */
        if (init == null || pred == null || update == null || body == null) {
            regularForVisit(node);
            return null;
        }

        /// Ensure that the loop is valid to optimize
        if (!(init instanceof AssignExpr) ||
            !(pred instanceof BinaryExpr) ||
            !(((BinaryCompExpr) pred).getLeftExpr() instanceof VarExpr) ||
            !(update instanceof UnaryExpr) ||
            !(((UnaryExpr) update).getExpr() instanceof VarExpr))
        {
            regularForVisit(node);
            return null;
        }

        // The var in question is the counter var written
        String varName = ((AssignExpr)init).getName();
        // the lh var in pred
        String predVarName = ((VarExpr) ((BinaryCompExpr)pred).getLeftExpr()).getName();

        // the var inc/decremented
        String updateVarName = ((VarExpr) ((UnaryExpr) update).getExpr()).getName();

        // Basically, we're looking for a simple for-loop structure like (i = a constant; i < another constant; ++i)
        if (!varName.equals(predVarName) &&
            !(((AssignExpr) init).getExpr() instanceof ConstIntExpr) &&
            !predVarName.equals(updateVarName) &&
            !(((BinaryCompExpr) pred).getRightExpr() instanceof ConstIntExpr))
        {
            regularForVisit(node);
            return null;
        }

        // Check to make sure that counter var (`i`) isn't modified within the body
        if (body instanceof BlockStmt) {
            for (ASTNode line : ((BlockStmt) body).getStmtList()) {
                if (isVarPotentiallyWrittenInLine(line, varName)) {
                    regularForVisit(node);
                    return null;
                }
            }
        } else {
            if (isVarPotentiallyWrittenInLine(body, varName)) {
                regularForVisit(node);
                return null;
            }
        }

        int i_init = ((ConstIntExpr) (((AssignExpr) init).getExpr())).getIntConstant();
        int i_pred = ((ConstIntExpr) ((BinaryCompExpr) pred).getRightExpr()).getIntConstant();

        /// We keep some of the same boilerplate as a regular For visit, just for consistency
        String forBegin = OurMipsSupport.getLabel();
        String bodyEnd  = OurMipsSupport.getLabel();

        this.loopEndStack.push(bodyEnd);
        this.currentClass.getVarSymbolTable().enterScope();

        this.support.genComment("for_initialization");
        // for init
        init.accept(this);

        // for condition
        this.support.genLabel(forBegin);
        this.support.setLastInstrComment("for_statement_begin (unrolled)");

        this.declared = false;
        // Write out the unrolled loop
        for (int i = i_init;
             compare((BinaryCompExpr) pred, i, i_pred);
             i = unaryOp((UnaryExpr) update, i))
        {
            this.support.genComment("for_body entered");
            body.accept(this);

            this.support.genComment("for_update entered");
            update.accept(this);

            this.declared = true;
        }
        this.declared = false;

        //for body end label
        this.support.genLabel(bodyEnd);
        this.support.setLastInstrComment("for_statement_body_end (unrolled)");
        this.loopEndStack.pop();
        this.currentClass.getVarSymbolTable().exitScope();

        return null;
    }

    private boolean declared = false;

    // var might potentially be modified in method, so forbid unrolling in this case
    private boolean isVarGivenIntoDispatch(ASTNode line, String var)
    {
        if (line instanceof ExprStmt && ((ExprStmt) line).getExpr() instanceof DispatchExpr) {
            for (ASTNode node : ((DispatchExpr) ((ExprStmt) line).getExpr()).getActualList()) {
                if (node instanceof VarExpr) {
                    if (((VarExpr) node).getName().equals(var)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Definitely not good
    private boolean isVarAssigned(ASTNode line, String var)
    {
        boolean isAssign = false;
        if (line instanceof ExprStmt) {
            isAssign = ((ExprStmt) line).getExpr() instanceof AssignExpr;
        }
        if (isAssign) {
            AssignExpr assignExpr = (AssignExpr) ((ExprStmt) line).getExpr();
            return assignExpr.getName().equals(var);
        }

        return false;
    }

    private boolean isVarPotentiallyWrittenInLine(ASTNode line, String var)
    {
        return isVarAssigned(line, var) || isVarGivenIntoDispatch(line, var);
    }

    private int unaryOp(UnaryExpr unary, int i)
    {
        if (unary instanceof UnaryDecrExpr) {
            return --i;
        } else if (unary instanceof UnaryIncrExpr) {
            return ++i;
        } else {
            System.err.println("This shouldn't have happened - forloop optimization went wrong " +
                                       "| see : UserDefinedMethodVisitor:unaryOp");
            System.exit(-1);
            return 0;
        }
    }

    private boolean compare(BinaryCompExpr compareOp, int op1, int op2)
    {
        if (compareOp instanceof BinaryCompEqExpr) {
            return op1 == op2;
        } else if (compareOp instanceof BinaryCompNeExpr) {
            return op1 != op2;
        } else if (compareOp instanceof BinaryCompGtExpr) {
            return op1 > op2;
        } else if (compareOp instanceof BinaryCompGeqExpr) {
            return op1 >= op2;
        } else if (compareOp instanceof BinaryCompLtExpr) {
            return op1 < op2;
        } else if (compareOp instanceof BinaryCompLeqExpr) {
            return op1 <= op2;
        } else {
            System.err.println("This shouldn't have happened - forloop optimization went wrong " +
                                       "| see : UserDefinedMethodVisitor:compare");
            System.exit(-1);
            return true;
        }
    }

    /**
     * Visit a break statement node
     *
     * @param node the break statement node
     * @return null
     */
    public Object visit(BreakStmt node)
    {
        this.support.genUncondBr(this.loopEndStack.peek());
        this.support.setLastInstrComment("Jump to loop body end");
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
        if (node.getExpr() != null) {
            node.getExpr().accept(this);
        }

        this.support.genUncondBr(this.currentEpilog);
        this.support.setLastInstrComment("Jump to method epilog");

        return null;
    }

    /**
     * Visit a dispatch expression node
     *
     * @param node the dispatch expression node
     * @return null
     */
    public Object visit(DispatchExpr node)
    {
        support.genComment("Dispatch expression begin", 1);
        this.genPush("$a0");

        // push parameters on the stack
        for (int i = 0; i < node.getActualList().getSize(); i++) {
            Expr arg = (Expr) node.getActualList().get(i);
            arg.accept(this);
            this.genPush("$v0");
        }

        // account for calling lambdas
        if (node.getExprType().startsWith("_")) {
            Location location = (Location) this.currentClass.getVarSymbolTable().lookup(node.getMethodName());
            support.genLoadWord("$t0", location.getOffset(), location.getBaseReg());
            support.setLastInstrComment("Load pointer to \"" + node.getMethodName() + "\" lambda to t0");
            support.genComment("Load lambda dispatch table");
            support.genLoadWord("$t0", 8, "$t0");
            support.genLoadWord("$t0", 0, "$t0");

            support.genInDirCall("$t0");
            support.setLastInstrComment("jump to address in $t0");

            this.genPop("$a0");
            support.genComment("Dispatch expression end", 1);

            return null;
        }

        // get the class containing the method to call
        ClassTreeNode refClass = currentClass;
        if (node.getRefExpr() != null) {
            // visit the reference expression
            node.getRefExpr().accept(this);
            this.checkNullPointer();
            // get the reference expression type
            refClass = currentClass.lookupClass(node.getRefExpr().getExprType());

            support.genMove("$a0", "$v0");
            support.setLastInstrComment("Set $a0 = $v0");
        }

        // if reference is "super" point to vft in parent template
        ClassTreeNode parent = currentClass.getParent();
        if (refClass.getName().equals(parent.getName())) {
            support.genLoadAddr("$t0", refClass.getName() + "_template");
            support.setLastInstrComment("Set $t0 to point to parent vft");
            support.genLoadWord("$t0", 8, "$t0");
            support.setLastInstrComment("Load address of vft to $t0");
        }
        // otherwise point to vft of reference
        else {
            support.genLoadWord("$t0", 8, "$a0");
            support.setLastInstrComment("Load address of vft to $t0");
        }

        Location loc = (Location) refClass.getMethodSymbolTable().lookup(node.getMethodName());
        support.genLoadWord("$t0", loc.getOffset(), "$t0");
        support.setLastInstrComment("Load address of desired method to $t0");

        support.genInDirCall("$t0");
        support.setLastInstrComment("jump to address in $t0");

        this.genPop("$a0");
        support.genComment("Dispatch expression end", 1);

        return null;
    }

    /**
     * Visit a new expression node
     *
     * @param node the new expression node
     * @return null
     */
    public Object visit(NewExpr node)
    {
        support.genComment("New expression begin", 1);
        this.genPush("$a0");

        support.genLoadAddr("$a0", node.getType() + "_template");
        support.setLastInstrComment("Create new " + node.getType() + " object");
        support.genDirCall("Object.clone");
        support.genMove("$a0", "$v0");
        support.setLastInstrComment("Set $a0 = $v0");
        support.genDirCall(node.getType() + "_init");
        support.setLastInstrComment("Initialize new object");
        support.genMove("$v0", "$a0");
        support.setLastInstrComment("Set $v0 = $a0");

        this.genPop("$a0");
        support.genComment("New expression end", 1);
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
        support.genComment("New array expression begin", 1);
        this.genPush("$a0");
        node.getSize().accept(this);
        this.checkArraySize();

        support.genLoadAddr("$a0", "Array_template");
        support.setLastInstrComment("Load new " + node.getType() + " array template");
        support.genStoreWord("$v0", 3 * 4, "$a0");
        support.setLastInstrComment("Store array length");
        support.genComment("Store array size");
        // size = 4*length + 16
        support.genMul("$v0", "$v0", 4);
        support.genAdd("$v0", "$v0", 16);
        support.genStoreWord("$v0", 4, "$a0");
        support.genComment("Store type id");
        String typeId = this.classEnumeration.get(node.getType() + "[]");
        support.genLoadImm("$v0", Integer.parseInt(typeId));
        support.genStoreWord("$v0", 0, "$a0");
        support.genDirCall("Object.clone");
        support.genMove("$a0", "$v0");
        support.setLastInstrComment("Set $a0 = $v0");
        support.genDirCall("Array_init");
        support.setLastInstrComment("Initialize new array");
        support.genMove("$v0", "$a0");
        support.setLastInstrComment("Set $v0 = $a0");

        this.genPop("$a0");
        support.genComment("New array expression end", 1);
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
        support.genComment("Instanceof begin", 1);
        node.getExpr().accept(this);
        support.genLoadWord("$t0", 0, "$v0");
        support.setLastInstrComment("Load ID of expression to t0");
        String typeId = this.classEnumeration.get(node.getType());
        support.genLoadImm("$t1", Integer.parseInt(typeId));
        support.setLastInstrComment("Load ID of type to t1");

        // get class referred to by type
        ClassTreeNode ctn = this.currentClass.lookupClass(node.getType());

        support.genLoadImm("$t2", ctn.getNumDescendants());
        support.setLastInstrComment("Load number of descendants of type to t2");
        support.genAdd("$t2", "$t1", "$t2");
        support.setLastInstrComment("Load type ID + numDescendants to t2");
        support.genBinaryOp("sge", "$v0", "$t0", "$t1");
        support.setLastInstrComment("Test expression ID >= type ID");

        String bodyEnd = support.getLabel();
        support.genCondBeq("$v0", "$zero", bodyEnd);
        support.setLastInstrComment("Break to instanceof_end if false");
        support.genBinaryOp("sle", "$v0", "$t0", "$t2");
        support.setLastInstrComment("Test expression ID <= (type ID + numDescendants)");
        support.genComment("instanceof_end", 1);
        support.genLabel(bodyEnd);

        return null;
    }

    /**
     * Visit a cast expression node
     *
     * @param node the cast expression node
     * @return null
     */
    public Object visit(CastExpr node)
    {
        support.genComment("Cast begin", 1);
        // if the node is a downcast, check that the class of expr is a subclass of type
        if (!node.getUpCast()) {
            // test if expr instance of type
            InstanceofExpr instanceofExpr = new InstanceofExpr(node.getLineNum(), node.getExpr(), node.getType());
            instanceofExpr.accept(this);
            // jump to class cast error if necessary
            String castError = support.getLabel();
            String castEnd   = support.getLabel();
            support.genCondBeq("$v0", "$zero", castError);
            support.setLastInstrComment("Jump to cast_error if not subclass");
            support.genUncondBr(castEnd);
            support.setLastInstrComment("Jump to cast_body_end");
            support.genLabel(castError);
            support.setLastInstrComment("cast_error");
            support.genDirCall("_class_cast_error");
            support.genComment("cast_body_end");
            support.genLabel(castEnd);
        }
        node.getExpr().accept(this);

        support.genComment("Cast end", 1);
        return null;
    }

    /**
     * Visit an assignment expression node
     *
     * @param node the assignment expression node
     * @return null
     */
    public Object visit(AssignExpr node)
    {
        String nodeName = node.getName();
        String ref      = node.getRefName();

        Location loc = this.getLocation(ref, nodeName);
        node.getExpr().accept(this);

        support.genStoreWord("$v0", loc.getOffset(), loc.getBaseReg());
        support.setLastInstrComment("Assign " + loc.getOffset() + "(" + loc.getBaseReg() + ") = $v0");

        return null;
    }

    /**
     * Visit an array assignment expression node
     *
     * @param node the array assignment expression node
     * @return null
     */
    public Object visit(ArrayAssignExpr node)
    {
        String nodeName = node.getName();
        String ref      = node.getRefName();

        support.genComment("Begin array assignment", 1);
        node.getIndex().accept(this);
        this.genPush("$v0");

        Location loc = this.getLocation(ref, nodeName);
        node.getExpr().accept(this);
        this.genPop("$t0");
        this.checkArrayStore(node);

        support.genLoadWord("$t1", loc.getOffset(), loc.getBaseReg());
        support.setLastInstrComment("Load address of array");
        this.checkArrayIndex();
        support.genMul("$t0", "$t0", 4);
        support.setLastInstrComment("Convert index to bytes");
        support.genAdd("$t0", "$t0", 16);
        support.setLastInstrComment("Skip first 4 words of array");
        support.genAdd("$t0", "$t0", "$t1");
        support.setLastInstrComment("Add index to address");
        support.genStoreWord("$v0", 0, "$t0");
        support.setLastInstrComment("Assign array element = $v0");
        support.genComment("End array assignment", 1);

        return null;
    }

    /**
     * Visit a lambda node
     *
     * @param node the lambda node
     * @return null
     */
    public Object visit(LambdaExpr node)
    {
        this.genPush("$a0");
        node.getClosure().accept(this);

        support.genLoadAddr("$a0", "Lambda_template");
        support.setLastInstrComment("Load new lambda template");
        support.genLoadAddr("$v0", this.lambdaNameMap.get(node) + "_dispatch_table");
        support.setLastInstrComment("Store lambda method");
        support.genStoreWord("$v0", 8, "$a0");
        support.genDirCall("Object.clone");
        this.genPop("$a0");

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
        this.visitBinaryChildren(node);
        support.genBinaryOp("seq", "$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 == v1");
        return null;
    }

    /**
     * Visit a binary comparison not equals expression node
     *
     * @param node the binary comparison not equals expression node
     * @return null
     */
    public Object visit(BinaryCompNeExpr node)
    {
        this.visitBinaryChildren(node);
        support.genBinaryOp("sne", "$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 != v1");
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
        this.visitBinaryChildren(node);
        support.genBinaryOp("slt", "$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 < v1");
        return null;
    }

    /**
     * Visit a binary comparison less than or equal to expression node
     *
     * @param node the binary comparison less than or equal to expression node
     * @return null
     */
    public Object visit(BinaryCompLeqExpr node)
    {
        this.visitBinaryChildren(node);
        support.genBinaryOp("sle", "$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 <= v1");
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
        this.visitBinaryChildren(node);
        support.genBinaryOp("sgt", "$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 > v1");
        return null;
    }

    /**
     * Visit a binary comparison greater than or equal to expression node
     *
     * @param node the binary comparison greater to or equal to expression node
     * @return null
     */
    public Object visit(BinaryCompGeqExpr node)
    {
        this.visitBinaryChildren(node);
        support.genBinaryOp("sge", "$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 >= v1");
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
        this.visitBinaryChildren(node);
        support.genAdd("$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 + v1");
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
        this.visitBinaryChildren(node);
        support.genSub("$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 - v1");
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
        this.visitBinaryChildren(node);
        support.genMul("$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 * v1");
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
        this.visitBinaryChildren(node);
        String divideByZero = support.getLabel();
        String divideEnd    = support.getLabel();
        support.genCondBeq("$v1", "$zero", divideByZero);
        support.setLastInstrComment("Jump to divide_by_zero if $v1 is 0");
        support.genDiv("$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 / v1");
        support.genUncondBr(divideEnd);
        support.genLabel(divideByZero);
        support.setLastInstrComment("divide_by_zero");
        support.genDirCall("_divide_zero_error");
        support.genLabel(divideEnd);
        support.setLastInstrComment("divide_end");
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
        this.visitBinaryChildren(node);
        support.genMod("$v0", "$v0", "$v1");
        support.setLastInstrComment("v0 % v1");
        return null;
    }

    /**
     * Visit a binary logical AND expression node
     *
     * @param node the binary logical AND expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicAndExpr node)
    {
        String bodyEnd = support.getLabel();

        node.getLeftExpr().accept(this);
        this.support.genCondBeq("$v0", "$zero", bodyEnd);
        this.support.setLastInstrComment("short circuit A if false, jumping over B to ||_body_end");
        node.getRightExpr().accept(this);

        // short circuited label
        this.support.genLabel(bodyEnd);
        this.support.setLastInstrComment("||_body_end");
        return null;
    }

    /**
     * Visit a binary logical OR expression node
     *
     * @param node the binary logical OR expression node
     * @return result of the visit
     */
    public Object visit(BinaryLogicOrExpr node)
    {
        String bodyEnd = support.getLabel();

        node.getLeftExpr().accept(this);
        this.support.genCondBne("$v0", "$zero", bodyEnd);
        this.support.setLastInstrComment("short circuit A if true, jumping over B to &&_body_end");
        node.getRightExpr().accept(this);

        // short circuited label
        this.support.genLabel(bodyEnd);
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
        node.getExpr().accept(this);
        this.support.genNeg("$v0", "$v0");
        this.support.setLastInstrComment("Negate contents of $v0");
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
        node.getExpr().accept(this);
        this.support.genXor("$v0", "$v0", 1);
        this.support.setLastInstrComment("Apply not operator to contents of $v0");
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
        if (node.getExpr() instanceof VarExpr) {
            this.visitUnaryVar(node, true);
        } else {
            this.visitUnaryArray(node, true);
        }

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
        if (node.getExpr() instanceof VarExpr) {
            this.visitUnaryVar(node, false);
        } else {
            this.visitUnaryArray(node, false);
        }

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
        Expr ref = node.getRef();

        ClassTreeNode currentClass = this.currentClass;
        Location      location;

        support.genComment("Begin VarExpr", 1);
        // Check for a ref expression
        if (ref != null) {
            // visit the ref expression
            ref.accept(this);
            this.checkNullPointer();
            support.genComment("    node: " + node.toString());
            int level = this.currentNumParents;
            // if the reference is to a different class, switch to that class and get its scope level
            if (!ref.getExprType().equals(this.currentClass.getName())) {
                currentClass = this.currentClass.lookupClass(ref.getExprType());
                level = currentClass.getVarSymbolTable().getCurrScopeLevel() - 1;
            }
            // if the reference is to super, go one level up
            else if (ref instanceof VarExpr && ((VarExpr) ref).getName().equals("super")) {
                level -= 1;
            }
            // get the location of the given field
            location = (Location) currentClass.getVarSymbolTable().lookup(node.getName(), level);
            support.genLoadWord("$v0", location.getOffset(), "$v0");
            support.setLastInstrComment("Load value of '" + node.getName() + "' from '" + currentClass.getName() + "' to v0");
        } else {
            support.genComment("    node: " + node.toString());
            if (node.getName().equals("this") || node.getName().equals("super")) {
                // set v0 to point to current object
                support.genMove("$v0", "$a0");
                support.setLastInstrComment("Set $v0 = $a0");
            } else {
                // set v0 to point to given variable
                location = (Location) this.currentClass.getVarSymbolTable().lookup(node.getName());
                support.genComment("Load value of \"" + node.getName() + "\" to v0");
                if (location == null) {
                    support.genLoadImm("$v0", 0);
                } else {
                    support.genLoadWord("$v0", location.getOffset(), location.getBaseReg());
                }
            }
        }
        support.genComment("End VarExpr", 1);
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
        Expr ref = node.getRef();

        ClassTreeNode currentClass = this.currentClass;
        Location      location;

        support.genComment("Begin ArrayExpr", 1);

        node.getIndex().accept(this);
        support.genComment("Push index");
        this.genPush("$v0");

        // Check for a ref expression
        if (ref != null) {
            // visit the ref expression
            ref.accept(this);
            this.checkNullPointer();
            int level = this.currentNumParents;
            // if the reference is to a different class, switch to that class and get its scope level
            if (!ref.getExprType().equals(currentClass.getName())) {
                currentClass = this.currentClass.lookupClass(ref.getExprType());
                level = currentClass.getVarSymbolTable().getCurrScopeLevel() - 1;
            }
            // if the reference is to super, go one level up
            else if (ref instanceof VarExpr && ((VarExpr) ref).getName().equals("super")) {
                level -= 1;
            }
            // get the location of the given field
            location = (Location) currentClass.getVarSymbolTable().lookup(node.getName(), level);
            support.genComment("Pop index");
            this.genPop("$t0");
            support.genLoadWord("$t1", location.getOffset(), "$v0");
            support.setLastInstrComment("Load address of array " + node.getName() + " to $t1");
            this.checkArrayIndex();
            support.genMul("$t0", "$t0", 4);
            support.setLastInstrComment("Convert index to bytes");
            support.genAdd("$t0", "$t0", "$t1");
            support.setLastInstrComment("Add index to address");
            support.genAdd("$t0", "$t0", 16);
            support.setLastInstrComment("Skip first 4 words of array");
            support.genLoadWord("$v0", 0, "$t0");
            support.setLastInstrComment("Load desired element of array to $v0");
        } else {
            support.genComment("Pop index");
            this.genPop("$t0");
            location = (Location) this.currentClass.getVarSymbolTable().lookup(node.getName());
            if (location == null) {
                support.genLoadImm("$v0", 0);
                support.setLastInstrComment(node.getName() + " is null");
            } else {
                support.genLoadWord("$t1", location.getOffset(), location.getBaseReg());
                support.setLastInstrComment("Load address of array");
                this.checkArrayIndex();
                support.genMul("$t0", "$t0", 4);
                support.setLastInstrComment("Convert index to bytes");
                support.genAdd("$t0", "$t0", 16);
                support.setLastInstrComment("Skip first 4 words in array");
                support.genAdd("$t0", "$t0", "$t1");
                support.setLastInstrComment("Add index to address");
                support.genLoadWord("$v0", 0, "$t0");
                support.setLastInstrComment("Load desired element of array to $v0");
            }
        }

        support.genComment("End ArrayExpr", 1);
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
        this.support.genLoadImm("$v0", node.getIntConstant());
        this.support.setLastInstrComment("Load constant integer " + node.getIntConstant() + " to $v0");
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
        int bool = node.getConstant().equals("false") ? 0 : 1;
        this.support.genLoadImm("$v0", bool);
        this.support.setLastInstrComment("Load constant boolean " + node.getConstant() + " to $v0");
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
        this.support.genLoadAddr("$v0", this.stringsMap.get(node.getConstant()));
        this.support.setLastInstrComment("Load constant String " + node.getConstant() + " to $v0");
        return null;
    }

    /**
     * Visit a unary expression incrementing or decrementing a variable
     *
     * @param node      the UnaryExpr node
     * @param increment true if increment, false if decrement
     */
    private void visitUnaryVar(UnaryExpr node, boolean increment)
    {
        this.support.genComment("Begin unary expression", 1);
        node.getExpr().accept(this);
        if (increment) {
            this.support.genAdd("$t0", "$v0", 1);
            this.support.setLastInstrComment("Increment contents of $v0");
        } else {
            this.support.genSub("$t0", "$v0", 1);
            this.support.setLastInstrComment("Decrement contents of $v0");
        }
        // Get var location, assuming reference is super, this, or null
        VarExpr  varExpr = (VarExpr) node.getExpr();
        VarExpr  refExpr = (VarExpr) varExpr.getRef();
        Location loc;
        if (refExpr == null) {
            loc = this.getLocation(null, varExpr.getName());
        } else {
            loc = this.getLocation(refExpr.getName(), varExpr.getName());
        }

        this.support.genStoreWord("$t0", loc.getOffset(), loc.getBaseReg());
        this.support.setLastInstrComment("Store the incremented result");

        if (!node.isPostfix()) {
            this.support.genMove("$v0", "$t0");
            this.support.setLastInstrComment("Store modified value back in v0");
        }
        this.support.genComment("End unary expression", 1);
    }

    /**
     * Visit a unary expression incrementing or decrementing an element of an array
     *
     * @param node      the UnaryExpr node
     * @param increment true if increment, false if decrement
     */
    private void visitUnaryArray(UnaryExpr node, boolean increment)
    {
        node.getExpr().accept(this);
        this.support.setLastInstrComment("Begin unary array expression");
        this.genPush("$v0");

        if (increment) {
            this.support.genAdd("$v0", "$v0", 1);
            this.support.setLastInstrComment("Increment value of $v0");
        } else {
            this.support.genSub("$v0", "$v0", 1);
            this.support.setLastInstrComment("Decrement value of $v0");
        }
        this.genPush("$v0");

        // Get var location, assuming reference is super, this, or null
        ArrayExpr arrayExpr = (ArrayExpr) node.getExpr();
        VarExpr   refExpr   = (VarExpr) arrayExpr.getRef();
        Location  loc;
        if (refExpr == null) {
            loc = this.getLocation(null, arrayExpr.getName());
        } else {
            loc = this.getLocation(refExpr.getName(), arrayExpr.getName());
        }

        arrayExpr.getIndex().accept(this);
        this.support.genMove("$t0", "$v0");
        this.support.setLastInstrComment("Move index to $t0");
        this.genPop("$v0");

        support.genLoadWord("$t1", loc.getOffset(), loc.getBaseReg());
        support.setLastInstrComment("Load address of array");
        this.checkArrayIndex();
        support.genMul("$t0", "$t0", 4);
        support.setLastInstrComment("Convert index to bytes");
        support.genAdd("$t0", "$t0", 16);
        support.setLastInstrComment("Skip first 4 words of array");
        support.genAdd("$t0", "$t0", "$t1");
        support.setLastInstrComment("Add index to address");
        support.genStoreWord("$v0", 0, "$t0");
        support.setLastInstrComment("Assign array element = $v0");
        this.genPop("$t0");

        if (!node.isPostfix()) {
            this.support.genMove("$v0", "$t0");
            this.support.setLastInstrComment("Store incremented value back in v0");
        }

        support.genComment("End unary array expression", 1);
    }

    /**
     * Visit left and right expr of binary node, placing results in v1 and v0 respectively
     *
     * @param node the BinaryExpr node being visited
     */
    private void visitBinaryChildren(BinaryExpr node)
    {
        node.getLeftExpr().accept(this);
        this.genPush("$v0");
        node.getRightExpr().accept(this);
        support.genMove("$v1", "$v0");
        support.setLastInstrComment("Set $v1 = $v0");
        this.genPop("$v0");
    }

    /**
     * Push given register onto the stack
     *
     * @param src the register to push
     */
    private void genPush(String src)
    {
        this.support.genComment("push " + src);
        this.support.genAdd("$sp", "$sp", -4);
        this.support.genStoreWord(src, 0, "$sp");
    }

    /**
     * Pop the top of the stack into the given register
     *
     * @param dest the register to pop into
     */
    private void genPop(String dest)
    {
        this.support.genComment("pop into " + dest);
        this.support.genLoadWord(dest, 0, "$sp");
        this.support.genSub("$sp", "$sp", -4);
    }

    /**
     * Generate code to check for an array index exception
     * Assumes index is in $t0, array address is in $t1
     */
    private void checkArrayIndex()
    {
        String arrayIndexError = this.support.getLabel();
        String arrayIndexEnd   = this.support.getLabel();
        this.support.genComment("Jump to array index error if index greater than size", 2);
        this.support.genLoadWord("$t2", 12, "$t1");
        this.support.genCondBgeq("$t0", "$t2", arrayIndexError);
        this.support.genCondBlt("$t0", "$zero", arrayIndexError);
        this.support.genUncondBr(arrayIndexEnd);
        this.support.genLabel(arrayIndexError);
        this.support.genDirCall("_array_index_error");
        this.support.genLabel(arrayIndexEnd);
    }

    /**
     * Generate code to check for an array store exception
     */
    private void checkArrayStore(ArrayAssignExpr node)
    {
        String arrayBaseType = node.getExprType();
        // if base type is primitive, no store error possible
        if (arrayBaseType.equals("int") || arrayBaseType.equals("boolean")) {
            return;
        }
        this.support.genComment("Begin array store error check", 1);
        this.genPush("$t0");
        this.genPush("$t1");
        this.genPush("$v0");
        String arrayStoreError = this.support.getLabel();
        String arrayStoreEnd   = this.support.getLabel();
        String arrayId         = this.classEnumeration.get(arrayBaseType);
        support.genLoadImm("$t0", Integer.parseInt(arrayId));
        String exprId = this.classEnumeration.get(node.getExpr().getExprType());
        support.genLoadImm("$t1", Integer.parseInt(exprId));
        support.setLastInstrComment("Load ID of expression to t1");

        support.genLoadImm("$t2", this.root.lookupClass(arrayBaseType).getNumDescendants());
        support.setLastInstrComment("Load number of descendants of type to t2");
        support.genAdd("$t2", "$t1", "$t2");
        support.setLastInstrComment("Load type ID + numDescendants to t2");
        support.genBinaryOp("sge", "$v0", "$t0", "$t1");
        support.setLastInstrComment("Test expression ID >= type ID");
        support.genCondBeq("$v0", "$zero", arrayStoreError);
        support.setLastInstrComment("Jump to array_store_error if false");
        support.genBinaryOp("sle", "$v0", "$t0", "$t2");
        support.setLastInstrComment("Test expression ID <= (type ID + numDescendants)");
        support.genCondBeq("$v0", "$zero", arrayStoreError);
        support.setLastInstrComment("Jump to array_store_error if false");

        support.genUncondBr(arrayStoreEnd);
        support.setLastInstrComment("Jump to array_store_end");
        support.genLabel(arrayStoreError);
        support.setLastInstrComment("array_store_error");
        support.genDirCall("_array_store_error");
        support.genLabel(arrayStoreEnd);
        support.setLastInstrComment("array_store_end");

        this.genPop("$v0");
        this.genPop("$t1");
        this.genPop("$t0");
        this.support.genComment("End array store error check", 1);
    }

    /**
     * Get the location of the variable refName.nodeName (assuming refName is "this" or "super")
     *
     * @param refName  "this" or "super"
     * @param nodeName the name of the field to access
     * @return the location of the variable
     */
    private Location getLocation(String refName, String nodeName)
    {
        SymbolTable symbolTable = this.currentClass.getVarSymbolTable();
        Location    loc         = (Location) symbolTable.lookup(nodeName);
        // check if expression has reference (this or super)
        if (refName != null) {
            if (refName.equals("this")) {
                // get field of current class
                loc = (Location) symbolTable.lookup(nodeName, this.currentNumParents);
            } else if (refName.equals("super")) {
                // get field of parent class
                symbolTable = currentClass.getParent().getVarSymbolTable();
                loc = (Location) symbolTable.lookup(nodeName);
            }
        }

        return loc;
    }

    /**
     * Generate code to check for an array size exception
     * Assumes size is in $v0
     */
    private void checkArraySize()
    {
        String arraySizeError = this.support.getLabel();
        String arraySizeEnd   = this.support.getLabel();
        this.support.genMove("$t0", "$v0");
        this.support.genComment("Jump to array size error if size is invalid", 2);
        this.support.genCondBlt("$t0", "$zero", arraySizeError);
        if (this.gc) {
            this.support.genLoadImm("$t1", 1500);
            this.support.genCondBgeq("$t0", "$t1", arraySizeError);
        }
        this.support.genUncondBr(arraySizeEnd);
        this.support.genLabel(arraySizeError);
        this.support.genDirCall("_array_size_error");
        this.support.genLabel(arraySizeEnd);
    }

    /**
     * Generate code to check for a null pointer exception
     */
    private void checkNullPointer()
    {
        String nullPointerError = this.support.getLabel();
        String nullPointerEnd   = this.support.getLabel();
        this.support.genComment("Jump to null pointer error if reference is null", 3);
        this.support.genCondBeq("$v0", "$zero", nullPointerError);
        this.support.genUncondBr(nullPointerEnd);
        this.support.genLabel(nullPointerError);
        this.support.genDirCall("_null_pointer_error");
        this.support.genLabel(nullPointerEnd);
    }
}
