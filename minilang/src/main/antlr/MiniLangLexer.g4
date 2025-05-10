lexer grammar MiniLangLexer;

options {
    caseInsensitive = false;
}


WS: [ \t\r\n]+ -> skip;

// Keywords
MATH: 'math';
FUN: 'fun';
VAR: 'var';
PLAYER: 'player';

// Operators
PLUS: '+';
MINUS: '-';
MULTIPLY: '*';
DIVIDE: '/';
POWER: '^';

// Punctuation
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
COMMA: ',';
DOT: '.';
STRING_OPEN: '"' -> pushMode(String);

// Literals
INTEGER: [0-9]+;
DOUBLE: [0-9]+ '.' [0-9]+;
BOOLEAN: 'true' | 'false';
NULL: 'null';

// Identifiers
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;


mode String;
STRING_CLOSE: '"' -> popMode;

StringText: ~('\\' | '"' | '$')+ | '$';
StringEscapedChar: '\\' .;

StringExpressionStart: '${' -> pushMode(StringExpression);

mode StringExpression;
StrExpr_RBRACE: RBRACE -> popMode, type(RBRACE);

StrExpr_MATH            : MATH               -> type(MATH);
StrExpr_FUN             : FUN                -> type(FUN);
StrExpr_VAR             : VAR                -> type(VAR);
StrExpr_PLAYER          : PLAYER             -> type(PLAYER);

StrExpr_PLUS            : PLUS               -> type(PLUS);
StrExpr_MINUS           : MINUS              -> type(MINUS);
StrExpr_MULTIPLY        : MULTIPLY           -> type(MULTIPLY);
StrExpr_DIVIDE          : DIVIDE             -> type(DIVIDE);
StrExpr_POWER           : POWER              -> type(POWER);
StrExpr_LPAREN          : LPAREN             -> type(LPAREN);
StrExpr_RPAREN          : RPAREN             -> type(RPAREN);
StrExpr_LBRACE          : LBRACE             -> type(LBRACE);
StrExpr_COMMA           : COMMA              -> type(COMMA);
StrExpr_DOT             : DOT                -> type(DOT);
StrExpr_STRING_OPEN     : STRING_OPEN        -> pushMode(String), type(STRING_OPEN);

StrExpr_INTEGER         : INTEGER            -> type(INTEGER);
StrExpr_DOUBLE          : DOUBLE             -> type(DOUBLE);
StrExpr_BOOLEAN         : BOOLEAN            -> type(BOOLEAN);
StrExpr_NULL            : NULL               -> type(NULL);

StrExpr_IDENTIFIER      : IDENTIFIER         -> type(IDENTIFIER);

StrExpr_WS              : WS                 -> skip;