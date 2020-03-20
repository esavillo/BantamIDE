/*
 * File: AutoCompletionStrategy.java
 * S19 CS461 Project 13
 * Names: Evan Savillo, Rob Durst, Martin Deutsch
 * Date: 3/8/19
 * This file contains the KeyPressStrategy for displaying autocomplete suggestions
 */

package proj19DeutschDurstSavillo.editor;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.semant.TypeCheckerVisitor;
import proj19DeutschDurstSavillo.bantam.util.ClassTreeNode;
import proj19DeutschDurstSavillo.bantam.util.ErrorHandler;
import proj19DeutschDurstSavillo.interfaces.KeyPressStrategy;

import java.util.*;


/**
 * A strategy to show autocomplete suggestions on each key press
 *
 * @author Evan Savillo
 * @author Rob Durst
 * @author Martin Deutsch
 */
public class AutoCompletionStrategy implements KeyPressStrategy
{
    private CustomCodeArea customCodeArea = null;
    private double[]       menuLocation   = {0.0, 0.0};
    private String         currentWord;

    /**
     * Run the autocomplete suggester
     *
     * @param customCodeArea the current code area
     * @param event        the key typed event
     */
    @Override
    public void execute(CustomCodeArea customCodeArea, KeyEvent event)
    {
        this.customCodeArea = customCodeArea;

        Hashtable<String, ClassTreeNode> classMap = this.customCodeArea.semanticAnalyzer.getClassMap();
        ContextMenu                      menu     = this.customCodeArea.autoCompleteMenu;

        if (classMap == null) {
            return;
        }

        menu.getItems().clear();

        // set the current word
        this.setCurrentWord(event);

        this.populateAutoCompleteMenu(menu.getItems(), this.getMatches(classMap));

        Optional<Bounds> bounds = customCodeArea.getCaretBounds();
        if (bounds.isPresent() && !menu.isShowing()) {
            this.menuLocation[0] = bounds.get().getMinX();
            this.menuLocation[1] = bounds.get().getMaxY();
        }

        menu.show(customCodeArea, this.menuLocation[0], this.menuLocation[1]);
    }

    /**
     * Determine available variables and methods that match typed word
     *
     * @param classMap the class name to ClassTreeNode map from the semantic analyzer
     * @return a map of all possible strings to that index at which the current word first matches
     */
    private Map<Integer, ArrayList<String>> getMatches(Hashtable<String, ClassTreeNode> classMap)
    {
        ArrayList<String> matches = new ArrayList<>();

        /* Get current class */
        HashMap<String, Integer> classLineNumMap = new HashMap<>();
        for (ClassTreeNode classTreeNode : classMap.values()) {
            // loop through classes and store classes before current line number
            int lineNum = classTreeNode.getASTNode().getLineNum();
            if (lineNum < this.customCodeArea.getCurrentParagraph()) {
                classLineNumMap.put(classTreeNode.getName(), lineNum);
            }
        }
        // get max line number in map of possibilities
        int    maxInt           = 0;
        String currentClassName = "";
        for (String name : classLineNumMap.keySet()) {
            if (classLineNumMap.get(name) > maxInt) {
                currentClassName = name;
                maxInt = classLineNumMap.get(name);
            }
        }

        // set ctn to current class
        ClassTreeNode ctn = classMap.get("Object");
        if (ctn != null) {
            ctn = ctn.lookupClass(currentClassName);
        }

        // some edge cases where there should be no matches
        if (ctn == null || this.currentWord.equals("") || this.currentWord.equals(" ")) {
            return null;
        }

        // get symbol tables for current location
        if (this.currentWord.startsWith("super.")) {
            this.currentWord = this.currentWord.substring(6);
            // get parent fields
            ctn = ctn.getParent();
            matches.addAll(ctn.getVarSymbolTable().dumpIntoList());
        } else {
            LocalVarVisitor localVarVisitor = new LocalVarVisitor();
            ErrorHandler    errorHandler    = new ErrorHandler();
            localVarVisitor.visitVars(ctn.lookupClass("Object"), (Program) this.customCodeArea.currentAST,
                                      errorHandler, this.customCodeArea.getCurrentParagraph() + 1);
            List<String> suggestions = localVarVisitor.getAvailableVars();
            if (suggestions == null) {
                suggestions = ctn.getVarSymbolTable().dumpIntoList();
            }
            matches.addAll(suggestions); // gets variables
        }
        matches.addAll(ctn.getMethodSymbolTable().dumpIntoList()); // gets methods

        // a map all possible strings to that index at which the current word first matches
        Hashtable<Integer, ArrayList<String>> indexMap = new Hashtable<>();

        matches.stream()
                .filter(s -> !s.endsWith("()"))
                .forEach(s ->
                         {
                             // starting index of substring, case sensitive
                             int index = s.indexOf(currentWord);

                             if (index == -1) {
                                 return;
                             }

                             if (!indexMap.containsKey(index)) {
                                 ArrayList<String> newArr = new ArrayList<>();
                                 newArr.add(s);
                                 indexMap.put(index, newArr);
                             } else {
                                 indexMap.get(index).add(s);
                             }
                         });

        // utilize a treemap here since it has sorted keys, useful since we want to display the matches
        // of least index matched first
        return new TreeMap<>(indexMap);
    }

