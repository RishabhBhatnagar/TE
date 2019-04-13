import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ICG {
    public static void main(String[] args) {
        String program = "a=b\n" +
                "f = c + d\n" +
                "e = a - f\n" +
                "g = b - c";
        Quadruple quadruple = new Quadruple(program);
        Triple triples = new Triple(program);
        IndirectTriple indirectTriple = new IndirectTriple(program);
        System.out.println(triples);
        System.out.println();
        System.out.print(quadruple);
        System.out.println();
        System.out.println();
        System.out.println(indirectTriple);
    }
}

class Quadruple{
    // takes as an input the entire program amd
    // converts each of the line to required format.
    List<QuadrupleInstruction> quadruples = new ArrayList<>();
    AtomicInteger addressPtr = new AtomicInteger(0);

    class QuadrupleInstruction extends Instruction{
        int instructionNumber;
        public QuadrupleInstruction(String instString) {
            super(instString);   // this build the instructionString.
                                 // sets o1, o2, operator, lhs.
            instructionNumber = addressPtr.incrementAndGet();
        }
        @Override
        public String toString() {
            return String.format("%s\t%s\t%s\t%s", operator, o1, o2, lhs);
        }
    }
    Quadruple(String program){
        for (String line: program.split("\n")) {
            this.quadruples.add(new QuadrupleInstruction(line));
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"\n"+String.join("\n", quadruples.stream().map(QuadrupleInstruction::toString).collect(Collectors.toList()));
    }
}

class Triple extends Quadruple{
    List<TripleInstruction> triples = new ArrayList<>();
    AtomicInteger addressPtr = new AtomicInteger(0);
    Dictionary<String, String> lhsAddress = new Hashtable<>();  // this stores the address of instruction lhs.

    class TripleInstruction extends Instruction{
        String address;

        public TripleInstruction(String instString) {
            super(instString);
            address = String.format("(%d)", addressPtr.incrementAndGet());
            lhsAddress.put(super.lhs, address);
            if(!o1.isEmpty() && lhsAddress.get(o1) != null){
                o1 = lhsAddress.get(o1);
            }
            if(!o2.isEmpty() && lhsAddress.get(o2) != null){
                o2 = lhsAddress.get(o2);
            }
        }
        @Override
        public String toString() {
            return String.format("%s\t%s\t%s\t%s", address, operator, o1, o2);
        }
    }
    Triple(String program) {
        super(program);
        super.addressPtr = new AtomicInteger(0);
        for (QuadrupleInstruction inst: quadruples) {
            triples.add(new TripleInstruction(inst.instString));
        }
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"\n"+String.join("\n", triples.stream().map(TripleInstruction::toString).collect(Collectors.toList()));
    }
}
class IndirectTriple extends Triple{
    List<IndirectTripleInstruction> indirectTriples = new ArrayList<>();
    class IndirectTripleInstruction extends Triple.TripleInstruction{
        String actualAddress;

        public IndirectTripleInstruction(String instString) {
            super(instString);
            this.actualAddress = address;
            this.address = String.format("(%d)", Math.abs(new Random(Integer.valueOf(address.substring(1, address.length()-1))).nextInt() % 100));
            if(o1.startsWith("(")){
                o1 = String.format("(%d)", Math.abs(new Random(Integer.valueOf(o1.substring(1, o1.length()-1))).nextInt() % 100));
            }
            if(o2.startsWith("(")){
                o2 = String.format("(%d)", Math.abs(new Random(Integer.valueOf(o2.substring(1, o2.length()-1))).nextInt() % 100));
            }
        }

        @Override
        public String toString() {
            return String.format("%s\t%s\t%s\t%s", address, operator, o1, o2);
        }
    }
    IndirectTriple(String program) {
        super(program);
        for (TripleInstruction inst: triples) {
            indirectTriples.add(new IndirectTripleInstruction(inst.instString));
        }
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"\n"+String.join("\n", indirectTriples.stream().map(IndirectTripleInstruction::toString).collect(Collectors.toList()));
    }
}


/*
* OUTPUT:
*
Triple
(1)	$	b	$
(2)	+	c	d
(3)	-	(1)	(2)
(4)	-	b	c

Quadruple
$	b	$	a
+	c	d	f
-	a	f	e
-	b	c	g

IndirectTriple
(21)	$	b	$
(74)	+	c	d
(23)	-	(21)	(74)
(68)	-	b	c
* */