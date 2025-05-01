// package maze;

// import java.util.ArrayList;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Random;

// import graphics.Texture;

// public class CoralManager {
//     private List<CoralObstacle> obstacles = new ArrayList<>();
//     private Random random = new Random();
//     private float spawnTimer = 0;

//     private static final float SPAWN_INTERVAL = 2f;
//     private static final int MAX_OBSTACLES = 3;

//     private Texture coralTexture;
//     private Texture trashTexture;

//     public CoralManager() {
//         coralTexture = new Texture("res/coral.png");
//         trashTexture = new Texture("res/trash.png");
//     }

//     public void update(float deltaTime, float mazeHeight, float scale) {
//         spawnTimer += deltaTime;

//         if (obstacles.size() < MAX_OBSTACLES && spawnTimer >= SPAWN_INTERVAL) {
//             spawnTimer = 0;

//             if (mazeHeight >= ProceduralSettings.MAZE_HEIGHT_THRESHOLD_FOR_SPAWN) {
//                 float baseHeight = 0.15f + random.nextFloat() * 0.2f;
//                 float baseWidth = 0.05f + random.nextFloat() * 0.1f;

//                 float height = baseHeight * scale;
//                 float width = baseWidth * scale;

//                 float gapStartY = (1.0f - mazeHeight) / 2.0f;
//                 float y = gapStartY + random.nextFloat() * (mazeHeight - height); // spawn inside gap

//                 float speed = 0.01f + random.nextFloat() * 0.1f;

//                 int type = random.nextFloat() < ProceduralSettings.getCoralTrashRatio() ? 1 : 0;
//                 Texture tex = (type == 0) ? coralTexture : trashTexture;

//                 obstacles.add(new CoralObstacle(1.2f, y, width, height, speed, type, tex));
//             }
//         }

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

public class CoralManager {
    private List<CoralObstacle> obstacles = new ArrayList<>();
    private Random random = new Random();
    private float spawnTimer = 0;

    private static final float SPAWN_INTERVAL = 2f;
    private static final float MIN_SAFE_GAP = 0.6f; // Don't spawn if maze too tight
    private static final int MAX_OBSTACLES = 3;

    private Texture coralTexture;
    private Texture trashTexture;

    public CoralManager() {
        coralTexture = new Texture("res/coral.png");
        trashTexture = new Texture("res/trash.png");
    }

    public void update(float deltaTime, float mazeGapHeight, float scale) {
        spawnTimer += deltaTime;

        if (mazeGapHeight < MIN_SAFE_GAP)
            return;

        if (spawnTimer >= SPAWN_INTERVAL && obstacles.size() < MAX_OBSTACLES) {
            spawnTimer = 0;

            float height = (0.1f + random.nextFloat() * 0.2f) * scale;
            float width = (0.05f + random.nextFloat() * 0.1f) * scale;

            // Ensure obstacle stays within central gap
            float safeMargin = 0.05f;
            float minY = -1.0f + (1.0f - mazeGapHeight) + safeMargin;
            float maxY = 1.0f - mazeGapHeight - height - safeMargin;
            float mazeHeight = 0.3f;

            if (maxY <= minY)
                return; // skip if there's no room

            float y = random.nextFloat() * (mazeHeight - height);

            float speed = 0.02f + random.nextFloat() * 0.03f;

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
