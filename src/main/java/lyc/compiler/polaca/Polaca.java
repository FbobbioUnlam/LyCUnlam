package lyc.compiler.polaca;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Polaca {
    private static final List<String> elementos = new ArrayList<>();
    public static Stack<Integer> pilaIf = new Stack<>();
    public static Stack<Integer> pilaWhile = new Stack<>();
    public static Stack<Integer> pilaNeg = new Stack<>();
    public static Stack<Integer> pilaNegV = new Stack<>();
    public static Boolean isOR = false;

    public static void insertarEnPolaca(Object elemento) {
        elementos.add(elemento.toString());
    }

    public static int getIndice() {
        return elementos.size();
    }

    public static String getPolaca() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elementos.size(); i++) {
            sb.append(String.format("[%02d] %s\n", i, elementos.get(i)));
        }
        return sb.toString();
    }

    public static List<String> getElementos() {
        return elementos;
    }
}