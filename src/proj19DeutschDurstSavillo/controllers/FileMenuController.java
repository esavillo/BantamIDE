/*
 * File: FileMenuController.java
 * F18 CS361 Project 10
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 12/6/2018
 * This file contains the FileMenuController class, handling File menu related actions.
 */

package proj19DeutschDurstSavillo.controllers;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import proj19DeutschDurstSavillo.BufferManager;
import proj19DeutschDurstSavillo.CodeTab;
import proj19DeutschDurstSavillo.CodeTabPane;
import proj19DeutschDurstSavillo.ControllerErrorCreator;
import proj19DeutschDurstSavillo.editor.CustomCodeArea;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;


/**
 * FileMenuController handles File menu related actions.
 *
 * @author Evan Savillo
 * @author Yi Feng
 * @author Zena Abulhab
 * @author Melody Mao
 */
public class FileMenuController
{
    public  BufferManager           bufferManager;
    /**
     * TabPane defined in Main.fxml
     */
    private CodeTabPane             codeTabPane;
    /**
     * Controller for the directory
     */
    private DirectoryViewController directoryViewController;
    /**
     * checkBox
     */
    private CheckBox                checkBox;
    private Menu                    editMenu;

    public FileMenuController()
    {
        this.bufferManager = new BufferManager();
    }


    public void setEditMenu(Menu editMenu)
    {
        this.editMenu = editMenu;
    }

    /**
     * Sets the directory controller.
     *
     * @param directoryViewController Controller for the directory created in Controller
     */
    public void setDirectoryViewController(DirectoryViewController directoryViewController)
    {
        this.directoryViewController = directoryViewController;
    }

    /**
     * Sets the checkBox, needed to update pane position
     *
     * @param checkBox the parent controller
     */
    public void setCheckBox(CheckBox checkBox)
    {
        this.checkBox = checkBox;
    }

    /**
     * Sets the tabPane.
     *
     * @param tabPane TabPane
     */
    public void setTabPane(CodeTabPane tabPane)
    {
        this.codeTabPane = tabPane;
    }

    /**
     * Returns a newly created code area for use.
     *
     * @return new CustomCodeArea to meet request for one
     */
    public CustomCodeArea giveNewCodeArea()
    {
        this.handleNewAction();
        return this.codeTabPane.getCurrentCodeArea();
    }

    /**
     * Handles the New button action.
     * Opens a styled code area embedded in a new tab.
     * Sets the newly opened tab to the the topmost one.
     */
    public void handleNewAction()
    {
        ObservableList<MenuItem> editMenuCopy = duplicateMenuItems(this.editMenu.getItems());

        File newbuffer = this.bufferManager.createLoneBuffer();

        this.codeTabPane.createTab("", newbuffer, this::handleCloseAction, editMenuCopy);
    }

    /**
     * Helper method to duplicate the contents of a menu
     *
     * @param menuItems List of Menu items to be duplicated
     * @return a clone of menu.getItems()
     */
    private ObservableList<MenuItem> duplicateMenuItems(ObservableList<MenuItem> menuItems)
    {
        ArrayList<MenuItem> clone = new ArrayList<>();

        menuItems.forEach(menuItem ->
                          {
                              MenuItem newItem = new MenuItem();
                              newItem.setText(menuItem.getText());
                              newItem.setOnAction(menuItem.getOnAction());
                              newItem.setId(menuItem.getId());
                              clone.add(newItem);
                          });

        return FXCollections.observableList(clone);
    }

    /**
     * Handles the close button action.
     * If the current styled code area has already been saved to a file, then the current tab is closed.
     * If the current styled code area has been changed since it was last saved to a file, a dialog
     * appears asking whether you want to save the text before closing it.
     *
     * @param event Event object
     */
    public void handleCloseAction(Event event)
    {
        CodeTab selectedTab = this.codeTabPane.getCurrentTab();

        // selectedTab is null if this method is evoked by closing a tab
        // in this case the selectedTab tab should be the tab that evokes this method
        if (selectedTab == null) {
            selectedTab = (CodeTab) event.getSource();
        }

        // Attempts to close the tab
        // If the user elects to not close the tab, then we consume the event (not performing the closing action)
        if (!this.closeTab(selectedTab)) {
            event.consume();
            return;
        }

        // If no tabs open after this, close structure view
        if (this.tablessProperty().getValue()) {
            this.checkBox.setSelected(false);
        }
    }

