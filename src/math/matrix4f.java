package math;

import static java.lang.Math.*;

import java.nio.FloatBuffer;
import util.BufferUtils;

public class Matrix4f {

    public float[] matrix = new float[4 * 4];

    public static Matrix4f identity() { // identity mat setup
        Matrix4f result = new Matrix4f();

        for (int i = 0; i < 4 * 4; i++) {
            result.matrix[i] = 0.0f;
        }

        // result.matrix[0 + 0 * 4] = 1.0f; // col 1 row 1 //how does this syntax work to create an identity matrix
        result.matrix[1 + 1 * 4] = 1.0f;
        result.matrix[2 + 2 * 4] = 1.0f;
        result.matrix[3 + 3 * 4] = 1.0f;

        return result;
    }

    public Matrix4f multiply(Matrix4f matrix) { // multiplication mat setup

        Matrix4f result = new Matrix4f();

        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                float sum = 0.0f;
                for (int e = 0; e < 4; e++) {
                    sum += this.matrix[e + y * 4] * matrix.matrix[x + e * 4];
                }
                result.matrix[x + y * 4] = sum;
            }
        }
        return result;
    }

    public static Matrix4f translate(Vector3f vector) { // translation mat setup

        Matrix4f result = identity();

        result.matrix[0 + 3 * 4] = vector.x;
        result.matrix[1 + 3 * 4] = vector.y;
        result.matrix[2 + 3 * 4] = vector.z;

        return result;
    }

    // since 2D, rot matrix is only set for z axis
    public static Matrix4f rotate(float angle) { // rotation mat setup
        // java takes angle in rad

        Matrix4f result = identity();
        float r = (float) toRadians(angle);
        float cos = (float) cos(r);
        float sin = (float) sin(r);

        result.matrix[0 + 0 * 4] = cos;
        result.matrix[1 + 0 * 4] = sin;
        result.matrix[0 + 1 * 4] = -sin;
        result.matrix[1 + 1 * 4] = cos;

        return result;
    }

    public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {

        Matrix4f result = identity();

        result.matrix[0 + 0 * 4] = 2.0f / (right - left);
        result.matrix[1 + 1 * 4] = 2.0f / (top - bottom);
        result.matrix[2 + 2 * 4] = 2.0f / (near - far);
        result.matrix[0 + 3 * 4] = (left + right) / (left - right);
        result.matrix[1 + 3 * 4] = (bottom + top) / (bottom - top);
        result.matrix[2 + 3 * 4] = (far + near) / (far - near);

        return result;

        // lwjgl wants float buffers instead of float arrays?

    }

    public FloatBuffer toFloatBuffer() {
        return BufferUtils.createFloatBuffer(matrix);
    }
}