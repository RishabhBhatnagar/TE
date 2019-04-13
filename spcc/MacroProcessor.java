package macroProcessor;

import java.util.*;

class MacroProcessor {
    private String macroString = "";
    private int mdtIndex = 1;
    private int alaIndex = 1;
    List<String> macroCallStrings;
    List<String> macroNames;

    private Dictionary<String, Integer> macroNameTable;  // name of table and it's index in mdt.
    private Dictionary<Integer, String> macroDefinitionTable;
    private Dictionary<Integer, String> argumentListArray;

    String parseDeclaration(String declarationString) {

        String[] parts = declarationString.split("\\s");


        // adding name of macro to name table.
        this.macroNameTable.put(parts[0], mdtIndex);
        this.macroNames.add(parts[0]);

        // adding all the args to ala first.
        for (int i = 1; i < parts.length; i++) {
            this.argumentListArray.put(this.alaIndex, parts[i]);
            parts[i] = "#" + this.alaIndex;
            this.alaIndex += 1;
        }
        return String.join(" ", parts);

    }

    void buildMacroFromString(String macroString) {
        String[] program = macroString.split("\n");

        // first line is Macro;
        //second line is macroName *args
        program[1] = this.parseDeclaration(program[1]);
        System.out.println(Arrays.toString(program));

        for (int i = 1; i < program.length; i++) {
            this.macroDefinitionTable.put(this.mdtIndex, program[i]);
            this.mdtIndex += 1;
        }
        System.out.println(this.macroDefinitionTable);
    }

    void buildAndResetMacro() {
        this.buildMacroFromString(this.macroString);
        this.macroString = "";
    }

    void appendLine(String line) {
        this.macroString += line;
    }

    MacroProcessor() {
        this.macroString = "";
        this.macroNameTable = new Hashtable<>();
        this.argumentListArray = new Hashtable<>();
        this.macroDefinitionTable = new Hashtable<>();
        this.macroCallStrings = new ArrayList<>();
        this.macroNames = new ArrayList<>();
    }
}
