parser grammar MiniLangParser;

options { tokenVocab=MiniLangLexer; }

// The main entry point for parsing a line
line
    : expression EOF
    ;

// Expressions can be literals, variables, function calls, or mathematical expressions
expression
    : literal                                                      # literalExpr
    | variableReference                                            # varRefExpr
    | functionCall                                                 # funcCallExpr
    | playerProperty                                               # playerPropExpr
    | LPAREN expression RPAREN                                     # parenExpr
    | expression POWER expression                                  # powerExpr
    | expression (MULTIPLY | DIVIDE) expression                    # mulDivExpr
    | expression (PLUS | MINUS) expression                         # addSubExpr
    | interpolatedString                                           # interpolatedStringExpr
    ;

// Literals: integers, doubles, strings, booleans, null
literal
    : INTEGER                                                      # intLiteral
    | DOUBLE                                                       # doubleLiteral
    | STRING                                                       # stringLiteral
    | BOOLEAN                                                      # booleanLiteral
    | NULL                                                         # nullLiteral
    ;

// Variable references: var.player(key), var.global(key)
variableReference
    : VAR DOT PLAYER LPAREN expression RPAREN                      # playerVarRef
    | VAR DOT GLOBAL LPAREN expression RPAREN                      # globalVarRef
    ;

// Function calls: math functions and custom functions
functionCall
    : mathFunction                                                 # mathFunctionCall
    | FUN DOT IDENTIFIER LPAREN functionArgs? RPAREN               # customFunctionCall
    ;

// Math functions
mathFunction
    : MATH ABS LPAREN expression RPAREN                            # mathAbsFunc
    | MATH SQRT LPAREN expression COMMA expression RPAREN          # mathSqrtFunc
    | MATH SIN LPAREN expression RPAREN                            # mathSinFunc
    | MATH COS LPAREN expression RPAREN                            # mathCosFunc
    | MATH TAN LPAREN expression RPAREN                            # mathTanFunc
    | MATH ASIN LPAREN expression RPAREN                           # mathAsinFunc
    | MATH ACOS LPAREN expression RPAREN                           # mathAcosFunc
    | MATH ATAN LPAREN expression RPAREN                           # mathAtanFunc
    | MATH LOG LPAREN expression COMMA expression RPAREN           # mathLogFunc
    | MATH RAND INT LPAREN expression COMMA expression RPAREN      # mathRandIntFunc
    | MATH RAND DEC LPAREN expression COMMA expression RPAREN      # mathRandDecFunc
    ;

// Player properties: player.name, player.ping, player.pos.x, etc.
playerProperty
    : PLAYER NAME                                                  # playerNameProp
    | PLAYER PING                                                  # playerPingProp
    | PLAYER POS X                                                 # playerPosXProp
    | PLAYER POS Y                                                 # playerPosYProp
    | PLAYER POS Z                                                 # playerPosZProp
    | PLAYER POS PITCH                                             # playerPosPitchProp
    | PLAYER POS YAW                                               # playerPosYawProp
    ;

// Function arguments: comma-separated list of expressions
functionArgs
    : expression (COMMA expression)*
    ;

// String interpolation: "You have ${var.player(points)} points."
interpolatedString
    : STRING (INTERPOLATION_START expression INTERPOLATION_END STRING?)*
    ;
