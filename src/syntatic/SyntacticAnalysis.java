package syntatic;

import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;
import semantic.SemanticAnalysis;
import semantic.IdentifierType;

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
    }

    private void program() {
        consume(TokenType.APP);
        semanticAnalysis.addVariable(current.token, IdentifierType.APP_NAME);
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
        IdentifierType identifierType = type();
        identList(identifierType);
    }

    private void identList(IdentifierType identifierType) {
        semanticAnalysis.addVariable(current.token, identifierType);
        consume(TokenType.IDENTIFIER);
        while (current.type == TokenType.COMMA) {
            consume(TokenType.COMMA);
            semanticAnalysis.addVariable(current.token, identifierType);
            consume(TokenType.IDENTIFIER);
        }
    }

    private IdentifierType type() {
        if (current.type == TokenType.INTEGER) {
            consume(TokenType.INTEGER);
            return IdentifierType.INTEGER;
        } else if (current.type == TokenType.REAL) {
            consume(TokenType.REAL);
            return IdentifierType.REAL;
        } else {
            showError();
            return null;
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
        String identifier = current.token;
        consume(TokenType.IDENTIFIER);
        consume(TokenType.ASSIGN);
        IdentifierType type = simpleExpr();
        semanticAnalysis.checkVariableDeclared(identifier);
        semanticAnalysis.checkAssignment(identifier, type);
    }

    private void ifStmt() {
        consume(TokenType.IF);
        IdentifierType conditionType = condition();
        semanticAnalysis.isBooleanCondition(conditionType);
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
        IdentifierType conditionType = condition();
        semanticAnalysis.isBooleanCondition(conditionType);
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

    private IdentifierType condition() {
        return expression();
    }

    private IdentifierType expression() {
        IdentifierType leftType = simpleExpr();
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
            IdentifierType rightType = simpleExpr();
            leftType = semanticAnalysis.checkComparisonOperation(leftType, rightType);
        }
        return leftType;
    }

    private IdentifierType simpleExpr() {
        IdentifierType leftType = term();
        while (Arrays.asList(TokenType.ADD, TokenType.SUB, TokenType.OR).contains(current.type)) {
            TokenType op = current.type;
            advance();
            IdentifierType rightType = term();
            leftType = semanticAnalysis.checkArithmeticOrLogicalOperation(leftType, rightType, op);
        }
        return leftType;
    }

    private IdentifierType term() {
        IdentifierType leftType = factorA();
        while (Arrays.asList(TokenType.MUL, TokenType.DIV, TokenType.AND).contains(current.type)) {
            TokenType op = current.type;
            advance();
            IdentifierType rightType = factorA();
            leftType = semanticAnalysis.checkArithmeticOrLogicalOperation(leftType, rightType, op);
        }
        return leftType;
    }

    private IdentifierType factorA() {
        IdentifierType type;
        if (current.type == TokenType.SUB) {
            consume(TokenType.SUB);
            type = factor();
            semanticAnalysis.checkUnaryArithmeticOperation(type);
        } else if (current.type == TokenType.NOT) {
            consume(TokenType.NOT);
            type = factor();
            semanticAnalysis.isBooleanCondition(type);
        } else {
            type = factor();
        }
        return type;
    }

    private IdentifierType factor() {
        IdentifierType type = null;
        if (current.type == TokenType.IDENTIFIER) {
            String identifier = current.token;
            consume(TokenType.IDENTIFIER);
            type = semanticAnalysis.getVariable(identifier).type;
        } else if (current.type == TokenType.INTEGER_CONST) {
            consume(TokenType.INTEGER_CONST);
            type = IdentifierType.INTEGER;
        } else if (current.type == TokenType.REAL_CONST) {
            consume(TokenType.REAL_CONST);
            type = IdentifierType.REAL;
        } else if (current.type == TokenType.OPEN_PAR) {
            consume(TokenType.OPEN_PAR);
            type = simpleExpr();
            consume(TokenType.CLOSE_PAR);
        } else {
            showError();
        }
        return type;
    }

    private void literal() {
        consume(TokenType.TEXT);
    }
}
