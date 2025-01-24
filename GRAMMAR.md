Here is a complete grammar for Lox.

## 1.1 Syntax Grammar

The syntactic grammar is used to parse the linear sequence of tokens into the nested syntax tree structure. It starts with the first rule that matches an entire Lox program (or a single REPL entry).

```
program     -> declaration* EOF ;
```

### 1.1.1 Declarations

A program is a series of declaration, which are the statements that bind new identifiers or any of the other statemetn types.

```
declaration -> classDecl
             | funDecl
             | varDecl
             | statement ;

classDecl   -> "class" IDENTIFIER ( "<" IDENTIFIER )? "{" function* "}" ;
funDecl     -> "fun" function ;
varDecl     -> "var" IDENTIFIER ( "=" expression )? ";" ;
```

### 1.1.2 Statements

The remaining statement rules produce side effects, but do not introduce bindings.

```
statement   -> exprStmt
             | forStmt
             | ifStmt
             | printStmt 
             | returnStmt
             | whileStmt
             | blockStmt ;

exprStmt    -> expression ";" ;
forStmt     -> "for" "(" ( varDecl | exprStmt | ";" ) expression? ";" expression? ")" statement ;
ifStmt      -> "if" "(" expression ")" statement ( "else" statement )? ;
printStmt   -> "print" expression ";" ;
returnStmt  -> "return" expression? ";" ;
whileStmt   -> "while" "(" expression ")" statement ;
blockStmt   -> "{" declaration* ")" statement ;
```

Note that **block** is a statement rule, but is also used a nonterminal in a couple of other rules for things like function bodies.

### 1.1.3 Expressions

Expressions produce values. Lox has a number of unary and binary operators with different levels of precedence. Some grammars for languages do not directly encode the precedence relationships and specify that elsewhere. Here, we use a separate rule for each precedence level to make it explicit.

```
expression  -> assignment ;

assignment  -> ( call "." )? IDENTIFIER "=" assignment
             | logic_or ;
logic_or    -> logic_and ( "or" logic_and )* ;
logic_and   -> equality ( "and" equality )* ;
equality    -> comparison ( ( "!=" | "==" ) comparison )* ;
comparison  -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term        -> factor ( ( "-" | "+" ) factor )* ;
factor      -> unary ( ( "/" | "*" ) unary )* ;

unary       -> ( "!" | "-" ) unary | call ;
call        -> primary ( "(" arguments? ")" | "." IDENTIFIER )* ;
primary     -> "true" 
             | "false
             | "nil"
             | "this"
             | NUMBER
             | STRING
             | IDENTIFIER
             | "(" expression ")"
             | "super" "." IDENTIFIER 
             | ( "!=" | "==" ) ;
```

### 1.1.4 Utility rules

In order to keep the above rules a litter cleaner, some of the grammar is split out into a few reused helper rules.

```
function    -> IDENTIFIER "(" parameters? ")" blockStmt ;
parameters  -> IDENTIFER ( "," IDENTIFIER )* ;
arguments   -> expression ( "," expression )* ;
```

## 1.2 Lexical Grammar

The lexical grammar is used by the scanner to group characters into tokens. Where the syntax is [context free](https://en.wikipedia.org/wiki/Context-free_grammar), the lexical grammer is [regular](https://en.wikipedia.org/wiki/Regular_grammar) -- note taht there are no recursive rules.

```
NUMBER      -> DIGIT+ ( "." DIGIT+ )? ;
STRING      -> "\"" ( "\x00" ... "!" | "#" ... "\xFF" )* "\"";
IDENTIFIER  -> ALPHA ( ALPHA | DIGIT )* ;
ALPHA       -> "a" ... "z" | "A" ... "Z" | "_" ;
DIGIT       -> "0" ... "9" ;
```
