/* Bantam Java Compiler Extension

    Martin Deutsch
    Robert Durst
    Evan Savillo
*/

package proj19DeutschDurstSavillo.bantam.ast;

import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

/**
 * The <tt>LambdaExpr</tt> class represents a lambda expression
 * a class declaration.  It contains a list of captured values (<tt>closure</tt>),
 * a list of formal parameters (<tt>formalList</tt>), the return type of the
 * lambda (<tt>returnType</tt>), and a list of statements from the lambda body
 * (<tt>stmtList</tt>).
 *
 * @see ASTNode
 */
public class LambdaExpr extends Expr {
    /**
     * The return type of the lambda
     */
    protected String returnType;

    /**
     * A list of formal parameters
     */
    protected FormalList formalList;

    /**
     * A list of closures
     */
    protected Closure closure;

    /**
     * A list of statements appearing in the lambda body
     */
    protected StmtList stmtList;

    /**
     * Method constructor
     *
     * @param lineNum    source line number corresponding to this AST node
     * @param returnType the return type of this lambda
     * @param formalList a list of formal parameters
     * @param closure    a list of closures
     * @param stmtList   a list of statements appearing in the lambda body
     */
    public LambdaExpr(int lineNum, String returnType,
                  FormalList formalList, Closure closure,
                  StmtList stmtList) {
        super(lineNum);
        this.returnType = returnType;
        this.formalList = formalList;
        this.closure = closure;
        this.stmtList = stmtList;

        String exprType = "_" + this.returnType + "(";
        for (ASTNode f : formalList) {
            exprType += ((Formal) f).getType() + ",";
        }

        this.setExprType(exprType.substring(0,exprType.length()-1) + ")");
    }

    /**
     * Get the return type of this lambda
     *
     * @return return type of lambda
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Get list of formal parameters
     *
     * @return list of formal parameters
     */
    public FormalList getFormalList() {
        return formalList;
    }

    /**
     * Get the closure of this lambda
     *
     * @return method name
     */
    public Closure getClosure() {
        return closure;
    }

    /**
     * Get list of statements from lambda body
     *
     * @return list of statements
     */
    public StmtList getStmtList() {
        return stmtList;
    }

    /**
     * Visitor method
     *
     * @param v bantam.visitor object
     * @return result of visiting this node
     * @see bantam.visitor.Visitor
     */
    public Object accept(Visitor v) {
        return v.visit(this);
    }
}
