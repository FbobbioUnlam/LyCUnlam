package lyc.compiler.files;

import lyc.compiler.polaca.Polaca;
import lyc.compiler.symbols.SymbolEntry;
import lyc.compiler.symbols.SymbolTable;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class AsmCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        // 1) INICIO
        fileWriter.write("include macros2.asm\n");
        fileWriter.write("include number.asm\n\n");
        fileWriter.write(".MODEL LARGE\n");
        fileWriter.write(".386\n");
        fileWriter.write(".STACK 200h\n\n");
        fileWriter.write("MAXTEXTSIZE equ 50\n\n");
        
        // 2) SECTION DATA.
        fileWriter.write(".DATA\n");

        for (SymbolEntry entry : SymbolTable.table.values()) {
            switch (entry.tipoDato) {
                case "CTE_ENTERA":
                    fileWriter.write(String.format("%s dd %s\n", entry.nombre, entry.valor + ".0"));
                    break;
                case "CTE_FLOTANTE":
                    String normalizedFloat = normalizeFloatName(entry.valor);
                    fileWriter.write(String.format("%s dd %s\n", normalizedFloat, entry.valor));
                    break;

                case "CTE_CADENA":
                    String normalizedName = normalizeStringName(entry.valor);
                    int longitud = entry.valor.length() + 2;
                    fileWriter.write(String.format("%s db %s, '$', %d dup (?)\n", normalizedName, entry.valor, longitud));
                    break;

                case "Int":
                case "Float":
                    fileWriter.write(String.format("%s dd ?\n", entry.nombre));
                    break;

                case "String":
                    fileWriter.write(String.format("%s db MAXTEXTSIZE dup (?),'$'\n", entry.nombre));
                    break;

                default:
                    throw new RuntimeException("Tipo de dato no soportado: " + entry.tipoDato);
            }
        }

        fileWriter.write("@aux dd ?\n");
        fileWriter.write("@aux2 dd ?\n");

        // 3) SECTION CODE.
        fileWriter.write("\n.CODE\n\n");
        fileWriter.write("START:\n\n");
        fileWriter.write("MOV AX, @DATA\n");
        fileWriter.write("MOV DS, AX\n");
        fileWriter.write("MOV ES, AX\n\n");

        Stack<String> pila = new Stack<>();
        Queue<Integer> colaJmp = new ArrayDeque<>();
        List<String> elementos = Polaca.getElementos();
        String token;
        Boolean isWhile = false;

        for (int i = 0; i < elementos.size(); i++) {
            token = elementos.get(i);

            switch (token) {
                case ":=":
                    String var = pila.pop();
                    String val = pila.pop();
                    SymbolEntry entryVar = SymbolTable.table.get(var);
                    SymbolEntry entryVal = null;
                    for (SymbolEntry s : SymbolTable.table.values()) {
                        if (s.valor != null && s.valor.equals(val)) {
                            entryVal = s;
                            break;
                        }
                    }

                    fileWriter.write(String.format("; %s := %s\n", var, val));

                    if (entryVar != null && "String".equals(entryVar.tipoDato)) {
                        String origen = adaptLiteral(val);
                        String destino = adaptLiteral(var);
                        int lengthofString = entryVal != null ? entryVal.valor.length() + 1 : 0;

                        fileWriter.write(String.format("lea si, %s\n", origen));
                        fileWriter.write(String.format("lea di, %s\n", destino));
                        fileWriter.write(String.format("mov cx, %d\n", lengthofString));
                        fileWriter.write("rep movsb\n\n");
                    } else {
                        String valNorm = adaptLiteral(val);

                        fileWriter.write(String.format("fld %s\n", valNorm));
                        //fileWriter.write("fstp @aux\n");
                        //fileWriter.write("fld @aux\n");
                        fileWriter.write(String.format("fstp %s\n\n", var));
                    }
                    break;

                case "+":
                case "-":
                case "*":
                case "/":
                    String op2 = pila.pop();
                    String op1 = pila.pop();
                    String op1Norm = adaptLiteral(op1);
                    String op2Norm = adaptLiteral(op2);

                    fileWriter.write(String.format("; %s %s %s\n", op1, token, op2));
                    fileWriter.write(String.format("fld %s\n", op1Norm));
                    fileWriter.write(String.format("fld %s\n", op2Norm));
                    switch (token) {
                        case "+": fileWriter.write("fadd\n"); break;
                        case "-": fileWriter.write("fsub\n"); break;
                        case "*": fileWriter.write("fmul\n"); break;
                        case "/": fileWriter.write("fdiv\n"); break;
                    }
                    fileWriter.write("fstp @aux2\n");
                    fileWriter.write("fld @aux2\n");
                    fileWriter.write("fstp @aux\n\n");

                    pila.push("@aux");
                    break;

                case("WRITE"):
                    String varW = pila.pop();
                    SymbolEntry entryW = SymbolTable.table.get(varW);
                    if (entryW == null) {
                        entryW = SymbolTable.table.get("_" + varW);
                    }
                    String varWNorm = adaptLiteral(varW);

                    fileWriter.write(String.format("; WRITE (%s)\n", varW));

                    switch (entryW.tipoDato) {
                        case "String":
                        case "CTE_CADENA":
                            fileWriter.write(String.format("displayString %s\n", varWNorm));
                            fileWriter.write("newLine 1\n\n");
                            break;

                        default:
                            fileWriter.write(String.format("fld %s\n", varWNorm));
                            fileWriter.write(String.format("DisplayFloat %s, 2\n", varWNorm));
                            fileWriter.write("newLine 2\n\n");
                            break;
                    }
                    break;

                case "CMP":
                    String rightCMP = pila.pop();
                    String leftCMP = pila.pop();
                    String rightNorm = adaptLiteral(rightCMP);
                    String leftNorm = adaptLiteral(leftCMP);
                    fileWriter.write(String.format("; %s CMP %s\n", leftCMP, rightCMP));
                    fileWriter.write(String.format("fld %s\n", leftNorm));
                    fileWriter.write(String.format("fld %s\n", rightNorm));
                    fileWriter.write(String.format("fxch\n"));
                    fileWriter.write("fcomp\n");
                    fileWriter.write("fstsw ax\n");
                    fileWriter.write("ffree st(0)\n");
                    fileWriter.write("sahf\n");
                    break;

                case "BLE":
                case "BGE":
                case "BEQ":
                case "BNE":
                case "BGT":
                case "BLT":
                    // Obtengo el lugar a saltar
                    Integer posJumpFalse = Integer.parseInt(elementos.get(i+1));
                    //System.err.println("Voy a pushear la primera pos a saltar " + posJumpFalse);
                    colaJmp.add(posJumpFalse);
                    String jmp = switch (token) {
                        case "BLE" -> "jbe";
                        case "BGE" -> "jae";
                        case "BEQ" -> "je";
                        case "BNE" -> "jne";
                        case "BGT" -> "ja";
                        case "BLT" -> "jb";
                        default -> throw new RuntimeException("Comparación no soportada: " + token);
                    };
                    fileWriter.write(String.format("%s etiq_%s\n", jmp, posJumpFalse));
                    break;

                case "BI":
                    // Obtengo el lugar a saltar
                    Integer posJumpBI = Integer.parseInt(elementos.get(i+1));
                    if (!isWhile) {
                        colaJmp.add(posJumpBI);
                    } else {
                        isWhile = false;
                    }
                    fileWriter.write(String.format("; Salto incondicional\n"));
                    fileWriter.write(String.format("jmp etiq_%s\n", posJumpBI));
                    break;
                case "WHILE":
                    fileWriter.write(String.format("etiq_%d:\n", i));
                    fileWriter.write(String.format("; While\n"));
                    isWhile = true;
                    break;

                default:
                    pila.push(token);
                    break;
            }

            if(!colaJmp.isEmpty() && i+1 == colaJmp.peek()) {
                fileWriter.write(String.format("etiq_%d:\n", colaJmp.remove()));
            }
        }

        // 4) FIN DEL PROGRAMA
        fileWriter.write("\nMOV EAX, 4C00h\n");
        fileWriter.write("INT 21h\n\n");
        fileWriter.write("END START\n");
    }

    private String normalizeFloatName(String valor) {
        String result = "";

        if (valor.startsWith("-")) {
            result = "neg";
            valor = valor.replace("-", "");
        }

        if (valor.startsWith(".")) {
            result += "0_" + valor.substring(1) + "_";
        } else if (valor.endsWith(".")) {
            result += valor.substring(0, valor.length() - 1) + "_0_";
        } else if (valor.contains(".")) {
            result += valor.replace(".", "_");
        } else {
            result += valor;
        }

        return "_" + result;
    }

    private String normalizeStringName(String valor) {
        return "_" + valor.replace("\"", "").replace(" ", "_").replace(":","");
    }

    private String adaptLiteral(String token) {
        for (SymbolEntry entry : SymbolTable.table.values()) {
            if (entry.valor != null && entry.valor.equals(token)) {
                switch (entry.tipoDato) {
                    case "CTE_FLOTANTE":
                        return normalizeFloatName(entry.valor);

                    case "CTE_ENTERA":
                        return "_" + entry.valor;

                    case "CTE_CADENA":
                        return normalizeStringName(entry.valor);

                    default:
                        return entry.nombre;
                }
            }
        }

        // Si no encuentra, se asume que es una variable ID normal
        return token;
    }
}
