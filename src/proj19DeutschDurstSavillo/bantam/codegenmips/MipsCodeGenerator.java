/*
 * File: MipsCodeGenerator.java
 * S19 CS461 Project 16
 * Names: Martin Deutsch, Robert Durst, Evan Savillo
 * Date: 4/11/2019
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
*/

package proj19DeutschDurstSavillo.bantam.codegenmips;

import proj19DeutschDurstSavillo.Main;
import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.astopt.ConstantFoldingVisitor;
import proj19DeutschDurstSavillo.bantam.astopt.ConstantPropVisitor;
import proj19DeutschDurstSavillo.bantam.parser.Parser;
import proj19DeutschDurstSavillo.bantam.semant.SemanticAnalyzer;
import proj19DeutschDurstSavillo.bantam.util.Error;
import proj19DeutschDurstSavillo.bantam.util.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * The <tt>MipsCodeGenerator</tt> class generates mips assembly code
 * targeted for the SPIM or Mars emulators.
 * <p/>
 * This class is incomplete and will need to be implemented by the student.
 */
public class MipsCodeGenerator
{
    /**
     * Root of the class hierarchy tree
     */
    private ClassTreeNode root;

    /**
     * Root of the AST
     */
    private Program program;

    /**
     * Print stream for output assembly file
     */
    private PrintStream out;

    /**
     * Assembly support object (using Mips assembly support)
     */
    private OurMipsSupport assemblySupport;

    /**
     * Boolean indicating whether garbage collection is enabled
     */
    private boolean gc = false;

    /**
     * Booleans indicating what optimizations are enabled {unrolling, folding}
     */
    private boolean[] opt = {false, false};

    /**
     * Boolean indicating whether debugging is enabled
     */
    private boolean debug = false;

    /**
     * for recording any errors that occur.
     */
    private ErrorHandler errorHandler;

    /**
     * For establishing an enumeration for all the various classes
     * Keys are class names, values are class id numbers
     */
    private HashMap<String, String> classEnumeration;

    /**
     * MipsCodeGenerator constructor
     *
     * @param errorHandler ErrorHandler to record all errors that occur
     * @param gc           boolean indicating whether garbage collection is enabled
     * @param opt          list of booleans denoting which optimizations are enabled
     */
    public MipsCodeGenerator(ErrorHandler errorHandler, boolean gc, boolean[] opt)
    {
        this.gc = gc;
        this.opt = opt;
        this.errorHandler = errorHandler;
    }

    /**
     * Run entire compilation process on files given on command line
     */
    public static void main(String[] args)
    {
        // make sure at least one filename was given
        if (args.length < 1) {
            System.err.println("Missing input filename");
            System.exit(-1);
        }

        ErrorHandler      errorHandler      = new ErrorHandler();
        Parser            parser            = new Parser(errorHandler);
        SemanticAnalyzer  analyzer          = new SemanticAnalyzer(errorHandler);
        MipsCodeGenerator mipsCodeGenerator = new MipsCodeGenerator(errorHandler, false, new boolean[] {false, false});

        // compile each file
        for (String filename : args) {
            // skip flag commands
            if (filename.startsWith("--"))
                continue;

            System.out.println("Compiling file: " + filename + "\n");

            int suffixIndex = filename.indexOf(".btm");
            if (suffixIndex == -1) {
                System.out.println("Error: File must be of type .btm");
                continue;
            }

            // parse and analyze
            try {
                Program       program = parser.parse(filename);
                ClassTreeNode ctn     = analyzer.analyze(program);
                if (!errorHandler.errorsFound()) {
                    String outfileName = filename.substring(0, suffixIndex) + ".asm";
                    mipsCodeGenerator.generate(ctn, outfileName);
                }
            } catch (CompilationException e) {
                System.out.println("Parsing error encountered:\n");
            }

            // check for errors
            if (errorHandler.errorsFound()) {
                errorHandler.getErrorList().forEach(error -> System.out.println(error.toString()));
                System.out.println(String.format("\n%d errors found", errorHandler.getErrorList().size()));
            } else {
                System.out.println("\nCompilation successful");
            }

            System.out.println("-----------------------------------------------");

            //clear errors to parse next file
            errorHandler.clear();
        }
    }

