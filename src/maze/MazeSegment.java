package maze;

import math.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class MazeSegment {

    public float x; // X position (scrolling)

    // obstacle sizes
    public float topY;
    public float bottomY;
    public static final float WIDTH = 0.5f; // Width of the maze segment

    public MazeSegment(float x, float topY, float bottomY) {
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
    }

    public void update(float speed) {
        x -= speed; // Move segment to the left (scrolling effect)
    }

    public boolean isOffScreen() {
        return x + WIDTH < -1.0f; // Off screen to the left
    }

    public void render() {
        // Floor
        glColor3f(0.2f, 0.2f, 0.2f); // Dark gray
        glBegin(GL_QUADS);
        glVertex2f(x, -1.0f);
        glVertex2f(x + WIDTH, -1.0f);
        glVertex2f(x + WIDTH, -1.0f + bottomY);
        glVertex2f(x, -1.0f + bottomY);
        glEnd();

        // Ceiling
        glColor3f(0.2f, 0.2f, 0.2f); // Dark gray
        glBegin(GL_QUADS);
        glVertex2f(x, 1.0f);
        glVertex2f(x + WIDTH, 1.0f);
        glVertex2f(x + WIDTH, 1.0f - topY);
        glVertex2f(x, 1.0f - topY);
        glEnd();
    }

    public float getX() {
        return x;
    }
}
