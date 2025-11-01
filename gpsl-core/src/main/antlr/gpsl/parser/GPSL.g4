grammar GPSL;

block : formulaDeclaration+;

formulaDeclaration : IDENTIFIER (SEQ|EQ) (formula | automaton);
formulaDeclarationList : formulaDeclaration (COMMA formulaDeclaration)* COMMA?;

formula :
          literal                                                                   #LiteralExp
        | IDENTIFIER                                                                #ReferenceExp
        | atom                                                                      #AtomExp
        | LPAREN formula RPAREN                                                     #ParenExp
        | operator=NEGATION formula                                                 #UnaryExp
        | operator=NEXT formula                                                     #UnaryExp
        | operator=(EVENTUALLY|GLOBALLY) formula                                    #UnaryExp
        |<assoc=right> formula operator=(SUNTIL|WUNTIL|SRELEASE|WRELEASE) formula   #BinaryExp
        | formula operator=CONJUNCTION formula                                      #BinaryExp
        | formula operator=DISJUNCTION formula                                      #BinaryExp
        | formula operator=XOR formula                                              #BinaryExp
        |<assoc=right> formula operator=(IMPLICATION | EQUIVALENCE) formula         #BinaryExp
        | letDecl formula                                                           #LetExp
        ;

literal : TRUE | FALSE;
atom : ATOMINLINE;

letDecl : LET formulaDeclarationList IN;

//buchi automata
stateDecl       : STATES IDENTIFIER (',' IDENTIFIER)*;
initialDecl     : INITIAL IDENTIFIER (',' IDENTIFIER)*;
acceptDecl      : ACCEPT IDENTIFIER (',' IDENTIFIER)*;
transitionDecl  : IDENTIFIER priority=(FALSE | TRUE | NATURAL)? '[' formula ']' IDENTIFIER;
automatonDecl   : (NFA | BUCHI)? stateDecl ';' initialDecl ';' acceptDecl ';'
                        transitionDecl (';' transitionDecl)*;
automaton       : letDecl? automatonDecl;

CONJUNCTION: 'and' | '&' | '&&' | '/\\' | '*' | '∧';
DISJUNCTION: 'or' | '||' | '\\/' | '+' | '∨';  // Removed single '|' to avoid conflict with PIPEATOM
EQUIVALENCE: 'iff' | '<->' | '<=>' | '⟺' | '↔';
EVENTUALLY: 'eventually' | 'F' | '<>' | '\u25C7' /*◇*/ | '\u2662' /*♢*/;
FALSE: 'false' | '0';
GLOBALLY: 'globally' | 'always' | 'G' | '[]' | '\u2610' /* ☐ */;
IMPLICATION: 'implies' | '->' | '=>' | '→' | '⟹';
IN : 'in';
LET : 'let' | '\\';
NEGATION: '!' | '~' | 'not' | '¬';
NEXT : 'next' | 'N' | '()' | '◯' | 'o' | 'X';
SUNTIL: 'until' | 'U' | 'SU' | 'strong-until';
WUNTIL: 'W' | 'WU' | 'weak-until';
SRELEASE: 'M' | 'SR' | 'strong-release';
WRELEASE: 'R' | 'WR' | 'weak-release';
TRUE: 'true' | '1';
XOR: 'xor' | '^' | '⊻' | '⊕';

NFA: 'nfa';
BUCHI: 'buchi';
STATES: 'states';
INITIAL: 'initial';
ACCEPT: 'accept';

reserved: CONJUNCTION | DISJUNCTION | EQUIVALENCE | EVENTUALLY | FALSE | GLOBALLY | IMPLICATION | IN | LET | NEGATION | NEXT | SUNTIL | WUNTIL | SRELEASE | WRELEASE | TRUE | XOR;

ATOMINLINE : PIPEATOM | QUOTEATOM;
PIPEATOM : '|' ('\\|' | ~[|])* '|';
QUOTEATOM: '"' ('\\"' | ~["])* '"';

IDENTIFIER : [a-zA-Z][a-zA-Z_0-9]*;
NATURAL: [0-9]+;

EQ : '=';
SEQ : '*=';
COMMA : ',';
SEMICOLON : ';';
LPAREN : '(';
RPAREN : ')';

LINE_COMMENT : '//' ~[\r\n]* -> skip ;
COMMENT : '/*' .*? '*/' -> skip ;
WS : [ \r\t\n]+ -> skip ;
