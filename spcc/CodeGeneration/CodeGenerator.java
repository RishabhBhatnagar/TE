import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class CodeGenerator {
    Quadruple quadrupleGenerator;
    CodeGenerator(Quadruple quadrupleInstructions){
        this.quadrupleGenerator = quadrupleInstructions;
    }
    String generateAssignInstr(Instruction instruction, String register){
        return String.format("MOV %s, %s\nMOV %s, %s\n", register, instruction.o1, instruction.lhs, register);
    }
    String generateOperationStmt(Instruction instruction, String register){
        String operation = "";
        switch(instruction.operator){
            case "+": operation = "ADD"; break;
            case "-": operation = "SUB"; break;
            case "*": operation = "MUL"; break;
            case "/": operation = "DIV"; break;
            default: throw new NotImplementedException();
        }
        return String.format("MOV %s, %s\n%s %s, %s\nMOV %s, %s\n", register, instruction.o1, operation, register, instruction.o2, instruction.lhs, register);
    }
    String generateCode(){
        // generates the code based on hard coded quadruple instructions.
        String generatedCode = "";
        for(Quadruple.QuadrupleInstruction instruction: quadrupleGenerator.quadruples) {
            if(instruction.type == InstructionType.COPY){
                generatedCode += generateAssignInstr(instruction, "R0");
            } else{
                generatedCode += generateOperationStmt(instruction, "R0");
            }
        }
        return generatedCode;
    }

    public static void main(String[] args) {
        String program = "a = b\n" +
                "f = c + d\n" +
                "e = a - f\n" +
                "g = b * c";
        Quadruple quadruple = new Quadruple(program);
        CodeGenerator cg = new CodeGenerator(quadruple);

        System.out.println("Program Fed to Code Generator:");
        System.out.println(program);
        System.out.println();
        System.out.println("Code Generated:");
        System.out.println(cg.generateCode());
    }
}


/*
* OUTPUT:
Program Fed to Code Generator:
a = b
f = c + d
e = a - f
g = b * c

Code Generated:
MOV R0, b
MOV a, R0
MOV R0, c
ADD R0, d
MOV f, R0
MOV R0, a
SUB R0, f
MOV e, R0
MOV R0, b
MUL R0, c
MOV g, R0

*/