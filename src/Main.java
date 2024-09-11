import lexical.LexicalAnalysis;
import syntatic.SyntacticAnalysis;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String fileName = null;

        if (args.length == 0) {
            Scanner scanner = new Scanner(System.in);
            fileName = scanner.nextLine();
        } else {
            fileName = args[0];
        }

        if (fileName == null) {
            System.out.println("File name not provided");
            System.exit(1);
        }

        LexicalAnalysis l = new LexicalAnalysis(fileName);
        SyntacticAnalysis s = new SyntacticAnalysis(l);
        s.start();
    }
}