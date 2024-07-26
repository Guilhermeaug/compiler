import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.SymbolTable;
import lexical.TokenType;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String fileName = "test-cases/1.cc";

//        if (args.length == 0) {
//            Scanner scanner = new Scanner(System.in);
//            fileName = scanner.nextLine();
//        } else {
//            fileName = args[0];
//        }

        if (fileName == null) {
            System.out.println("File name not provided");
            System.exit(1);
        }

        SymbolTable st = new SymbolTable();

        LexicalAnalysis l = new LexicalAnalysis(st, fileName);
        Lexeme lex;
        do {
            lex = l.nextToken();
            System.out.printf("%02d: (\"%s\", %s)\n", l.getLine(), lex.token, lex.type);
        } while (lex.type != TokenType.END_OF_FILE &&
                lex.type != TokenType.INVALID_TOKEN &&
                lex.type != TokenType.UNEXPECTED_EOF
        );
    }
}