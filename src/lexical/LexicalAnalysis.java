package lexical;

import java.io.FileInputStream;
import java.io.PushbackInputStream;

public class LexicalAnalysis implements AutoCloseable {
    private int line;
    private final SymbolTable st;
    private final PushbackInputStream input;

    public LexicalAnalysis(SymbolTable st, String filename) {
        this.st = st;

        try {
            input = new PushbackInputStream(new FileInputStream(filename), 2);
        } catch (Exception e) {
            throw new LexicalException("Unable to open file");
        }

        line = 1;
    }

    public Lexeme nextToken() {
        Lexeme lex = new Lexeme("", TokenType.END_OF_FILE);

        int state = 1;
        while (state != 98 && state != 99) {
            int c = getc();

            switch (state) {
                case 1:
                    if (c == ' ' || c == '\t' || c == '\r') {
                        continue;
                    } else if (c == '\n') {
                        this.line++;
                    } else if (c == '%') {
                        state = 2;
                    } else if (c == '>' || c == '<' || c == '!') {
                        lex.token += (char) c;
                        state = 3;
                    } else if (c == '&') {
                        lex.token += (char) c;
                        state = 4;
                    } else if (c == '|') {
                        lex.token += (char) c;
                        state = 5;
                    } else if (c == '=' || c == ';' || c == ',' || c == '+' || c == '-' || c == '*' ||
                            c == '(' || c == ')' || c == '/') {
                        lex.token += (char) c;
                        state = 98;
                    } else if (c == '_' ||
                            Character.isLetter(c)) {
                        lex.token += (char) c;
                        state = 6;
                    } else if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 7;
                    } else if (c == '{') {
                        lex.token += (char) c;
                        state = 8;
                    } else if (c == ':') {
                        lex.token += (char) c;
                        state = 11;
                    } else if (c == -1) {
                        lex.type = TokenType.END_OF_FILE;
                        state = 99;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 99;
                    }
                    break;
                case 2:
                    if (c == '\n') {
                        state = 1;
                        this.line++;
                    } else if (c == -1) {
                        lex.type = TokenType.UNEXPECTED_EOF;
                        state = 99;
                    }
                    break;
                case 3:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 98;
                    } else {
                        ungetc(c);
                        state = 98;
                    }
                    break;
                case 4:
                    if (c == '&') {
                        lex.token += (char) c;
                        state = 98;
                    } else {
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 99;
                    }
                    break;
                case 5:
                    if (c == '|') {
                        lex.token += (char) c;
                        state = 98;
                    } else {
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 99;
                    }
                    break;
                case 6:
                    if (c == '_' ||
                            Character.isLetter(c) ||
                            Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 6;
                    } else {
                        ungetc(c);
                        state = 98;
                    }
                    break;
                case 7:
                    if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 7;
                    } else if (c == '.') {
                        lex.token += (char) c;
                        state = 9;
                    } else {
                        ungetc(c);
                        lex.type = TokenType.NUMBER;
                        state = 99;
                    }
                    break;
                case 8:
                    if (c == '}') {
                        lex.token += (char) c;
                        lex.type = TokenType.TEXT;
                        state = 99;
                    } else if (c == '\n') {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 99;
                    } else {
                        lex.token += (char) c;
                    }
                    break;
                case 9:
                    if (Character.isDigit(c)) {
                        lex.token += (char) c;
                        state = 10;
                    } else {
                        lex.token += (char) c;
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 99;
                    }
                    break;
                case 10:
                    if (Character.isDigit(c)) {
                        lex.token += (char) c;
                    } else {
                        ungetc(c);
                        lex.type = TokenType.NUMBER;
                        state = 99;
                    }
                    break;
                case 11:
                    if (c == '=') {
                        lex.token += (char) c;
                        state = 98;
                    } else {
                        lex.type = TokenType.INVALID_TOKEN;
                        state = 99;
                    }
                    break;
            }
        }

        if (state == 98) {
            lex.type = st.find(lex.token);
        }

        return lex;
    }

    private int getc() {
        try {
            return input.read();
        } catch (Exception e) {
            throw new LexicalException("Unable to read file");
        }
    }

    private void ungetc(int c) {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new LexicalException("Unable to ungetc");
            }
        }
    }

    @Override
    public void close() throws Exception {
        try {
            input.close();
        } catch (Exception e) {
            throw new LexicalException("Unable to close file");
        }
    }

    public int getLine() {
        return this.line;
    }
}
