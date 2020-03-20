/*
 * File: StyledCodeArea.java
 * F18 CS361 Project 9
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/20/2018
 * This file contains the StyledCodeArea class, which extends the CodeArea class
 * to handle syntax highlighting for Java.
 */

package proj19DeutschDurstSavillo.editor;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import proj19DeutschDurstSavillo.bantam.ast.ASTNode;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.bantam.semant.SemanticAnalyzer;
import proj19DeutschDurstSavillo.bantam.util.CompilationException;
import proj19DeutschDurstSavillo.bantam.util.ErrorHandler;
import proj19DeutschDurstSavillo.interfaces.ASTObserver;
import proj19DeutschDurstSavillo.interfaces.HighlightingMode;
import proj19DeutschDurstSavillo.interfaces.KeyPressStrategy;

import java.time.Duration;
import java.util.Collection;


/**
 * This class extends the CodeArea class from RichTextFx to handle
 * syntax highlighting for Java.
 *
 * @author Evan Savillo
 * @author Yi Feng
 * @author Zena Abulhab
 * @author Melody Mao
 */
public class CustomCodeArea
        extends CodeArea
        implements ASTObserver
{
    public  ContextMenu        autoCompleteMenu;
    public  SemanticAnalyzer   semanticAnalyzer;
    public  ASTNode            currentAST;
    /** The menu that appears on right-clicking this code area */
    private ContextMenu        editContextMenu;
    private KeyPressStrategist keyPressStrategist;
    private Runnable           writeBuffer;
    private HighlightingMode   highlightingMode;

    /**
     * Creates a new empty CustomCodeArea
     *
     * @param menu the right-click menu
     */
    public CustomCodeArea(ObservableList<MenuItem> menu, Runnable writeBuffer, HighlightingMode highlightingMode)
    {
        this.highlightingMode = highlightingMode;

        // Handles coloring the text by syntax immediately
        this.handleTextChange();
        // Sets up highlighting for syntax
        this.highlightText();
        // Enables line numbering
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        // Sets up the right-click menu
        this.addRightClickMenu(menu);

        this.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePress);

        // Enables auto-closing parentheses, brackets, and curly braces
        this.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPress);
        this.setOnKeyTyped(event -> writeBuffer.run());

        this.currentAST = null;
        this.semanticAnalyzer = new SemanticAnalyzer(new ErrorHandler());

        this.autoCompleteMenu = new ContextMenu();
        this.keyPressStrategist = new KeyPressStrategist();

        this.writeBuffer = writeBuffer;
    }

    public CustomCodeArea(ObservableList<MenuItem> menu, Runnable writeBuffer)
    {
        this(menu, writeBuffer, new MipsMode());
    }

    /**
     * Gets the strategy for electric parens/brackets/braces and tries to
     * execute it, as well as that for autocompletion
     */
    private void onKeyPress(KeyEvent event)
    {
        KeyPressStrategy electricStrategy =
                this.keyPressStrategist.getElectricStrategy(event);

        if (electricStrategy != null) {
            electricStrategy.execute(this, event);
        }

        AutoCompletionStrategy autoCompletionStrategy =
                this.keyPressStrategist.getAutoCompletionStrategy(event);

        if (autoCompletionStrategy != null) {
            autoCompletionStrategy.execute(this, event);
        }

        this.handleTextChange();
    }

    /**
     * Updates the current buffer file, hides any autocomplete menu
     * and potential displays edit menu
     */
    private void onMousePress(MouseEvent event)
    {
        this.writeBuffer.run();

        this.autoCompleteMenu.hide();

        if (event.isSecondaryButtonDown()) {
            this.editContextMenu.show(this,
                                      event.getScreenX(),
                                      event.getScreenY());
        } else if (event.isPrimaryButtonDown() && this.editContextMenu.isShowing()) {
            this.editContextMenu.hide();
        }
    }

    /**
     * Handles the text change action.
     * Listens to the text changes and highlights the keywords real-time.
     */
    private void handleTextChange()
    {
        this
                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                // when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()

                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))

                // run the following code block when previous stream emits an event
                .subscribe(ignore -> this.highlightText());
    }

    /**
     * Helper function to highlight the text within the StyledCodeArea.
     */
    private void highlightText()
    {
        this.setStyleSpans(0, computeHighlighting(this.getText()));
    }

    private StyleSpans<Collection<String>> computeHighlighting(String text)
    {
        return this.highlightingMode.computeHighlighting(text);
    }

    public void setHighlightingMode(HighlightingMode highlightingMode)
    {
        this.highlightingMode = highlightingMode;
    }

    /**
     * Populates the right click menu with the edit menu items
     */
    private void addRightClickMenu(ObservableList<MenuItem> menu)
    {
        this.editContextMenu = new ContextMenu();
        this.editContextMenu.getItems().addAll(menu);
    }

    /**
     * Recieves a broadcast AST, on which
     * semantic analysis is then performed.
     */
    @Override
    public void update(ASTNode currentAST)
    {
        this.currentAST = currentAST;

        try {
            if (this.currentAST != null) {
                this.semanticAnalyzer.analyze((Program) this.currentAST);
            }
        } catch (CompilationException e) {
            System.out.println("parsing error - javacodearea:update");
            System.out.println(e.getMessage());
        }
    }
}
