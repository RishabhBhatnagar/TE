enum TypeSymbol {
    // only good thing I found in Java programming language.
    TERMINAL, NON_TERMINAL
}

class Symbol {
    String data;
    TypeSymbol typeSymbol;

    Symbol(String data, TypeSymbol typeSymbol) {
        this.data = data;
        this.typeSymbol = typeSymbol;
    }

    @Override
    public String toString() {
        return this.data;
    }
}

class ProductionRule {
    final String productionString;
    Symbol nonTerminal;
    LinkedSymbols symbols = null;

    /*
     * This class will store specific rule for the given non terminal.
     * For example:
     * if A -> BC | D | id,
     * This class will store
     *   --> A -> BC
     *   --> A -> D
     *   These as seperate instances.
     * */
    void separateAllSymbols(String[] nonTerminals) {
        /*
         * From a String of terminal and NonTerminal combined, seggregate all the non terminals from the production rule.
         * For example:
         * If the given grammar is:
         *     A -> (BC) | id
         *     B -> id
         *     C -> A
         *
         *     if A -> BC is the production rule to be represented by this instance,
         *     B, C should be seperated as non-terminals and (, ) should be classified as terminal.
         * */
        String productionString = this.productionString;    //creating a temporary copy of production string.

        symbols = new LinkedSymbols();                        // all the parsed symbols will be stored here.

        // Parsing the rule to extract terminal and non-terminals from the production string.
        while (productionString.length() != 0) {

            // flag to check if nonTerminal is found at start of the production rule.
            boolean found = false;

            // iterating over all non-terminals to check if it is the starting of production string.
            for (String nonTerminal : nonTerminals) {
                if (productionString.startsWith(nonTerminal)) {
                    // production string starts with the current non-terminal
                    found = true;

                    int lenNonTerminal = nonTerminal.length();
                    int n = productionString.length();

                    // removing nonTerminal string from start of the productionString.
                    productionString = productionString.substring(lenNonTerminal, n);

                    // adding nonTerminal to the set of seenSymbols
                    symbols.append(new Symbol(nonTerminal, TypeSymbol.NON_TERMINAL));
                }
            }

            if (found) {
                continue;  // non-terminal was found at start of the production string, program can continue parsing.
            }

            // The production string doesn't start with Non-Terminal.
            String terminal = "";

            // find append characters to terminal until we find a non-terminal at start.
            while (!found) {

                // removing one character from production string and appending it to terminal.
                terminal += productionString.substring(0, 1);
                productionString = productionString.substring(1);

                //checking if the new production string starts with non-terminal
                for (String nonTerminal : nonTerminals) {

                    // checking if production string starts with current non-terminal.
                    if (productionString.startsWith(nonTerminal) || productionString.length() == 0) {
                        found = true;
                        // adding extracted terminal to symbols.
                        symbols.append(new Symbol(terminal, TypeSymbol.TERMINAL));

                        if(productionString.length() == 0){
                            break;
                        }

                        // adding the current non-terminal to symbol_table.
                        int lenNonTerminal = nonTerminal.length();
                        int n = productionString.length();
                        // removing nonTerminal string from start of the productionString.
                        productionString = productionString.substring(lenNonTerminal, n);
                        // adding nonTerminal to the set of seenSymbols
                        symbols.append(new Symbol(nonTerminal, TypeSymbol.NON_TERMINAL));

                        break;
                    }
                }
            }
        }
    }

    ProductionRule(String productionString, Symbol nonTerminal, String[] nonTerminals) {
        this.nonTerminal = nonTerminal;
        this.productionString = productionString;
        this.separateAllSymbols(nonTerminals);
    }

    ProductionRule(String productionString, Symbol nonTerminal) {
        this.productionString = productionString;
        this.nonTerminal = nonTerminal;
    }
}

class Production {
    /*
     * This class will store all the production rules for a specific non terminal.
     * For example:
     * A -> BC | D | id
     * This will be stored by this class' variables.
     * */

    // String which assigns production to a non-terminal. Usually "->" is used.
    String productionAssignment;

    Symbol nonTerminal;         // non-terminal of current production.
    String productionSplitBy;   // string wrt which two prod of same non-terminal is seperated. Usually '|'.
    ProductionRule[] productionRules;        // separate rules.
    int nProductionRules;

