%%
%{
    public boolean hayErrores = false;
%}
%class Lexer
%unicode
%cup
%line
%column
%ignorecase

WHITESPACE = [ \t\r\n]+
DIGIT      = [0-9]
DECIMAL_NUM = {DIGIT}+"."{DIGIT}+
ID         = [a-zA-Z_][a-zA-Z0-9_]*
STRING     = \'[^\']*\'

%%

{WHITESPACE}          { /* ignorar espacios */ }

"CREATE"  { return new java_cup.runtime.Symbol(sym.CREATE, yyline+1, yycolumn+1); }
"DATABASE" { return new java_cup.runtime.Symbol(sym.DATABASE, yyline+1, yycolumn+1); }
"TABLE"   { return new java_cup.runtime.Symbol(sym.TABLE, yyline+1, yycolumn+1); }
"INSERT"  { return new java_cup.runtime.Symbol(sym.INSERT, yyline+1, yycolumn+1); }
"INTO"    { return new java_cup.runtime.Symbol(sym.INTO, yyline+1, yycolumn+1); }
"VALUES"  { return new java_cup.runtime.Symbol(sym.VALUES, yyline+1, yycolumn+1); }
"SELECT"  { return new java_cup.runtime.Symbol(sym.SELECT, yyline+1, yycolumn+1); }
"FROM"    { return new java_cup.runtime.Symbol(sym.FROM, yyline+1, yycolumn+1); }
"USE"     { return new java_cup.runtime.Symbol(sym.USE, yyline+1, yycolumn+1); }
"*"       { return new java_cup.runtime.Symbol(sym.STAR, yyline+1, yycolumn+1); }
";"       { return new java_cup.runtime.Symbol(sym.SEMI, yyline+1, yycolumn+1); }
","       { return new java_cup.runtime.Symbol(sym.COMMA, yyline+1, yycolumn+1); }
"("       { return new java_cup.runtime.Symbol(sym.LPAREN, yyline+1, yycolumn+1); }
")"       { return new java_cup.runtime.Symbol(sym.RPAREN, yyline+1, yycolumn+1); }

"UPDATE"  { return new java_cup.runtime.Symbol(sym.UPDATE, yyline+1, yycolumn+1); }
"SET"     { return new java_cup.runtime.Symbol(sym.SET, yyline+1, yycolumn+1); }
"WHERE"   { return new java_cup.runtime.Symbol(sym.WHERE, yyline+1, yycolumn+1); }
"JOIN"    { return new java_cup.runtime.Symbol(sym.JOIN, yyline+1, yycolumn+1); }
"ON"      { return new java_cup.runtime.Symbol(sym.ON, yyline+1, yycolumn+1); }
"AS"      { return new java_cup.runtime.Symbol(sym.AS, yyline+1, yycolumn+1); }

"INT"      { return new java_cup.runtime.Symbol(sym.INT, yyline+1, yycolumn+1); }
"VARCHAR"  { return new java_cup.runtime.Symbol(sym.VARCHAR, yyline+1, yycolumn+1); }
"DATETIME" { return new java_cup.runtime.Symbol(sym.DATETIME, yyline+1, yycolumn+1); }
"DECIMAL"  { return new java_cup.runtime.Symbol(sym.DECIMAL, yyline+1, yycolumn+1); }

"conteo"  { return new java_cup.runtime.Symbol(sym.CONTEO, yyline+1, yycolumn+1); }

"="  { return new java_cup.runtime.Symbol(sym.EQUALS, yyline+1, yycolumn+1); }
">=" { return new java_cup.runtime.Symbol(sym.GREATER_THAN_OR_EQUALS, yyline+1, yycolumn+1); }
"<=" { return new java_cup.runtime.Symbol(sym.LESS_THAN_OR_EQUALS, yyline+1, yycolumn+1); }
"!=" { return new java_cup.runtime.Symbol(sym.NOT_EQUALS, yyline+1, yycolumn+1); }
">"  { return new java_cup.runtime.Symbol(sym.GREATER_THAN, yyline+1, yycolumn+1); }
"<"  { return new java_cup.runtime.Symbol(sym.LESS_THAN, yyline+1, yycolumn+1); }
"."  { return new java_cup.runtime.Symbol(sym.DOT, yyline+1, yycolumn+1); }

{DECIMAL_NUM} { return new java_cup.runtime.Symbol(sym.NUMBER, yyline+1, yycolumn+1, Double.valueOf(yytext())); }
{DIGIT}+      { return new java_cup.runtime.Symbol(sym.NUMBER, yyline+1, yycolumn+1, Integer.valueOf(yytext())); }
{STRING}      { return new java_cup.runtime.Symbol(sym.STRING, yyline+1, yycolumn+1, yytext()); }
{ID}          { return new java_cup.runtime.Symbol(sym.ID, yyline+1, yycolumn+1, yytext()); }

. { 
    Errors.hayErrores = true;
    System.out.println(
        "ERROR: Lexico | linea: " + (yyline+1) + 
        " columna: " + (yycolumn+1) + 
        " valor: '" + yytext() + "'"
    ); 
}
