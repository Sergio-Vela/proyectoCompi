public class Errors {
    public static boolean hayErrores = false;
    public static String ultimaClaveError = "";
    public static TokenInfo ultimoToken = null;
    public static TokenInfo tokenActual = null;
    public static int ultimaLineaLexica = -1;
    public static int ultimaColumnaLexica = -1;
    public static String ultimoValorLexico = "";

    public static void reiniciar() {
        hayErrores = false;
        ultimaClaveError = "";
        ultimoToken = null;
        tokenActual = null;
        ultimaLineaLexica = -1;
        ultimaColumnaLexica = -1;
        ultimoValorLexico = "";
    }

    public static void registrarToken(int sym, int line, int column, String lexema) {
        ultimoToken = tokenActual;
        tokenActual = new TokenInfo(sym, line, column, lexema);
    }

    public static boolean debeReportar(String clave) {
        if (clave == null || clave.isEmpty()) {
            return true;
        }

        if (clave.equals(ultimaClaveError)) {
            return false;
        }

        ultimaClaveError = clave;
        return true;
    }

    public static void registrarErrorLexico(int line, int column, String lexema) {
        hayErrores = true;
        ultimaLineaLexica = line;
        ultimaColumnaLexica = column;
        ultimoValorLexico = lexema;
    }

    public static boolean hayErrorLexicoCercano(int line, int column) {
        return ultimaLineaLexica == line && Math.abs(ultimaColumnaLexica - column) <= 1;
    }

    public static int columnaFinal(TokenInfo token) {
        if (token == null) {
            return 0;
        }

        int longitud = (token.lexema == null || token.lexema.isEmpty()) ? 1 : token.lexema.length();
        return token.column + longitud - 1;
    }

    public static class TokenInfo {
        public final int sym;
        public final int line;
        public final int column;
        public final String lexema;

        public TokenInfo(int sym, int line, int column, String lexema) {
            this.sym = sym;
            this.line = line;
            this.column = column;
            this.lexema = lexema;
        }
    }
}