    /**
     * Generate assembly file
     * <p/>
     * In particular, you will need to do the following:
     * 1 - start the data section
     * 2 - generate data for the garbage collector
     * 3 - generate string constants
     * 4 - generate class name table
     * 5 - generate object templates
     * 6 - generate dispatch tables
     * 7 - start the text section
     * 8 - generate initialization subroutines
     * 9 - generate user-defined methods
     * See the lab manual for the details of each of these steps.
     *
     * @param root    root of the class hierarchy tree
     * @param outFile filename of the assembly output file
     */
    public void generate(ClassTreeNode root, String outFile)
    {
        this.root = root;

        // set up the PrintStream for writing the assembly file.
        try {
            this.out = new PrintStream(new FileOutputStream(outFile));
            this.assemblySupport = new OurMipsSupport(out);
        } catch (IOException e) {
            // if don't have permission to write to file then throw an exception
            errorHandler.register(Error.Kind.CODEGEN_ERROR, "IOException when writing " +
                    "to file: " + outFile);
            throw new CompilationException("Couldn't write to output file.");
        }

        // build new Program node containing built-in and user-defined classes
        ClassList     classList   = new ClassList(0);
        AtomicInteger enumCounter = new AtomicInteger();

        this.classEnumeration = new LinkedHashMap<>();
        root.getClassMap().forEach((className, classTreeNode) ->
                                   {
                                       classList.addElement(classTreeNode.getASTNode());
                                   });
        this.program = new Program(0, classList);
        this.addArrayTypesToHierarchy(program);
        this.preorderTraverseClassHierarchy(root, enumCounter);

        // optimize ast if optimization is enabled
        if (this.opt[1]) {
            this.optimize();
        }

        // begin MIPS program generation
        final String headerComment = "File: " + outFile + "\n" +
                "S19 CS461 " + Main.getProject() + "\n" +
                "Names: Martin Deutsch, Robert Durst, and Evan Savillo\n" +
                "Date: 4/11/2019\n";
        writeMultilineComment(headerComment);

        this.writeSectionDividerComment(".data");
        this.assemblySupport.genDataStart();
        this.genGarbageCollectorData();


        // generate data for string constants in program
        this.writeSectionDividerComment("String Constants");
        StringConstantsVisitor stringConstantsVisitor = new StringConstantsVisitor();
        // stringsMap: keys are string constants, values are unique ids
        Map<String, String>    stringsMap             = stringConstantsVisitor.getStringConstants(program);
        this.genStringConstants(stringsMap);
        this.genString(outFile, "filename");

        // generate class name string
        this.writeSectionDividerComment("Class Name Strings");
        this.genClassNames(stringsMap);

        this.writeSectionDividerComment("Object Templates");
        // generate globals list for templates
        root.getClassMap().forEach((className, classTreeNode) ->
        {
            if (!className.endsWith("[]"))
                this.assemblySupport.genGlobal(className + "_template");
        });
        this.assemblySupport.genGlobal("Array_template");
        this.assemblySupport.genGlobal("Lambda_template");
        // generate object templates
        this.genObjectTemplates();

        // generate globals list for dispatch table
        root.getClassMap().forEach((className, classTreeNode) ->
                                   {
                                       if (!className.endsWith("[]"))
                                           this.assemblySupport.genGlobal(className + "_dispatch_table");
                                   });

        // generate dispatch tables for classes
        this.writeSectionDividerComment("Dispatch Tables");
        root.getClassMap().forEach((className, classTreeNode) ->
                {
                    if (!className.endsWith("[]"))
                        genDispatchTable(classTreeNode);
                });

        // get mapping of lambdas to unique identifiers
        LambdaVisitor lambdaVisitor = new LambdaVisitor();
        Map<LambdaExpr, String> lambdaNameMap = lambdaVisitor.getLambdaNameMap(program);
        // generate lambda dispatch tables
        lambdaNameMap.forEach((lambdaNode, name) -> {
                this.assemblySupport.genGlobal(name + "_dispatch_table");
                this.assemblySupport.genLabel(name + "_dispatch_table");
                this.assemblySupport.genWord(name);
        });

        // ------- generate .text section --------- //
        this.writeSectionDividerComment(".text");
        this.assemblySupport.genTextStart();

        // generate class init labels
        ClassInitVisitor classInitVisitor = new ClassInitVisitor();
        classInitVisitor.genClassInitSubroutines(this.root, lambdaNameMap,
                                                 program,
                                                 this.assemblySupport,
                                                 stringsMap,
                                                 this.classEnumeration, this.gc);

        // determine number of local vars in each method
        NumLocalVarsVisitor numLocalVarsVisitor = new NumLocalVarsVisitor();
        Map<String, Integer> numLocalVarsMap = numLocalVarsVisitor.getNumLocalVars(program);

        // generate user-defined method labels
        UserDefinedMethodVisitor userDefinedMethodVisitor = new UserDefinedMethodVisitor(this.opt[0]);
        if (this.gc) {
            userDefinedMethodVisitor.enableGC();
        }
        userDefinedMethodVisitor.genUserDefinedMethods(this.root,
                                                       program,
                                                       this.assemblySupport,
                                                       stringsMap,
                                                       numLocalVarsMap,
                                                       this.classEnumeration,
                                                       lambdaNameMap);

        // generate lambda methods and fill out map
        lambdaVisitor.generateLambdaMethods(userDefinedMethodVisitor, program, root);

        this.assemblySupport.writeToOut();
    }

