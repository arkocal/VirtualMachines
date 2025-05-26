package tum.i2.cma;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CMaParser {
    // We keep the parser as simple as possible,
    // since this is not a course on compiler construction,
    // particularly not for machine code.

    // We parse line by line
    private class ParsedLine {
        ParsedInstruction instruction;
        String symbolic_label;
        int line_number;

        public ParsedLine(int line_number, ParsedInstruction instruction, String symbolic_label) {
            this.line_number = line_number;
            this.instruction = instruction;
            this.symbolic_label = symbolic_label;
        }
    }

    private class ParsedInstruction {
        String type;
        String[] args;

        public ParsedInstruction(String type, String[] args) {
            this.type = type;
            this.args = args;
        }
    }

    int position;
    int line_number;
    String current_line;
    List<ParsedLine> parsed_lines;
    List<CMaInstruction> instructions;
    HashMap<String, Integer> labelPositions;

    public CMaParser() {
        resetParserState();
    }

    public CMaInstruction[] parseFile(String filePath) throws IOException {
        String fileContent = "";
        fileContent = Files.readString(Path.of(filePath));
        return parse(fileContent);
    }

    public CMaInstruction[] parse(String input) {
        resetParserState();

        String[] lines = input.split("\n");
        for (String l : lines) {
            line_number++;
            position = 0;
            current_line = l;
            parseLine();
        }

        return postProcessParsedLines().toArray(new CMaInstruction[0]);
    }

    private void resetParserState() {
        position = 0;
        line_number = 0;
        current_line = "";
        parsed_lines = new ArrayList<>();
        instructions = new ArrayList<>();
        labelPositions = new HashMap<>();
    }

    private void parseLine() {
        String label = null;
        ParsedInstruction instruction = null;
        readWhitespace();
        if (current_line.contains(":")) {
            label = readLabel();
        }
        readWhitespace();
        if (position >= current_line.length()) {
            parsed_lines.add(new ParsedLine(line_number, null, label));
            return;
        }
        if (Character.isAlphabetic(current_line.charAt(position))) {
            instruction = readInstruction();
        }
        readWhitespace();
        readComment();
        if (position < current_line.length()) {
            throw new RuntimeException("Unexpected character at line " + line_number);
        }
        // Store the parsed line
        ParsedLine parsed_line = new ParsedLine(line_number, instruction, label);
        parsed_lines.add(parsed_line);
    }

    private List<CMaInstruction> postProcessParsedLines() {
        checkForRepeatingLabels();
        removeEmptyLines();
        labelPositions = getLabelPositions();

        for (ParsedLine parsedLine: parsed_lines) {
            CMaInstruction instruction = makeCMaInstruction(parsedLine);
            if (instruction == null) continue;
            instructions.add(instruction);
        }
        return instructions;
    }

    private CMaInstruction makeCMaInstruction(ParsedLine parsedLine) {
        if (parsedLine.instruction == null) {
            return null;
        }
        CMaInstructionType type = getCMaInstructionType(parsedLine);
        int[] args = getArgsAsInts(parsedLine);
        CMaInstruction instruction = new CMaInstruction(type, args);
        validateInstruction(parsedLine, instruction);
        return instruction;
    }

    private static void validateInstruction(ParsedLine parsedLine, CMaInstruction instruction) {
        if (!instruction.hasRightNumberOfArguments()) {
            throw new RuntimeException("Invalid number of arguments for instruction " + parsedLine.instruction.type + " at line " + parsedLine.line_number);
        }
    }

    private int[] getArgsAsInts(ParsedLine parsedLine) {
        int[] args = new int[parsedLine.instruction.args.length];
        int i = 0;
        for (String str_arg: parsedLine.instruction.args) {
            if (Character.isAlphabetic(str_arg.charAt(0))) {
                if (!labelPositions.containsKey(str_arg)) {
                    throw new RuntimeException("Unknown label " + str_arg + " at line " + parsedLine.line_number);
                }
                args[i] = labelPositions.get(str_arg);
            }
            else {
                try {
                    args[i] = Integer.parseInt(str_arg);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Invalid argument " + str_arg + " at line " + parsedLine.line_number);
                }
            }
            i++;
        }
        return args;
    }

    private static CMaInstructionType getCMaInstructionType(ParsedLine parsedLine) {
        CMaInstructionType type;
        try {
            type = CMaInstructionType.fromString(parsedLine.instruction.type);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown instruction " + parsedLine.instruction.type + " at line " + parsedLine.line_number);
        }
        return type;
    }

    private void removeEmptyLines() {
        parsed_lines.removeIf(parsed_line -> parsed_line.instruction == null && parsed_line.symbolic_label == null);
    }

    private HashMap<String, Integer> getLabelPositions() {
        HashMap<String, Integer> label_map = new HashMap<>();
        int location = 0;
        for (ParsedLine parsed_line : parsed_lines) {
            if (parsed_line.symbolic_label != null) {
                label_map.put(parsed_line.symbolic_label, location);
            }
            if (parsed_line.instruction != null) {
                location++;
            }
        }
        return label_map;
    }

    private void checkForRepeatingLabels() {
        java.util.HashMap<String, Integer> label_map = new java.util.HashMap<>();
        for (ParsedLine parsed_line : parsed_lines) {
            if (parsed_line.symbolic_label != null) {
                if (label_map.containsKey(parsed_line.symbolic_label)) {
                    throw new RuntimeException("Label " + parsed_line.symbolic_label + " is defined multiple times at lines " + label_map.get(parsed_line.symbolic_label) + " and " + parsed_line.line_number);
                }
                label_map.put(parsed_line.symbolic_label, parsed_line.line_number);
            }
        }
    }

    private void readComment() {
        if (position >= current_line.length()) {
            return;
        }
        if (current_line.charAt(position) == '/') {
            if (!current_line.substring(position).startsWith("//")) {
                throw new RuntimeException("Invalid comment at line " + line_number);
            }
            // Skip the comment
            while (position < current_line.length()) {
                position++;
            }
        }
    }

    private void readWhitespace() {
        while (position < current_line.length() && Character.isWhitespace(current_line.charAt(position))) {
            position++;
        }
    }

    private String readLabel() {
        String label = "";
        while (current_line.charAt(position) != ':') {
            if (position >= current_line.length()) {
                throw new RuntimeException("Unexpected end of line while reading label at line " + line_number);
            }
            label += current_line.charAt(position);
            position++;
        }
        // Check if the label is valid
        if (label.isEmpty()) {
            throw new RuntimeException("Empty label at line " + line_number);
        }
        position++; // Skip the ':'
        return label;
    }

    private String readLabelArg() {
        if (!Character.isAlphabetic(current_line.charAt(position))) {
            return null;
        }
        String label = "";
        while (position < current_line.length() &&
                (Character.isAlphabetic(current_line.charAt(position))
                || Character.isDigit(current_line.charAt(position)))) {
            label += current_line.charAt(position);
            position++;
        }
        return label;
    }

    private ParsedInstruction readInstruction() {
        String whole_instruction = "";
        while (position < current_line.length() && current_line.charAt(position) != '/') {
            whole_instruction += current_line.charAt(position);
            position++;
        }
        // First is the instruction, rest are in arguments
        String[] parts = whole_instruction.split(" ");
        String instruction = parts[0];
        String[] args = new String[parts.length - 1];
        for (int i = 1; i < parts.length; i++) {
            args[i - 1] = parts[i];
        }
        return new ParsedInstruction(instruction, args);
    }
}