    /**
     * Helper method to handle closing tab action.
     * If the text embedded in the tab window has not been saved yet,
     * or if a saved file has been changed, asks the user if to save
     * the file via a dialog window.
     *
     * @param tab Tab to be closed
     * @return true if the tab is closed successfully; false if the user clicks cancel.
     */
    private boolean closeTab(CodeTab tab)
    {
        // if the file has not been saved or has been changed
        // pop up a dialog window asking whether to save the file
        if (this.tabNeedsSaving(tab, false)) {
            int buttonClicked = this.createConfirmationDialog("Save Changes?",
                                                              "Do you want to save the changes you made?",
                                                              "Your changes will be lost if you don't save them.");

            // if user presses No button, close the tab without saving
            if (buttonClicked == 0) {
                this.removeTab(tab);
                return true;
            }
            // if user presses Yes button, close the tab and save the tab content
            else if (buttonClicked == 1) {
                if (this.handleSaveAction()) {
                    this.removeTab(tab);
                    return true;
                }
                return false;
            }
            // if user presses cancel button
            else {
                return false;
            }
        }
        // if the file has not been changed, close the tab
        else {
            this.removeTab(tab);
            return true;
        }
    }

    /**
     * Helper method to handle closing tab action.
     * Checks if the text content within the specified tab window should be saved.
     *
     * @param tab             Tab to be closed
     * @param ifSaveEmptyFile boolean false if not to save the empty file; true if to save the empty file
     * @return true if the tab needs saving; false if the tab does not need saving.
     */
    public boolean tabNeedsSaving(CodeTab tab, boolean ifSaveEmptyFile)
    {
        CustomCodeArea activeCustomCodeArea = tab.getCustomCodeArea();
        // check whether the embedded text has been saved or not
        if (this.bufferManager.getOriginalFile(tab.getFile()) == null) {
            // if the newly created file is empty, don't save
            if (!ifSaveEmptyFile) {
                if (activeCustomCodeArea.getText().equals("")) {
//                    this.bufferManager.deleteBuffer(tab.getFile());
                    return false;
                }
            }

            return true;
        }
        // check whether the saved file match the tab content or not
        else {
            return !this.fileContainsMatch(activeCustomCodeArea,
                                           this.bufferManager.getOriginalFile(tab.getFile()));
        }
    }

    /**
     * Helper method to check if the content of the specified CustomCodeArea
     * matches the content of the specified File.
     *
     * @param customCodeArea CustomCodeArea to compare with the the specified File
     * @param file         File to compare with the the specified CustomCodeArea
     * @return true if the content of the CustomCodeArea matches the content of the File; false if not
     */
    public boolean fileContainsMatch(CustomCodeArea customCodeArea, File file)
    {
        String javaCodeAreaContent = customCodeArea.getText();
        String fileContent         = this.getFileContent(file);

        return javaCodeAreaContent.equals(fileContent);
    }

    /**
     * Helper method to get the text content of a specified file.
     *
     * @param file File to get the text content from
     * @return the text content of the specified file; null if an error occurs when reading the specified file.
     */
    private String getFileContent(File file)
    {
        try {
            return new String(Files.readAllBytes(Paths.get(file.toURI())));
        } catch (Exception ex) {
            ControllerErrorCreator.createErrorDialog("Reading File", "Cannot read " + file.getName() + ".");
            return null;
        }
    }

    /**
     * Calls the removeTab function in codeTabPane
     *
     * @param tab Tab to be closed
     */
    private void removeTab(CodeTab tab)
    {
        this.bufferManager.deleteBuffer(tab.getFile());
        this.codeTabPane.removeTab(tab);
    }

