import java.util.Scanner;

public class Python {
    static Scanner scanner = new Scanner(System.in);
    private static int input(String s){
        System.out.print(s);
        return scanner.nextInt();
    }
    public static void main(String[] args) {
        int choice = input(
                "-------------------------------------\n"+
                   "|Number  |   Function               |\n" +
                   "|--------|--------------------------|\n" +
                   "|   1    | Get random number        |\n" +
                   "|   2    | Get factorial of a number|\n" +
                   "-------------------------------------\n" +
                        "Enter your choice: "
        );
        if(choice == 1){
            int n1 = input("Enter first number: ");
            int n2 = input("Enter second number: ");
            System.out.println(
                    String.format(
                            "random number  between %d and %d is : %d",
                            n1,
                            n2,
                            MathLib.random(n1, n2)
                    )
            );
        }
        else if(choice == 2){
            int n3 = input("Enter the number to find factorial of: ");
            int fact = MathLib.factorial(n3);
            System.out.println(
                    String.format(
                            "factorial of %d is: %d",
                            n3,
                            fact
                    )
            );
        }
    }
}
