package maze;

import graphics.Texture;
import static org.lwjgl.opengl.GL11.*;

public class MazeSegment {

    public float x; // X position (scrolling)
    public float topY; // Ceiling height
    public float bottomY; // Floor height

    public static final float WIDTH = 0.5f; // Width of the maze segment

    private Texture texture;

    // Constructor with texture passed in
    public MazeSegment(float x, float topY, float bottomY, Texture texture) {
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
        this.texture = texture;
    }

    public void update(float speed) {
        x -= speed; // Scroll the segment to the left
    }

    public boolean isOffScreen() {
        return x + WIDTH < -1.0f;
    }

    public void render() {
        if (texture == null)
            return; // Safety check!

        texture.bind();

        glColor3f(1f, 1f, 1f); // Use full texture color

        // --- Draw Floor ---
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, -1.0f);
        glTexCoord2f(1, 0);
        glVertex2f(x + WIDTH, -1.0f);
        glTexCoord2f(1, 1);
        glVertex2f(x + WIDTH, -1.0f + bottomY / 1.0f);
        glTexCoord2f(0, 1);
        glVertex2f(x, -1.0f + bottomY / 1.0f);
        glEnd();

        // --- Draw Ceiling ---
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, 1.0f);
        glTexCoord2f(1, 0);
        glVertex2f(x + WIDTH, 1.0f);
        glTexCoord2f(1, 1);
        glVertex2f(x + WIDTH, 1.0f - topY / 1.0f);
        glTexCoord2f(0, 1);
        glVertex2f(x, 1.0f - topY / 1.0f);
        glEnd();

        texture.unbind();
    }

    public float getX() {
        return x;
    }
}