    /**
     * Helper method to create a confirmation dialog window.
     *
     * @param title       the title of the confirmation dialog
     * @param headerText  the header text of the confirmation dialog
     * @param contentText the content text of the confirmation dialog
     * @return 0 if the user clicks No button; 1 if the user clicks the Yes button; 2 if the user clicks cancel button.
     */
    public int createConfirmationDialog(String title, String headerText, String contentText)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        ButtonType buttonYes    = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonNo     = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonYes, buttonNo, buttonCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonNo) {
            return 0;
        } else if (result.get() == buttonYes) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * Handles the save button action.
     * If a styled code area was not loaded from a file nor ever saved to a file,
     * behaves the same as the save as button.
     * If the current styled code area was loaded from a file or previously saved
     * to a file, then the styled code area is saved to that file.
     *
     * @return true if save was successful; false if cancels or an error occurs when saving the file.
     */
    public boolean handleSaveAction()
    {
        // if the tab content was not loaded from a file nor ever saved to a file
        // save the content of the active styled code area to the selected file path

        File currentBuffer = this.codeTabPane.getCurrentFile();
        if (this.bufferManager.getOriginalFile(currentBuffer) == null) {
            return this.handleSaveAsAction();
        }
        // if the current styled code area was loaded from a file or previously saved to a file,
        // then the styled code area is saved to that file
        else {
            if (!this.setFileContent(this.codeTabPane.getCurrentTab().getCustomCodeArea().getText(),
                                     this.bufferManager.getOriginalFile(currentBuffer))) {
                return false;
            }

//            this.codeTabPane.getCurrentTab().setStyle("-fx-text-base-color: black");
            return true;
        }
    }

    /**
     * Handles the Save As button action.
     * Shows a dialog in which the user is asked for the name of the file into
     * which the contents of the current styled code area are to be saved.
     * If the user enters any legal name for a file and presses the OK button in the dialog,
     * then creates a new text file by that name and write to that file all the current
     * contents of the styled code area so that those contents can later be reloaded.
     * If the user presses the Cancel button in the dialog, then the dialog closes and no saving occurs.
     *
     * @return true if save was successful; false if cancelled or an error occurs when saving the file.
     */
    public boolean handleSaveAsAction()
    {
        FileChooser fileChooser = new FileChooser();
        File        saveFile    = fileChooser.showSaveDialog(this.codeTabPane.getScene().getWindow());

        if (saveFile != null) {
            // get the selected tab from the tab pane

            CodeTab        selectedTab          = this.codeTabPane.getCurrentTab();
            CustomCodeArea activeCustomCodeArea = this.codeTabPane.getCurrentCodeArea();

            if (!this.setFileContent(activeCustomCodeArea.getText(), saveFile)) {
                return false;
            }
            // update the title of the tab to the name of the saved file
            selectedTab.setText(saveFile.getName());
            selectedTab.setStyle("-fx-text-base-color: black");

            // map the tab and the associated file
            this.bufferManager.deleteBuffer(this.codeTabPane.getCurrentFile());
            selectedTab.setFile(this.bufferManager.createBuffer(saveFile));

            // call this for some reason
            this.directoryViewController.createDirectoryTree();

            return true;
        }
        return false;
    }

    /**
     * Helper method to save the input string to a specified file.
     *
     * @param content String that is saved to the specified file
     * @param file    File that the input string is saved to
     * @return true is the specified file is successfully saved; false if an error occurs when saving the specified file.
     */
    public boolean setFileContent(String content, File file)
    {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
            return true;
        } catch (IOException ex) {
            ControllerErrorCreator.createErrorDialog("Saving File", "Cannot save to " + file.getName() + ".");
            return false;
        }
    }

    /**
     * Property which indicates if there are currently any tabs open.
     *
     * @return truth value indicating if there are any tabs currently open
     */
    public ReadOnlyBooleanProperty tablessProperty()
    {
        return new SimpleListProperty<>(this.codeTabPane.getTabs()).emptyProperty();
    }

    public BooleanProperty isMIPSProperty()
    {
        return this.codeTabPane.isMIPSProperty;
    }

    public BooleanProperty isBTMProperty()
    {
        return this.codeTabPane.isBTMProperty;
    }

    /**
     * Checks whether a file embedded in the specified tab should be saved before compiling.
     * Pops up a dialog asking whether the user wants to save the file before compiling.
     * Saves the file if the user agrees so.
     *
     * @return 0 if user clicked NO button; 1 if user clicked OK button;
     * 2 is user clicked Cancel button; -1 is no saving is needed
     */
    public int checkSaveBeforeScan()
    {

        // if the file has not been saved or has been changed
        if (this.tabNeedsSaving(this.codeTabPane.getCurrentTab(), true)) {
            int buttonClicked = createConfirmationDialog("Save Changes?",
                                                         "Do you want to save the changes before scanning?",
                                                         "Your recent file changes would not be scanned if not saved.");
            // if user presses Yes button
            if (buttonClicked == 1) {
                if (this.handleSaveAction()) { return 1; } else { return 2; }
            } else { return 2; }

        }
        return -1;
    }

    /**
     * Handles the open button action.
     * Opens a dialog in which the user can select a file to open.
     * If the user chooses a valid file, a new tab is created and the file is loaded into the styled code area.
     * If the user cancels, the dialog disappears without doing anything.
     */
    public void handleOpenAction()
    {
        FileChooser fileChooser = new FileChooser();
        File        openedFile  = fileChooser.showOpenDialog(this.codeTabPane.getScene().getWindow());

        if (openedFile != null) {
            openFile(openedFile);
        } else {
            return;
        }

        this.checkBox.setSelected(true);
    }

    /**
     * Handles the opening of a file
     *
     * @param file
     */
    public void openFile(File file)
    {
        // If the selected file is already open, it cannot be opened twice
        // the tab containing this file becomes the current (topmost) one
        for (Tab tab : this.codeTabPane.getTabs()) {
            CodeTab codeTab = (CodeTab) tab;
            File    tabFile = this.bufferManager.getOriginalFile(codeTab.getFile());

            if (tabFile != null) {
                if (tabFile.equals(file)) {
                    this.codeTabPane.getSelectionModel().select(tab);
                    return;
                }
            }
        }

        String contentString = this.getFileContent(file);

        if (contentString == null) { return; }

        // Create the tab for the file
        ObservableList<MenuItem> editMenuCopy = duplicateMenuItems(this.editMenu.getItems());

        File newbuffer = this.bufferManager.createBuffer(file);
        this.codeTabPane.createTab(contentString, newbuffer, this::handleCloseAction, editMenuCopy);
    }

    /**
     * If the given file is open in a tab, close the tab unconditionally
     * @param file the File to close
     */
    public void closeFile(File file) {
        CodeTab codeTab = null;
        boolean tabOpen = false;
        // Determine if the file is open in the tab pane
        for (Tab tab : this.codeTabPane.getTabs()) {
            codeTab = (CodeTab) tab;
            File tabFile = this.bufferManager.getOriginalFile(codeTab.getFile());

            if (tabFile != null) {
                if (tabFile.equals(file)) {
                    tabOpen = true;
                    break;
                }
            }
        }
        if (tabOpen) {
            this.removeTab(codeTab);
        }
    }

    /**
     * Handles the Exit button action.
     * Exits the program when the Exit button is clicked.
     *
     * @param event Event object
     */
    public void handleExitAction(Event event)
    {
        ArrayList<Tab> list = new ArrayList<>(this.codeTabPane.getTabs());
        Iterator<Tab>  iter = list.iterator();
        while (iter.hasNext()) {
            CodeTab tab = (CodeTab) iter.next();
            // if the user cancels closing the tab, then we consume the event (not performing the closing action)
            if (!this.closeTab(tab)) {
                event.consume();
                return;
            }

        }
        Platform.exit();
    }

    /**
     * Handles the About button action.
     * Creates a dialog window that displays the authors' names.
     */
    public void handleAboutAction()
    {
        // create a information dialog window displaying the About text
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);

        // enable to close the window by clicking on the red cross on the top left corner of the window
        Window window = dialog.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());

        // set the title and the content of the About window
        dialog.setTitle("About");
        dialog.setHeaderText("Authors");
        dialog.setContentText(
                "_.-- Project 6,7,9 --._ \n    Melody Mao\n    Zena Abulhab\n    Yi Feng\n    Evan Savillo" +
                        "\n\n_.-- Project 5 --._ \n    Liwei Jiang\n    Martin Deutsch\n    Melody Mao\n    Tatsuya Yakota\n\n" +
                        "_.-- Project 4 --._ \n    Liwei Jiang\n    Danqing Zhao\n    Wyett MacDonald\n    Zeb Keith-Hardy"
        );

        dialog.showAndWait();
    }
}
