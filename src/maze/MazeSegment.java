package maze;

import math.Vector3f;

import static org.lwjgl.opengl.GL11.*;

public class MazeSegment {

    public float x; // X position (scrolling)
    public float topY; // Ceiling height
    public float bottomY; // Floor height
    public static final float WIDTH = 1.0f; // Width of the segment
    private Vector3f position;

    public Vector3f getPosition() {
        return new Vector3f(x, 0, 0); // Fake Z value since we use x scrolling
    }

    public MazeSegment(float x, float topY, float bottomY) {
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
        this.position = new Vector3f(x, 0, 0); // not strictly necessary but safe
    }

    public void update(float speed) {
        x -= speed; // Move segment to the left
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
        glVertex2f(x + WIDTH, -1.0f + bottomY / 10.0f);
        glVertex2f(x, -1.0f + bottomY / 10.0f);
        glEnd();

        // Ceiling
        glColor3f(0.2f, 0.2f, 0.2f); // Dark gray
        glBegin(GL_QUADS);
        glVertex2f(x, 1.0f);
        glVertex2f(x + WIDTH, 1.0f);
        glVertex2f(x + WIDTH, 1.0f - topY / 10.0f);
        glVertex2f(x, 1.0f - topY / 10.0f);
        glEnd();
    }
}
