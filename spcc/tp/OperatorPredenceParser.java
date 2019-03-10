import java.util.Dictionary;
import java.util.List;

public class OperatorPredenceParser {
    public static void main(String[] args) {
        String prodRules = "A -> BaC|aBC|B\nB -> Cd\nC -> c";
        Grammar grammar = new Grammar(prodRules);

        System.out.println(grammar.getPrintableGrammar());

        Dictionary<Symbol, List<Symbol>> firstSet= first_follow.getFirstSet(grammar);
        Dictionary<Symbol, List<Symbol>> followSet = first_follow.getFollowSet(grammar);
    }
}
