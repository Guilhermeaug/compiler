package syntatic;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;
import semantic.SemanticAnalysis;
import semantic.Type;

import java.util.Arrays;

public class SyntacticAnalysis {
    private final LexicalAnalysis lex;
    private final SemanticAnalysis semanticAnalysis;
    private Lexeme current;

    public SyntacticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        this.semanticAnalysis = new SemanticAnalysis();
    }

    public void start() {
        try {
            program();
        } catch (RuntimeException e) {
            System.out.printf("Erro na linha %02d\n", lex.getLine());
            throw e;
        }
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
        while (current.type == TokenType.SEMICOLON) {
            consume(TokenType.SEMICOLON);
            decl();
        }
    }

    private void decl() {
        Type type = type();
        identList(type);
    }

    private void identList(Type type) {
        semanticAnalysis.addIdentifier(current.token, type);
        consume(TokenType.IDENTIFIER);
        while (current.type == TokenType.COMMA) {
            consume(TokenType.COMMA);
            semanticAnalysis.addIdentifier(current.token, type);
            consume(TokenType.IDENTIFIER);
        }
    }

    private Type type() {
        Type type = null;
        if (current.type == TokenType.INTEGER) {
            consume(TokenType.INTEGER);
            type = Type.INTEGER;
        } else if (current.type == TokenType.REAL) {
            consume(TokenType.REAL);
            type = Type.REAL;
        } else {
            showError();
        }
        return type;
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
        String identifier = current.token;
        consume(TokenType.IDENTIFIER);
        consume(TokenType.ASSIGN);
        Type type = simpleExpr();
        semanticAnalysis.checkVariableDeclared(identifier);
        semanticAnalysis.checkAssignment(identifier, type);
    }

    private void ifStmt() {
        consume(TokenType.IF);
        Type conditionType = condition();
        semanticAnalysis.checkCondition(conditionType);
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
        Type conditionType = condition();
        semanticAnalysis.checkCondition(conditionType);
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

    private Type condition() {
        return expression();
    }

    private Type expression() {
        Type leftType = simpleExpr();
        while (Arrays.asList(
                TokenType.EQUAL,
                TokenType.GREATER_THAN,
                TokenType.GREATER_EQUAL,
                TokenType.LOWER_THAN,
                TokenType.LOWER_EQUAL,
                TokenType.NOT_EQUAL
        ).contains(current.type)) {
            TokenType op = current.type;
            advance();
            Type rightType = simpleExpr();
            leftType = semanticAnalysis.checkComparisonOperation(leftType, rightType, op);
        }
        return leftType;
    }

    private Type simpleExpr() {
        Type leftType = term();
        while (Arrays.asList(TokenType.ADD, TokenType.SUB, TokenType.OR).contains(current.type)) {
            TokenType op = current.type;
            advance();
            Type rightType = term();
            leftType = semanticAnalysis.checkArithmeticOrLogicalOperation(leftType, rightType, op);
        }
        return leftType;
    }

    private Type term() {
        Type leftType = factorA();
        while (Arrays.asList(TokenType.MUL, TokenType.DIV, TokenType.AND).contains(current.type)) {
            TokenType op = current.type;
            advance();
            Type rightType = factorA();
            leftType = semanticAnalysis.checkArithmeticOrLogicalOperation(leftType, rightType, op);
        }
        return leftType;
    }

    private Type factorA() {
        Type type;
        if (current.type == TokenType.SUB) {
            consume(TokenType.SUB);
            type = factor();
            semanticAnalysis.checkUnaryArithmeticOperation(type);
        } else if (current.type == TokenType.NOT) {
            consume(TokenType.NOT);
            type = factor();
            semanticAnalysis.checkCondition(type);
        } else {
            type = factor();
        }
        return type;
    }

    private Type factor() {
        Type type = null;
        if (current.type == TokenType.IDENTIFIER) {
            String identifier = current.token;
            consume(TokenType.IDENTIFIER);
            type = semanticAnalysis.getVariable(identifier).type;
        } else if (current.type == TokenType.INTEGER_CONST) {
            consume(TokenType.INTEGER_CONST);
            type = Type.INTEGER;
        } else if (current.type == TokenType.REAL_CONST) {
            consume(TokenType.REAL_CONST);
            type = Type.REAL;
        } else if (current.type == TokenType.OPEN_PAR) {
            consume(TokenType.OPEN_PAR);
            type = expression();
            consume(TokenType.CLOSE_PAR);
        } else {
            showError();
        }
        return type;
    }

    private void literal() {
        consume(TokenType.TEXT);
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

    private void showError() throws SyntacticException {
        switch (current.type) {
            case INVALID_TOKEN -> System.out.printf("Lexema inválido [%s]\n", current.token);
            case UNEXPECTED_EOF, END_OF_FILE -> System.out.print("Fim de arquivo inesperado\n");
            default -> System.out.printf("Lexema não esperado [%s]\n", current.token);
        }
        throw new SyntacticException("Syntax error");
    }
}
