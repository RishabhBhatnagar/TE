import static java.lang.Integer.min;

public class MathLib {
    static int factorial(int n){
        if(n==0 || n==1)
            return 1;
        return n*factorial(n-1);
    }
    static int random(int n1, int n2){
        return (int) (min(n1, n2)+Math.random()/(n2-n1));
    }
}
