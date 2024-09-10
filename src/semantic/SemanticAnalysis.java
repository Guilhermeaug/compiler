package semantic;

import lexical.SymbolTable;
import lexical.TokenType;

import java.util.Arrays;
import java.util.HashMap;

public class SemanticAnalysis {
    private final SymbolTable st;
    private final HashMap<String, Variable> variables;

    public SemanticAnalysis() {
        this.st = new SymbolTable();
        this.variables = new HashMap<>();
    }

    private boolean isReservedKeyword(String token) {
        return st.contains(token);
    }

    private boolean isVariableDeclared(String token) {
        return variables.containsKey(token);
    }

    public void addVariable(String token, IdentifierType type) {
        if (isReservedKeyword(token)) {
            throw new RuntimeException("Variable name cannot be a reserved keyword");
        }
        if (isVariableDeclared(token)) {
            throw new RuntimeException("Variable already declared");
        }

        variables.put(token, new Variable(token, type));
    }

    public void checkVariableDeclared(String token) {
        if (!isVariableDeclared(token)) {
            throw new RuntimeException("Variable not declared");
        }
    }

    public void checkAssignment(String token, IdentifierType type) {
        Variable variable = getVariable(token);
        if (variable.type.equals(IdentifierType.APP_NAME)) {
            throw new RuntimeException("Cannot assign to app name");
        }
        if (variable.type.equals(IdentifierType.REAL) && type.equals(IdentifierType.INTEGER)) {
            return;
        }
        if (!variable.type.equals(type)) {
            throw new RuntimeException("Type mismatch");
        }
    }

    public IdentifierType checkArithmeticOperation(IdentifierType type1, IdentifierType type2) {
        if (type1.equals(IdentifierType.APP_NAME) || type2.equals(IdentifierType.APP_NAME)) {
            throw new RuntimeException("Cannot perform arithmetic operation with app name");
        }
        if (type1.equals(IdentifierType.REAL) || type2.equals(IdentifierType.REAL)) {
            return IdentifierType.REAL;
        }
        return IdentifierType.INTEGER;
    }

    public IdentifierType checkComparisonOperation(IdentifierType type1, IdentifierType type2) {
        if (type1.equals(IdentifierType.APP_NAME) || type2.equals(IdentifierType.APP_NAME)) {
            throw new RuntimeException("Cannot perform comparison operation with app name");
        }
        return IdentifierType.BOOLEAN;
    }

    public IdentifierType checkLogicalOperation(IdentifierType type1, IdentifierType type2) {
        if (type1.equals(IdentifierType.APP_NAME) || type2.equals(IdentifierType.APP_NAME)) {
            throw new RuntimeException("Cannot perform logical operation with app name");
        }
        if (type1.equals(IdentifierType.BOOLEAN) && type2.equals(IdentifierType.BOOLEAN)) {
            return IdentifierType.BOOLEAN;
        }
        throw new RuntimeException("Type mismatch");
    }

    public IdentifierType checkArithmeticOrLogicalOperation(IdentifierType leftType, IdentifierType rightType, TokenType op) {
        if (op.equals(TokenType.OR)) {
            if (leftType.equals(IdentifierType.BOOLEAN) && rightType.equals(IdentifierType.BOOLEAN)) {
                return IdentifierType.BOOLEAN;
            } else {
                throw new RuntimeException("Type mismatch");
            }
        } else if (Arrays.asList(TokenType.ADD, TokenType.SUB).contains(op)) {
            return checkArithmeticOperation(leftType, rightType);
        }
        throw new RuntimeException("Invalid operation");
    }

    public void isBooleanCondition(IdentifierType type) {
        if (!type.equals(IdentifierType.BOOLEAN)) {
            throw new RuntimeException("Condition must be boolean");
        }
    }

    public Variable getVariable(String token) {
        return variables.get(token);
    }

    public void checkUnaryArithmeticOperation(IdentifierType type) {
        if (type.equals(IdentifierType.APP_NAME)) {
            throw new RuntimeException("Cannot perform unary arithmetic operation with app name");
        }
        if (!type.equals(IdentifierType.INTEGER) && !type.equals(IdentifierType.REAL)) {
            throw new RuntimeException("Type mismatch");
        }
    }
}
