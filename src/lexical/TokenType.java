package lexical;

public enum TokenType {
    UNEXPECTED_EOF,
    INVALID_TOKEN,
    END_OF_FILE,

    SEMICOLON,
    COMMA,
    OPEN_PAR,
    CLOSE_PAR,
    OPEN_CURLY,
    CLOSE_CURLY,

    ASSIGN, // :=
    NOT, // !
    ADD, // +
    SUB, // -
    MUL, // *
    DIV, // /
    EQUAL, // =
    GREATER_THAN, // >
    GREATER_EQUAL, // >=
    LOWER_THAN, // <
    LOWER_EQUAL, // <=
    NOT_EQUAL, // !=
    OR, // ||
    AND, // &&

    APP, // app
    VAR, // var,
    INIT, // init
    RETURN, // return
    INTEGER, // integer
    REAL, // real
    IF, // if
    THEN, // then,
    ELSE, // else
    END, // end
    REPEAT, // repeat
    UNTIL, // until
    READ, // read
    WRITE, // write

    IDENTIFIER, // identifer
    INTEGER_CONST, // integer constant
    REAL_CONST, // real constant
    TEXT
}
