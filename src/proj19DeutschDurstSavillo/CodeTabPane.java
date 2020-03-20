/*
 * File: CodeTabPane
 * F18 CS361 Project 9
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/20/18
 */

package proj19DeutschDurstSavillo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import proj19DeutschDurstSavillo.editor.CustomCodeArea;

import java.io.File;


/**
 * This class extends the TabPane class from JavaFx to handle JavaTabs.
 *
 * @author Evan Savillo
 * @author Yi Feng
 * @author Zena Abulhab
 * @author Melody Mao
 */
public class CodeTabPane extends TabPane
{
    public  BooleanProperty isMIPSProperty;
    public  BooleanProperty isBTMProperty;
    private ASTBroadcaster  astBroadcaster = null;
    private Runnable        postWriteFn    = null;

    public CodeTabPane()
    {
        this.isMIPSProperty = new SimpleBooleanProperty(false);
        this.isBTMProperty = new SimpleBooleanProperty(false);

        this.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                {
                    if (newValue != null) {
                        this.isMIPSProperty.setValue(((CodeTab) newValue).getFile().getName().endsWith(".asm"));
                        this.isBTMProperty.setValue(((CodeTab) newValue).getFile().getName().endsWith(".btm"));
                    }
                }
        );
    }

    public void setASTBroadcaster(ASTBroadcaster astBroadcaster)
    {
        this.astBroadcaster = astBroadcaster;
    }

    /**
     * Sets the optional function which is called after a write
     * (this gets passed to all new tabs
     *
     * @param postWriteFn
     */
    public void setPostWriteFn(Runnable postWriteFn)
    {
        this.postWriteFn = postWriteFn;
    }

    /**
     * Create a new tab
     */
    public void createTab(String contentString,
                          File file,
                          EventHandler<Event> handler,
                          ObservableList<MenuItem> rightClickMenu)
    {
        CodeTab newTab = new CodeTab(contentString,
                                     file,
                                     handler,
                                     this.postWriteFn,
                                     rightClickMenu,
                                     this.astBroadcaster);

        this.getSelectionModel().select(newTab);

        this.getTabs().add(newTab);
    }

    /**
     * remove a tab from the tabpane and the tablist
     *
     * @param tab
     */
    public void removeTab(CodeTab tab)
    {
        this.getTabs().remove(tab);
    }

    /**
     * Returns the code area currently being viewed in the current tab
     *
     * @return the CustomCodeArea object of the open tab if there is one,
     * return null otherwise.
     */
    public CustomCodeArea getCurrentCodeArea()
    {
        CodeTab selectedTab = getCurrentTab();

        if (selectedTab != null) {
            return selectedTab.getCustomCodeArea();
        }

        return null;
    }

    /**
     * Returns the current tab
     *
     * @return the current tab if there is one, return null otherwise.
     */
    public CodeTab getCurrentTab()
    {
        return (CodeTab) this.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns the file object in the current tab
     *
     * @return the File object of the item selected in the tab pane if there is one,
     * return null otherwise.
     */
    public File getCurrentFile()
    {
        CodeTab selectedTab = getCurrentTab();

        if (selectedTab != null) {
            return selectedTab.getFile();
        }

        return null;
    }
}
