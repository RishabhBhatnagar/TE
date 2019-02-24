public class first_follow {
    public static void main(String[] args) {
        String prodRules = "A -> BaC|adf\nB -> Cc\n C -> c";

        Grammar grammar = new Grammar(
                prodRules,
                "\n",
                "->",
                "\\|"
        );
        Production[] productions = grammar.productions;
        for (Production production : productions) {
            System.out.println(production.nonTerminal);
        }
        System.out.println(productions[2].nonTerminal);

    }
}