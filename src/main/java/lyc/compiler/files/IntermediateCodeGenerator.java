package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import lyc.compiler.polaca.Polaca;

public class IntermediateCodeGenerator implements FileGenerator {

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        fileWriter.write(Polaca.getPolaca());
    }
}
