import java.util.*;

public class first_follow {
    public static void main(String[] args) {
        String prodRules = "A -> BaC|adf\nB -> Cc\n";
        Grammar grammar = new Grammar(prodRules);
        System.out.println(getFirstSet(grammar));
    }
    static List<Symbol> getInitialSymbols(Production production){
        List<Symbol> firstSymbols = new ArrayList<>();
        for(ProductionRule productionRule: production.productionRules){
            firstSymbols.add(productionRule.symbols.get(0));
        }
        return firstSymbols;
    }
    static Dictionary<Symbol, List<Symbol>> getFirstSet(Grammar grammar){
        Dictionary<Symbol, List<Symbol>> firstSet = new Hashtable<>();
        for(Production production: grammar.productions){
            firstSet.put(production.nonTerminal, getFirstForNonTerminal(grammar, production));
        }
        return firstSet;
    }
    static List<Symbol> getFirstForNonTerminal(Grammar grammar, Production production){
        Symbol currentNonTerminal = production.nonTerminal;
        List<Symbol> firstList = new ArrayList<>();

        for(Symbol symbol: getInitialSymbols(production)){
            if(symbol.typeSymbol == TypeSymbol.TERMINAL){
                firstList.add(symbol);
            } else{
                firstList.addAll(getFirstForNonTerminal(grammar, grammar.getProductionOfNonTerminal(symbol.data)));
            }
        }
        return firstList;
    }
}