package lexical;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, TokenType> st;

    public SymbolTable() {
        st = new HashMap<>();

        st.put(",", TokenType.COMMA);
        st.put(";", TokenType.SEMICOLON);
        st.put("(", TokenType.OPEN_PAR);
        st.put(")", TokenType.CLOSE_PAR);
        st.put(":=", TokenType.ASSIGN);

        st.put("&&", TokenType.AND);
        st.put("||", TokenType.OR);
        st.put("<", TokenType.LOWER_THAN);
        st.put(">", TokenType.GREATER_THAN);
        st.put("<=", TokenType.LOWER_EQUAL);
        st.put(">=", TokenType.GREATER_EQUAL);
        st.put("=", TokenType.EQUAL);
        st.put("!=", TokenType.NOT_EQUAL);
        st.put("+", TokenType.ADD);
        st.put("-", TokenType.SUB);
        st.put("*", TokenType.MUL);
        st.put("/", TokenType.DIV);
        st.put("!", TokenType.NOT);

        st.put("app", TokenType.APP);
        st.put("var", TokenType.VAR);
        st.put("init", TokenType.INIT);
        st.put("return", TokenType.RETURN);
        st.put("integer", TokenType.INTEGER);
        st.put("real", TokenType.REAL);
        st.put("if", TokenType.IF);
        st.put("then", TokenType.THEN);
        st.put("else", TokenType.ELSE);
        st.put("end", TokenType.END);
        st.put("until", TokenType.UNTIL);
        st.put("repeat", TokenType.REPEAT);
        st.put("read", TokenType.READ);
        st.put("write", TokenType.WRITE);
    }

    public boolean contains(String token) {
        return st.containsKey(token);
    }

    public TokenType find(String token) {
        return this.contains(token) ? st.get(token) : TokenType.IDENTIFIER;
    }

    public void add(String token, TokenType type) {
        st.put(token, type);
    }
}
