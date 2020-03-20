/* Bantam Java Compiler Extension

    Martin Deutsch
    Robert Durst
    Evan Savillo
*/

package proj19DeutschDurstSavillo.bantam.ast;

import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

/**
 * The <tt>Closure</tt> class represents a list of var expr's utilized within an enclosre.
 *
 * @see ListNode
 * @see VarExpr
 * @see LambdaExpr
 */
public class Closure extends ListNode {
    /**
     * Expr list constructor
     *
     * @param lineNum source line number corresponding to this AST node
     */
    public Closure(int lineNum) {
        super(lineNum);
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
