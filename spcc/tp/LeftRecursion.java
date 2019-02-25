import java.util.ArrayList;
import java.util.List;

public class LeftRecursion {
    public static void main(String[] args) {
        String prodRules = "A -> AaC|aBC|B\nB -> Cd\nC -> c";
        Grammar grammar = new Grammar(prodRules);
        System.out.println(grammar.getPrintableGrammar());


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

        if(lrSufferingProductions.size() == 0){
            System.out.println("Given production doesn't suffer from left recursion.");
        } else{
            System.out.println("There exists left recursion in given grammar.");
        }
        System.out.println();
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
            System.out.print(production.nonTerminal+" -> ");
            for (ProductionRule productionRule: beta){
                System.out.print(productionRule.symbols.getPrintable()+dash+"|");
            }
            System.out.println();
            System.out.print(dash+"-> "+alphaString+dash+"|ε");
        }
        System.out.println();
        for(Production production: lrNotSufferingProductions){
            System.out.print(production.nonTerminal.data + " -> ");
            for(int i = 0; i<production.productionRules.length; i++){
                ProductionRule productionRule = production.productionRules[i];
                System.out.print(productionRule.symbols.getPrintable());
                if(i<production.productionRules.length - 1){
                    System.out.print("|");
                }
            }
            System.out.println();
        }
    }
}