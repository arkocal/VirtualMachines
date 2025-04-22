package tum.i2.cma;

import java.util.HashMap;
import java.util.Map;

public enum CMaInstructionType {
    LOADC,
    // Arithmetic and logical (as introduced in Simple expressions and assignments)
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    AND,
    OR,
    XOR,
    // Comparison  (as introduced in Simple expressions and assignments)
    EQ,
    NEQ,
    LE,
    LEQ,
    GR,
    GEQ,
    // Negation (as introduced in Simple expressions and assignments)
    NOT,
    NEG,
    // Assignments
    LOAD,
    STORE,
    LOADA,
    STOREA,
    // Statements (as introduced in Statements and Statement Sequences)
    POP,
    // Conditional and Iterative Statements
    JUMP,
    JUMPZ,
    // Introduced in the Switch Statement
    JUMPI,
    DUP,
    // Introduced in Storage Allocation for Variables
    ALLOC;
    //
    private static final Map<String, CMaInstructionType> STRING_TO_ENUM = new HashMap<>();

    static {
        for (CMaInstructionType type : values()) {
            STRING_TO_ENUM.put(type.name(), type);
        }
    }

    public static CMaInstructionType fromString(String name) {
        CMaInstructionType type = STRING_TO_ENUM.get(name.toUpperCase());
        if (type == null) {
            throw new IllegalArgumentException("Unknown instruction type: " + name);
        }
        return type;
    }

    static int expectedNumberOfArguments(CMaInstructionType type) {
        switch (type) {
            case LOADC:
            case LOAD:
            case STORE:
            case LOADA:
            case STOREA:
            case JUMP:
            case JUMPZ:
            case JUMPI:
            case ALLOC:
                return 1;
            default:
                return 0;
        }
    }
}
