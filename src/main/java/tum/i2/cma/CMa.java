package tum.i2.cma;

import tum.i2.common.VirtualMachine;

public class CMa implements VirtualMachine {
    // TODO
    // Describe the CMa architecture here

    public CMa(CMaInstruction[] instructions) {
        // TODO Initialize the CMa architecture here
    }

    @Override
    public void step() {
        // TODO Implement one execution step

    }

    @Override
    public int run() {
        // TODO Implement the main loop of the VM
        // as introduced in the lecture
        // We have defined the step() method here,
        // because it might make it easier to debug,
        // and would be required if you wish to implement
        // an interface with step function
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void execute(CMaInstruction instruction) {
        // CMaInstructionType enum contains comments,
        // describing where the operations are defined
        switch (instruction.getType()) {
            // TODO Implement the instruction set as far as introduced
            default:
                throw new UnsupportedOperationException("Unknown instruction type: " + instruction.getType());
        }
    }

    // TODO: If you wish, you can implement each instruction as a method
}
