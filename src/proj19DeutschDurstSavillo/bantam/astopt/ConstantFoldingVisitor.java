/*
 * File: ConstantFoldingVisitor.java

 * S19 CS461 Project 19
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 5/16/19
 * This file contains a visitor that folds constant integers
 */

package proj19DeutschDurstSavillo.bantam.astopt;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.parser.Parser;
import proj19DeutschDurstSavillo.bantam.semant.SemanticAnalyzer;
import proj19DeutschDurstSavillo.bantam.treedrawer.Drawer;
import proj19DeutschDurstSavillo.bantam.util.CompilationException;
import proj19DeutschDurstSavillo.bantam.util.ErrorHandler;

/**
 * ConstantFoldingVisitor visits an AST node and simplifies expressions with constants
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class ConstantFoldingVisitor extends OptimizerVisitor {

    private boolean fold;
    private boolean modified;

    /**
     * Perform constant folding on given AST
     *
     * @param root the AST to optimize
     * @return the AST with a round of constant folding applied
     */
    public Program constantFoldAST(Program root) {
        this.fold = true;
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
     * Visit a while statement node
     *
     * @param node the while statement node
     * @return result of the visit
     */
    public Object visit(WhileStmt node) {
        this.fold = false;
        Expr pred = (Expr) node.getPredExpr().accept(this);
        this.fold = true;
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
        this.fold = false;
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
        this.fold = true;

        Stmt body = (Stmt) node.getBodyStmt().accept(this);
        return new ForStmt(node.getLineNum(), init, pred, update, body);
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();
                boolean b = leftInt.equals(rightInt);

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(), Boolean.toString(b));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();
                boolean b = !leftInt.equals(rightInt);

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(), Boolean.toString(b));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();
                boolean b = leftInt < rightInt;

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(), Boolean.toString(b));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();
                boolean b = leftInt <= rightInt;

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(), Boolean.toString(b));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();
                boolean b = leftInt > rightInt;

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(), Boolean.toString(b));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();
                boolean b = leftInt >= rightInt;

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(), Boolean.toString(b));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();

                ConstIntExpr constIntExpr = new ConstIntExpr(node.getLineNum(),
                        Integer.toString(leftInt + rightInt));
                constIntExpr.setExprType(node.getExprType());
                return constIntExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();

                ConstIntExpr constIntExpr = new ConstIntExpr(node.getLineNum(),
                        Integer.toString(leftInt - rightInt));
                constIntExpr.setExprType(node.getExprType());
                return constIntExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();

                ConstIntExpr constIntExpr = new ConstIntExpr(node.getLineNum(),
                        Integer.toString(leftInt * rightInt));
                constIntExpr.setExprType(node.getExprType());
                return constIntExpr;
            }
        }

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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                int leftInt = ((ConstIntExpr) left).getIntConstant();
                int rightInt = ((ConstIntExpr) right).getIntConstant();
                if (rightInt != 0) {
                    ConstIntExpr constIntExpr = new ConstIntExpr(node.getLineNum(),
                            Integer.toString(leftInt / rightInt));
                    constIntExpr.setExprType(node.getExprType());
                    this.modified = true;
                    return constIntExpr;
                }
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstIntExpr && right instanceof ConstIntExpr) {
                this.modified = true;
                Integer leftInt = ((ConstIntExpr) left).getIntConstant();
                Integer rightInt = ((ConstIntExpr) right).getIntConstant();

                ConstIntExpr constIntExpr = new ConstIntExpr(node.getLineNum(),
                        Integer.toString(leftInt % rightInt));
                constIntExpr.setExprType(node.getExprType());
                return constIntExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstBooleanExpr && right instanceof ConstBooleanExpr) {
                this.modified = true;
                boolean leftBool = Boolean.getBoolean(((ConstBooleanExpr) left).getConstant());
                boolean rightBool = Boolean.getBoolean(((ConstBooleanExpr) right).getConstant());

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(),
                        Boolean.toString(leftBool && rightBool));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
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
        if (this.fold) {
            if (left instanceof ConstBooleanExpr && right instanceof ConstBooleanExpr) {
                this.modified = true;
                boolean leftBool = Boolean.getBoolean(((ConstBooleanExpr) left).getConstant());
                boolean rightBool = Boolean.getBoolean(((ConstBooleanExpr) right).getConstant());

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(),
                        Boolean.toString(leftBool || rightBool));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
        BinaryLogicOrExpr binaryLogicOrExpr = new BinaryLogicOrExpr(node.getLineNum(), left, right);
        binaryLogicOrExpr.setExprType(node.getExprType());
        return binaryLogicOrExpr;
    }

    /**
     * Visit a unary NOT expression node
     *
     * @param node the unary NOT expression node
     * @return result of the visit
     */
    public Object visit(UnaryNotExpr node) {
        Expr bool = (Expr) node.getExpr().accept(this);
        if (this.fold) {
            if (bool instanceof ConstBooleanExpr) {
                this.modified = true;
                boolean boolConst = Boolean.getBoolean(((ConstBooleanExpr) bool).getConstant());

                ConstBooleanExpr constBooleanExpr = new ConstBooleanExpr(node.getLineNum(),
                        Boolean.toString(!boolConst));
                constBooleanExpr.setExprType(node.getExprType());
                return constBooleanExpr;
            }
        }
        UnaryNotExpr unaryNotExpr = new UnaryNotExpr(node.getLineNum(), bool);
        unaryNotExpr.setExprType(node.getExprType());
        return unaryNotExpr;
    }

    public static void main(String[] args) {
        //make sure at least one filename was given
        if (args.length < 1) {
            System.err.println("Missing input filename");
            System.exit(-1);
        }

        ErrorHandler errorHandler = new ErrorHandler();
        Parser parser = new Parser(errorHandler);
        SemanticAnalyzer analyzer = new SemanticAnalyzer(errorHandler);
        ConstantPropVisitor constantPropVisitor = new ConstantPropVisitor();
        ConstantFoldingVisitor constantFoldingVisitor = new ConstantFoldingVisitor();

        for (String filename : args) {
            System.out.println("Scanning and Parsing file: " + filename + "\n");

            // parse and analyze
            try {
                Program program = parser.parse(filename);
                analyzer.analyze(program);
                do {
                    program = constantPropVisitor.constantPropAST(program);
                    program = constantFoldingVisitor.constantFoldAST(program);
                }
                while (constantFoldingVisitor.madeModifications());
                Drawer drawer = new Drawer();
                drawer.draw(filename, program);
            } catch (CompilationException e) {
                System.out.println("Parsing error encountered:\n");
            }

            // check for errors
            if (errorHandler.errorsFound()) {
                errorHandler.getErrorList().forEach(error -> System.out.println(error.toString()));
                System.out.println(String.format("\n%d errors found", errorHandler.getErrorList().size()));
            } else {
                System.out.println("\nParsing and semantic analysis successful");
            }

            System.out.println("-----------------------------------------------");

            //clear errors to parse next file
            errorHandler.clear();
        }
    }
}