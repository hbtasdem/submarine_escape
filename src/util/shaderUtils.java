package util;

import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FALSE;

public class ShaderUtils {

    private ShaderUtils() {

    }

    public static int load(String vertPath, String fragPath) {
        // used since we create shaders from files and not from strings, acts as the
        // bridge between fileUtils and here
        String vert = FileUtils.load_as_string(vertPath);
        String frag = FileUtils.load_as_string(fragPath);
        return create(vert, frag);
    }

    public static int create(String vert, String frag) { // will create a program containing our two shaders based on
                                                         // string from src code
        int program = glCreateProgram(); // program is a collection of diff types of shaders that work together
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);

        // pass in the src code to opengl to compile
        glShaderSource(vertID, vert);
        glShaderSource(fragID, frag);

        // compile both shaders
        glCompileShader(vertID);

        // shader syntax checker for vert
        if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile the vertex shader..");
            System.err.println(glGetShaderInfoLog(vertID, 2048));
        }

        glCompileShader(fragID);

        // shader syntax checker for frag
        if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile the fragment shader..");
            System.err.println(glGetShaderInfoLog(fragID, 2048));
        }

        // attach shaders to program
        glAttachShader(program, vertID);
        glAttachShader(program, fragID);

        // link & validate the program
        glLinkProgram(program);
        glValidateProgram(program);

        return program;
    }

    public static String load_as_string(String filePath) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}