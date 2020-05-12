package pleasepleasepleasepleaspleasdontoverwhelmyourself.mainboi.programmablegolems;

public enum InstructionSetEnum {
    REFERENCE_ARRAY_LOAD((byte) -0b0100_1110, "aaload", (byte) 0),
    REFERENCE_ARRAY_STORE((byte) -0b0010_1101, "aastore", (byte) 0),
    REFERENCE_CONST_NULL((byte) -0b0000_0001, "aconst_null", (byte) 0),
    REFERENCE_LOAD((byte) -0b0110_0111, "aload", (byte) 1),
    REFERENCE_LOAD_0((byte) -0b0101_0110, "aload_0", (byte) 0),
    REFERENCE_LOAD_1((byte) -0b0101_0101, "aload_1", (byte) 0),
    REFERENCE_LOAD_2((byte) -0b0101_0100, "aload_2", (byte) 0),
    REFERENCE_LOAD_3((byte) -0b0101_0011, "aload_3", (byte) 0),
    REFERENCE_NEW_ARRAY((byte) 0b0011_1101, "anewarray", (byte) 2),
    REFERENCE_RETURN((byte) 0b0011_0000, "areturn", (byte) 0),

    ARRAY_LENGTH((byte) 0b0011_1110, "arraylength", (byte) 0),

    REFERENCE_STORE((byte) -0b0100_0110, "astore", (byte) 1),
    REFERENCE_STORE_0((byte) -0b0011_0101, "astore_0", (byte) 0),
    REFERENCE_STORE_1((byte) -0b0011_0100, "astore_1", (byte) 0),
    REFERENCE_STORE_2((byte) -0b0011_0011, "astore_2", (byte) 0),
    REFERENCE_STORE_3((byte) -0b0011_0010, "astore_3", (byte) 0),

    REFERENCE_THROW((byte) 0b0011_1111, "athrow", (byte) 0),

    BYTE_ARRAY_LOAD((byte) -0b0100_1101, "baload", (byte) 0),
    BYTE_ARRAY_STORE((byte) -0b0010_1100, "bastore", (byte) 0),
    BYTE_INT_PUSH((byte) -0b0111_0000, "bipush", (byte) 1),

    BREAK_POINT((byte) 0b0100_1010, "breakpoint", (byte) 0),

    CHARACTER_ARRAY_LOAD((byte) -0b0100_1100, "caload", (byte) 0),
    CHARACTER_ARRAY_STORE((byte) -0b0010_1011, "castore", (byte) 0),

    CHECK_CAST((byte) 0b0100_0000, "checkcast", (byte) 2),

    DOUBLE_TO_FLOAT((byte) 0b0001_0000, "d2f", (byte) 0),
    DOUBLE_TO_INTEGER((byte) 0b0000_1110, "d2i", (byte) 0),
    DOUBLE_TO_LONG((byte) 0b0000_1111, "d2l", (byte) 0),
    DOUBLE_ADD((byte) -0b0001_1101, "dadd", (byte) 0),
    DOUBLE_ARRAY_LOAD((byte) -0b0100_1111, "daload", (byte) 0),
    DOUBLE_ARRAY_STORE((byte) -0b0010_1110, "dastore", (byte) 0),
    DOUBLE_COMPARE_GREATER((byte) 0b0001_1000, "dcmpg", (byte) 0),
    DOUBLE_COMPARE_LESSER((byte) 0b0001_0111, "dcmpl", (byte) 0),
    DOUBLE_CONSTANT_0((byte) -0b0111_0010, "dconst_0", (byte) 0),
    DOUBLE_CONSTANT_1((byte) -0b0111_0001, "dconst_1", (byte) 0),
    DOUBLE_DIVIDE((byte) -0b0001_0001, "ddiv", (byte) 0),
    DOUBLE_LOAD((byte) -0b0110_1000, "dload", (byte) 1),
    DOUBLE_LOAD_0((byte) -0b0101_1010, "dload_0", (byte) 0),
    DOUBLE_LOAD_1((byte) -0b0101_1001, "dload_1", (byte) 0),
    DOUBLE_LOAD_2((byte) -0b0101_1000, "dload_2", (byte) 0),
    DOUBLE_LOAD_3((byte) -0b0101_0111, "dload_3", (byte) 0),
    DOUBLE_MULTIPLY((byte) -0b0001_0101, "dmul", (byte) 0),
    DOUBLE_NEGATE((byte) -0b0000_1001, "dneg", (byte) 0),
    DOUBLE_REMAINDER((byte) -0b0000_1101, "drem", (byte) 0),
    DOUBLE_RETURN((byte) 0b0010_1111, "dreturn", (byte) 0),
    DOUBLE_STORE((byte) -0b0100_0111, "dstore", (byte) 1),
    DOUBLE_STORE_0((byte) -0b0011_1001, "dstore_0", (byte) 0),
    DOUBLE_STORE_1((byte) -0b0011_1000, "dstore_1", (byte) 0),
    DOUBLE_STORE_2((byte) -0b0011_0111, "dstore_2", (byte) 0),
    DOUBLE_STORE_3((byte) -0b0011_0110, "dstore_3", (byte) 0),
    DOUBLE_SUBTRACT((byte) -0b0001_1001, "dsub", (byte) 0),

