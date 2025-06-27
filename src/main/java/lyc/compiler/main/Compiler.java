package lyc.compiler.main;

import lyc.compiler.Parser;
import lyc.compiler.factories.FileFactory;
import lyc.compiler.factories.ParserFactory;
import lyc.compiler.files.FileOutputWriter;
import lyc.compiler.files.SymbolTableGenerator;
import lyc.compiler.polaca.Polaca;
import lyc.compiler.files.IntermediateCodeGenerator;
import lyc.compiler.files.AsmCodeGenerator;

import java.io.IOException;
import java.io.Reader;

public final class Compiler {

    private Compiler(){}

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Filename must be provided as argument.");
            System.exit(0);
        }

        try (Reader reader = FileFactory.create(args[0])) {
            Parser parser = ParserFactory.create(reader);
            parser.parse();
            FileOutputWriter.writeOutput("symbol-table.txt", new SymbolTableGenerator());
            FileOutputWriter.writeOutput("intermediate-code.txt", new IntermediateCodeGenerator());
            FileOutputWriter.writeOutput("final.asm", new AsmCodeGenerator());

            System.out.println("----- POLACA INVERSA GENERADA -----");
            System.out.println(Polaca.getPolaca());
        } catch (IOException e) {
            System.err.println("\nHubo un error al intentar leer el archivo de entrada " + e.getMessage());
            System.exit(0);
        } catch (Exception e) {
            System.err.println("\n*************************\nError de compilacion: " + e.getMessage() + "\n*************************\n");
            System.exit(0);
        }

        System.out.println("\nCompilacion exitosa!");
    }
}
