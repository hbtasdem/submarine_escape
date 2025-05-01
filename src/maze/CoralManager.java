// package maze;

// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Random;

// public class CoralManager {
//     private List<CoralObstacle> obstacles = new ArrayList<>();
//     private Random random = new Random();
//     private float spawnTimer = 0;
//     private final float SPAWN_INTERVAL = 2f;

//     public void update(float deltaTime, float mazeHeight) {
//         spawnTimer += deltaTime;

//         if (spawnTimer >= SPAWN_INTERVAL) {
//             spawnTimer = 0;
//             float height = 0.15f + random.nextFloat() * 0.25f; // between 0.15 and 0.4
//             float width = 0.05f + random.nextFloat() * 0.1f; // between 0.05 and 0.15
//             float y = random.nextFloat() * (mazeHeight - height);
//             float speed = 0.5f + random.nextFloat() * 1.0f;
//             int type = 0;

//             obstacles.add(new CoralObstacle(1.2f, y, width, height, speed, type));
//         }

//         // Move and remove off-screen corals
//         Iterator<CoralObstacle> it = obstacles.iterator();
//         while (it.hasNext()) {
//             CoralObstacle coral = it.next();
//             coral.update(deltaTime);
//             if (coral.isOffScreen()) {
//                 it.remove();
//             }
//         }
//     }

//     public void render() {
//         for (CoralObstacle coral : obstacles) {
//             coral.render();
//         }
//     }

//     public boolean checkCollisions(float subX, float subY, float subW, float subH) {
//         for (CoralObstacle coral : obstacles) {
//             if (coral.collidesWith(subX, subY, subW, subH)) {
//                 return true;
//             }
//         }
//         return false;
//     }
// }

package maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import graphics.Texture;

import static org.lwjgl.opengl.GL11.*;

public class CoralManager {
    private List<CoralObstacle> obstacles = new ArrayList<>();
    private Random random = new Random();
    private float spawnTimer = 0;

    private static final float SPAWN_INTERVAL = 2f;
    private static final int MAX_OBSTACLES = 3;

    private Texture coralTexture;
    private Texture trashTexture;

    public CoralManager() {
        coralTexture = new Texture("res/coral.png");
        trashTexture = new Texture("res/trash.png");
    }

    public void update(float deltaTime, float mazeHeight, float scale) {
        spawnTimer += deltaTime;

        if (obstacles.size() < MAX_OBSTACLES && spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;

            float baseHeight = 0.15f + random.nextFloat() * 0.15f;
            float baseWidth = 0.05f + random.nextFloat() * 0.1f;

            float height = baseHeight * scale;
            float width = baseWidth * scale;

            float maxY = Math.max(0.01f, mazeHeight - height);
            float y = random.nextFloat() * maxY;
            float speed = 0.01f + random.nextFloat() * 0.1f;

            int type = random.nextFloat() < ProceduralSettings.getCoralTrashRatio() ? 1 : 0;
            Texture tex = (type == 0) ? coralTexture : trashTexture;

            obstacles.add(new CoralObstacle(1.2f, y, width, height, speed, type, tex));
        }

        Iterator<CoralObstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            CoralObstacle coral = it.next();
            coral.update(deltaTime);
            if (coral.isOffScreen()) {
                it.remove();
            }
        }
    }

    public void render() {
        for (CoralObstacle coral : obstacles) {
            coral.render();
        }
    }

    public boolean checkCollisions(float subX, float subY, float subW, float subH) {
        for (CoralObstacle coral : obstacles) {
            if (coral.collidesWith(subX, subY, subW, subH)) {
                return true;
            }
        }
        return false;
    }
}
