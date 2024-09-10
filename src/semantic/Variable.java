package semantic;

public class Variable {
    public final String name;
    public final IdentifierType type;

    public Variable(String name, IdentifierType type) {
        this.name = name;
        this.type = type;
    }
}
