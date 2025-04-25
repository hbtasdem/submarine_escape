package maze;

import static org.lwjgl.opengl.GL11.*;

public class CoralObstacle {
    private float x, y, width, height, speed;

    public CoralObstacle(float x, float y, float width, float height, float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public void update(float deltaTime) {
        x -= speed * deltaTime;
    }

    public void render() {
        glColor3f(0.9f, 0.4f, 0.6f); // Soft coral pink
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public boolean collidesWith(float subX, float subY, float subW, float subH) {
        return x < subX + subW && x + width > subX &&
                y < subY + subH && y + height > subY;
    }
}
