package graphics;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import math.Matrix4f;
import math.Vector3f;
import util.ShaderUtils;

public class Shader {

    public static final int VERTEX_ATTRIB = 0;
    public static final int TCOORD_ATTRIB = 1;

    public static Shader GRAY, HIT;

    private boolean enabled = false;
    private int ID = -1;
    private Map<String, Integer> locationCache = new HashMap<>();

    public Shader() {
        
    }

    public void load(String vertexPath, String fragmentPath) {
        String vertexSource = ShaderUtils.load_as_string(vertexPath);
        String fragmentSource = ShaderUtils.load_as_string(fragmentPath);

        int vertexID = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        if (glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Vertex shader compilation failed:\n" + glGetShaderInfoLog(vertexID));

        int fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        if (glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Fragment shader compilation failed:\n" + glGetShaderInfoLog(fragmentID));

        ID = glCreateProgram();
        glAttachShader(ID, vertexID);
        glAttachShader(ID, fragmentID);

        glBindAttribLocation(ID, VERTEX_ATTRIB, "position");

        glLinkProgram(ID);
        if (glGetProgrami(ID, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Program linking failed:\n" + glGetProgramInfoLog(ID));

        glValidateProgram(ID);
        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);
    }

    public static void loadAll() {

        HIT = new Shader();
        HIT.load("shaders/hit.vert", "shaders/hit.frag");
    }

    public int getUniform(String name) {
        if (locationCache.containsKey(name))
            return locationCache.get(name);

        int result = glGetUniformLocation(ID, name);
        if (result == -1)
            System.err.println("Could not find uniform variable '" + name + "'!");
        else
            locationCache.put(name, result);
        return result;
    }

    public void setUniform1f(String name, float value) {
        if (!enabled)
            enable();
        glUniform1f(getUniform(name), value);
    }

    public void setUniform3f(String name, Vector3f vector) {
        if (!enabled)
            enable();
        glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
    }

    public void setUniformMat4f(String name, Matrix4f matrix) {
        if (!enabled)
            enable();
        glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
    }

    public void enable() {
        glUseProgram(ID);
        enabled = true;
    }

    public void disable() {
        glUseProgram(0);
        enabled = false;
    }
}