    public void buildProduction(String prodStr, String productionAssignment, String productionSplitBy) {
        /*
         * @prodStr: Production rule represented in string format
         *            For eg:   A -> BC
         * @productionAssignment:  String by which the lhs non terminal and rhs production is separated.
         *                         For eg: In above case, "->" was the assignment string.
         * @productionSplitBy: String using which production rules of same non-terminal are separated.
         * */
        this.productionAssignment = productionAssignment;
        this.productionSplitBy = productionSplitBy;

        // To-do: handle case when splitBy has regex chars.
        String[] parts = prodStr.split(this.productionAssignment, 2);

        // assigning lhs symbol to non-terminal for current production.
        this.nonTerminal = new Symbol(parts[0].strip(), TypeSymbol.NON_TERMINAL);

        // trimming white space from production
        String allProductions = parts[1].strip();

        // all the rhs of productions for a given terminal in string format.
        String[] productionRulesStrings = allProductions.split(productionSplitBy);
        nProductionRules = productionRulesStrings.length;

        // building productions from given production strings.
        productionRules = new ProductionRule[productionRulesStrings.length];
        for (int i = 0; i < nProductionRules; i++) {
            productionRules[i] = new ProductionRule(productionRulesStrings[i], nonTerminal);
        }
    }

    void buildProductionRules(String[] nonTerminals) {
        for (ProductionRule production : productionRules) {
            production.separateAllSymbols(nonTerminals);
        }
    }

    Production(String productionString, String productionAssignment, String productionSplitBy) {
        buildProduction(productionString, productionAssignment, productionSplitBy);
    }
}

class Grammar {
    // This has all the production rules defined.
    int nProductions = 0;     // number of productions.
    Production[] productions; // All the productions
    String grammar;
    String nonTerminalSeparator;
    String productionAssignment;
    String productionDelimiter;
    Symbol startSymbol;

    public Production[] buildGrammarFromString(String grammar, String nonTerminalSeparator, String productionAssignment, String productionDelimiter) {
        /* This method provides user to update the grammar without creating new Grammar instance from existing object.
         * @grammar: All the production rules in String format.
         * @nonTerminalSeparator: String that separates two non terminal productions. Usually newline(CRLF).
         *                        OR String using which productions are split.
         * @productionAssignment: String which assigns prod rules of rhs to the non-terminal to the left. Usually '->'
         * @productionDelimiter: String that separates production rules of same non terminal from each other.
         * */

        // splitting the grammar string to get all the productions string.
        String[] stringProductionRules = grammar.split(nonTerminalSeparator);

        // resetting the instance variables.
        nProductions = stringProductionRules.length;
        productions = new Production[nProductions];

        //
        this.grammar = grammar;
        this.nonTerminalSeparator = nonTerminalSeparator;
        this.productionAssignment = productionAssignment;
        this.productionDelimiter = productionDelimiter;

        // for all production strings in the productionRulesStrings, parse and create a production.
        for (int i = 0; i < nProductions; i++) {
            String productionString = stringProductionRules[i];
            productions[i] = new Production(
                    productionString,
                    productionAssignment,
                    productionDelimiter
            );
        }
        // all the production strings are loaded to instances of productions.

        // separating all the terminals and non-terminals from string productions.
        String[] nonTerminals = extractNonTerminals();
        for (Production production : productions) {
            production.buildProductionRules(nonTerminals);  // separates non-terminals from the productions.
        }
        this.startSymbol = productions[0].nonTerminal;
        return productions;
    }

    String[] extractNonTerminals() {
        String nonTerminals[] = new String[productions.length];
        for (int i = 0; i < productions.length; i++) {
            nonTerminals[i] = productions[i].nonTerminal.data;
        }
        return nonTerminals;
    }

    Grammar(String grammarString, String nonTerminalSeparator, String productionAssignment, String productionDelimiter) {
        buildGrammarFromString(grammarString, nonTerminalSeparator, productionAssignment, productionDelimiter);
        this.startSymbol = this.productions[0].nonTerminal;
    }

    Grammar() {
        // Empty constructor allowing user to create a grammar without any production rules.
    }

    Grammar(String grammarString) {
        // Default grammar that is used.
        buildGrammarFromString(grammarString,
                "\n", //nonTerminalSeparator,
                "->", // productionAssignment,
                "\\|"// productionDelimiter
        );
    }

    void addProduction(String production){
        // This is a very inefficient way.
        // It is almost equivalent to creating a new object.
        // This appends new string to the grammar and rebuilds it.
        this.grammar += this.nonTerminalSeparator + production;
        buildGrammarFromString(
                this.grammar,
                this.nonTerminalSeparator,
                this.productionAssignment,
                this.productionDelimiter
        );
    }

    Production getProductionOfNonTerminal(String nonTerminal){
        for(Production production: this.productions){
            if(production.nonTerminal.data.equals(nonTerminal)){
                return production;
            }
        }
        return null;
    }

    String getPrintableGrammar(){
        String grammarString = "";
        for(Production production:this.productions){
            grammarString += production.nonTerminal + this.productionAssignment;
            for(ProductionRule productionRule: production.productionRules){
                for(int i=0; i<productionRule.symbols.count; i++){
                    grammarString += productionRule.symbols.get(i);
                }
                grammarString += productionDelimiter.replace("\\", "");
            }
            grammarString += nonTerminalSeparator;
        }
        return grammarString;
    }
}