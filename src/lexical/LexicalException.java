package lexical;

public class LexicalException extends RuntimeException {
    public LexicalException(String msg) {
        super("Erro léxico: " + msg);
    }
}
