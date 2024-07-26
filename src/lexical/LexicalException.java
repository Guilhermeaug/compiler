package lexical;

public class LexicalException extends RuntimeException {
    public LexicalException(String msg) {
        super("Lexical error: " + msg);
    }
}
