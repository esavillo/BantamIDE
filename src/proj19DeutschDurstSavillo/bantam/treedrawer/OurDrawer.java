package proj19DeutschDurstSavillo.bantam.treedrawer;

import proj19DeutschDurstSavillo.bantam.ast.ASTNode;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.interfaces.PostParseActor;

import java.util.Map;

public class OurDrawer
        extends Drawer
        implements PostParseActor

{
    @Override
    public Object act(ASTNode node, Map<String, Object> knowledge)
    {
        this.draw(knowledge.get("filename").toString(), (Program) node);

        return "Scan and parse successful!";
    }
}