    DUPLICATE((byte) -0b0010_0111, "dup", (byte) 0),
    DUPLICATE_X1((byte) -0b0010_0110, "dup_x1", (byte) 0),
    DUPLICATE_X2((byte) -0b0010_0101, "dup_x2", (byte) 0),
    DUPLICATE_2((byte) -0b0010_0100, "dup2", (byte) 0),
    DUPLICATE_2_X1((byte) -0b0010_0011, "dup2_x1", (byte) 0),
    DUPLICATE_2_X2((byte) -0b0010_0010, "dup2_x2", (byte) 0),

    FLOAT_TO_DOUBLE((byte) 0b0000_1101, "f2d", (byte) 0),
    FLOAT_TO_INTEGER((byte) 0b0000_1011, "f2i", (byte) 0),
    FLOAT_TO_LONG((byte) 0b0000_1100, "f2l", (byte) 0),
    FLOAT_ADD((byte) -0b0001_1110, "fadd", (byte) 0),
    FLOAT_ARRAY_LOAD((byte) -0b0101_0000, "faload", (byte) 0),
    FLOAT_ARRAY_STORE((byte) -0b0010_1111, "fastore", (byte) 0),
    FLOAT_COMPARE_GREATER((byte) 0b0001_0110, "fcmpg", (byte) 0),
    FLOAT_COMPARE_LESSER((byte) 0b0001_0101, "fcmpl", (byte) 0),
    FLOAT_CONSTANT_0((byte) -0b0111_0101, "fconst_0", (byte) 0),
    FLOAT_CONSTANT_1((byte) -0b0111_0100, "fconst_1", (byte) 0),
    FLOAT_CONSTANT_2((byte) -0b0111_0011, "fconst_2", (byte) 0),
    FLOAT_DIVIDE((byte) -0b0001_0010, "fdiv", (byte) 0),
    FLOAT_LOAD((byte) -0b0110_1001, "fload", (byte) 1),
    FLOAT_LOAD_0((byte) -0b0101_1110, "fload_0", (byte) 0),
    FLOAT_LOAD_1((byte) -0b0101_1101, "fload_1", (byte) 0),
    FLOAT_LOAD_2((byte) -0b0101_1100, "fload_2", (byte) 0),
    FLOAT_LOAD_3((byte) -0b0101_1011, "fload_3", (byte) 0),
    FLOAT_MULTIPLY((byte) -0b0001_0110, "fmul", (byte) 0),
    FLOAT_NEGATION((byte) -0b0000_1010, "fneg", (byte) 0),
    FLOAT_REMAINDER((byte) -0b0000_1110, "frem", (byte) 0),
    FLOAT_RETURN((byte) 0b0010_1110, "freturn", (byte) 0),
    FLOAT_STORE((byte) -0b0100_1000, "fstore", (byte) 1),
    FLOAT_STORE_0((byte) -0b0011_1101, "fstore_0", (byte) 0),
    FLOAT_STORE_1((byte) -0b0011_1100, "fstore_1", (byte) 0),
    FLOAT_STORE_2((byte) -0b0011_1011, "fstore_2", (byte) 0),
    FLOAT_STORE_3((byte) -0b0011_1010, "fstore_3", (byte) 0),
    FLOAT_SUBTRACT((byte) -0b0001_1010, "fsub", (byte) 0),

    GET_FIELD((byte) 0b0011_0100, "getfield", (byte) 2),
    GET_STATIC((byte) 0b0011_0010, "getstatic", (byte) 2),

    GOTO((byte) 0b0010_0111, "goto", (byte) 2),
    GOTO_WORD((byte) 0b0100_1000, "goto_w", (byte) 4),

