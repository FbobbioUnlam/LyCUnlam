package lyc.compiler.symbols;

import java.util.HashMap;
import java.util.Stack;

import lyc.compiler.model.MyCompilerException;

public class SymbolTable {
    public static final HashMap<String, SymbolEntry> table = new HashMap<>();

    public static void insert(String nombre, String tipoDato, String valor) {
        if (!table.containsKey(nombre)) {
            String longitud;
            
            if ("ID".equals(tipoDato)) {
                tipoDato = "";
                valor = "";
                longitud = "";
            } else if ("CTE_CADENA".equals(tipoDato)) {
                longitud = String.valueOf(valor.length());
            } else {
                longitud = "";
            }

            table.put(nombre, new SymbolEntry(nombre, tipoDato, valor, longitud));
        }
    }

    public static String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════╦══════════════╦══════════════════════════════════════════════════════╦══════════╗\n");
        sb.append(String.format("║ %-52s ║ %-12s ║ %-52s ║ %-8s ║\n", "NOMBRE", "TIPODATO", "VALOR", "LONGITUD"));
        sb.append("╠══════════════════════════════════════════════════════╬══════════════╬══════════════════════════════════════════════════════╬══════════╣\n");
        for (SymbolEntry s : table.values()) {
            String nombreMostrado = s.nombre;
            String valorMostrado = s.valor;
            if ("String".equals(s.tipoDato) && s.valor.length() > 15) {
                nombreMostrado = s.nombre.substring(0, 15);
                nombreMostrado = nombreMostrado + "...\"";
                valorMostrado = s.valor.substring(0, 15);
                valorMostrado = valorMostrado + "...\"";
            }
            sb.append(String.format("║ %-52s ║ %-12s ║ %-52s ║ %-8s ║\n",
                nombreMostrado, s.tipoDato, valorMostrado, s.longitud));
        }
        sb.append("╚══════════════════════════════════════════════════════╩══════════════╩══════════════════════════════════════════════════════╩══════════╝\n");
        return sb.toString();
    }

    public static void declare(Object nombre, Object tipoDato) throws MyCompilerException {
        SymbolEntry entry = table.get(nombre);
        if (hasBeenDeclared(nombre)) {
            throw new MyCompilerException("Variable ya declarada: '" + nombre + "'.");
        }

        entry.tipoDato = (String)tipoDato;
    }

    public static boolean hasBeenDeclared(Object nombre) {
        SymbolEntry entry = table.get(nombre);
        return entry != null && entry.tipoDato != null && !entry.tipoDato.isEmpty();
    }

    public static void checkDeclared(Object nombre) throws MyCompilerException {
        if(!hasBeenDeclared(nombre)) {
            throw new MyCompilerException("Variable no declarada: '" + nombre + "'.");
        }
    }

    public static String getType(Object nombre) {
        SymbolEntry entry = table.get(nombre);
        return entry.tipoDato;
    }

    public void checkAssignmentType(Stack<String> pilaTipos, String leftType, Object nombre) throws MyCompilerException {
        if (pilaTipos.size() > 1 && pilaTipos.search("String") != -1) {
            /* Sólo permito hacer asignaciones con Strings
            for (String elemento : pilaTipos) {
                System.out.println(elemento);
            }
            */
            throw new MyCompilerException("No se pueden realizar operaciones aritmeticas sobre cadenas. Variable asignacion: '" + nombre + "'.");
        }
        
        for (String tipo : pilaTipos) {
            if(!tipo.equals(leftType)) {
                throw new MyCompilerException("No se puede asignar un " + tipo + " a la variable '" + nombre + "' de tipo " + leftType + ".");
            }
        }
    }

    public void checkComparationType(String leftType, String rightType) throws MyCompilerException {
        if (leftType.equals("String") || rightType.equals("String")) {
            throw new MyCompilerException("No se pueden realizar comparaciones sobre cadenas.");
        }
        
        if(!leftType.equals(rightType)) {
            throw new MyCompilerException("No se puede comparar un " + leftType + " con un " + rightType + ".");
        }
    }

    public void checkParamType(Object param, String type) {
        SymbolEntry entry = table.get(param);
        if(!entry.tipoDato.equals(type)) {
            throw new IllegalArgumentException("Funcion negativeCalculation - El parametro '" + entry.nombre + "' debe ser de tipo " + type + ".");
        }
    }

    public static SymbolEntry get(String nombre) {
        return table.get(nombre);
    }
}