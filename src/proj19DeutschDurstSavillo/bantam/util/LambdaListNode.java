package proj19DeutschDurstSavillo.bantam.util;

import proj19DeutschDurstSavillo.bantam.ast.LambdaExpr;

import java.util.ArrayList;


public class LambdaListNode
{
    private LambdaExpr astNode;
    private ArrayList<SymbolTable> closure;
    private boolean emptyClosure = false;

    public LambdaListNode(LambdaExpr astNode)
    {
        this.astNode = astNode;
        this.closure = new ArrayList<>();
    }

    public LambdaListNode(LambdaExpr astNode, SymbolTable closure)
    {
        this.astNode = astNode;
        this.closure = new ArrayList<>();
        this.closure.add(closure);
    }

    public void addClosure(SymbolTable closure)
    {
        this.closure.add(closure);
    }

    public void setEmptyClosure(boolean b)
    {
        this.emptyClosure = b;
    }

    public Object lookup(String s) {
        if (this.emptyClosure) {
            // Although the closure is empty, we made sure to pass it the vartable of 'this'
            SymbolTable symtable = this.closure.get(0);
            return symtable.peek(s);
        }

        for (SymbolTable capturedScope : this.closure) {
            Object found = capturedScope.lookup(s);

            if (found != null) {
                return found;
            }
        }

        return null;
    }

}
