package tum.i2;

import java.io.IOException;

import tum.i2.cma.Helpers;
import tum.i2.common.VirtualMachine;

public class Main {
    public static void main(String[] args) throws IOException {
        VirtualMachine machine = Helpers.fromCMaCodeFile("src/main/resources/example.cma");
        machine.run();
    }
}

