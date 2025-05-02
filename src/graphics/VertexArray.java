package graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import util.BufferUtils;

public class VertexArray {

    private int vao, vbo, ibo, tbo;
    private int count;

    private VertexArray() {
        // Use static factory method to create safely
    }

    public static VertexArray createBasic(int count) {
        VertexArray va = new VertexArray();
        va.count = count;
        va.vao = glGenVertexArrays();
        return va;
    }

    public static VertexArray createFull(float[] vertices, byte[] indices, float[] textureCoordinates) {
        VertexArray va = new VertexArray();
        va.count = (indices != null) ? indices.length : vertices.length / 2;

        va.vao = glGenVertexArrays();
        glBindVertexArray(va.vao);

        va.vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, va.vbo);
        glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(vertices), GL_STATIC_DRAW);
        glVertexAttribPointer(Shader.VERTEX_ATTRIB, 2, GL_FLOAT, false, 0, 0); // 2D coords
        glEnableVertexAttribArray(Shader.VERTEX_ATTRIB);

        if (textureCoordinates != null) {
            va.tbo = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, va.tbo);
            glBufferData(GL_ARRAY_BUFFER, BufferUtils.createFloatBuffer(textureCoordinates), GL_STATIC_DRAW);
            glVertexAttribPointer(Shader.TCOORD_ATTRIB, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(Shader.TCOORD_ATTRIB);
        }

        if (indices != null) {
            va.ibo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, va.ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, BufferUtils.createByteBuffer(indices), GL_STATIC_DRAW);
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        return va;
    }

    public void bind() {
        glBindVertexArray(vao);
        if (ibo > 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
    }

    public void unbind() {
        if (ibo > 0)
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void draw() {
        if (ibo > 0)
            glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_BYTE, 0);
        else
            glDrawArrays(GL_TRIANGLES, 0, count);
    }

    public void render() {
        bind();
        draw();
        unbind();
    }
}
