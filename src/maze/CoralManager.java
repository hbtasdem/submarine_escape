package maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import graphics.Texture;

public class CoralManager {

    private List<CoralObstacle> obstacles = new ArrayList<>(); // record currently active coral/trash obstacles
    private Random random = new Random();
    private float spawnTimer = 0;

    private static final float SPAWN_INTERVAL = 1.4f; // time between spawns
    private static final float MIN_SAFE_GAP = 0.6f; // min gap height needed to spawn obstacles
    private static final int MAX_OBSTACLES = 6;

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

            float safeMargin = 0.05f;
            float minY = -1.0f + (1.0f - mazeGapHeight) + safeMargin;
            float maxY = 1.0f - mazeGapHeight - height - safeMargin;
            // float mazeHeight = 0.3f;

            if (maxY <= minY)
                return;

            // safe y range for obstacle creation (randomize obstcale height)
            float gapStartY = -mazeGapHeight / 2f;
            float yRange = mazeGapHeight - height;

            if (yRange <= 0f)
                return; // don't spawn if obstacle can't fit
            float y = gapStartY + random.nextFloat() * yRange;

            float speed = 0.4f + random.nextFloat() * 0.2f; // Fast enough to be visible and challenging

            // this diva line
            // int type = random.nextFloat() < ProceduralSettings.getCoralTrashRatio() ? 1 :
            // 0;

            float threshold = ProceduralSettings.getCoralTrashRatio();
            int type = (random.nextInt(10) < threshold * 10) ? 1 : 0;
            Texture tex = random.nextBoolean() ? coralTexture : trashTexture;
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

    public int getCollisionType(float subX, float subY, float subW, float subH) {
        for (CoralObstacle coral : obstacles) {
            if (coral.collidesWith(subX, subY, subW, subH)) {
                return coral.getType(); // 0 = coral, 1 = trash
            }
        }
        return -1; // no collision
    }
}
