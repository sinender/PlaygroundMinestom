parser grammar MiniLangParser;

options {
    tokenVocab = MiniLangLexer;
}

// The main entry point for parsing a line
line
    : expression EOF
    ;

// Expressions can be literals, variables, function calls, or mathematical expressions
expression
    : primary                                               # primaryExprAtom // An atom can be an expression
    | expression LPAREN arguments? RPAREN                   # callExpr        // Function/method call
    | expression DOT IDENTIFIER                             # memberAccessExpr// Member access
    | expression POWER expression                           # powerExpr       // Power (a^b)
    | expression op=(MULTIPLY | DIVIDE) expression          # mulDivExpr      // Multiplication/Division
    | expression op=(PLUS | MINUS) expression               # addSubExpr      // Addition/Subtraction
    ;

primary
    : LPAREN expression RPAREN                              # parenExpr               // Parenthesized expression
    | qualifiedIdentifier                                   # qualifiedIdentifierAtom // e.g., math, math.abs, player.pos
    | literal                                               # literalAtom             // Numeric, boolean, null literals
    ;

qualifiedIdentifier
    : (MATH | FUN | VAR | PLAYER | IDENTIFIER) (DOT IDENTIFIER)*
    ;

// Literals: integers, doubles, strings, booleans, null
literal
    : INTEGER                                 # intLiteral
    | DOUBLE                                  # doubleLiteral
    | string                                  # stringLiteral
    | BOOLEAN                                 # booleanLiteral
    | NULL                                    # nullLiteral
    ;

arguments
    : expression (COMMA expression)*
    ;

// String interpolation: "You have ${var.player(points)} points."
string
    : STRING_OPEN (stringContent | stringExpression)* STRING_CLOSE
    ;

stringContent
    : StringText
    | StringEscapedChar
    ;

stringExpression
    : StringExpressionStart expression RBRACE
    ;