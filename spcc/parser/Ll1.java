import java.util.*;
import java.util.stream.Collectors;

public class Ll1 {
    private String grammarString;
    private static String epsilon = "ε";
    private static Grammar grammar;
    private static Dictionary<Symbol, List<Symbol>> firstSet, followSet;
    private static Dictionary<String, Dictionary<String, ProductionRule>> parsingTable;
    private static boolean isParsingTableBuilt = false;

    Ll1(String productionString) {
        this.grammarString = LeftRecursion.removeLrGrammarString(productionString);
        this.grammar = new Grammar(grammarString);
        firstSet = first_follow.getFirstSet(grammar);
        followSet = first_follow.getFollowSet(grammar);
        buildParsingTable();
    }

    static boolean terminalExists(ProductionRule productionRule, String targetTerminal, Dictionary<Symbol, List<Symbol>> firstSet) {
        Dictionary<String, Boolean> isSeen = new Hashtable<String, Boolean>();
        Set<String> intermediateNonTerminals = new HashSet<String>();
        for (int i = 0; i < productionRule.symbols.count; i++) {
            Symbol currentSymbol = productionRule.symbols.get(i);
            if (currentSymbol.data.equals(targetTerminal)) return true;
            else if (currentSymbol.typeSymbol == TypeSymbol.NON_TERMINAL) {
                if (isSeen.get(currentSymbol.data) == null) {
                    intermediateNonTerminals.add(currentSymbol.data);
                    isSeen.put(currentSymbol.data, true);
                }
            }
        }
        for (String nonTerminal : intermediateNonTerminals) {
            for (ProductionRule productionRule1 : grammar.getProductionOfNonTerminal(nonTerminal).productionRules) {
                if (productionRule1.symbols.get(0).data.equals(targetTerminal)) {
                    return true;
                }
            }
        }
        return false;
    }

    static ProductionRule findProductionRuleForTerminal(String nonTerminal, Symbol target) {
        /*
         * This returns the production which generated the given terminal symbol.
         * This approach is flawed in one terms.
         * */
        if (grammar.getProductionOfNonTerminal(nonTerminal).productionRules.length == 1) {
            return grammar.getProductionOfNonTerminal(nonTerminal).productionRules[0];
        }
        for (Production production : grammar.productions) {
            for (ProductionRule productionRule : production.productionRules) {
                for (int i = 0; i < productionRule.symbols.count; i++) {
                    if (productionRule.symbols.get(i).data.equals(target.data)) {
                        return productionRule;
                    }
                }
            }
        }

        return null;
    }

    private static void buildParsingTable() {
        parsingTable = new Hashtable<>();
        Enumeration<Symbol> nonTerminal = firstSet.keys();
        // Enumerating over all the non-terminals:
        while (nonTerminal.hasMoreElements()) {
            Symbol currentNonTerminal = nonTerminal.nextElement();
            List<Symbol> firstSymbols = firstSet.get(currentNonTerminal);
            for (Symbol firstSymbol : firstSymbols) {
                if (firstSymbol.data.equals(epsilon)) {
                    List<Symbol> _symbols1 = followSet.get(currentNonTerminal);
                    for (Symbol _symbol : _symbols1) {
                        Dictionary<String, ProductionRule> _newDictionary = new Hashtable<>();
                        _newDictionary.put(firstSymbol.data, findProductionRuleForTerminal(currentNonTerminal.data, new Symbol(epsilon, TypeSymbol.TERMINAL)));
                        parsingTable.put(
                                currentNonTerminal.data,
                                _newDictionary
                        );
                    }
                } else {
                    ProductionRule productionRule = findProductionRuleForTerminal(currentNonTerminal.data, firstSymbol);
                    if (productionRule != null) {
                        if (parsingTable.get(productionRule.nonTerminal.data) == null) {
                            Dictionary<String, ProductionRule> newDictionary = new Hashtable<>();
                            newDictionary.put(firstSymbol.data, productionRule);
                            parsingTable.put(
                                    productionRule.nonTerminal.data,
                                    newDictionary
                            );
                        } else {
                            parsingTable.get(productionRule.nonTerminal.data).put(firstSymbol.data, productionRule);
                        }
                    }
                }
            }
        }
        String p = parsingTable.toString();
        isParsingTableBuilt = true;
    }

    static List<String> tokenizeUsingList(String input, List<String> list) {
        List<String> result = new ArrayList<String>();
        while (!input.equals("")) {
            input = input.trim();
            int i = 0;
            while (list.parallelStream().noneMatch(input.substring(0, i)::contains)) i += 1;
            result.add(input.substring(0, i));
            input = input.substring(i, input.length());
            input = input.trim();
        }
        return result;
    }

    int validateString(String input, Symbol nonTerminal) {
        if (isParsingTableBuilt && parsingTable == null) {
            // There was an error while building parsing table.
            return 0;
        }
        List<Symbol> allTerminalSymbols = grammar.getAllTerminals();
        List<String> allTerminalStrings = allTerminalSymbols.stream().map(symbol -> symbol.data).collect(Collectors.toList());
        List<String> ip = tokenizeUsingList(input, allTerminalStrings);
        ip.add("$");
        Stack<String> stack = new Stack<String>();
        stack.push("$");
        stack.push(nonTerminal.data);

        int index = 0;
        while (!stack.empty()) {
            String stackTop = stack.pop();
            if (stackTop.equals(ip.get(index))) {
                index += 1;
            } else {
                try {
                    ProductionRule productionRule = parsingTable.get(stackTop).get(ip.get(index));
                    for (int i = 0; i < productionRule.symbols.count; i++) {
                        stack.push(productionRule.symbols.get(productionRule.symbols.count - 1 - i).data);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    return 0;
                }
            }
        }
        return 1;
    }

    public static void main(String[] args) {
        String prodRules = "Stmt->if Expr then Stmt else Stmt|while Expr do Stmt| begin Stmt end|something\nExpr->id";
        // removing left recursion
        prodRules = LeftRecursion.removeLrGrammarString(prodRules);
        Ll1 ll1 = new Ll1(prodRules);
        System.out.println(ll1.validateString("if id then something else something", grammar.startSymbol));
    }
}