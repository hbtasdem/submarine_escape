package maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CoralManager {
    private List<CoralObstacle> obstacles = new ArrayList<>();
    private Random random = new Random();
    private float spawnTimer = 0;
    private final float SPAWN_INTERVAL = 2f;

    public void update(float deltaTime, float mazeHeight) {
        spawnTimer += deltaTime;

        // if (spawnTimer >= SPAWN_INTERVAL) {
        // spawnTimer = 0;
        // float height = 0.1f + random.nextFloat() * 0.2f; // 0.1 - 0.3
        // float width = 0.05f + random.nextFloat() * 0.1f; // 0.05 - 0.15
        // float y = random.nextFloat() * (mazeHeight - height); // Stay within maze
        // float speed = 0.5f + random.nextFloat() * 1.0f;

        // obstacles.add(new CoralObstacle(1.2f, y, width, height, speed));
        // }
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            float height = 0.15f + random.nextFloat() * 0.25f; // between 0.15 and 0.4
            float width = 0.05f + random.nextFloat() * 0.1f; // between 0.05 and 0.15
            float y = random.nextFloat() * (mazeHeight - height);
            float speed = 0.5f + random.nextFloat() * 1.0f;

            obstacles.add(new CoralObstacle(1.2f, y, width, height, speed));
        }

        // Move and remove off-screen corals
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
