/*
 * File: Instruction.java
 * S19 CS461 Project 17
 * Names: Martin Deutsch, Evan Savillo and Robert Durst
 * Date: 4/22/19
 * This file represents a single MIPS instruction
 */

package proj19DeutschDurstSavillo.bantam.codegenmips;

/**
 * Class representing a single MIPS instruction
 *
 * @author Martin Deutsch
 * @author Robert Durst
 * @author Evan Savillo
 */
public class Instruction
{
    private static final int operandBeginColumn = 15; // The column where operand's start should be aligned
    private static       int maxLength          = 0; /* a class variable providing the string length
     * of the longest instruction, sans comment */
    private              int length;
    private String   operation;
    private String[] operands = {"", "", ""};
    private String   comment;

    private StringBuilder self; /* A String representing the instruction on a line. */

    public Instruction(String operation, String[] operands)
    {
        this(operation, operands, "");
    }

    public Instruction(String operation, String[] operands, String comment)
    {
        this.operation = operation;

        if (operands.length > 3) {
            throw new IllegalArgumentException("More than 3 operands given.");
        }

        System.arraycopy(operands, 0, this.operands, 0, operands.length);

        this.comment = comment;

        this.make();
    }

    /**
     * Remakes the beautifully aligned text representation of the selfsame instruction
     */
    private void make()
    {
        // If instruction is just a label, don't indent
        this.self = new StringBuilder(isLabel() ? "\n" : "      ");
        this.length = 0;

        if (this.isComment()) {
            // If special header comment, extra blank line
            if (operation.length() > 6 && operation.substring(0, 7).equals("# _/--[")) {
                this.self.append("\n");
            }
            this.self.append(operation);
            this.self.append("\n");
            return;
        }

        this.self.append(operation);
        // after operation, calculate and write spacing to arrive at decided column
        int offset = operandBeginColumn - this.self.toString().length();
        this.self.append(generateSpace(offset));

        // write first operand. if none, don't worry, because it's initialized to "" anyway
        this.self.append(this.operands[0]);

        for (int i = 1; i < 3 && !this.operands[i].equals(""); ++i) {
            this.self.append(", ");
            this.self.append(this.operands[i]);
        }

        // re-evaluate the maximum length of any Instruction
        this.length = this.self.length();
        maxLength = (maxLength < this.length) ? this.length : maxLength;

        this.self.append("%s#");
        if (!comment.equals("")) {
            this.self.append(" ");

            // Check to make sure '%' isn't inserted naked in comment
            comment = comment.replaceAll("%", "%%");
            this.self.append(comment);
        }

        this.self.append("\n");
    }

    /**
     * Small helper to generate a number of whitespaces
     */
    private String generateSpace(int numspaces)
    {
        StringBuilder space = new StringBuilder();

        while (numspaces > 0) {
            space.append(" ");
            --numspaces;
        }

        return space.toString();
    }

    public boolean isLabel()
    {
        return this.operation.endsWith(":");
    }

    public boolean isComment()
    {
        return this.operation.startsWith("#");
    }

    public Instruction(String operation)
    {
        this(operation, "");
    }

    public Instruction(String operation, String comment)
    {
        this.operation = operation;
        this.comment = comment;

        this.make();
    }

    /**
     * Finalizes the string representation by applying the class-wide (static) offset
     */
    public String toString()
    {
        StringBuilder offset = new StringBuilder();

        while (offset.length() < maxLength - this.length) {
            offset.append(" ");
        }
        offset.append("\t");

        return String.format(this.self.toString(), offset.toString());
    }

    private static int d = 0;

    public String getComment()
    {
        return this.comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
        this.make();
    }
}
