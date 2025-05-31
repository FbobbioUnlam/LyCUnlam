package lyc.compiler.symbols;

import java.util.HashMap;

public class SymbolTable {
    private static final HashMap<String, SymbolEntry> table = new HashMap<>();

    public static void insert(String nombre, String tipoDato, String valor) {
        if (!table.containsKey(nombre)) {
            String longitud;
            
            if ("ID".equals(tipoDato)) {
                tipoDato = "";
                valor = "";
                longitud = "";
            } else if ("String".equals(tipoDato)) {
                longitud = String.valueOf(valor.length());
            } else {
                longitud = "";
            }

            table.put(nombre, new SymbolEntry(nombre, tipoDato, valor, longitud));
        }
    }

    public static String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════╦════════════╦══════════════════════╦══════════╗\n");
        sb.append(String.format("║ %-20s ║ %-10s ║ %-20s ║ %-8s ║\n", "NOMBRE", "TIPODATO", "VALOR", "LONGITUD"));
        sb.append("╠══════════════════════╬════════════╬══════════════════════╬══════════╣\n");
        for (SymbolEntry s : table.values()) {
            String nombreMostrado = s.nombre;
            String valorMostrado = s.valor;
            if ("String".equals(s.tipoDato) && s.valor.length() > 15) {
                nombreMostrado = s.nombre.substring(0, 15);
                nombreMostrado = nombreMostrado + "...\"";
                valorMostrado = s.valor.substring(0, 15);
                valorMostrado = valorMostrado + "...\"";
            }
            sb.append(String.format("║ %-20s ║ %-10s ║ %-20s ║ %-8s ║\n",
                nombreMostrado, s.tipoDato, valorMostrado, s.longitud));
        }
        sb.append("╚══════════════════════╩════════════╩══════════════════════╩══════════╝\n");
        return sb.toString();
    }
}