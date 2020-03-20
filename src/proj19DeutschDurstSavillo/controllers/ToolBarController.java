/*
 * File: ToolBarController.java
 * F18 CS361 Project 10
 * Names: Melody Mao, Zena Abulhab, Yi Feng, Evan Savillo
 * Date: 12/6/2018
 */

package proj19DeutschDurstSavillo.controllers;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.StyleClassedTextArea;
import proj19DeutschDurstSavillo.CodeTab;
import proj19DeutschDurstSavillo.ControllerErrorCreator;
import proj19DeutschDurstSavillo.bantam.ast.Program;
import proj19DeutschDurstSavillo.bantam.codegenmips.MipsCodeGenerator;
import proj19DeutschDurstSavillo.bantam.lexer.Scanner;
import proj19DeutschDurstSavillo.bantam.lexer.Token;
import proj19DeutschDurstSavillo.bantam.parser.Parser;
import proj19DeutschDurstSavillo.bantam.semant.SemanticAnalyzer;
import proj19DeutschDurstSavillo.bantam.treedrawer.OurDrawer;
import proj19DeutschDurstSavillo.bantam.util.ClassTreeNode;
import proj19DeutschDurstSavillo.bantam.util.CompilationException;
import proj19DeutschDurstSavillo.bantam.util.Error;
import proj19DeutschDurstSavillo.bantam.util.ErrorHandler;
import proj19DeutschDurstSavillo.editor.CustomCodeArea;
import proj19DeutschDurstSavillo.interfaces.PostParseActor;
import proj19DeutschDurstSavillo.bantam.codeanalysis.ControlFlowGraphVisitor;
import proj19DeutschDurstSavillo.bantam.codeanalysis.ControlFlowGraphAnalyzer;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;


/**
 * ToolbarController handles Toolbar related actions.
 *
 * @author Evan Savillo
 * @author Yi Feng
 * @author Zena Abulhab
 * @author Melody Mao
 */
public class ToolBarController
{
    /**
     * The FileMenuController
     */
    private FileMenuController   fileMenuController;
    /**
     * Console defined in Main.fxml
     */
    private StyleClassedTextArea console;
    private ScanWorker           scanWorker;
    private ParseWorker          parseWorker;
    private OurDrawer            drawer;
    private SemanticAnalyzer     semanticAnalyzer;
    private MipsCodeGenerator    mipsCodeGenerator;

    /**
     * Process currently compiling or running a Java file
     */
    private Process curProcess;
    /**
     * Thread representing the Java program input stream
     */
    private Thread  inThread;
    /**
     * Thread representing the Java program output stream
     */
    private Thread  outThread;

    /**
     * Mutex lock to control input and output threads' access to console
     */
    private Semaphore            mutex;
    /**
     * The consoleLength of the output on the console
     */
    private int                  consoleLength;
    /**
     * A AssembleWorker object compiles a Java file in a separate thread.
     */
    private AssembleWorker       assembleWorker;
    /**
     * A AssembleAndRunWorker object compiles and runs a Java file in a separate thread.
     */
    private AssembleAndRunWorker assembleAndRunWorker;

    private Dialog<ButtonType>  optDialog;
    private ArrayList<CheckBox> optFlagCheckBoxes;


