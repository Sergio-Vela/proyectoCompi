import java.io.*;
import java_cup.runtime.Symbol;

public class Main {
    public static void main(String[] args) {

        try {
            Reader reader;

            if (args.length > 0) {
                reader = new FileReader(args[0]);
            } else {
                reader = new InputStreamReader(System.in);
            }

            Lexer lexer = new Lexer(reader);
            parser p = new parser(lexer);

            p.parse();

            if (!Errors.hayErrores) {
                System.out.println("OK");
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}