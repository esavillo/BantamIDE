/*
 * File: MipsMode.java
 * F18 CS361 Project 15
 * Names: Martin Deutsch, Rob Durst, Evan Savillo
 * Date: 3/22/2019
 * This file contains the MipsMode class, which implements the
 * HighlightingMode interface to handle syntax highlighting for MIPS.
 */

package proj19DeutschDurstSavillo.editor;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import proj19DeutschDurstSavillo.interfaces.HighlightingMode;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaMode handles syntax highlighting for MIPS
 *
 * @author Evan Savillo
 * @author Rob Durst
 * @author Martin Deutsch
 */
public final class MipsMode implements HighlightingMode
{
    /** A list of instructions to be highlighted
     *  Note: we took the time to write these all out manually in the name of
     *  some basic error checking, instead of writing a simple regex that would
     *  match potentially incorrect instructions. Batch text processing made this
     *  not so bad, anyway.
     */
    private static final String[] MIPS_INSTRUCTIONS = new String[] {
            "abs(.d|.s)?", "add(.d|.s)?", "addi", "addiu",
            "addu", "and", "andi", "b", "bc1f", "bc1t", "beq", "beqz", "bge",
            "bgeu", "bgez", "bgezal", "bgt", "bgtu", "bgtz", "ble", "bleu",
            "blez", "blt", "bltu", "bltz", "bltzal", "bne", "bnez", "break",
            "c(.)eq(.d|.s)", "c(.)le(.d|.s)", "c(.)lt(.d|.s)",
            "ceil(.)w(.d|.s)", "clo", "clz", "cvt(.)d(.s|.w)",
            "cvt(.)s(.d|.w)", "cvt(.)w(.d|.s)", "div(.d|.s)?",
            "divu", "eret", "floor(.)w(.d|.s)", "j", "jal", "jalr",
            "jr", "l(.d|.s)?", "la", "lb", "lbu", "ld", "ldc1", "lh", "lhu",
            "li", "ll", "lui", "lw", "lwc1", "lwl", "lwr", "madd", "maddu",
            "mfc0", "mfc1", "mfc1(.)d", "mfhi", "mflo", "mov(.d|.s)?", "move",
            "movf(.d|.s)?", "movn(.d|.s)?",
            "movt(.d|.s)?", "movz(.d|.s)?", "msub", "msubu",
            "mtc0", "mtc1", "mtc1(.)d", "mthi", "mtlo", "mul(.d|.s)*",
            "mulo", "mulou", "mult", "multu", "mulu", "neg(.d|.s)?",
            "negu", "nop", "nor", "not", "or", "ori", "rem", "remu", "rol",
            "ror", "round(.)w(.d|.s)", "s(.d|.s)", "sb", "sc", "sd",
            "sdc1", "seq", "sge", "sgeu", "sgt", "sgtu", "sh", "sle", "sleu",
            "sll", "sllv", "slt", "slti", "sltiu", "sltu", "sne", "sqrt(.d|.s)",
            "sra", "srav", "srl", "srlv", "sub(.d|.s)?",
            "subi", "subiu", "subu", "sw", "swc1", "swl", "swr", "syscall",
            "teq", "teqi", "tge", "tgei", "tgeiu", "tgeu", "tlt", "tlti",
            "tltiu", "tltu", "tne", "tnei", "trunc(.)w(.d|.s)", "ulh",
            "ulhu", "ulw", "ush", "usw", "xor", "xori", "xori"
    };

    /** A list of pseduo-names for registers to be highlighted */
    private static final String[] PSEDUO_REGISTERS = new String[] {
            "zero",
            "v0", "v1",
            "a0", "a1", "a2", "a3",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7", "t8",
            "t9",
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8",
            "k0", "k1",
            "gp", "sp", "fp", "ra"
    };

    /** A list of assembler directives to be highlighted */
    private static final String[] ASSEMBLER_DIRECTIVES = new String[] {
            "align", "ascii", "asciiz", "byte", "data", "double", "end_macro",
            "eqv", "extern", "float", "globl", "half", "include", "kdata",
            "ktext", "macro", "set", "text", "word"
    };

    private static final String COMMENT_PATTERN        = "#[^\\n]*";
    private static final String LABEL_PATTERN          = "\\w*\\:";
    private static final String INSTRUCTION_PATTERN    = "\\b(" + String.join("|", MIPS_INSTRUCTIONS) + ")\\b";
    private static final String DIRECTIVE_PATTERN      = "\\b(" + String.join("|", ASSEMBLER_DIRECTIVES) + ")\\b";
    private static final String REGISTER_PATTERN       = "\\$(" + String.join("|", PSEDUO_REGISTERS) + "|[0-9]|[1-2][0-9]|3[0-1])\\b";
    private static final String MACROPARAMETER_PATTERN = "\\%\\w*";
    private static final String STRING_PATTERN         = "\"[^#]*?\"";
    private static final String CHARACTER_PATTERN      = "\'[^#]*?\'";
    private static final String NUMERIC_PATTERN        = "\\b(0x[0-9,A-F]+\\b|(?<![\\d.])[0-9]+(?![\\d.])(?![\\w]))";
    private static final String PAREN_PATTERN          = "[()]";

    /**
     * patterns to be highlighted
     */
    private static final Pattern PATTERN = Pattern.compile(
            "(?<INSTRUCTION>" + INSTRUCTION_PATTERN + ")"
                    + "|(?<LABEL>" + LABEL_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<DIRECTIVE>" + DIRECTIVE_PATTERN + ")"
                    + "|(?<REGISTER>" + REGISTER_PATTERN + ")"
                    + "|(?<MACROPARAMETER>" + MACROPARAMETER_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<CHARACTER>" + CHARACTER_PATTERN + ")"
                    + "|(?<NUMERIC>" + NUMERIC_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
    );

    /**
     * Computes the highlighting of substrings of text to return the style of each substring.
     *
     * @param text string to compute highlighting of
     * @return StyleSpans Collection Object
     */
    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text)
    {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        Matcher matcher   = PATTERN.matcher(text);
        int     lastKwEnd = 0;

        while (matcher.find()) {
            String styleClass =
                    matcher.group("COMMENT") != null ? "comment" :
                            matcher.group("INSTRUCTION") != null ? "instruction" :
                                    matcher.group("STRING") != null ? "string" :
                                            matcher.group("CHARACTER") != null ? "character" :
                                                    matcher.group("LABEL") != null ? "label" :
                                                            matcher.group("DIRECTIVE") != null ? "directive" :
                                                                    matcher.group("REGISTER") != null ? "register" :
                                                                            matcher.group("MACROPARAMETER") != null ? "macroparameter" :
                                                                                    matcher.group("NUMERIC") != null ? "numeric" :
                                                                                            matcher.group("PAREN") != null ? "paren" :
                                                                                                    null; /* never happens */

            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

        return spansBuilder.create();
    }
}
