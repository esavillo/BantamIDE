/*
File: StructureViewController.java
CS361 Project 10
Names: Melody Mao, Zena Abulhab, Yi Feng, and Evan Savillo
Date: 12/6/2018
*/

package proj19DeutschDurstSavillo.controllers;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import proj19DeutschDurstSavillo.ASTBroadcaster;
import proj19DeutschDurstSavillo.CodeTabPane;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.bantam.parser.Parser;
import proj19DeutschDurstSavillo.bantam.semant.CodeStructureVisitor;
import proj19DeutschDurstSavillo.bantam.util.ErrorHandler;
import proj19DeutschDurstSavillo.editor.CustomCodeArea;

import java.util.HashMap;
import java.util.Map;


/**
 * Controller that manages the generation and display of the structure of the
 * java code in the file currently being viewed.
 */
public class StructureViewController
{
    private Map<TreeItem, Integer> treeItemLineNumMap;
    private TreeView<String>       treeView;
    private StructureViewWorker    structureViewWorker;
    private ASTBroadcaster         astBroadcaster;

    /**
     * Constructor for this class
     */
    public StructureViewController(ASTBroadcaster astBroadcaster)
    {
        this.treeItemLineNumMap = new HashMap<>();
        this.structureViewWorker = new StructureViewWorker();

        this.astBroadcaster = astBroadcaster;

        this.structureViewWorker.setOnSucceeded(event ->
                                                {
                                                    Program program =
                                                            ((StructureViewWorker) event.getSource()).getValue();

                                                    if (program != null) {

                                                        CodeStructureVisitor codeStructureVisitor =
                                                                new CodeStructureVisitor(treeView.getRoot(),
                                                                                         treeItemLineNumMap);

                                                        codeStructureVisitor.visit(program);

                                                        this.astBroadcaster.broadcast(program);
                                                    }
                                                });

        this.structureViewWorker.setOnCancelled(event ->
                                                {
                                                    ((StructureViewWorker) event.getSource()).resetFields();
                                                });

    }

    /**
     * Takes in the fxml item treeView from main Controller.
     *
     * @param treeView TreeView item representing structure display
     */
    public void setTreeView(TreeView<String> treeView)
    {
        this.treeView = treeView;
    }

    /**
     * Handles when the user clicks on the file structure tree view
     *
     * @param tabPane the tab pane
     */
    public void handleTreeItemClicked(CodeTabPane tabPane)
    {
        TreeItem       selectedTreeItem = this.treeView.getSelectionModel().getSelectedItem();
        CustomCodeArea currentCodeArea  = tabPane.getCurrentCodeArea();
        if (selectedTreeItem != null) {
            int lineNum = this.getTreeItemLineNum(selectedTreeItem);
            if (currentCodeArea != null) {
                currentCodeArea.showParagraphAtTop(lineNum - 1);
                currentCodeArea.moveTo(lineNum - 1, 0);
                currentCodeArea.selectLine();
            }
        }
    }

    /**
     * Returns the line number currently associated with the specified tree item
     *
     * @param treeItem Which TreeItem to get the line number of
     * @return the line number corresponding with that tree item
     */
    private Integer getTreeItemLineNum(TreeItem treeItem)
    {
        return this.treeItemLineNumMap.get(treeItem);
    }

    /**
     * Parses a file thereby storing contents as TreeItems in our special tree.
     *
     * @param fileName the file to be parsed
     */
    public void generateStructureTree(String fileName)
    {
        try {
            this.structureViewWorker.cancel();

            TreeItem<String> newRoot = new TreeItem<>(fileName);
            this.setRootNode(newRoot);

            this.structureViewWorker.setFileContents(fileName);

            this.structureViewWorker.reset();
            this.structureViewWorker.restart();
        } catch (Exception e) {
            // structure tree generation failed
        }
    }


    /**
     * Sets the currently displaying File TreeItem<String> View.
     *
     * @param root root node corresponding to currently displaying file
     */
    private void setRootNode(TreeItem<String> root)
    {
        this.treeView.setRoot(root);
        this.treeView.setShowRoot(false);
    }

    /**
     * Sets the currently displaying file to nothing.
     */
    public void resetRootNode()
    {
        this.setRootNode(null);
    }

    /**
     * An implementation of a Worker which parses in another thread
     */
    protected class StructureViewWorker extends Service<Program>
    {
        private String fileName;

        public void setFileContents(String fileName)
        {
            this.fileName = fileName;
        }

        public void resetFields()
        {
            this.fileName = null;
        }

        /**
         * Lexes and parses file set as input
         *
         * @return root node of completed parse tree
         */
        @Override
        protected Task<Program> createTask()
        {
            return new Task<Program>()
            {
                @Override
                protected Program call()
                {
                    ErrorHandler errorHandler = new ErrorHandler();
                    Parser       parser       = new Parser(errorHandler);

                    Program p = null;

                    try {
                        p = parser.parse(fileName);
                    } catch (Exception e) {
                        // parsing failed
//                        System.out.println(e.getMessage());
                    }

                    return p;
                }
            };
        }
    }
}