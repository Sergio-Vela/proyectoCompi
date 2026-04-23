%%
%{
    public boolean hayErrores = false;

    private java_cup.runtime.Symbol symbol(int type) {
        return symbol(type, yytext());
    }

    private java_cup.runtime.Symbol symbol(int type, Object value) {
        Errors.registrarToken(type, yyline + 1, yycolumn + 1, yytext());
        return new java_cup.runtime.Symbol(type, yyline + 1, yycolumn + 1, value);
    }

    private java_cup.runtime.Symbol eofSymbol() {
        int line = yyline + 1;
        int column = yycolumn + 1;

        if (Errors.tokenActual != null) {
            line = Errors.tokenActual.line;
            column = Errors.columnaFinal(Errors.tokenActual) + 1;
        }

        Errors.registrarToken(sym.EOF, line, column, "EOF");
        return new java_cup.runtime.Symbol(sym.EOF, line, column, null);
    }
%}
%class Lexer
%unicode
%cup
%line
%column
%ignorecase
%eofval{
    return eofSymbol();
%eofval}

WHITESPACE = [ \t\r\n]+
DIGIT      = [0-9]
DECIMAL_NUM = {DIGIT}+"."{DIGIT}+
ID         = [a-zA-Z_][a-zA-Z0-9_]*
STRING     = \'[^\']*\'

%%

{WHITESPACE}          { /* ignorar espacios */ }

"CREATE"  { return symbol(sym.CREATE); }
"TABLE"   { return symbol(sym.TABLE); }
"INSERT"  { return symbol(sym.INSERT); }
"INTO"    { return symbol(sym.INTO); }
"VALUES"  { return symbol(sym.VALUES); }
"SELECT"  { return symbol(sym.SELECT); }
"FROM"    { return symbol(sym.FROM); }
"*"       { return symbol(sym.STAR); }
";"       { return symbol(sym.SEMI); }
","       { return symbol(sym.COMMA); }
"("       { return symbol(sym.LPAREN); }
")"       { return symbol(sym.RPAREN); }

"UPDATE"  { return symbol(sym.UPDATE); }
"SET"     { return symbol(sym.SET); }
"WHERE"   { return symbol(sym.WHERE); }
"JOIN"    { return symbol(sym.JOIN); }
"ON"      { return symbol(sym.ON); }
"AS"      { return symbol(sym.AS); }

"INT"      { return symbol(sym.INT); }
"VARCHAR"  { return symbol(sym.VARCHAR); }
"DATETIME" { return symbol(sym.DATETIME); }
"DECIMAL"  { return symbol(sym.DECIMAL); }

"conteo"  { return symbol(sym.CONTEO); }

"="  { return symbol(sym.EQUALS); }
"."  { return symbol(sym.DOT); }

{DECIMAL_NUM} { return symbol(sym.NUMBER, Double.valueOf(yytext())); }
{DIGIT}+      { return symbol(sym.NUMBER, Double.valueOf(yytext())); }
{STRING}      { return symbol(sym.STRING, yytext()); }
{ID}          { return symbol(sym.ID, yytext()); }

. { 
    Errors.registrarErrorLexico(yyline + 1, yycolumn + 1, yytext());
    String clave = "LEX:" + (yyline + 1) + ":" + (yycolumn + 1) + ":" + yytext();

    if (Errors.debeReportar(clave)) {
        System.out.println(
            "ERROR: Lexico | linea: " + (yyline + 1) +
            " | columna: " + (yycolumn + 1) +
            " | valor: '" + yytext() + "'" +
            " | detalle: Caracter no reconocido"
        );
    }
}
