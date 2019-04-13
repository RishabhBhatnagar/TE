package macroProcessor;

import macroProcessor.MacroProcessor;

public class Main{
    static void parseProgram(String program, MacroProcessor processor){
        String[] locs = program.split("\n");
        boolean macroFound = false;
        for (String line: locs){
            line = line.replaceAll("\n", "");
            if(line.equalsIgnoreCase("Macro")) macroFound = true;
            if(macroFound) processor.appendLine(line+"\n");
            if(line.equalsIgnoreCase("mend0")){
                macroFound = false;
                processor.buildAndResetMacro();
            }
            if(processor.macroNames.contains(line.split("\\s")[0])){
                processor.macroCallStrings.add(line);
            }
        }

        for(String str: processor.macroCallStrings){
            String macroName = str.split("\\n")[0];
            System.out.print(macroName);
            for(String line: locs){
                if(line.startsWith(macroName)){
                    System.out.print(line);
                }
            }
        }
    }
    public static void main(String[] args) {
        String input = "START\n" +
                "MACRO\n" +
                "ADD &ARG1, &ARG2\n" +
                "L 1, &ARG1\n" +
                "A 1, &ARG2\n" +
                "MEND\n" +
                "MACRO\n" +
                "SUB &ARG3, &ARG4\n" +
                "MEND\n" +
                "ADD DATA1, DATA2\n" +
                "SUB DATA1, DATA2\n" +
                "DATA1 DC F'9'\n" +
                "DATA DC F'5\n";
        MacroProcessor processor = new MacroProcessor();
        parseProgram(input, processor);

    }
}