    /**
     * Perform constant propagation and folding on the AST
     */
    private void optimize() {
        ConstantPropVisitor constantPropVisitor = new ConstantPropVisitor();
        ConstantFoldingVisitor constantFoldingVisitor = new ConstantFoldingVisitor();
        do {
            this.program = constantPropVisitor.constantPropAST(program);
            this.program = constantFoldingVisitor.constantFoldAST(program);
        }
        while (constantFoldingVisitor.madeModifications());
    }

    /**
     * Traverse the class hierarchy tree in preorder and add classes to class map
     * @param node the ClassTreeNode to visit
     * @param i the id of the class
     */
    private void preorderTraverseClassHierarchy(ClassTreeNode node, AtomicInteger i)
    {
        if (node == null) {
            return;
        }
        this.classEnumeration.put(node.getName(), String.valueOf(i.getAndIncrement()));
        Iterator<ClassTreeNode> iterator = node.getChildrenList();
        while (iterator.hasNext()) {
            preorderTraverseClassHierarchy(iterator.next(), i);
        }
    }

    /**
     * genString encapsulates the generation of string constant code
     *
     * @param name   - the string value
     * @param header - the label value
     */
    private void genString(String name, String header)
    {
        // calc bytes, adding sixteen for the base and one for the null terminator
        int bytes = roundUpToWord(name.length() + 16 + 1);

        this.assemblySupport.genLabel(header);
        this.assemblySupport.genWord(this.classEnumeration.get("String"));
        this.assemblySupport.genWord(Integer.toString(bytes));
        this.assemblySupport.genWord("String_dispatch_table");
        this.assemblySupport.genWord(Integer.toString(name.length()));
        this.assemblySupport.genAsciiz(name);
    }

    /**
     * roundUpToWord helps generator methods adhere to MIPS's rule that each
     * data structure must be a multiple of a word, aka 4 bytes.
     *
     * @param bytes - number of bytes before rounding up
     * @return - number of bytes after rounding up
     */
    private int roundUpToWord(int bytes)
    {
        return bytes + (4 - bytes % 4) % 4;
    }

