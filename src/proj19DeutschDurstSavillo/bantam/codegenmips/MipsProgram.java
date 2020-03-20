package proj19DeutschDurstSavillo.bantam.codegenmips;

import java.io.PrintStream;
import java.util.ArrayList;


// TODO more appropriate name? MipsDotText?
public class MipsProgram
{
    private ArrayList<Instruction> program;
    private PrintStream out;

    public MipsProgram(PrintStream out)
    {
        this.program = new ArrayList<>();
        this.out = out;
    }

    public void write(Instruction instr, String comment)
    {
        instr.setComment(comment);
        this.program.add(instr);
    }

    public void write(Instruction instr)
    {
        this.program.add(instr);
    }

    public void writeComment(String comment)
    {
        this.program.add(new Instruction("# " + comment));
    }

    public String getLastInstrComment()
    {
        return this.program.get(this.program.size() - 1).getComment();
    }

    public void setLastInstrComment(String comment)
    {
        this.program.get(this.program.size() - 1).setComment(comment);
    }

    public void  optimize()
    {
        return;
    }

    public void erase()
    {
        this.program.clear();
    }

    /**
     * Writes all the instructions to standard output
     */
    public void dump()
    {
        this.program.stream().map(Instruction::toString).forEach(System.out::print);
    }

    /**
     * Writes all the instructions to output as set in main class
     */
    public void dumpIntoOut()
    {
        this.program.stream().map(Instruction::toString).forEach(this.out::print);
    }
}
