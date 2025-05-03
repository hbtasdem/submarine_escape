package maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import graphics.Texture;
import maze.ProceduralSettings;

public class MazeGenerator {

    private List<MazeSegment> segments;
    private Texture texture;

    private float spawnDistance = 200.0f;
    private float despawnDistance = 100.0f;
    private float currentX = 0.0f;

    private Random random = new Random();

    public MazeGenerator() {
        segments = new ArrayList<>();
    }

    public void init() {
        texture = new Texture("res/maze.png");

        for (int i = 0; i < 5; i++) {
            addSegment();
        }
    }

    public void update(float speed, float offsetX) {
        for (MazeSegment segment : segments) {
            segment.update(speed);
        }

        // removes old segments
        Iterator<MazeSegment> it = segments.iterator();
        while (it.hasNext()) {
            MazeSegment segment = it.next();
            if (segment.getX() - offsetX < -despawnDistance) {
                it.remove();
            }
        }

        // adds new segments if needed
        while (currentX - offsetX < spawnDistance) {
            addSegment();
        }
    }

    private void addSegment() {
        float topY = ProceduralSettings.MAZE_TOP_HEIGHT_MIN +
                random.nextFloat() * (ProceduralSettings.MAZE_TOP_HEIGHT_MAX - ProceduralSettings.MAZE_TOP_HEIGHT_MIN);
        float bottomY = ProceduralSettings.MAZE_BOTTOM_HEIGHT_MIN +
                random.nextFloat()
                        * (ProceduralSettings.MAZE_BOTTOM_HEIGHT_MAX - ProceduralSettings.MAZE_BOTTOM_HEIGHT_MIN);

        MazeSegment segment = new MazeSegment(currentX, topY, bottomY, texture);
        segments.add(segment);
        currentX += MazeSegment.WIDTH;
    }

    public float getLatestGapHeight() {
        if (segments.isEmpty())
            return 0;

        MazeSegment last = segments.get(segments.size() - 1);
        return 2.0f - (last.topY + last.bottomY); // 2.0f is total vertical space
    }

    public void render() {
        for (MazeSegment segment : segments) {
            segment.render();
        }
    }

    public List<MazeSegment> getSegments() {
        return segments;
    }
}
