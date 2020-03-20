package proj19DeutschDurstSavillo;

import proj19DeutschDurstSavillo.bantam.ast.ASTNode;
import proj19DeutschDurstSavillo.interfaces.ASTObserver;

import java.util.ArrayList;


/**
 * Broadcasts a message to all registered objects.
 */
public class ASTBroadcaster
{
    private ArrayList<ASTObserver> subscribers;

    public ASTBroadcaster()
    {
        this.subscribers = new ArrayList<>();
    }

    /**
     * Register a subscriber object to the list of objects which will be updated
     * when something (an AST) is broadcast
     *
     * @param subscriber - the object to be registered
     */
    public void register(ASTObserver subscriber)
    {
        if (subscriber != null) {
            this.subscribers.add(subscriber);
        }
    }

    /**
     * Update all subscribers with the given AST
     *
     * @param currentAST - the AST to broadcast
     */
    public void broadcast(ASTNode currentAST)
    {
        this.subscribers.forEach(astObserver -> astObserver.update(currentAST));
    }

}