    /**
     * Generate the autocomplete suggestions menu and display to the user
     *
     * @param mitems     the list of menu items
     * @param matchesMap the map of suggestions
     */
    private void populateAutoCompleteMenu(ObservableList<MenuItem> mitems, Map<Integer, ArrayList<String>> matchesMap)
    {
        if (matchesMap == null) {
            return;
        }
        for (Integer index : matchesMap.keySet()) {
            ArrayList<String> matches = matchesMap.get(index);

            matches.forEach(match ->
                            {
                /*
                    Format strategy: 
                        1. get the first split index
                        2. get the second split index
                            
                    NORMAL [firstIndex] BOLDED [secondIndex] NORMAL
                */
                                int firstIndex  = index;
                                int secondIndex = firstIndex + this.currentWord.length();

                                TextFlow flow = new TextFlow();

                                Text text0 = new Text(match.substring(0, firstIndex));
                                text0.setStyle("-fx-font-weight: normal");

                                Text text1 = new Text(match.substring(firstIndex, secondIndex));
                                text1.setStyle("-fx-font-weight: bold");

                                Text text2 = new Text(match.substring(secondIndex, match.length()));
                                text2.setStyle("-fx-font-weight: normal");

                                flow.getChildren().addAll(text0, text1, text2);
                                // text is empty since we utilize the JavaFx node... so ugly who designed this and didn't include
                                // the option to not include the text
                                MenuItem newItem = new MenuItem("", flow);
                                newItem.setOnAction(this::completionAction);
                                mitems.add(newItem);
                            });

        }
    }

    /**
     * Insert the selected autocomplete suggestion into the code area
     *
     * @param event the key event
     */
    private void completionAction(ActionEvent event)
    {
        MenuItem mitem = (MenuItem) event.getSource();
        // get text of chosen menu item
        TextFlow   flow          = (TextFlow) mitem.getGraphic();
        List<Node> nodeList      = flow.getChildren();
        String     completedWord = "";
        for (int i = 0; i < nodeList.size(); i++) {
            Text child = (Text) nodeList.get(i);
            completedWord += child.getText();
        }

        int currentPosition = this.customCodeArea.getCaretPosition();

        this.customCodeArea.insertText(currentPosition, completedWord);
        this.customCodeArea.deleteText(currentPosition - currentWord.length(), currentPosition);
        this.customCodeArea.moveTo(currentPosition + completedWord.length() - currentWord.length());
    }

    /**
     * helper method for setting the word being currently editted (where the curror resides)
     *
     * @param event the key event
     */
    private void setCurrentWord(KeyEvent event)
    {
        int    caretPosition = this.customCodeArea.getCaretPosition();
        String text          = this.customCodeArea.getText();

        /*
            A word or phrase is defined as a set of characters between whitespaces
            thus here we capture the index of the front and back whitespace. Then
            we must update the current word for the current event since this completion
            strategy is triggered before the javacodearea is updated. The two possible actions
            are an addition or a backspace. For the addition we put the char at the caret
            position, for a deletion, we delete the char at the caret position.
        */

        int frontWhiteSpaceIndex = 0;
        for (int i = caretPosition - 1; i > -1; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
                frontWhiteSpaceIndex = i;
                break;
            }
        }

