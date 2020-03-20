package proj19DeutschDurstSavillo.interfaces;

import proj19DeutschDurstSavillo.bantam.ast.ASTNode;


/**
 * Listens for the broadcast of an AST
 */
public interface ASTObserver
{
    /**
     * Receive an AST
     *
     * @param currentAST - the broadcast AST
     */
    void update(ASTNode currentAST);
}
