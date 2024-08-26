package syntatic;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.SymbolTable;
import lexical.TokenType;

import java.util.Arrays;

public class SyntacticAnalysis {
    private final LexicalAnalysis lex;
    private Lexeme current;

    private SymbolTable st;

    public SyntacticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
    }

    private void advance() {
        current = lex.nextToken();
    }

    private void consume(TokenType type) {
        if (type == current.type) {
            current = lex.nextToken();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN -> System.out.printf("Lexema inválido [%s]\n", current.token);
            case UNEXPECTED_EOF, END_OF_FILE -> System.out.print("Fim de arquivo inesperado\n");
            default -> System.out.printf("Lexema não esperado [%s]\n", current.token);
        }
        throw new RuntimeException("Syntax error");
    }


    public void start() {
        program();
        System.out.println("Programa sintaticamente correto");
    }

    private void program() {
        consume(TokenType.APP);
        consume(TokenType.IDENTIFIER);
        body();
    }

    private void body() {
        if (current.type == TokenType.VAR) {
            consume(TokenType.VAR);
            declList();
        }
        consume(TokenType.INIT);
        stmtList();
        consume(TokenType.RETURN);
    }

    private void declList() {
        decl();
        consume(TokenType.SEMICOLON);
        decl();
    }

    private void decl() {
        type();
        identList();
    }

    private void identList() {
        consume(TokenType.IDENTIFIER);
        while (current.type == TokenType.COMMA) {
            consume(TokenType.COMMA);
            consume(TokenType.IDENTIFIER);
        }
    }

    private void type() {
        if (current.type == TokenType.INTEGER) {
            consume(TokenType.INTEGER);
        } else if (current.type == TokenType.REAL) {
            consume(TokenType.REAL);
        } else {
            showError();
        }
    }

    private void stmtList() {
        stmt();
        while (current.type == TokenType.SEMICOLON) {
            consume(TokenType.SEMICOLON);
            stmt();
        }
    }

    private void stmt() {
        switch (current.type) {
            case IDENTIFIER -> assignStmt();
            case IF -> ifStmt();
            case REPEAT -> repeatStmt();
            case READ -> readStmt();
            case WRITE -> writeStmt();
            default -> showError();
        }
    }

    private void assignStmt() {
        consume(TokenType.IDENTIFIER);
        consume(TokenType.ASSIGN);
        simpleExpr();
    }

    private void ifStmt() {
        consume(TokenType.IF);
        condition();
        consume(TokenType.THEN);
        stmtList();
        if (current.type == TokenType.ELSE) {
            consume(TokenType.ELSE);
            stmtList();
        }
        consume(TokenType.END);
    }

    private void repeatStmt() {
        consume(TokenType.REPEAT);
        stmtList();
        stmtSuffix();
    }

    private void stmtSuffix() {
        consume(TokenType.UNTIL);
        condition();
    }

    private void readStmt() {
        consume(TokenType.READ);
        consume(TokenType.OPEN_PAR);
        consume(TokenType.IDENTIFIER);
        consume(TokenType.CLOSE_PAR);
    }

    private void writeStmt() {
        consume(TokenType.WRITE);
        consume(TokenType.OPEN_PAR);
        writable();
        consume(TokenType.CLOSE_PAR);
    }

    private void writable() {
        switch (current.type) {
            case IDENTIFIER:
            case IF:
            case INTEGER_CONST:
            case REAL_CONST:
            case OPEN_PAR:
            case SUB:
            case NOT:
                simpleExpr();
                break;
            case TEXT:
                literal();
                break;
            default:
                showError();
        }
    }

    private void condition() {
        expression();
    }

    private void expression() {
        simpleExpr();
        while (Arrays.asList(
                TokenType.EQUAL,
                TokenType.GREATER_THAN,
                TokenType.GREATER_EQUAL,
                TokenType.LOWER_THAN,
                TokenType.LOWER_EQUAL,
                TokenType.NOT_EQUAL
        ).contains(current.type)) {
            advance();
            simpleExpr();
        }
    }

    private void simpleExpr() {
        term();
        while (Arrays.asList(TokenType.ADD, TokenType.SUB, TokenType.OR).contains(current.type)) {
            advance();
            term();
        }
    }

    private void term() {
        factorA();
        while (Arrays.asList(TokenType.MUL, TokenType.DIV, TokenType.AND).contains(current.type)) {
            advance();
            factorA();
        }
    }

    private void factorA() {
        if (current.type == TokenType.SUB) {
            consume(TokenType.SUB);
        } else if (current.type == TokenType.NOT) {
            consume(TokenType.NOT);
        }
        factor();
    }

    private void factor() {
        if (current.type == TokenType.IDENTIFIER) {
            consume(TokenType.IDENTIFIER);
        } else if (current.type == TokenType.INTEGER_CONST) {
            consume(TokenType.INTEGER_CONST);
        } else if (current.type == TokenType.REAL_CONST) {
            consume(TokenType.REAL_CONST);
        } else if (current.type == TokenType.OPEN_PAR) {
            consume(TokenType.OPEN_PAR);
            expression();
            consume(TokenType.CLOSE_PAR);
        } else {
            showError();
        }
    }

    private void literal() {
        consume(TokenType.TEXT);
    }
}