        int backWhiteSpaceIndex = text.length();
        for (int i = caretPosition; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                backWhiteSpaceIndex = i;
                break;
            }
        }

        this.currentWord = text.substring(frontWhiteSpaceIndex, backWhiteSpaceIndex);

        // adjust current word for event
        int middleIndex = caretPosition - frontWhiteSpaceIndex;
        int endIndex    = backWhiteSpaceIndex - frontWhiteSpaceIndex;
        if (event.getCode().equals(KeyCode.BACK_SPACE)) {
            String left  = this.currentWord.substring(0, middleIndex);
            String right = this.currentWord.substring(middleIndex, endIndex);
            this.currentWord = left.concat(right);
        } else {
            // edge case where the file is empty
            if (backWhiteSpaceIndex == 0) {
                this.currentWord = event.getText();
            } else {
                String left = this.currentWord.substring(0, middleIndex);
                left = left.concat(event.getText());
                String right = this.currentWord.substring(middleIndex, endIndex);
                this.currentWord = left.concat(right);
            }
        }

        // remove extra whitespace
        this.currentWord = this.currentWord.trim();
    }

    /**
     * Private helper class that runs type checker visitor and stores
     * the contents of the symbol table at the given line number
     */
    public class LocalVarVisitor extends TypeCheckerVisitor
    {
        private int          lineNum;
        private List<String> availableVars = null;

        /**
         * Runs the typeCheckerVisitor and stores the symbol table at the given line
         *
         * @param root         the ClassTreeNode at the root of the inheritance tree
         * @param program      the root node of the AST
         * @param errorHandler the errorHandler to register any errors with
         * @param lineNum      the current line number
         */
        public void visitVars(ClassTreeNode root, Program program, ErrorHandler errorHandler, int lineNum)
        {
            this.lineNum = lineNum;
            super.typeCheck(root, program, errorHandler);
        }

        /**
         * Visit a method node
         *
         * @param node the method node
         * @return result of the visit
         */
        @Override
        public Object visit(Method node)
        {
            // if the cursor is before the start of the method, we're done
            if (node.getLineNum() >= lineNum && availableVars == null) {
                availableVars = super.getCurrentSymbolTableList();
                return null;
            }

            // if the current line is outside this method, continue traversal
            int stmtListSize = node.getStmtList().getSize();
            if (stmtListSize == 0 || node.getStmtList().get(stmtListSize - 1).getLineNum() < lineNum) {
                super.visit(node);
                return null;
            }

            node.getFormalList().accept(this);

            for (ASTNode astNode : node.getStmtList()) {
                Stmt stmt = (Stmt) astNode;
                stmt.accept(this);
                // when we've reached the current line, we're done
                if (stmt.getLineNum() + 1 >= lineNum && availableVars == null) {
                    availableVars = super.getCurrentSymbolTableList();
                    return null;
                }
            }

            return null;
        }

        /**
         * Visit a block statement node
         *
         * @param node the block statement node
         * @return null
         */
        @Override
        public Object visit(BlockStmt node)
        {
            // if the cursor is before the start of the block, we're done
            if (node.getLineNum() >= lineNum && availableVars == null) {
                availableVars = super.getCurrentSymbolTableList();
                return null;
            }
            for (ASTNode astNode : node.getStmtList()) {
                Stmt stmt = (Stmt) astNode;
                stmt.accept(this);
                // if we reached the current line, store the symbol table
                if (stmt.getLineNum() >= lineNum && availableVars == null) {
                    availableVars = super.getCurrentSymbolTableList();
                    return null;
                }
            }

            super.visit(node);
            return null;
        }

        /**
         * Returns the list of variables in the symbol table
         * at the desired line number
         *
         * @return list of variable names
         */
        public List<String> getAvailableVars()
        {
            return this.availableVars;
        }
    }
}
