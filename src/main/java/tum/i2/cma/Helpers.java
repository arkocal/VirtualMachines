package tum.i2.cma;

import java.io.IOException;

public class Helpers {

    public static CMa fromCMaCodeFile(String filePath) throws IOException {
        CMaParser parser = new CMaParser();
        CMaInstruction[] instructions = parser.parseFile(filePath);
        return new CMa(instructions);
    }
}
