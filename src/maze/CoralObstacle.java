
package maze;

import static org.lwjgl.opengl.GL11.*;
import graphics.Texture;

public class CoralObstacle {
    private float x, y, width, height, speed;
    private int type; // 0 = coral, 1 = trash
    private Texture texture;

    private static Texture coralTexture = new Texture("res/coral.png");
    private static Texture trashTexture = new Texture("res/trash.png");

    // ✅ NEW constructor for procedural generation
    public CoralObstacle(Texture texture, float height, float scale) {
        this.texture = texture;
        this.height = height;
        this.width = 0.1f * scale; // or scale width however you want
        this.speed = 0.5f;

        this.x = 1.2f; // start offscreen to the right
        this.y = (float) (Math.random() * (1.5f - height)); // randomized vertical position

        this.type = texture == coralTexture ? 0 : 1;
    }

    // 🟡 Existing detailed constructor (unchanged)
    public CoralObstacle(float x, float y, float width, float height, float speed, int type, Texture texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.type = type;
        this.texture = texture;
    }

    public void update(float deltaTime) {
        x -= speed * deltaTime;
    }

    public void render() {
        Texture textureToUse = (type == 0) ? coralTexture : trashTexture;
        textureToUse.bind();

        glColor3f(1f, 1f, 1f);

        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(x, y);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y + height);
        glTexCoord2f(0, 0);
        glVertex2f(x, y + height);
        glEnd();

        textureToUse.unbind();
    }

    public boolean isOffScreen() {
        return x + width < -1.1f;
    }

    // public boolean collidesWith(float subX, float subY, float subW, float subH) {
    // float subRight = subX + subW / 2;

    // return subRight >= x && subRight <= x + width && // Right edge touches coral
    // subY + subH / 2 > y && subY - subH / 2 < y + height; // Vertical overlap
    // }
    public boolean collidesWith(float subX, float subY, float subW, float subH) {
        return subX - subW / 2 < x + width &&
                subX + subW / 2 > x &&
                subY - subH / 2 < y + height &&
                subY + subH / 2 > y;
    }

}