    /**
     * Initializes the ToolBarController controller.
     * Sets the Drawer, and the Scan- and Parse-Workers.
     * Also sets OnStatus behavior of workers.
     */
    public void initialize()
    {
        this.drawer = new OurDrawer();

        // setup scan worker
        this.scanWorker = new ScanWorker();

        this.scanWorker.setOnSucceeded(event ->
                                       {
                                           /*
                                           On success, get the completed value, which is a string
                                           to be 'printed' to the console informing the user
                                           of the results
                                            */
                                           ((ScanWorker) event.getSource()).resetFields();

                                           // Clear the console before printing
                                           console.clear();
                                           console.appendText((String) (event.getSource().getValue()));
                                       });

        this.scanWorker.setOnCancelled(event ->
                                       {
                                           ((ScanWorker) event.getSource()).resetFields();
                                       }
        );

        //setup parse worker
        this.parseWorker = new ParseWorker();
        /* On completion of the building of a parse tree, obtain the appropriate info from
         * the parseworker, such as the the postParseActor, if any, then subsequently
         * execute on the parsed tree and print results to the console.
         */
        this.parseWorker.setOnSucceeded(event ->
                                        {
                                            Object      result      = null;
                                            ParseWorker parseWorker = ((ParseWorker) event.getSource());

                                            if (parseWorker.isErrorFree) {
                                                Program root = ((ParseWorker) event.getSource()).root;

                                                String   filename      = parseWorker.filename;
                                                String[] splitFilename = filename.split("/");
                                                filename = splitFilename[splitFilename.length - 1];

                                                PostParseActor postParseActor = parseWorker.postParseActor;
                                                // if no PPA was specified, default to drawer.
                                                postParseActor = (postParseActor == null) ?
                                                        this.drawer : postParseActor;

                                                HashMap<String, Object> knowledge = new HashMap<>();
                                                knowledge.put("filename", filename);

                                                result = postParseActor.act(root, knowledge);

                                                // determine if should compile
                                                if (parseWorker.shouldCompile && postParseActor instanceof SemanticAnalyzer) {
                                                    String outfileName =
                                                            "include/" + filename.substring(1,
                                                                                            filename.indexOf(".btm")) + ".asm";
                                                    if (!parseWorker.errorHandler.errorsFound()) {
                                                        compileBantamFile(((SemanticAnalyzer) postParseActor).getRoot(),
                                                                          parseWorker.errorHandler, outfileName,
                                                                          false);

                                                        File outfile = new File(outfileName);
                                                        this.fileMenuController.closeFile(outfile);
                                                        fileMenuController.openFile(outfile);
                                                    }
                                                }
                                            }

                                            ((ParseWorker) event.getSource()).resetFields();

                                            // Clear the console before printing
                                            console.clear();
                                            console.appendText((String) (event.getSource().getValue()));
                                            result = (result == null) ? "" : result;
                                            console.appendText(result.toString());
                                        });

        this.parseWorker.setOnCancelled(event ->
                                        {
                                            ((ParseWorker) event.getSource()).resetFields();
                                        }
        );

        this.mutex = new Semaphore(1);
        this.assembleWorker = new AssembleWorker();
        this.assembleAndRunWorker = new AssembleAndRunWorker();

        /// Create and setup Dialog Box wherefrom the user can select desired optimizations
        optDialog = new Dialog<>();
        optDialog.setTitle("    Select Desired Optimizations");

        ArrayList<ButtonType> buttons = new ArrayList<>();
        buttons.add(new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE));
        buttons.add(new ButtonType("Compile", ButtonBar.ButtonData.YES));

        optDialog.getDialogPane().getButtonTypes().addAll(buttons);

        this.optFlagCheckBoxes = new ArrayList<>();
        optFlagCheckBoxes.add(new CheckBox("Code Folding"));
        optFlagCheckBoxes.add(new CheckBox("Unroll Loops"));

