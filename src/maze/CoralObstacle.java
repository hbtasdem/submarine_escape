package maze;

import static org.lwjgl.opengl.GL11.*;
import java.util.Random;

public class CoralObstacle {
    private float x, y, width, height, speed;
    private int shapeType; // 0 = rectangle, 1 = spiky

    private static Random random = new Random();

    public CoralObstacle(float x, float y, float width, float height, float speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.shapeType = random.nextInt(2); // 0 or 1 randomly
    }

    public void update(float deltaTime) {
        x -= speed * deltaTime;
    }

    public void render() {
        glColor3f(0.9f, 0.4f, 0.6f); // Coral color

        if (shapeType == 0) {
            drawRectangle();
        } else if (shapeType == 1) {
            drawSpikyCoral();
        }

        drawBoundingBox();
    }

    private void drawRectangle() {
        glBegin(GL_QUADS);
        glVertex2f(x, y);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y + height);
        glVertex2f(x, y + height);
        glEnd();
    }

    private void drawSpikyCoral() {
        glBegin(GL_TRIANGLE_STRIP);

        float spikeCount = 5 + random.nextInt(5); // 5-9 spikes
        float spikeWidth = width / spikeCount;

        for (int i = 0; i <= spikeCount; i++) {
            float spikeX = x + i * spikeWidth;
            glVertex2f(spikeX, y);

            // Alternate between high and low spike
            if (i % 2 == 0) {
                glVertex2f(spikeX, y + height + 0.02f); // Extra tall spike
            } else {
                glVertex2f(spikeX, y + height * 0.8f); // Shorter spike
            }
        }

        glEnd();
    }

    private void drawBoundingBox() {
        glPushMatrix();
        glTranslatef(x, y, 0f);

        glColor3f(1f, 0f, 0f); // Red bounding box for debug
        glBegin(GL_LINE_LOOP);
        glVertex2f(-width / 2, height / 2);
        glVertex2f(width / 2, height / 2);
        glVertex2f(width / 2, -height / 2);
        glVertex2f(-width / 2, -height / 2);
        glEnd();

        glPopMatrix();
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public boolean collidesWith(float subX, float subY, float subW, float subH) {
        // return x < subX + subW && x + width > subX &&
        // y < subY + subH && y + height > subY;
        return subX - subW / 2 < x + width / 2 &&
                subX + subW / 2 > x - width / 2 &&
                subY - subH / 2 < y + height / 2 &&
                subY + subH / 2 > y - height / 2;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
