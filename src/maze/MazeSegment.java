package maze;

import graphics.Texture;
import static org.lwjgl.opengl.GL11.*;

public class MazeSegment {

    public float x; // X position (for scrolling purposes)
    public float topY; // ceiling height
    public float bottomY; // floor height

    public static final float WIDTH = 0.5f; // width of the maze segment

    private Texture texture;

    // constructor with texture passed in
    public MazeSegment(float x, float topY, float bottomY, Texture texture) {
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
        this.texture = texture;
    }

    public void update(float speed) {
        x -= speed; // scrolling to left
    }

    public boolean isOffScreen() {
        return x + WIDTH < -1.0f;
    }

    public boolean collidesWith(float subX, float subY, float subW, float subH) {
        // submarine bbox
        float subLeft = subX - subW / 2;
        float subRight = subX + subW / 2;
        float subTop = subY + subH / 2;
        float subBottom = subY - subH / 2;

        // maze bbox
        float segLeft = x;
        float segRight = x + WIDTH;

        float floorTop = -1.0f + bottomY; // top coord of the floor
        float ceilingBottom = 1.0f - topY; // bottom coord of the top

        // if submarine overlaps horizontally with maze segment
        if (subRight > segLeft && subLeft < segRight) {
            // if submarine overlaps with floor
            if (subBottom < floorTop)
                return true;

            // if submarine overlaps with ceiling
            if (subTop > ceilingBottom)
                return true;
        }

        return false;
    }

    public void render() {
        if (texture == null)
            return; // safety check

        texture.bind();

        glColor3f(1f, 1f, 1f); // use full tex color

        // === Draw Floor ===
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

        // === Draw Ceil ===
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
