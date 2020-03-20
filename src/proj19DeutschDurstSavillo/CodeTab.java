/*
 * File: CodeTab
 * F18 CS361 Project 9
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 11/20/18
 */

package proj19DeutschDurstSavillo;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import org.fxmisc.flowless.VirtualizedScrollPane;
import proj19DeutschDurstSavillo.editor.CustomCodeArea;
import proj19DeutschDurstSavillo.editor.JavaMode;
import proj19DeutschDurstSavillo.editor.MipsMode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Subclass of the JavaFX Tab class.
 */
public class CodeTab
        extends Tab
{
    private CustomCodeArea customCodeArea;
    private File           file;
    private Runnable       postWriteFn;

    /**
     * Constructor of Java Tab, initialize code area and set tab name and content
     * Set up the handler for close request
     */
    public CodeTab(String contentString,
                   File file,
                   EventHandler<Event> handler,
                   Runnable postWriteFn,
                   ObservableList<MenuItem> rightClickMenu,
                   ASTBroadcaster astBroadcaster)
    {
        if (file.getName().endsWith(".asm") || file.getName().endsWith(".s")) {
            this.customCodeArea = new CustomCodeArea(rightClickMenu, this::writeBuffer, new MipsMode());
        } else {
            this.customCodeArea = new CustomCodeArea(rightClickMenu, this::writeBuffer, new JavaMode());
        }

        this.customCodeArea.appendText(contentString);

        this.setContent(new VirtualizedScrollPane<>(this.customCodeArea));

        this.setFile(file);
        this.setTabName();

        this.setOnCloseRequest(handler);
        this.postWriteFn = postWriteFn;

        astBroadcaster.register(this.customCodeArea);

        this.writeBuffer();
    }

    /**
     * Sets the name of the tab to untitled if the file is new, or to the name of an existing file
     */
    private void setTabName()
    {
        String name = (file == null) ? "Untitled" : this.file.getName();
        this.setText(name);
    }

    /**
     * @return file
     */
    public File getFile()
    {
        return file;
    }

    /**
     * set file
     *
     * @param file
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    /**
     * @return customCodeArea
     */
    public CustomCodeArea getCustomCodeArea()
    {
        return customCodeArea;
    }

    /**
     * Writes the buffer file (not the original!) with the current contents
     * of the tab's code area.
     */
    public void writeBuffer()
    {
        try {
            FileWriter fileWriter = new FileWriter(this.file);
            fileWriter.write(this.customCodeArea.getText());
            fileWriter.close();

            this.postWriteFn.run();
        } catch (IOException e) {
            System.out.println("[!] Fatal error saving to buffer - CodeTab:writeBuffer");
            System.exit(-1);
        }
    }
}
