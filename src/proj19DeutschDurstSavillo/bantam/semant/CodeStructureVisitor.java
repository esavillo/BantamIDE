package proj19DeutschDurstSavillo.bantam.semant;

import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import proj19DeutschDurstSavillo.bantam.ast.Class_;
import proj19DeutschDurstSavillo.bantam.ast.Field;
import proj19DeutschDurstSavillo.bantam.ast.Method;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;


/**
 * Private helper class that listens for code structure declarations
 * (classes, fields, methods) during a parse tree walk and builds a
 * TreeView subtree representing the code structure.
 */
public class CodeStructureVisitor extends Visitor
{
    private Image classPic;
    private Image methodPic;
    private Image fieldPic;
    private TreeItem<String>       currentNode;
    private Map<TreeItem, Integer> treeItemIntegerMap;

    /**
     * creates a new CodeStructureListener that builds a subtree
     * from the given root TreeItem
     *
     * @param root root TreeItem to build subtree from
     */
    public CodeStructureVisitor(TreeItem<String> root, Map<TreeItem, Integer> treeItemIntegerMap)
    {
        this.currentNode = root;
        this.treeItemIntegerMap = treeItemIntegerMap;

        String path = "/proj19DeutschDurstSavillo/resources/icons/";
        try {
            this.classPic = new Image(new FileInputStream(
                    getClass().getResource(path + "c.png").getFile()));
            this.methodPic = new Image(new FileInputStream(
                    getClass().getResource(path + "m.png").getFile()));
            this.fieldPic = new Image(new FileInputStream(
                    getClass().getResource(path + "f.png").getFile()));
        } catch (IOException e) {
            // error loading images
        }
    }

    /**
     * Starts a new subtree for the class declaration entered
     */
    public Object visit(Class_ node)
    {
        if (currentNode == null) {
            return null;
        }

        //get class name
        String className = node.getName();

        //add class to TreeView under the current class tree
        //set up the icon
        //store the line number of its declaration
        TreeItem<String> newNode = new TreeItem<>(className);
        newNode.setGraphic(new ImageView(this.classPic));
        newNode.setExpanded(true);
        this.currentNode.getChildren().add(newNode);
        this.currentNode = newNode; //move current node into new subtree
        this.treeItemIntegerMap.put(newNode, node.getLineNum());

        node.getMemberList().accept(this);
        this.currentNode = this.currentNode.getParent(); //move current node back to parent

        return null;
    }

    /**
     * adds a child node for the field entered under the TreeItem for the current class
     */
    @Override
    public Object visit(Field node)
    {
        //get field name
        String fieldName = node.getName();

        //add field to TreeView under the current class tree
        //set up the icon
        //store the line number of its declaration
        TreeItem<String> newNode = new TreeItem<>(fieldName);
        newNode.setGraphic(new ImageView(this.fieldPic));
        this.currentNode.getChildren().add(newNode);
        this.treeItemIntegerMap.put(newNode, node.getLineNum());

        return null;
    }

    /**
     * adds a child node for the method entered under the TreeItem for the current class
     */
    @Override
    public Object visit(Method node)
    {
        //get method name
        String methodName = node.getName();

        //add method to TreeView under the current class tree
        //set up the icon
        //store the line number of its declaration
        TreeItem<String> newNode = new TreeItem<>(methodName);
        newNode.setGraphic(new ImageView(this.methodPic));
        this.currentNode.getChildren().add(newNode);
        this.treeItemIntegerMap.put(newNode, node.getLineNum());

        return null;
    }
}