    /**
     * genTable encapsulates generation logic for tables
     *
     * @param header - the label value
     * @param values - a list of values to add to the table
     */
    private void genTable(String header, Collection<String> values)
    {
        this.assemblySupport.genLabel(header);
        values.forEach((name) -> this.assemblySupport.genWord(name));
    }

    /**
     * generate the gc_flag label and its contents
     */
    private void genGarbageCollectorData()
    {
        this.assemblySupport.genLabel("gc_flag");
        int gc_flag = this.gc ? 1 : 0;
        this.assemblySupport.genWord(Integer.toString(gc_flag));
    }

    /**
     * generate the data for each string constant in the program
     *
     * @param stringsMap mapping of string constants to unique identifiers
     */
    private void genStringConstants(Map<String, String> stringsMap)
    {
        for (String stringConst : stringsMap.keySet()) {
            String stringStrippedQuotes = stringConst.substring(1, stringConst.length() - 1);
            this.genString(stringStrippedQuotes, stringsMap.get(stringConst));
        }
    }

    /**
     * genTable encapsulates generation logic for dispatch tables
     *
     * @param node - the classTreeNode representing the class to generate a dispatch table for
     */
    private void genDispatchTable(ClassTreeNode node)
    {
        ClassTreeNode currentClass = node;
        this.assemblySupport.genLabel(node.getName() + "_dispatch_table");

        // ascend up inheritance tree, adding new unseen methods or moving overriding
        // methods to front
        // uniqueMethods: keys are method names, values are of form class.methodName
        Map<String, String> uniqueMethods = new LinkedHashMap<>();
        while (node != null) {
            final String className = node.getName();
            node.getASTNode().getMemberList().forEach((member) ->
                                                      {
                                                          if (member instanceof Method) {
                                                              String methodName = ((Method) member).getName();
                                                              // if method is overridden, move overriding method to back of hashmap
                                                              if (uniqueMethods.containsKey(methodName)) {
                                                                  String classAndMethod = uniqueMethods.remove(
                                                                          methodName);
                                                                  uniqueMethods.put(methodName, classAndMethod);
                                                              }
                                                              // add unique method to hashmap
                                                              else {
                                                                  uniqueMethods.put(methodName,
                                                                                    className + "." + methodName);
                                                              }
                                                          }
                                                      });

            node = node.getParent();
        }

        // add in reverse order so parent methods are first
        List<String> keyList = new ArrayList<>(uniqueMethods.keySet());
        int offset = 0;
        for (int i = uniqueMethods.size() - 1; i >= 0; i--) {
            // store className.methodName in dispatch table
            String methodName = keyList.get(i);
            this.assemblySupport.genWord(uniqueMethods.get(methodName));
            // store location of method in symbol table
            currentClass.getMethodSymbolTable().set(methodName, new Location("$a0", offset));
            offset += 4;
        }
    }

    /**
     * Generate strings storing class names and the class name table
     */
    private void genClassNames(Map<String, String> stringsMap)
    {
        // collect class names to feed into table afterwards
        ArrayList<String> classNames = new ArrayList<>();
        // safe since otherwise must be final to be used in a lambda
        this.classEnumeration.forEach((className, classTreeNode) ->
                                   {
                                       if (stringsMap.keySet().contains("\"" + className + "\"")) {
                                           classNames.add(stringsMap.get("\"" + className + "\""));
                                       }
                                       else {
                                           String header = "class_name_" + this.classEnumeration.get(className);
                                           genString(className, header);
                                           classNames.add(header);
                                       }
                                   });

        // generate class name table
        genTable("class_name_table", classNames);
    }

    /**
     * Given a string written with line breaks, separates the string at those breaks
     * into a multiline comment in mips
     */
    private void writeMultilineComment(String comment)
    {
        String[] lines = comment.split("\n");
        for (String line : lines) {
            this.assemblySupport.genComment(line);
        }
    }

