
package graphics;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {
    private static final float CHAR_WIDTH = 0.02f;
    private static final float CHAR_HEIGHT = 0.04f;

    public void renderLine(String line, float startX, float startY) {
        float x = startX;
        float y = startY;

        for (char c : line.toCharArray()) {
            if (c != ' ') {
                drawCharQuad(x, y);
            }
            x += CHAR_WIDTH;
        }
    }

    private void drawCharQuad(float x, float y) {
        glColor3f(1f, 1f, 1f); // White
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + CHAR_WIDTH, y);
        glVertex2f(x + CHAR_WIDTH, y - CHAR_HEIGHT);
        glVertex2f(x, y - CHAR_HEIGHT);
        glEnd();
    }
}
