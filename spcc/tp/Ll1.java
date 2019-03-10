import java.util.*;
import java.util.stream.Collectors;

public class Ll1 {
    String grammarString;
    static String epsilon = "ε";
    static Grammar grammar;
    static Dictionary<Symbol, List<Symbol>> firstSet, followSet;
    static Dictionary<String, Dictionary<String, ProductionRule>> parsingTable;
    static boolean isParsingTableBuilt = false;

    Ll1(String productionString){
        this.grammarString = LeftRecursion.removeLrGrammarString(productionString);
        this.grammar = new Grammar(grammarString);
        firstSet = first_follow.getFirstSet(grammar);
        followSet = first_follow.getFollowSet(grammar);
        buildParsingTable();
    }
    static boolean terminalExists(Production production, String targetTerminal){
        for(Symbol symbol: first_follow.getInitialSymbols(production)){
            if(symbol.typeSymbol == TypeSymbol.TERMINAL){
                if(symbol.data.equals(targetTerminal)){
                    return true;
                }
            } else{
                terminalExists(grammar.getProductionOfNonTerminal(symbol.data), targetTerminal);
            }
        }
        return false;
    }
    static ProductionRule findProductionRuleForTerminal(String nonTerminal, Symbol target){
        /*
        * This returns the production which generated the given terminal symbol.
        * This approach is flawed in one terms.
        * */
        Production sourceProductions = grammar.getProductionOfNonTerminal(nonTerminal);
        if(sourceProductions.nProductionRules == 1){
            return sourceProductions.productionRules[0];
        } else{
            for(ProductionRule productionRule:sourceProductions.productionRules){
                if(terminalExists(grammar.getProductionOfNonTerminal(productionRule.nonTerminal.data), target.data)){
                    return productionRule;
                }
            }
        }

        for(Production production: grammar.productions){
            for(ProductionRule productionRule:production.productionRules){
                for(int i=0; i<productionRule.symbols.count; i++){
                    if(productionRule.symbols.get(i).data == target.data){
                        return productionRule;
                    }
                }
            }
        }
        return null;
    }
    static void buildParsingTable(){
        parsingTable = new Hashtable<>();
        Enumeration<Symbol> nonTerminal = firstSet.keys();
        // Enumerating over all the non-terminals:
        while(nonTerminal.hasMoreElements()) {
            Symbol currentNonTerminal = nonTerminal.nextElement();
            List<Symbol> firstSymbols = firstSet.get(currentNonTerminal);
            for (Symbol firstSymbol: firstSymbols) {
                if(firstSymbol.data.equals(epsilon)){
                    List<Symbol> _symbols1 = followSet.get(currentNonTerminal);
                    for (Symbol _symbol: _symbols1) {
                        Dictionary<String, ProductionRule> _newDictionary = new Hashtable<>();
                        _newDictionary.put(firstSymbol.data, findProductionRuleForTerminal(currentNonTerminal.data, new Symbol(epsilon, TypeSymbol.TERMINAL)));
                        parsingTable.put(
                            currentNonTerminal.data,
                            _newDictionary
                        );
                    }
                }
                else {
                    ProductionRule productionRule = findProductionRuleForTerminal(currentNonTerminal.data, firstSymbol);
                    if (productionRule != null) {
                        if(parsingTable.get(productionRule.nonTerminal.data) == null) {
                            Dictionary<String, ProductionRule> newDictionary = new Hashtable<>();
                            newDictionary.put(firstSymbol.data, productionRule);
                            parsingTable.put(
                                    productionRule.nonTerminal.data,
                                    newDictionary
                            );
                        } else{
                            parsingTable.get(productionRule.nonTerminal.data).put(firstSymbol.data, productionRule);
                        }
                    }
                }
            }
        }
        String p = parsingTable.toString();
        isParsingTableBuilt = true;
    }
    static List<String> splitStringKeepingElement(String string, String delimiter){
        List<String> result = new ArrayList<String>();
        delimiter = delimiter.replace("+", "\\+");
        delimiter = delimiter.replace("*", "\\*");
        delimiter = delimiter.replace("(", "\\(");
        delimiter = delimiter.replace(")", "\\)");
        for(String part: string.split(delimiter)){
            result.add(part);
            result.add(delimiter);
        }
        return result;
    }
    static List<String> splitListKeepingElement(List<String> string, String delimiter){
        List<String> result = new ArrayList<>();
        for(String part:string){
            result.addAll(splitStringKeepingElement(part, delimiter));
        }
        return result;
    }
    static List<String> tokenizeUsingList(String input, List<String> list){
        List<String> result = new ArrayList<String>();
        result.add(input);
        for(String delimiter: list){

            result = splitListKeepingElement(result, delimiter);
        }
        return result;
    }
    int validateString(String input){
        if(isParsingTableBuilt && parsingTable == null){
            // There was an error while building parsing table.
            return 0;
        }
        List<Symbol> allTerminalSymbols = grammar.getAllTerminals();
        List<String> allTerminalStrings = allTerminalSymbols.stream().map(symbol -> symbol.data).collect(Collectors.toList());
        System.out.println(tokenizeUsingList("id+id*id", allTerminalStrings));
        System.out.println(allTerminalStrings);
        return 0;
    }

    public static void main(String[] args) {
        String prodRules = "E -> TZ\nZ -> +TZ|ε\nT -> FY\nY -> *FY|ε\nF -> id|(E)";
        // removing left recursion
        prodRules = LeftRecursion.removeLrGrammarString(prodRules);
        Ll1 ll1 = new Ll1(prodRules);
        ll1.validateString("id");
    }
}
