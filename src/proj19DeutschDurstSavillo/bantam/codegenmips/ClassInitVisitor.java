/*
 * File: ClassInitVisitor.java
 * S19 CS461 Project 17
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 4/22/19
 * This file contains a visitor that generates class init subroutines
 */

package proj19DeutschDurstSavillo.bantam.codegenmips;

import proj19DeutschDurstSavillo.bantam.ast.*;
import proj19DeutschDurstSavillo.bantam.util.ClassTreeNode;
import proj19DeutschDurstSavillo.bantam.util.Location;
import proj19DeutschDurstSavillo.bantam.visitor.Visitor;

import java.util.Map;

/**
 * ClassInitVisitor visits an AST and generates class init subroutines
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class ClassInitVisitor extends Visitor {

    private OurMipsSupport assemblySupport;
    private ClassTreeNode root;
    private ClassTreeNode currentClass;
    private UserDefinedMethodVisitor initVisitor;
    private int fieldIndex;

    /**
     * Generate the class init subroutines for each class in the given program
     * @param root the root of the ClassTreeNode hierarchy
     * @param program the root of the AST
     * @param assemblySupport the MipsSupport object used to generate the MIPS program
     * @param stringsMap the mapping of string constants to unique identifiers
     */
    public void genClassInitSubroutines(ClassTreeNode root, Map<LambdaExpr, String> map, Program program,
                                        OurMipsSupport assemblySupport, Map<String, String> stringsMap,
                                        Map<String, String> classEnumeration, boolean gc) {
        this.root = root;
        this.currentClass = root;
        this.assemblySupport = assemblySupport;
        this.initVisitor = new UserDefinedMethodVisitor(false);
        initVisitor.setAssemblySupport(this.assemblySupport);
        initVisitor.setStringsMap(stringsMap);
        initVisitor.setRoot(this.root);
        initVisitor.setClassEnumeration(classEnumeration);
        initVisitor.setLambdaNameMap(map);
        if (gc) {
            this.initVisitor.enableGC();
        }

        program.accept(this);
        this.genArrayInit();
    }

    /**
     * Visit a class node
     *
     * @param node the class node
     * @return null
     */
    public Object visit(Class_ node) {
        // set current class
        this.currentClass = this.root.lookupClass(node.getName());
        // generate init label
        this.assemblySupport.genLabel(node.getName() + "_init");
        if (node.getName().equals("Main")) {
            this.assemblySupport.genLoadAddr("$a2", "filename");
            this.assemblySupport.setLastInstrComment("Load the filename into $a2 for error printing");
        }

        // generate method prologue
        initVisitor.generateProlog(null);

        // call parent init unless class is built-in
        if (!currentClass.isBuiltIn()) {
            this.assemblySupport.genDirCall(currentClass.getParent().getName() + "_init");
            this.assemblySupport.setLastInstrComment("Call parent init subroutine");
        }

        // fields start at index 3 in memory
        this.fieldIndex = 3;
        // skip any parent fields
        ClassTreeNode parent = this.currentClass.getParent();
        if (parent != null) {
            for (ASTNode member : parent.getASTNode().getMemberList()) {
                if (member instanceof Field) {
                    this.fieldIndex++;
                }
            }
        }

        // traverse the rest of the AST
        node.getMemberList().accept(this);

        this.assemblySupport.genMove("$v0", "$a0");
        this.assemblySupport.setLastInstrComment("Move pointer to new object from $a0 to $v0");

        // generate method epilogue
        initVisitor.generateEpilog(null, this.assemblySupport.getLabel());
        return null;
    }

    /**
     * Visit only fields of each class
     *
     * @param node the member list node
     * @return null
     */
    public Object visit(MemberList node) {
        for (ASTNode child : node) {
            if (child instanceof Field) {
                child.accept(this);
            }
        }
        return null;
    }

    /**
     * Visit a field node
     *
     * @param node the field node
     * @return null
     */
    public Object visit(Field node) {
        int offset = this.assemblySupport.getWordSize()*this.fieldIndex;

        // visit init expression, if necessary
        if (node.getInit() != null) {
            // generate code for init expression using UserDefinedMethodVisitor
            node.getInit().accept(initVisitor);

            this.assemblySupport.genStoreWord("$v0", offset, "$a0");
            this.assemblySupport.setLastInstrComment("Store initial value of \"" + node.getName() + "\" field from $v0");
        }

        // store field location in symbol table
        Location fieldLocation = new Location("$a0", offset);
        this.currentClass.getVarSymbolTable().set(node.getName(), fieldLocation);
        this.fieldIndex++;

        return null;
    }

    /**
     * Generate initialization routine for arrays
     */
    private void genArrayInit() {
        this.assemblySupport.genLabel("Array_init");
        initVisitor.generateProlog(null);

        this.assemblySupport.genLoadWord("$t0", 12, "$a0");
        this.assemblySupport.setLastInstrComment("Load array length to $t0");
        this.assemblySupport.genAdd("$t1", "$a0", 16);
        this.assemblySupport.setLastInstrComment("Load address of first element to $t1");
        String loopBegin = this.assemblySupport.getLabel();
        String loopEnd = this.assemblySupport.getLabel();
        this.assemblySupport.genLabel(loopBegin);
        this.assemblySupport.setLastInstrComment("Begin loop of storing zeros");
        this.assemblySupport.genStoreWord("$zero", 0, "$t1");
        this.assemblySupport.genAdd("$t1", "$t1", 4);
        this.assemblySupport.setLastInstrComment("Go to next element");
        this.assemblySupport.genSub("$t0", "$t0", 1);
        this.assemblySupport.setLastInstrComment("Decrement $t0");
        this.assemblySupport.genCondBeq("$t0", "$zero", loopEnd);
        this.assemblySupport.genUncondBr(loopBegin);
        this.assemblySupport.genLabel(loopEnd);

        this.assemblySupport.genMove("$v0", "$a0");
        this.assemblySupport.setLastInstrComment("Move pointer to new object from $a0 to $v0");

        initVisitor.generateEpilog(null, this.assemblySupport.getLabel());
    }
}