    /**
     * Writes a comment inserted into a fancy dividing line
     */
    private void writeSectionDividerComment(String comment)
    {
        StringBuilder sectionDivider = new StringBuilder("_/--[ " + comment + " ]");

        for (int remaining = 74 - sectionDivider.length(); remaining > 0; --remaining) {
            sectionDivider.append("-");
        }

        this.assemblySupport.genComment(sectionDivider.toString());
    }

    /**
     * Generate object templates for all classes
     */
    private void genObjectTemplates()
    {
        // Write out comments in the asm file
        final String comment = "Object templates are written according to the following scheme:\n" +
                "<classname>_template:\n" +
                "    .word <numeric identifier>\n" +
                "    .word <size in bytes>\n" +
                "    .word <dispatch table pointer>\n" +
                "    .word <field 0>\n" +
                "    ...\n" +
                "    .word <field n - 1>";
        writeMultilineComment(comment);

        Hashtable<String, ClassTreeNode> classes = this.root.getClassMap();
        for (String className : classes.keySet()) {
            // skip arrays as they are all based off one template
            if (className.endsWith("[]")) {
                continue;
            }

            int           fieldCount   = 0;
            ClassTreeNode currentClass = classes.get(className);

            // search for fields in the membersList of a class node,
            // and while there are parents, search them too
            do {
                for (ASTNode member : currentClass.getASTNode().getMemberList()) {
                    if (member instanceof Field) {
                        fieldCount++;
                    }
                }
            } while ((currentClass = currentClass.getParent()) != null);

            genObjectTemplate(className, className, this.classEnumeration.get(className), fieldCount);
        }
        // generate single array template
        genObjectTemplate("Array", "Object", "0", 1);
        genObjectTemplate("Lambda", "Object", "0", 1);
    }

    /**
     * Generate an object template according to the following scheme:
     * <classname>_template:
     * .word <numeric identifier>
     * .word <size in bytes>
     * .word <dispatch table pointer>
     * .word <field 0>
     * ...
     * .word <field n - 1>
     */
    private void genObjectTemplate(String classname, String dispatchName, String numericID, int numfields)
    {
        String size = String.valueOf(12 + (numfields * 4));

        this.assemblySupport.genLabel(classname + "_template");
        this.assemblySupport.genWord(numericID);
        this.assemblySupport.genWord(size);
        this.assemblySupport.genWord(dispatchName + "_dispatch_table");

        for (int i = 0; i < numfields; ++i) {
            this.assemblySupport.genWord("0");
        }
    }

    /**
     * Add the array types to the class hierarchy tree
     */
    private void addArrayTypesToHierarchy(Program ast) {
        // get all types appearing in program
        ArrayTypesVisitor arrayTypesVisitor = new ArrayTypesVisitor();
        List<String> arrayTypes = arrayTypesVisitor.getArrayTypes(ast);

        // add types to class map
        Hashtable<String, ClassTreeNode> classMap = this.root.getClassMap();
        for (String type : arrayTypes) {
            Class_ class_ = new Class_(0, this.root.getASTNode().getFilename(), type, null, null);
            ClassTreeNode arrayClass = new ClassTreeNode(class_, false, true, classMap);
            classMap.put(type, arrayClass);
        }

        // set root type
        ClassTreeNode root = this.root;
        if (arrayTypes.contains("Object[]")) {
            root = classMap.get("Object[]");
            this.root.addChild(root);
        }

        // connect inheritance
        for (String type : arrayTypes) {
            // we've already taken care of the root array type
            if (type.equals("Object[]")) {
                continue;
            }
            // determine parent from the base type's class tree node
            ClassTreeNode baseClass = this.root.lookupClass(type.substring(0, type.length() - 2));
            if (baseClass != null && baseClass.getParent() != this.root) {
                ClassTreeNode parent = classMap.get(baseClass.getParent().getName() + "[]");
                parent.addChild(classMap.get(type));
            } else {
                root.addChild(classMap.get(type));
            }
        }
    }
}

