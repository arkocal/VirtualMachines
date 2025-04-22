package tum.i2.cma;

public class CMaInstruction {
    private CMaInstructionType cMaInstructionType;
    private int[] args;

    public CMaInstruction(CMaInstructionType cMaInstructionType, int[] args) {
        this.cMaInstructionType = cMaInstructionType;
        this.args = args;
    }

    public CMaInstructionType getType() {
        return cMaInstructionType;
    }

    public int[] getArgs() {
        return args;
    }

    public int getFirstArg() {
        return args[0];
    }

    public boolean hasRightNumberOfArguments() {
        return args.length == CMaInstructionType.expectedNumberOfArguments(cMaInstructionType);
    }


}
