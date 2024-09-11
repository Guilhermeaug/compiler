package semantic;

import lexical.SymbolTable;
import lexical.TokenType;

import java.util.Arrays;
import java.util.HashMap;

public class SemanticAnalysis {
    private final SymbolTable st;
    private final HashMap<String, Variable> memory;

    public SemanticAnalysis() {
        this.st = new SymbolTable();
        this.memory = new HashMap<>();
    }

    public void addIdentifier(String token, Type type) {
        if (isReservedKeyword(token)) {
            throw new SemanticException("Não é possível usar nomes reservados para declarar variáveis.");
        }
        if (isVariableDeclared(token)) {
            throw new SemanticException("Variável já declarada.");
        }
        memory.put(token, new Variable(token, type));
    }

    public void checkVariableDeclared(String token) {
        if (!isVariableDeclared(token)) {
            throw new SemanticException("Variável não foi declarada antes do uso");
        }
    }

    public void checkAssignment(String token, Type exprType) {
        Variable variable = getVariable(token);
        if (variable.type.equals(Type.REAL) && exprType.equals(Type.INTEGER)) {
            return;
        }
        if (!variable.type.equals(exprType)) {
            throw new SemanticException("Erro de atribuição");
        }
    }

    public Type checkArithmeticOrLogicalOperation(Type leftType, Type rightType, TokenType op) {
        if (Arrays.asList(TokenType.OR, TokenType.AND).contains(op)) {
            return checkLogicalOperation(leftType, rightType);
        } else if (Arrays.asList(TokenType.ADD, TokenType.SUB, TokenType.DIV, TokenType.MUL).contains(op)) {
            return checkArithmeticOperation(leftType, rightType);
        }
        throw new SemanticException("Operação inválida");
    }

    public void checkUnaryArithmeticOperation(Type type) {
        if (!type.equals(Type.INTEGER) && !type.equals(Type.REAL)) {
            throw new SemanticException("Operação inválida");
        }
    }

    public Type checkArithmeticOperation(Type leftType, Type rightType) {
        if (leftType.equals(Type.BOOLEAN) || rightType.equals(Type.BOOLEAN)) {
            throw new SemanticException("Operação inválida");
        }
        if (leftType.equals(Type.REAL) || rightType.equals(Type.REAL)) {
            return Type.REAL;
        }
        return Type.INTEGER;
    }

    public Type checkLogicalOperation(Type leftType, Type rightType) {
        if (leftType.equals(Type.BOOLEAN) && rightType.equals(Type.BOOLEAN)) {
            return Type.BOOLEAN;
        }
        throw new SemanticException("Os dois operandos devem ser booleanos");
    }

    public Type checkComparisonOperation(Type leftType, Type rightType, TokenType op) {
        if (Arrays.asList(TokenType.EQUAL, TokenType.NOT_EQUAL).contains(op)) {
            if (leftType.equals(rightType)) {
                return Type.BOOLEAN;
            }
            throw new SemanticException("Tipos incompatíveis");
        } else if (Arrays.asList(TokenType.GREATER_THAN, TokenType.GREATER_EQUAL, TokenType.LOWER_THAN, TokenType.LOWER_EQUAL).contains(op)) {
            if (leftType.equals(Type.BOOLEAN) || rightType.equals(Type.BOOLEAN)) {
                throw new SemanticException("Tipos incompatíveis");
            }
            return Type.BOOLEAN;
        }
        throw new SemanticException("Operação inválida");
    }

    public void checkCondition(Type type) {
        if (!type.equals(Type.BOOLEAN)) {
            throw new SemanticException("Condição deve ser booleana");
        }
    }

    public Variable getVariable(String token) {
        if (!isVariableDeclared(token)) {
            throw new SemanticException("Variável não foi declarada antes do uso");
        }
        return memory.get(token);
    }

    private boolean isReservedKeyword(String token) {
        return st.contains(token);
    }

    private boolean isVariableDeclared(String token) {
        return memory.containsKey(token);
    }
}
