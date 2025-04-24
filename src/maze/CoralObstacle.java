package maze;

import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.GL11;

public class CoralObstacle {
    private float x, y;
    private static final float WIDTH = 0.05f; // adjust as needed
    private static final float HEIGHT = 0.05f;
    private static final float SPEED = 0.02f;

    public CoralObstacle(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(float speed) {
        x -= speed * 1.2f;
    }

    public void render() {
        // Draw the coral here - placeholder
        // Example: Renderer.drawRectangle(x, y, WIDTH, HEIGHT, Color.PINK);
        glColor3f(1.0f, 0.5f, 0.6f); // Dark gray
        glBegin(GL11.GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + WIDTH, y);
        glVertex2f(x + WIDTH, y + HEIGHT);
        glVertex2f(x, y + HEIGHT);
        glEnd();
    }

    public boolean isOffScreen() {
        return x + WIDTH < 0.0f;
    }

    public boolean collidesWith(Player player) {
        // Add proper AABB collision logic based on player dimensions
        float px = player.getX();
        float py = player.getY();
        float pWidth = player.getWidth();
        float pHeight = player.getHeight();

        return (x);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