        VBox content = new VBox();
        content.setMinWidth(300.0);
        content.setSpacing(5.0);
        content.getChildren().addAll(optFlagCheckBoxes);
        optDialog.getDialogPane().setContent(content);
    }

    private void compileBantamFile(ClassTreeNode root, ErrorHandler errorHandler, String outfile, boolean gc)
    {
        boolean[] optFlags = {
                this.optFlagCheckBoxes.get(1).isSelected(),
                this.optFlagCheckBoxes.get(0).isSelected()
        };

        MipsCodeGenerator mipsCodeGenerator = new MipsCodeGenerator(errorHandler, gc, optFlags);
        mipsCodeGenerator.generate(root, outfile);
    }

    /**
     * Sets the console pane.
     *
     * @param console StyleClassedTextArea defined in Main.fxml
     */
    public void setConsole(StyleClassedTextArea console)
    {
        this.console = console;
    }

    /**
     * Sets the FileMenuController.
     *
     * @param fileMenuController FileMenuController created in main Controller.
     */
    public void setFileMenuController(FileMenuController fileMenuController)
    {
        this.fileMenuController = fileMenuController;
    }

    /**
     * Handles when the scan button is clicked; the current file
     * is run through a lexical scanner.
     *
     * @param event the event triggered
     * @param tab   the current tab
     */
    public void handleScanButtonAction(Event event, CodeTab tab)
    {
        // user selects cancel button
        if (this.fileMenuController.checkSaveBeforeScan() == 2) {
            event.consume();
        } else {
            this.scanWorker.cancel();

            ErrorHandler errorHandler = new ErrorHandler();
            String       filename     = tab.getFile().getAbsolutePath();
            // Request that the filemenucontroller create a new tab in which to print
            CustomCodeArea outputArea = requestAreaForOutput();

            this.scanWorker.setErrorHandler(errorHandler);
            this.scanWorker.setFilename(filename);
            this.scanWorker.setOutputArea(outputArea);

            this.scanWorker.reset();
            this.scanWorker.restart();
        }

    }

    /**
     * Helper method, Request a new tab be made
     *
     * @return the code area in the newly made tab
     */
    private CustomCodeArea requestAreaForOutput()
    {
        return this.fileMenuController.giveNewCodeArea();
    }

    /**
     * then scans and parses the current file.
     *
     * @param event the event triggered
     * @param tab   the current tab
     */
    public void handleScanAndParseButtonAction(Event event, CodeTab tab)
    {
        this.handleParse(event, tab, this.drawer, false);
    }

    /**
     * First ensures the user agrees to save file, then
     * handles the bestowal of a new task to a parseWorker based on the currently
     * selected tab and some given postParseActor
     */
    private void handleParse(Event event, CodeTab tab, PostParseActor postParseActor, boolean shouldCompile)
    {
        // user selects cancel button
        if (this.fileMenuController.checkSaveBeforeScan() == 2) {
            event.consume();
        } else {
            // cancel any currently running parse
            this.parseWorker.cancel();

            ErrorHandler errorHandler = new ErrorHandler();
            String       filename     = tab.getFile().getAbsolutePath();

            this.parseWorker.setErrorHandler(errorHandler);
            this.parseWorker.setFilename(filename);
            this.parseWorker.setPostParseActor(postParseActor);
            this.parseWorker.setShouldCompile(shouldCompile);

            if (this.semanticAnalyzer != null) {
                this.semanticAnalyzer.errorHandler = errorHandler;
            }

            // prime the state, then restart
            this.parseWorker.reset();
            this.parseWorker.restart();
        }
    }

    /**
     * Handles the event where the Main( main) Button was clicked. The
     * current file is parsed, and then the MainMainVisitor visits the tree.
     */
    public void handleScanParseCheckButtonAction(Event event, CodeTab tab)
    {
        this.semanticAnalyzer = new SemanticAnalyzer(null);

        this.handleParse(event, tab, this.semanticAnalyzer, false);
    }

    /**
     * Handles the event where the Main( main) Button was clicked. The
     * current file is parsed, and then the MainMainVisitor visits the tree.
     */
    public void handleCycleAnalysisButtonAction(Event event, CodeTab tab)
    {
        // no need to run this in a separate thread
        this.console.clear();

        ErrorHandler errorHandler = new ErrorHandler();
        Parser parser = new Parser(errorHandler);
        ControlFlowGraphVisitor cfgv = new ControlFlowGraphVisitor();

        try {
            Program program = parser.parse(tab.getFile().getAbsolutePath());
            cfgv.visit(program);
            this.console.appendText("Cyclomatic Complexity Analysis Results Per Method:\n");
            this.console.appendText("------------------------------------------------------------\n");
            for (String name: cfgv.methodCfgs.keySet()){
                ControlFlowGraphAnalyzer cfgna = new ControlFlowGraphAnalyzer(cfgv.methodCfgs.get(name));
                this.console.appendText(cfgna.getCyclomaticComplexityAnalysisResults());
            }
        } catch (CompilationException e) {
            this.console.appendText("Parsing error encountered:\n");
            this.console.appendText(e.getMessage());
        }

        this.console.requestFollowCaret();
    }

    /**
     * Gets the AssembleWorker.
     *
     * @return AssembleWorker
     */
    public AssembleWorker getAssembleWorker()
    {
        return this.assembleWorker;
    }

    /**
     * Gets the AssembleAndRunWorker.
     *
     * @return AssembleAndRunWorker
     */
    public AssembleAndRunWorker getAssembleAndRunWorker()
    {
        return this.assembleAndRunWorker;
    }

    /**
     * Helper method for running Mips Compiler.
     */
    private boolean assembleMipsFile(File file)
    {

        try {
            Platform.runLater(() ->
                              {
                                  this.console.clear();
                                  this.consoleLength = 0;
                              });

            ProcessBuilder pb = new ProcessBuilder(Arrays.asList("java",
                                                                 "-jar",
                                                                 "include/Mars4_5.jar",
                                                                 "nc",
                                                                 "a",
                                                                 "ae1",
                                                                 file.getAbsolutePath(),
                                                                 "include/exceptions.s"));
            this.curProcess = pb.start();
            this.outputToConsole();

            // true if compiled without compile-time error, else false
            return this.curProcess.waitFor() == 0;
        } catch (Throwable e) {
            Platform.runLater(() ->
                              {
                                  System.out.println(e.getMessage());
                                  ControllerErrorCreator.createErrorDialog("File Compilation",
                                                                           "Error compiling.\nPlease try again with another valid MIPS File.");
                              });
            return false;
        }
    }

    /**
     * Helper method for getting program output
     */
    private void outputToConsole() throws java.io.IOException, java.lang.InterruptedException
    {
        InputStream stdout = this.curProcess.getInputStream();
        InputStream stderr = this.curProcess.getErrorStream();

        BufferedReader outputReader = new BufferedReader(new InputStreamReader(stdout));
        printOutput(outputReader);

        BufferedReader errorReader = new BufferedReader(new InputStreamReader(stderr));
        printOutput(errorReader);
    }

    /**
     * Helper method for printing to console
     *
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    private void printOutput(BufferedReader reader) throws java.io.IOException, java.lang.InterruptedException
    {
        // if the output stream is paused, signal the input thread
        if (!reader.ready()) {
            this.mutex.release();
        }

        int intch;
        // read in program output one character at a time
        while ((intch = reader.read()) != -1) {
            this.mutex.tryAcquire();
            char   ch  = (char) intch;
            String out = Character.toString(ch);
            Platform.runLater(() ->
                              {
                                  // add output to console
                                  this.console.appendText(out);
                                  this.console.requestFollowCaret();
                              });
            // update console length tracker to include output character
            this.consoleLength++;

            // if the output stream is paused, signal the input thread
            if (!reader.ready()) {
                this.mutex.release();
            }
            // wait for input thread to acquire mutex if necessary
            Thread.sleep(1);
        }
        this.mutex.release();
        reader.close();
    }

    /**
     * Helper method for running Mips Program.
     */
    private boolean runMipsFile(File file)
    {
        try {
            Platform.runLater(() ->
                              {
                                  this.console.clear();
                                  consoleLength = 0;
                              });

            ProcessBuilder pb = new ProcessBuilder(Arrays.asList("java",
                                                                 "-jar",
                                                                 "include/Mars4_5.jar",
                                                                 "se1",
                                                                 "nc",
                                                                 file.getAbsolutePath(),
                                                                 "include/exceptions.s"));
            this.curProcess = pb.start();

            // Start output and input in different threads to avoid deadlock
            this.outThread = new Thread()
            {
                public void run()
                {
                    try {
                        // start output thread first
                        mutex.acquire();
                        outputToConsole();
                    } catch (Throwable e) {
                        Platform.runLater(() ->
                                          {
                                              // print stop message if other thread hasn't
                                              if (consoleLength == console.getLength()) {
                                                  console.appendText("\nProgram exited unexpectedly\n");
                                                  console.requestFollowCaret();
                                              }
                                          });
                    }
                }
            };
            outThread.start();

            inThread = new Thread()
            {
                public void run()
                {
                    try {
                        inputFromConsole();
                    } catch (Throwable e) {
                        Platform.runLater(() ->
                                          {
                                              // print stop message if other thread hasn't
                                              if (consoleLength == console.getLength()) {
                                                  console.appendText("\nProgram exited unexpectedly\n");
                                                  console.requestFollowCaret();
                                              }
                                          });
                    }
                }
            };
            inThread.start();

            // true if ran without error, else false
            return curProcess.waitFor() == 0;
        } catch (Throwable e) {
            Platform.runLater(() ->
                              {
                                  ControllerErrorCreator.createErrorDialog("File Running",
                                                                           "Error running " + file.getName() + ".");
                              });
            return false;
        }
    }

    /**
     * Helper method for getting program input
     */
    public void inputFromConsole() throws java.io.IOException, java.lang.InterruptedException
    {
        OutputStream   stdin       = curProcess.getOutputStream();
        BufferedWriter inputWriter = new BufferedWriter(new OutputStreamWriter(stdin));

        while (curProcess.isAlive()) {
            // wait until signaled by output thread
            this.mutex.acquire();
            // write input to program
            writeInput(inputWriter);
            // signal output thread
            this.mutex.release();
            // wait for output to acquire mutex
            Thread.sleep(500);
        }
        inputWriter.close();
    }

    /**
     * Helper function to write user input
     */
    public void writeInput(BufferedWriter writer) throws java.io.IOException
    {
        // wait for user to input line of text
        while (true) {
            if (this.console.getLength() > this.consoleLength) {
                // check if user has hit enter
                if (this.console.getText().substring(this.consoleLength).contains("\n")) {
                    break;
                }
            }
        }
        // write user-entered text to program input
        writer.write(this.console.getText().substring(this.consoleLength));
        writer.flush();
        // update console length to include user input
        this.consoleLength = this.console.getLength();
    }

    /**
     * Handles the Compile button action.
     *
     * @param event Event object
     * @param file  the Selected file
     */
    public void handleAssembleButtonAction(Event event, File file)
    {
        // user select cancel button
        if (this.fileMenuController.checkSaveBeforeScan() == 2) {
            event.consume();
        } else {
            assembleWorker.setFile(file);
            assembleWorker.restart();
        }
    }

    /**
     * Handles the CompileRun button action.
     *
     * @param event Event object
     * @param file  the Selected file
     */
    public void handleAssembleAndRunButtonAction(Event event, File file)
    {
        // user select cancel button
        if (this.fileMenuController.checkSaveBeforeScan() == 2) {
            event.consume();
        } else {
            assembleAndRunWorker.setFile(file);
            assembleAndRunWorker.restart();
        }
    }

    /**
     * Handles the Stop button action.
     */
    public void handleStopButtonAction()
    {
        try {
            if (this.curProcess.isAlive()) {
                this.inThread.interrupt();
                this.outThread.interrupt();
                this.curProcess.destroy();
            }
        } catch (Throwable e) {
            ControllerErrorCreator.createErrorDialog("Program Stop", "Error stopping the program.");
        }
    }

    /**
     * Handles the compile button Action
     */
    public void handleCompileButtonAction(Event event, CodeTab tab)
    {
        Optional<ButtonType> response = this.optDialog.showAndWait();

        if (response.isPresent() && !response.get().getButtonData().isCancelButton()) {
            // compile
            this.semanticAnalyzer = new SemanticAnalyzer(null);

            this.handleParse(event, tab, this.semanticAnalyzer, true);
        }
    }


    /**
     * Parse Worker which manages the Task of creating a Parser, parsing a scanned file,
     * and reporting the results (viz. the root node and whether or not parsing encountered errors
     */
    protected static class ParseWorker extends Service<String>
    {
        public  boolean        isErrorFree; /* whether or not there were any errors parsing,
                                        determining whether or not a tree will be drawn
                                      */
        private ErrorHandler   errorHandler;
        private String         filename;
        private Parser         parser;
        private Program        root;
        private PostParseActor postParseActor;
        private boolean        shouldCompile;

        public void resetFields()
        {
            this.isErrorFree = false;
            this.errorHandler = null;
            this.filename = null;
            this.parser = null;
            this.root = null;
            this.postParseActor = null;
            this.shouldCompile = false;
        }

        public void setFilename(String filename)
        {
            this.filename = filename;
        }

        public void setErrorHandler(ErrorHandler errorHandler)
        {
            this.errorHandler = errorHandler;
        }

        public void setPostParseActor(PostParseActor postParseActor)
        {
            this.postParseActor = postParseActor;
        }

        public void setShouldCompile(boolean shouldCompile)
        {
            this.shouldCompile = shouldCompile;
        }

        /**
         * Attempts to parse the file as set before (re)starting this worker
         *
         * @return
         */
        @Override
        protected Task<String> createTask()
        {
            this.parser = new Parser(this.errorHandler);

            return new Task<String>()
            {
                @Override
                protected String call() throws Exception
                {
                    StringBuilder results = new StringBuilder();

                    try {
                        root = parser.parse(filename);
                        ControlFlowGraphVisitor cfgv = new ControlFlowGraphVisitor();
                        cfgv.visit(root);
                        for (String name: cfgv.methodCfgs.keySet()){
                            ControlFlowGraphAnalyzer cfgna = new ControlFlowGraphAnalyzer(cfgv.methodCfgs.get(name));
                        } 

                    } catch (CompilationException e) {
                        errorHandler.register(Error.Kind.LEX_ERROR, "Compilation Error:\n" + e.getMessage());
                    }

                    // Detect any errors
                    List<Error> errorList  = errorHandler.getErrorList();
                    int         errorCount = errorList.size();
                    if (errorCount == 0) {
                        isErrorFree = true;
                    } else {
                        isErrorFree = false;
                        final int[] parseErrorCount = {0};
                        errorList.forEach((error) ->
                                          {
                                              results.append(error.toString()).append("\n");

                                              if (error.getKind() == Error.Kind.PARSE_ERROR) {
                                                  parseErrorCount[0]++;
                                              }
                                          });
                        results.append(String.format("Found %d (parse) error(s)\n", parseErrorCount[0]));
                    }

                    return results.toString();
                }
            };
        }
    }


    /**
     * Scan Worker which manages the Task of creating a Scanner, scanning through a file,
     * and reporting results
     */
    protected static class ScanWorker extends Service<String>
    {
        private ErrorHandler   errorHandler;
        private String         filename;
        private Scanner        scanner;
        private CustomCodeArea outputArea;

        public void setFilename(String filename)
        {
            this.filename = filename;
        }

        public void resetFields()
        {
            this.errorHandler = null;
            this.filename = null;
            this.scanner = null;
            this.outputArea = null;
        }

        public void setErrorHandler(ErrorHandler errorHandler)
        {
            this.errorHandler = errorHandler;
        }

        public void setOutputArea(CustomCodeArea outputArea)
        {
            this.outputArea = outputArea;
        }

        @Override
        protected Task<String> createTask()
        {
            this.scanner = new Scanner(filename, errorHandler);

            this.outputArea.setEditable(false); //user no touch until we're done

            return new Task<String>()
            {
                @Override
                protected String call()
                {
                    StringBuilder results = new StringBuilder();

                    try {
                        // Scan the file and retrieve each token
                        Token currentToken = scanner.scan();
                        while (currentToken.kind != Token.Kind.EOF) {
                            if (outputArea != null) {
                                String s = currentToken.toString();
                                Platform.runLater(() -> outputArea.appendText(s + "\n"));
                            }

                            currentToken = scanner.scan();
                        }
                    } catch (CompilationException e) {
                        errorHandler.register(Error.Kind.LEX_ERROR, "Compilation Error:\n" + e.getMessage());
                    }

                    // Detect any errors
                    List<Error> errorList  = errorHandler.getErrorList();
                    int         errorCount = errorList.size();
                    if (errorCount == 0) {
                        results.append("No errors detected\n");
                    } else {
                        errorList.forEach((error) ->
                                          {
                                              results.append(error.toString()).append("\n");
                                          });
                        results.append(String.format("Found %d error(s)\n", errorCount));
                    }

                    return results.toString();
                }
            };
        }
    }


    /**
     * A AssembleWorker subclass handling Java program compiling in a separated thread in the background.
     * AssembleWorker extends the javafx Service class.
     */
    protected class AssembleWorker extends Service<Boolean>
    {
        /**
         * the file to be compiled.
         */
        private File file;

        /**
         * Sets the selected file.
         *
         * @param file the file to be compiled.
         */
        private void setFile(File file)
        {
            this.file = file;
        }

        /**
         * Overrides the createTask method in Service class.
         * Compiles the file embedded in the selected tab, if appropriate.
         *
         * @return true if the program compiles successfully;
         * false otherwise.
         */
        @Override
        protected Task<Boolean> createTask()
        {
            return new Task<Boolean>()
            {
                /**
                 * Called when we execute the start() method of a AssembleAndRunWorker object
                 * Compiles the file.
                 *
                 * @return true if the program compiles successfully;
                 *         false otherwise.
                 */
                @Override
                protected Boolean call()
                {
                    Boolean assembleResult = assembleMipsFile(file);
                    if (assembleResult) {
                        Platform.runLater(() -> console.appendText("Assemblation was successful!\n"));
                    }
                    return assembleResult;
                }
            };
        }
    }


    /**
     * A AssembleAndRunWorker subclass handling Java program compiling and running in a separated thread in the background.
     * AssembleWorker extends the javafx Service class.
     */
    protected class AssembleAndRunWorker extends Service<Boolean>
    {
        /**
         * the file to be compiled.
         */
        private File file;

        /**
         * Sets the selected file.
         *
         * @param file the file to be compiled.
         */
        private void setFile(File file)
        {
            this.file = file;
        }

        /**
         * Overrides the createTask method in Service class.
         * Compiles and runs the file embedded in the selected tab, if appropriate.
         *
         * @return true if the program runs successfully;
         * false otherwise.
         */
        @Override
        protected Task<Boolean> createTask()
        {
            return new Task<Boolean>()
            {
                /**
                 * Called when we execute the start() method of a AssembleAndRunWorker object.
                 * Compiles the file and runs it if compiles successfully.
                 *
                 * @return true if the program runs successfully;
                 *         false otherwise.
                 */
                @Override
                protected Boolean call()
                {
                    Boolean runResult = runMipsFile(file);
                    if (runResult) {
                        Platform.runLater(() -> console.appendText("Program has finished running\n"));
                    }
                    return runResult;
                }
            };
        }
    }
}