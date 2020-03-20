/*
 * File: SemanticAnalyzer.java
 * S19 CS461 Project 12
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 2/25/19
 * This file runs the semantic analyzer to check a Bantam Java file
 */

/* Bantam Java Compiler and Language Toolset.

   Copyright (C) 2009 by Marc Corliss (corliss@hws.edu) and
                         David Furcy (furcyd@uwosh.edu) and
                         E Christopher Lewis (lewis@vmware.com).
   ALL RIGHTS RESERVED.

   The Bantam Java toolset is distributed under the following
   conditions:

     You may make copies of the toolset for your own use and
     modify those copies.

     All copies of the toolset must retain the author names and
     copyright notice.

     You may not sell the toolset or distribute it in
     conjunction with a commerical product or service without
     the expressed written consent of the authors.

   THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS
   OR IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE
   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
   PARTICULAR PURPOSE.

   This file was modified by Dale Skrien, February, 2019.
*/

package proj19DeutschDurstSavillo.bantam.semant;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.parser.Parser;
import proj19DeutschDurstSavillo.bantam.util.ClassTreeNode;
import proj19DeutschDurstSavillo.bantam.util.CompilationException;
import proj19DeutschDurstSavillo.bantam.util.Error;
import proj19DeutschDurstSavillo.bantam.util.ErrorHandler;
import proj19DeutschDurstSavillo.interfaces.PostParseActor;

import java.util.*;


/**
 * The <tt>SemanticAnalyzer</tt> class performs semantic analysis.
 * In particular this class is able to perform (via the <tt>analyze()</tt>
 * method) the following tests and analyses: (1) legal inheritence
 * hierarchy (all classes have existing parent, no cycles), (2)
 * legal class member declaration, (3) there is a correct bantam.Main class
 * and main() method, and (4) each class member is correctly typed.
 * <p>
 * This class is incomplete and will need to be implemented by the student.
 */
