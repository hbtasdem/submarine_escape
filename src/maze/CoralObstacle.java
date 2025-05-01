// package maze;

// import static org.lwjgl.opengl.GL11.*;
// import java.util.Random;

// public class CoralObstacle {
//     private float x, y, width, height, speed;
//     private int type; // 0 = pink coral, 1 = green trash

//     public CoralObstacle(float x, float y, float width, float height, float speed, int type) {
//         this.x = x;
//         this.y = y;
//         this.width = width;
//         this.height = height;
//         this.speed = speed;
//         this.type = type;
//     }

//     public void update(float deltaTime) {
//         x -= speed * deltaTime;
//     }

//     public void render() {
//         // if (type == 0)
//         // glColor3f(0.9f, 0.4f, 0.6f); // pink coral
//         // else
//         // glColor3f(0.2f, 1.0f, 0.2f); // green trash

//         // glBegin(GL_QUADS);
//         // glVertex2f(x, y);
//         // glVertex2f(x + width, y);
//         // glVertex2f(x + width, y + height);
//         // glVertex2f(x, y + height);
//         // glEnd();

//         if (type == 0)
//             glColor3f(1f, 0f, 0f); // Pink-ish coral
//         else
//             glColor3f(0f, 1f, 0f); // Green trash

//         glBegin(GL_QUADS);
//         glVertex2f(x, y);
//         glVertex2f(x + width, y);
//         glVertex2f(x + width, y + height);
//         glVertex2f(x, y + height);
//         drawBoundingBox();

//         glEnd();

//     }

//     private void drawBoundingBox() {
//         glColor3f(1f, 1f, 1f); // white outline
//         glBegin(GL_LINE_LOOP);
//         glVertex2f(x, y);
//         glVertex2f(x + width, y);
//         glVertex2f(x + width, y + height);
//         glVertex2f(x, y + height);
//         glEnd();
//     }

//     public boolean isOffScreen() {
//         return x + width < 0;
//     }

//     public boolean collidesWith(float subX, float subY, float subW, float subH) {
//         return subX - subW / 2 < x + width &&
//                 subX + subW / 2 > x &&
//                 subY - subH / 2 < y + height &&
//                 subY + subH / 2 > y;
//     }
// }

package maze;

import static org.lwjgl.opengl.GL11.*;
import graphics.Texture;

public class CoralObstacle {
    private float x, y, width, height, speed;
    private int type; // 0 = coral, 1 = trash
    private Texture texture;

    private static Texture coralTexture = new Texture("res/coral.png");
    private static Texture trashTexture = new Texture("res/trash.png");

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
        Texture texture = (type == 0) ? coralTexture : trashTexture;
        texture.bind();

        glColor3f(1f, 1f, 1f); // Show texture without color tint

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

        texture.unbind();
    }

    public boolean isOffScreen() {
        return x + width < -1.1f;
    }

    public boolean collidesWith(float subX, float subY, float subW, float subH) {
        return subX - subW / 2 < x + width &&
                subX + subW / 2 > x &&
                subY - subH / 2 < y + height &&
                subY + subH / 2 > y;
    }
}
