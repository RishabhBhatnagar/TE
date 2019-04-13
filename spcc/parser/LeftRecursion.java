import java.util.ArrayList;
import java.util.List;

public class LeftRecursion {
    static String removeLrGrammarString(String prodRules){
        Grammar grammar = new Grammar(prodRules);
        List<Production> lrSufferingProductions = new ArrayList<>();
        List<Production> lrNotSufferingProductions = new ArrayList<>();
        for (Production production : grammar.productions) {
            boolean found = false;
            for (ProductionRule productionRule : production.productionRules) {
                if (productionRule.symbols.get(0).data.equals(productionRule.nonTerminal.data)) {
                    lrSufferingProductions.add(production);
                    found = true;
                    break;
                }
            }
            if(!found){
                lrNotSufferingProductions.add(production);
            }
        }

        if(lrSufferingProductions.size() == 0)
            return grammar.getPrintableGrammar();

        StringBuilder LRRemovedGrammar = new StringBuilder();
        for (Production production : lrSufferingProductions) {
            List<ProductionRule> beta = new ArrayList<>();
            String alphaString = null;
            for(ProductionRule productionRule: production.productionRules){
                if(!productionRule.symbols.get(0).data.equals(productionRule.nonTerminal.data)){
                    beta.add(productionRule);
                } else{
                    for(int i=1; i<productionRule.symbols.count; i++){
                        alphaString += productionRule.symbols.get(i).data;
                    }
                    assert alphaString != null;
                    alphaString = alphaString.replaceFirst("null", "");
                }
            }

            String dash = production.nonTerminal+"'";
            LRRemovedGrammar.append(production.nonTerminal).append(" -> ");
            for (ProductionRule productionRule: beta){
                LRRemovedGrammar.append(productionRule.symbols.getPrintable()).append(dash).append("|");
            }
            LRRemovedGrammar.append('\n');
            LRRemovedGrammar.append(dash).append("-> ").append(alphaString).append(dash).append("|ε");
        }
        LRRemovedGrammar.append('\n');
        for(Production production: lrNotSufferingProductions){
            LRRemovedGrammar.append(production.nonTerminal.data + " -> ");
            for(int i = 0; i<production.productionRules.length; i++){
                ProductionRule productionRule = production.productionRules[i];
                LRRemovedGrammar.append(productionRule.symbols.getPrintable());
                if(i<production.productionRules.length - 1){
                    LRRemovedGrammar.append("|");
                }
            }
            LRRemovedGrammar.append('\n');
        }
        return LRRemovedGrammar.toString();
    }
    public static void main(String[] args) {
        String prodRules = "A -> aC|aBC|B\nB -> Cd\nC -> c";
        Grammar grammar = new Grammar(prodRules);
        System.out.println(grammar.getPrintableGrammar());
        String alteredGrammar = removeLrGrammarString(prodRules);
        if(alteredGrammar.equals(grammar.getPrintableGrammar())){
            System.out.println("Given production doesn't suffer from left recursion.");
        } else {
            System.out.println("There exists left recursion in given grammar.");
        }
        System.out.println(alteredGrammar);
    }
}

/*
OUTPUT:
Grammar with left recursion:
A->AaC|aBC|B|
B->Cd|
C->c|

There exists left recursion in given grammar.

A -> aBCA'|BA'|
A'-> aCA'|ε
B -> Cd
C -> c


Grammar without left recursion
A->aC|aBC|B|
B->Cd|
C->c|

Given production doesn't suffer from left recursion.


A -> aC|aBC|B
B -> Cd
C -> c
*/