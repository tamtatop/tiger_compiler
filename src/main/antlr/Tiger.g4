grammar Tiger;

start: EOF;

tiger_program: PROGRAM ID LET declaration_segment BEGIN funct_list END;

declaration_segment: type_declaration_list var_declaration_list;

type_declaration_list: type_declaration type_declaration_list | ;

var_declaration_list: var_declaration var_declaration_list | ;

funct_list: funct funct_list | ;

type_declaration: TYPE ID TASSIGN type SEMICOLON;

type: base_type | ARRAY OPENBRACK INTLIT CLOSEBRACK OF base_type | ID;

base_type: INT | FLOAT;

var_declaration: storage_class id_list COLON type optional_init SEMICOLON;

storage_class: VAR | STATIC;

id_list: ID | ID COMMA id_list;

optional_init: ASSIGN const | ;

funct: FUNCTION ID OPENPAREN param_list CLOSEPAREN ret_type BEGIN stat_seq END;

param_list: param param_list_tail | ;

param_list_tail: COMMA param param_list_tail | ;

ret_type: COLON type | ;

param: ID COLON type;

stat_seq: stat | stat stat_seq;

stat: value ASSIGN expr SEMICOLON
| IF expr THEN stat_seq ENDIF SEMICOLON
| IF expr THEN stat_seq ELSE stat_seq ENDIF SEMICOLON
| WHILE expr DO stat_seq ENDDO SEMICOLON
| FOR ID ASSIGN expr TO expr DO stat_seq ENDDO SEMICOLON
| optprefix ID OPENPAREN expr_list CLOSEPAREN SEMICOLON
| BREAK SEMICOLON
| RETURN optreturn SEMICOLON
| LET declaration_segment BEGIN stat_seq END;

optreturn: expr | ;

optprefix: value ASSIGN | ;

expr: const | value | expr binary_operator expr | OPENPAREN expr CLOSEPAREN;

const: INTLIT | FLOATLIT;

binary_operator: PLUS | MINUS | MULT | DIV | POW | EQUAL | NEQUAL | LESS | GREAT | LESSEQ | GREATEQ | AND | OR;

expr_list: expr expr_list_tail | ;

expr_list_tail: COMMA expr expr_list_tail | ;

value: ID value_tail;

value_tail: OPENBRACK expr CLOSEBRACK | ;


ARRAY: 'array';
BEGIN: 'begin';
BREAK: 'break';
DO: 'do';
ELSE: 'else';
END: 'end';
ENDDO: 'enddo';
ENDIF: 'endif';
FLOAT: 'float';
FOR: 'for';
FUNCTION: 'function';
IF: 'if';
INT: 'int';
LET: 'let';
OF: 'of';
PROGRAM: 'program';
RETURN: 'return';
STATIC: 'static';
THEN: 'then';
TO: 'to';
TYPE: 'type';
VAR: 'var';
WHILE: 'while';

COMMA: ',';
DOT: '.';
COLON: ':';
SEMICOLON: ';';
OPENPAREN: '(';
CLOSEPAREN: ')';
OPENBRACK: '[';
CLOSEBRACK: ']';
OPENCURLY: '{';
CLOSECURLY: '}';

PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
POW: '**';
EQUAL: '==';
NEQUAL: '!=';
LESS: '<';
GREAT: '>';
LESSEQ: '<=';
GREATEQ: '>=';
AND: '&';
OR: '|';

ASSIGN: ':=';
TASSIGN: '=';

COMMENT: '/*' .*? '*/' -> skip;
ID: [a-zA-Z][a-zA-Z0-9_]*;
INTLIT: ([1-9]DIGIT*) | '0';
fragment DIGIT: [0-9];
FLOATLIT: INTLIT '.' DIGIT*;


WHITESPACE : [ \r\t\n]+ -> skip ;


