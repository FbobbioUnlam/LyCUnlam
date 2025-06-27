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
    public static Boolean isWhile = false;

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

    public static String sliceAndConcat(Object limiteInicial, Object limiteFinal, Object palabra1, Object palabra2, Object concatenarEnPalabra1) {
        int limitS = Integer.parseInt(limiteInicial.toString());
        int limitE = Integer.parseInt(limiteFinal.toString());
        String word1 = palabra1.toString();
        String word2 = palabra2.toString();
        //Les quito las comillas
        word1 = word1.substring(1, word1.length() - 1);
        word2 = word2.substring(1, word2.length() - 1);
        int concatFirstW = Integer.parseInt(concatenarEnPalabra1.toString());

        if (limitS < 0 || limitS >= limitE) {
            throw new IllegalArgumentException("Los limites de corte son invalidos: " + limiteInicial + " - " + limiteFinal);
        }

        String cut;
        String result = "";

        //Tengo que sumar por las ""
        if (concatFirstW == 1) {
            if (limitE+1 > word2.length()) {
                throw new IllegalArgumentException("El limite final excede la longitud de palabra2: " + palabra2);
            }
            cut = word2.substring(limitS, limitE+1);
            //System.out.println("Le concateno a la palabra 1: " + cut);
            result = "\"" + word1 + cut + "\"";
        } else {
            if (limitE+1 > word1.length()) {
                throw new IllegalArgumentException("El limite final excede la longitud de palabra1: " + palabra1);
            }
            cut = word1.substring(limitS, limitE+1);
            //System.out.println("Le concateno a la palabra 2: " + cut);
            result = "\"" + word2 + cut + "\"";
        }

        return result;
    }
}