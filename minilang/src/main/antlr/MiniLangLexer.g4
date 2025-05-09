lexer grammar MiniLangLexer;

options { caseInsensitive = false; }

// Keywords
MATH: 'math';
FUN: 'fun';
VAR: 'var';
PLAYER: 'player';

// Math functions
ABS: '.abs';
SQRT: '.sqrt';
SIN: '.sin';
COS: '.cos';
TAN: '.tan';
ASIN: '.asin';
ACOS: '.acos';
ATAN: '.atan';
LOG: '.log';
RAND: '.rand';
INT: '.int';
DEC: '.dec';
FLOOR: '.floor';
CEIL: '.ceil';

// Variable and player properties
GLOBAL: '.global';
NAME: '.name';
PING: '.ping';
POS: '.pos';
X: '.x';
Y: '.y';
Z: '.z';
PITCH: '.pitch';
YAW: '.yaw';

// Operators
PLUS: '+';
MINUS: '-';
MULTIPLY: '*';
DIVIDE: '/';
POWER: '^';
LPAREN: '(';
RPAREN: ')';
COMMA: ',';
DOT: '.';

// String interpolation
INTERPOLATION_START: '${';
INTERPOLATION_END: '}';

// Literals
INTEGER: [0-9]+;
DOUBLE: [0-9]+ '.' [0-9]+;
STRING: '"' .*? '"' | ~[$+\-*/^(){},.\r\n \t]+;
BOOLEAN: 'true' | 'false';
NULL: 'null';

// Identifiers
IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;

// Whitespace
WS: [ \t\r\n]+ -> skip;