public class SemanticAnalyzer
        implements PostParseActor
{

    /**
     * reserved words that are tokens of type ID, but cannot be declared as the
     * names of (a) classes, (b) methods, (c) fields, (d) variables.
     * These words are:  null, this, super, void, int, boolean.
     * However, class names can be used as variable names.
     */
    public static final Set<String> reservedIdentifiers = new HashSet<>(Arrays.asList(
            "null", "this", "super", "void", "int", "boolean"));
    /**
     * Maximum number of inherited and non-inherited fields that can be defined for any
     * one class
     */
    private final int MAX_NUM_FIELDS = 1500;
    /**
     * error handling
     */
    public ErrorHandler errorHandler;

    public ClassTreeNode getRoot()
    {
        return root;
    }

    public void setRoot(ClassTreeNode root)
    {
        this.root = root;
    }

    /**
     * Root of the AST
     */
    private Program program;
    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;
    /**
     * Maps class names to ClassTreeNode objects representing the class
     */
    private Hashtable<String, ClassTreeNode> classMap = new Hashtable<String, ClassTreeNode>();

    /**
     * SemanticAnalyzer constructor
     *
     * @param errorHandler the ErrorHandler to use for reporting errors
     */
    public SemanticAnalyzer(ErrorHandler errorHandler)
    {
        this.errorHandler = errorHandler;
    }

    /**
     * Test code for SemanticAnalyzer
     */
    public static void main(String[] args)
    {
        //make sure at least one filename was given
        if (args.length < 1) {
            System.err.println("Missing input filename");
            System.exit(-1);
        }

        ErrorHandler     errorHandler = new ErrorHandler();
        Parser           parser       = new Parser(errorHandler);
        SemanticAnalyzer analyzer     = new SemanticAnalyzer(errorHandler);

        // for each file, parse and check
        for (String filename : args) {
            System.out.println("Scanning and Parsing file: " + filename + "\n");

            // parse and analyze
            try {
                Program program = parser.parse(filename);
                analyzer.analyze(program);
            } catch (CompilationException e) {
                System.out.println("Parsing error encountered:\n");
            }

            // check for errors
            if (errorHandler.errorsFound()) {
                errorHandler.getErrorList().forEach(error -> System.out.println(error.toString()));
                System.out.println(String.format("\n%d errors found", errorHandler.getErrorList().size()));
            } else {
                System.out.println("\nParsing and semantic analysis successful");
            }

            System.out.println("-----------------------------------------------");

            //clear errors to parse next file
            errorHandler.clear();
        }
    }

    @Override
    public Object act(ASTNode root, Map<String, Object> knowledge)
    {
        this.analyze((Program) root);

        if (this.errorHandler.getErrorList().size() == 0) {
            return "Semantic analysis completed successfully";
        }

        StringBuilder errors = new StringBuilder();
        this.errorHandler.getErrorList().forEach((error) ->
                                                 {
                                                     errors.append(error.toString()).append("\n");
                                                 });
        return "Errors:\n" + errors.toString();
    }

    /**
     * Analyze the AST checking for semantic errors and annotating the tree
     * Also builds an auxiliary class hierarchy tree
     *
     * @param program root of the AST to be checked
     * @return root of the class hierarchy tree (needed for code generation)
     * <p>
     * Must add code to do the following:
     * 1 - add built-in classes in classMap (already done)
     * 2 - add user-defined classes and build the inheritance tree of ClassTreeNodes
     * 3 - build the environment for each class (add class members only) and check
     * that members are declared properly
     * 4 - check that the Main class and main method are declared properly
     * 5 - type check everything
     * See the lab manual for more details on each of these steps.
     */
    public ClassTreeNode analyze(Program program)
    {
        this.program = program;
        this.classMap.clear();
        this.errorHandler.clear();

        // step 1:  add built-in classes to classMap
        addBuiltins();

        // step 2: add user-defined classes to classMap and build inheritance tree
        addUserDefinedClasses();
        buildInheritanceTree();

        // step 3: add class members to symbol tables
        buildClassEnvironments();

        // step 4: check that main class and main method are declared properly
        checkMainMain();

        // step 5: check types
        checkTypes();

        return root;
    }

    /**
     * Add built-in classes to the classMap.
     * These are the classes Object, String, Sys, and TextIO
     */
    private void addBuiltins()
    {
        // create AST node for object
        Class_ astNode = new Class_(-1, "<built-in class>", "Object", null,
                                    (MemberList) (new MemberList(-1)).addElement(new Method(-1, "Object",
                                                                                            "clone", new FormalList(-1),
                                                                                            (StmtList) (new StmtList(-1)).addElement(
                                                                                                    new ReturnStmt(-1,
                                                                                                                   new VarExpr(
                                                                                                                           -1,
                                                                                                                           null,
                                                                                                                           "null"))))).addElement(
                                            new Method(-1,
                                                       "boolean",
                                                       "equals",
                                                       (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                               "Object",
                                                                                                               "o")),
                                                       (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                               new ConstBooleanExpr(
                                                                                                                       -1,
                                                                                                                       "false"))))).addElement(
                                            new Method(-1,
                                                       "String",
                                                       "toString",
                                                       new FormalList(-1),
                                                       (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                               new VarExpr(
                                                                                                                       -1,
                                                                                                                       null,
                                                                                                                       "null"))))));
        // create a class tree node for object, save in variable root
        root = new ClassTreeNode(astNode, /*built-in?*/true, /*extendable?*/true,
                                 classMap);
        // add object class tree node to the mapping
        classMap.put("Object", root);

        // note: String, TextIO, and Sys all have fields that are not shown below.
        // Because these classes cannot be extended and their fields are protected,
        // they cannot be
        // accessed by other classes, so they do not have to be included in the AST.

        // create AST node for String
        astNode = new Class_(-1, "<built-in class>", "String", "Object",
                             (MemberList) (new MemberList(-1)).addElement(new Field(-1, "int",
                                                                                    "length", /*0 by default*/null))
                                     /* note: str is the character sequence -- no applicable type for a
                                    character sequence so it is just made an int.  it's OK to
                                    do this since this field is only accessed (directly) within
                                    the runtime system */.addElement(new Method(-1, "int", "length",
                                                                   new FormalList(-1),
                                                                   (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(
                                                                           -1,
                                                                           new ConstIntExpr(-1,
                                                                                            "0"))))).addElement(new Method(
                                             -1,
                                             "boolean",
                                             "equals",
                                             (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                     "Object",
                                                                                                     "str")),
                                             (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                     new ConstBooleanExpr(
                                                                                                             -1,
                                                                                                             "false"))))).addElement(
                                             new Method(-1,
                                                        "String",
                                                        "toString",
                                                        new FormalList(-1),
                                                        (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                                new VarExpr(
                                                                                                                        -1,
                                                                                                                        null,
                                                                                                                        "null"))))).addElement(
                                             new Method(-1,
                                                        "String",
                                                        "substring",
                                                        (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                                "int",
                                                                                                                "beginIndex")).addElement(
                                                                new Formal(-1, "int", "endIndex")),
                                                        (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                                new VarExpr(
                                                                                                                        -1,
                                                                                                                        null,
                                                                                                                        "null"))))).addElement(
                                             new Method(-1,
                                                        "String",
                                                        "concat",
                                                        (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                                "String",
                                                                                                                "str")),
                                                        (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                                new VarExpr(
                                                                                                                        -1,
                                                                                                                        null,
                                                                                                                        "null"))))));
        // create class tree node for String, add it to the mapping
        classMap.put("String", new ClassTreeNode(astNode, /*built-in?*/true,
                /*extendable?*/false, classMap));

        // create AST node for TextIO
        astNode = new Class_(-1, "<built-in class>", "TextIO", "Object",
                             (MemberList) (new MemberList(-1)).addElement(new Field(-1,
                                                                                    "int",
                                                                                    "readFD", /*0 by default*/
                                                                                    null)).addElement(new Field(-1,
                                                                                                                "int"
                                     ,
                                                                                                                "writeFD",
                                                                                                                new ConstIntExpr(
                                                                                                                        -1,
                                                                                                                        "1"))).addElement(
                                     new Method(-1,
                                                "void",
                                                "readStdin",
                                                new FormalList(-1),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        null)))).addElement(
                                     new Method(-1,
                                                "void",
                                                "readFile",
                                                (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                        "String",
                                                                                                        "readFile")),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        null)))).addElement(
                                     new Method(-1,
                                                "void",
                                                "writeStdout",
                                                new FormalList(-1),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        null)))).addElement(
                                     new Method(-1,
                                                "void",
                                                "writeStderr",
                                                new FormalList(-1),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        null)))).addElement(
                                     new Method(-1,
                                                "void",
                                                "writeFile",
                                                (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                        "String",
                                                                                                        "writeFile")),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        null)))).addElement(
                                     new Method(-1,
                                                "String",
                                                "getString",
                                                new FormalList(-1),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        new VarExpr(-1,
                                                                                                                    null,
                                                                                                                    "null"))))).addElement(
                                     new Method(-1,
                                                "int",
                                                "getInt",
                                                new FormalList(-1),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        new ConstIntExpr(
                                                                                                                -1,
                                                                                                                "0"))))).addElement(
                                     new Method(-1,
                                                "TextIO",
                                                "putString",
                                                (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                        "String",
                                                                                                        "str")),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        new VarExpr(-1,
                                                                                                                    null,
                                                                                                                    "null"))))).addElement(
                                     new Method(-1,
                                                "TextIO",
                                                "putInt",
                                                (FormalList) (new FormalList(-1)).addElement(new Formal(-1,
                                                                                                        "int",
                                                                                                        "n")),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        new VarExpr(-1,
                                                                                                                    null,
                                                                                                                    "null"))))));
        // create class tree node for TextIO, add it to the mapping
        classMap.put("TextIO", new ClassTreeNode(astNode, /*built-in?*/true,
                /*extendable?*/false, classMap));

        // create AST node for Sys
        astNode = new Class_(-1, "<built-in class>", "Sys", "Object",
                             (MemberList) (new MemberList(-1)).addElement(new Method(-1, "void",
                                                                                     "exit",
                                                                                     (FormalList) (new FormalList(-1)).addElement(
                                                                                             new Formal(-1,
                                                                                                        "int",
                                                                                                        "status")),
                                                                                     (StmtList) (new StmtList(-1)).addElement(
                                                                                             new ReturnStmt(-1,
                                                                                                            null))))
                                     /* MC: time() and random() requires modifying SPIM to add a time system
                                      call
                                    (note: random() does not need its own system call although it uses the time
                                    system call).  We have a version of SPIM with this system call available,
                                    otherwise, just comment out. (For x86 and jvm there are no issues.)
                                    */.addElement(new Method(-1, "int", "time", new FormalList(-1),
                                                (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                        new ConstIntExpr(
                                                                                                                -1,
                                                                                                                "0"))))).addElement(
                                             new Method(-1,
                                                        "int",
                                                        "random",
                                                        new FormalList(-1),
                                                        (StmtList) (new StmtList(-1)).addElement(new ReturnStmt(-1,
                                                                                                                new ConstIntExpr(
                                                                                                                        -1,
                                                                                                                        "0"))))));
        // create class tree node for Sys, add it to the mapping
        classMap.put("Sys", new ClassTreeNode(astNode, /*built-in?*/true, /*extendable
        ?*/false, classMap));
    }

    /**
     * Add user defined classes to the classMap.
     */
    private void addUserDefinedClasses()
    {
        // build ClassTreeNode for each Class_ node in AST
        for (ASTNode node : program.getClassList()) {
            Class_ classNode = (Class_) node;
            String className = classNode.getName();
            // register error and skip this class if class of same name is already defined
            if (classMap.containsKey(className)) {
                errorHandler.register(Error.Kind.SEMANT_ERROR, classNode.getFilename(),
                                      classNode.getLineNum(), "Duplicate class: " + className);
            } else {
                classMap.put(className, new ClassTreeNode(classNode, false, true, classMap));
            }
        }
    }

    /**
     * Build the inheritance tree of classTreeNodes.
     */
    private void buildInheritanceTree()
    {
        // connect inheritance
        for (ClassTreeNode classTreeNode : classMap.values()) {
            Class_ classNode = classTreeNode.getASTNode();
            // Object class does not inherit from anything
            if (classTreeNode == root) {
                continue;
            }

            // if no declared parent, inherits from Object
            if (classNode.getParent().equals("")) {
                root.addChild(classTreeNode);
            }
            // otherwise connect to inheritance tree
            else {
                // register error if inherits from undefined class
                if (!classMap.containsKey(classNode.getParent())) {
                    root.addChild(classTreeNode);
                    errorHandler.register(Error.Kind.SEMANT_ERROR, classNode.getFilename(),
                                          classNode.getLineNum(),
                                          "Cannot find definition for parent class " + classNode.getParent());
                } else {
                    ClassTreeNode parent = classMap.get(classNode.getParent());
                    // check for cycles
                    boolean cycleDetected = false;
                    for (ClassTreeNode ctn = parent; ctn != null; ctn = ctn.getParent()) {
                        // register error if cycle detected
                        if (ctn.getParent() == classTreeNode) {
                            errorHandler.register(Error.Kind.SEMANT_ERROR, classNode.getFilename(),
                                                  classNode.getLineNum(),
                                                  "Cyclic inheritance involving " + classNode.getName());
                            cycleDetected = true;
                            break;
                        }
                    }
                    if (!cycleDetected) {
                        parent.addChild(classTreeNode);
                    } else {
                        root.addChild(classTreeNode);
                    }
                }
            }
        }
    }

    /**
     * Add class members to class symbol tables.
     */
    private void buildClassEnvironments()
    {
        // initialize symbol tables
        for (ClassTreeNode ctn : classMap.values()) {
            ctn.getVarSymbolTable().enterScope();
            ctn.getMethodSymbolTable().enterScope();
            if (ctn.isBuiltIn()) {
                this.program.getClassList().addElement(ctn.getASTNode());
            }
        }

        // populate symbol tables
        ClassMemberVisitor classMemberVisitor = new ClassMemberVisitor();
        classMemberVisitor.addMembers(this.program, this.root, this.errorHandler);

        // check number of fields
        for (ClassTreeNode ctn : classMap.values()) {
            int numFields = ctn.getVarSymbolTable().getSize();
            if (numFields > MAX_NUM_FIELDS) {
                errorHandler.register(Error.Kind.SEMANT_ERROR,
                                      "Class " + ctn.getName() + " has " + numFields
                                              + " declared which is greater than the maximum of " + MAX_NUM_FIELDS);
            }
        }
    }

    /**
     * Checks for valid Main.main method, registers error if not found
     */
    private void checkMainMain()
    {
        // note: only need to create a single class; it handles resetting itself
        MainMainVisitor mainMainVisitor = new MainMainVisitor();
        mainMainVisitor.mainMainCheck(this.root, this.program, this.errorHandler);
    }

    /**
     * Checks the types for all class members
     */
    private void checkTypes()
    {
        TypeCheckerVisitor typeCheckerVisitor = new TypeCheckerVisitor();
        typeCheckerVisitor.typeCheck(this.root, this.program, this.errorHandler);
    }

    /**
     * @return the ErrorHandler for this Parser
     */
    public ErrorHandler getErrorHandler() { return errorHandler; }

    public Hashtable<String, ClassTreeNode> getClassMap()
    {
        return this.classMap;
    }
}