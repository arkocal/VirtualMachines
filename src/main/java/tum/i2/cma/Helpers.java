package tum.i2.cma;

import java.io.IOException;

public class Helpers {

    public static CMa fromCMaCodeFile(String filePath) throws IOException {
        CmaParser parser = new CmaParser();
        CMaInstruction[] instructions = parser.parseFile(filePath);
        return new CMa(instructions);
    }
}
