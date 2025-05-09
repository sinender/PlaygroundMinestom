# MiniLang - A Mini Language for Expressions

This module implements a mini language for expressions as specified in the requirements. The language supports various data types, functions, variables, and operations.

## Features

### Data Types
- **Integer**: Simple integer values (e.g., `5`, `8`, `15`)
- **Double**: Floating-point values (e.g., `1.5`, `0.83`, `157.342`)
- **String**: Text values (e.g., `"Test, this is a string"`, `this is another string!`)
- **Boolean**: `true` or `false`
- **Null**: `null`

### Functions
- **Math Functions**:
  - `.abs(x)`: Absolute value
  - `.sqrt(root, x)`: Square root (or other roots)
  - `.sin(x)`, `.cos(x)`, `.tan(x)`: Trigonometric functions
  - `.asin(x)`, `.acos(x)`, `.atan(x)`: Inverse trigonometric functions
  - `.log(base, x)`: Logarithm
  - `.rand.int(lo, hi)`: Random integer
  - `.rand.dec(lo, hi)`: Random decimal

- **Custom Functions**:
  - `fun.<identifier>(...)`: Call your own functions with their arguments

### Variables
- **Player Variables**:
  - `var.player(key)`: Access player-specific variables
- **Global Variables**:
  - `var.global(key)`: Access global variables

### Player Properties
- `player.name`: Player name
- `player.ping`: Player ping
- `player.pos.x`, `player.pos.y`, `player.pos.z`: Player position
- `player.pos.pitch`, `player.pos.yaw`: Player orientation

### Expressions
- **Mathematical Operations**: `+`, `-`, `*`, `/`, `^` (power)
- **Parentheses**: For grouping expressions
- **String Interpolation**: `You have ${var.player(points)} points.`

## Usage

### Parsing Expressions

```kotlin
import net.sinender.minilang.MiniLangParser

// Parse an expression
val parseTree = MiniLangParser.parse("15.3 + player.pos.x - var.global(testvar)")

// Evaluate a simple expression (only handles literals in this example)
val result = MiniLangParser.evaluate("5")
println(result.toDisplayString()) // Outputs: 5
```

### Evaluating Expressions

For full expression evaluation, use the `MiniLangEvaluator`:

```kotlin
import net.sinender.minilang.MiniLangEvaluator

val evaluator = MiniLangEvaluator()
val parseTree = MiniLangParser.parse("math.abs(-5)")
val result = evaluator.visit(parseTree)
println(result.toDisplayString()) // Outputs: 5
```

## Implementation Details

The implementation uses ANTLR4 to generate a lexer and parser from grammar files:

- `MiniLangLexer.g4`: Defines the tokens for the language
- `MiniLangParser.g4`: Defines the grammar rules for parsing expressions

The `MiniLangEvaluator` class implements the visitor pattern to evaluate expressions by traversing the parse tree.

## Building

The project uses Gradle with the ANTLR plugin to generate the lexer and parser classes. To build:

```bash
./gradlew :minilang:build
```

This will generate the necessary ANTLR classes in the `build/generatedAntlr` directory.