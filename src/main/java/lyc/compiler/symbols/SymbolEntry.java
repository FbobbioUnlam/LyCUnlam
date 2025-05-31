package lyc.compiler.symbols;

public class SymbolEntry {
    public String nombre;
    public String tipoDato;
    public String valor;
    public String longitud;

    public SymbolEntry(String nombre, String tipoDato, String valor, String longitud) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.valor = valor;
        this.longitud = longitud;
    }
}