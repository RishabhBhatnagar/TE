import java.util.*;
public class first_follow {
    public static void main(String[] args) {
        String prodRules = "A -> BaC|aBC|B\nB -> Cd\nC -> c";
        Grammar grammar = new Grammar(prodRules);
        System.out.println(grammar.getPrintableGrammar());
        System.out.println(getFirstSet(grammar));
        System.out.println(getFollowSet(grammar));
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
    static Dictionary<Symbol, List<Symbol>> getFollowSet(Grammar grammar){
        Dictionary<Symbol, List<Symbol>> followSet = new Hashtable<>();
        for(Production production: grammar.productions){
            followSet.put(production.nonTerminal, getFollowForProduction(grammar, production));
        }
        return followSet;
    }

    static List<Symbol> getFirstForNonTerminal(Grammar grammar, Production production){
        List<Symbol> firstList = new ArrayList<>();

        for(Symbol symbol: getInitialSymbols(production)){
            if(symbol.typeSymbol == TypeSymbol.TERMINAL){
                firstList.add(symbol);
            } else{
                firstList.addAll(getFirstForNonTerminal(grammar, grammar.getProductionOfNonTerminal(symbol.data)));
            }
        }
        return getSet(firstList);
    }

    static class Pair{
        public Pair(ProductionRule productionRule, int position) {
            this.productionRule = productionRule;
            this.position = position;
        }
        ProductionRule productionRule;
        int position;
    }

    static List<Pair> getProductionRulesWithNonTerminals(Grammar grammar, Symbol nonTerminal){
        // productions with a specific non terminal in rhs.

        List<Pair> productionRules = new ArrayList<>();

        for (Production production: grammar.productions){
            for(ProductionRule productionRule: production.productionRules){
                for(int i = 0; i<productionRule.symbols.count; i++){
                    Symbol symbol = productionRule.symbols.get(i);
                    if(symbol.data.equals(nonTerminal.data)){
                        productionRules.add(new Pair(productionRule, i));
                        break;
                    }
                }
            }
        }
        return productionRules;
    }
    static List<Symbol> getSet(List<Symbol> set){
        List<String> symbolString = new ArrayList<>();
        List<Symbol> tempSymbol = new ArrayList<>();
        for(int i = 0; i<set.size(); i++){
            if(!symbolString.contains(set.get(i).data)){
                symbolString.add(set.get(i).data);
                tempSymbol.add(set.get(i));
            }
        }
        return tempSymbol;
    }
    static List<Symbol> getFollowForProduction(Grammar grammar, Production production){
        Symbol nonTerminal = production.nonTerminal;
        List<Symbol> followSet = new ArrayList<>();

        if(production.nonTerminal.data.equals(grammar.startSymbol.data)){
            followSet.add(new Symbol("$", TypeSymbol.TERMINAL));
        }

        for(Pair pair: getProductionRulesWithNonTerminals(grammar, nonTerminal)){
            ProductionRule productionRule = pair.productionRule;
            int position = pair.position;
            if(position + 1 < productionRule.symbols.count){
                Symbol sym = productionRule.symbols.get(position+1);
                if(sym.typeSymbol == TypeSymbol.NON_TERMINAL){
                    for(Symbol symbol: getFirstForNonTerminal(grammar, grammar.getProductionOfNonTerminal(sym.data))){
                        followSet.add(symbol);
                    }
                } else {
                    // found a terminal
                    followSet.add(sym);
                }
            } else{
                // terminal is found at last position.
                if(!productionRule.nonTerminal.data.equals(production.nonTerminal.data)){
                    for(Symbol symbol: getFollowForProduction(grammar, grammar.getProductionOfNonTerminal(productionRule.nonTerminal.data))){
                        followSet.add(symbol);
                    }
                }
            }
        }
        return getSet(followSet);
    }
}