    INTEGER_TO_BYTE((byte) 0b0001_0001, "i2b", (byte) 0),
    INTEGER_TO_CHARACTER((byte) 0b0001_0010, "i2c", (byte) 0),
    INTEGER_TO_DOUBLE((byte) 0b0000_0111, "i2d", (byte) 0),
    INTEGER_TO_FLOAT((byte) 0b0000_0110, "i2f", (byte) 0),
    INTEGER_TO_LONG((byte) 0b0000_0101, "i2l", (byte) 0),
    INTEGER_TO_SHORT((byte) 0b0001_0011, "i2s", (byte) 0),
    INTEGER_ADD((byte) -0b0010_0000, "iadd", (byte) 0),
    INTEGER_ARRAY_LOAD((byte) -0b0101_0010, "iaload", (byte) 0),
    INTEGER_AND((byte) -0b0000_0010, "iand", (byte) 0),
    INTEGER_ARRAY_STORE((byte) -0b0011_0001, "iastore", (byte) 0),
    INTEGER_CONSTANT_0((byte) -0b0111_1101, "iconst_0", (byte) 0),
    INTEGER_CONSTANT_1((byte) -0b0111_1100, "iconst_1", (byte) 0),
    INTEGER_CONSTANT_2((byte) -0b0111_1011, "iconst_2", (byte) 0),
    INTEGER_CONSTANT_3((byte) -0b0111_1010, "iconst_3", (byte) 0),
    INTEGER_CONSTANT_4((byte) -0b0111_1001, "iconst_4", (byte) 0),
    INTEGER_CONSTANT_5((byte) -0b0111_1000, "iconst_5", (byte) 0),
    INTEGER_CONSTANT_M1((byte) -0b0111_1110, "iconst_m1", (byte) 0),
    INTEGER_DIVISION((byte) -0b0001_0100, "idiv", (byte) 0),

    IF_REFERENCE_COMPARE_EQUALS((byte) 0b0010_0101, "if_acmpeq", (byte) 2),
    IF_REFERENCE_COMPARE_NOT_EQUALS((byte) 0b0010_0110, "if_acmpne", (byte) 2),

    IF_INTEGER_COMPARE_EQUALS((byte) 0b0001_1111, "if_icmpeq", (byte) 2),
    IF_INTEGER_COMPARE_GREATER_OR_EQUALS((byte) 0b0010_0010, "if_icmpge", (byte) 2),
    IF_INTEGER_COMPARE_GREATER((byte) 0b0010_0011, "if_icmpgt", (byte) 2),
    IF_INTEGER_COMPARE_LESSER_OR_EQUALS((byte) 0b0010_0100, "if_icmple", (byte) 2),
    IF_INTEGER_COMPARE_LESSER((byte) 0b0010_0001, "if_icmplt", (byte) 2),
    IF_INTEGER_COMPARE_NOT_EQUALS((byte) 0b0010_0000, "if_icmpne", (byte) 2),

    IF_EQUALS_0((byte) 0b0001_1001, "ifeq", (byte) 2),
    IF_GREATER_OR_EQUALS_0((byte) 0b0001_1100, "ifge", (byte) 2),
    IF_GREATER_0((byte) 0b0001_1101, "ifgt", (byte) 2),
    IF_LESSER_OR_EQUALS_0((byte) 0b0001_1110, "ifle", (byte) 2),
    IF_LESSER_0((byte) 0b0001_1011, "iflt", (byte) 2),
    IF_NOT_EQUALS_0((byte) 0b0001_1010, "ifne", (byte) 2),

    IF_NOT_NULL((byte) 0b0100_0111, "ifnonnull", (byte) 2),
    IF_NULL((byte) 0b0100_0110, "ifnull", (byte) 2),

    INCREMENT_INDEX_CONSTANT((byte) 0b0000_0100, "iinc", (byte) 2),

    INTEGER_LOAD((byte) -0b0110_1011, "iload", (byte) 1),
    INTEGER_LOAD_0((byte) -0b0110_0110, "iload_0", (byte) 0),
    INTEGER_LOAD_1((byte) -0b0110_0101, "iload_1", (byte) 0),
    INTEGER_LOAD_2((byte) -0b0110_0100, "iload_2", (byte) 0),
    INTEGER_LOAD_3((byte) -0b0110_0011, "iload_3", (byte) 0),
    ;



    private final byte instructionCode, parameterCount;
    private final String mnemonic;

    InstructionSetEnum(byte instructionCode, String mnemonic, byte parameterCount) {
        this.instructionCode = instructionCode;
        this.mnemonic = mnemonic;
        this.parameterCount = parameterCount;
    }

    public byte getInstructionCode() {
        return instructionCode;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public byte getParameterCount() {
        return parameterCount;
    }
}
