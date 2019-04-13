import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

enum InstructionType{
    COPY,
    OPERATION
}
public class Instruction {

    /*
    * Instruction is a combination of lhs and rhs having only binary operations
    * Only assignment instructions are parsed.
    * Each operation should be of form:
    * lhs = o1 * o2    * is the operator.
    *                  * and o2 can be empty.
    * */

    String instString;
    String o1, o2;
    String lhs;
    String operator;
    InstructionType type;
    static String  SENTINEL = "$";
    public Instruction(String instString) {
        this.instString = instString;
        buildInstruction(instString);
    }
    void buildInstruction(String instString){
        String[] parts = instString.split("=");
        lhs = parts[0].trim();
        String rhs = parts[1];

        // here, rhs can be of two different forms:
        // 1. Assignment: a = b
        // 2. General:    a = b * c

        // set1 will be set of character common in the operators and instruction.
        Set<Character> set1 = new HashSet<>(instString.chars().mapToObj(x->(char)x).collect(Collectors.toSet()));
        set1.retainAll(new HashSet<>("+-*/%&^".chars().mapToObj(x->(char)x).collect(Collectors.toSet())));

        if(set1.isEmpty()){
            // assignment type instruction found.
            type = InstructionType.COPY;
            o1 = rhs.trim();
            operator = SENTINEL;
            o2 = SENTINEL;
        } else{
            type = InstructionType.OPERATION;
            operator = set1.iterator().next().toString();
            String[] operands = rhs.split(String.format("[%s]", operator));
            o1 = operands[0].trim();
            o2 = operands[1].trim();
        }
    }
    @Override
    public String toString() {
        return String.format("%s = %s %s %s", lhs, o1, operator, o2);
    }
}