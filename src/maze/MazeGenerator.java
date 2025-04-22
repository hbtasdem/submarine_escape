package maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class MazeGenerator {

    private List<MazeSegment> segments;
    private float segmentLength = 20.0f; // Distance between segments
    private float spawnDistance = 100.0f; // How far ahead segments are generated
    private float despawnDistance = 50.0f; // How far behind segments are removed
    private Random random;

    private float currentX = 0.0f; // Tracks the front of the maze
    private static final float SEGMENT_WIDTH = MazeSegment.WIDTH;

    public MazeGenerator() {
        segments = new ArrayList<>();
        random = new Random();

        // Initial segments
        for (int i = 0; i < 5; i++) {
            addSegment();
        }
    }

    public void update(float speed) {
        // Add new segments if player is approaching the end
        for (MazeSegment segment : segments) {
            segment.update(speed);
        }

        // Remove segments far behind
        Iterator<MazeSegment> it = segments.iterator();
        while (it.hasNext()) {
            MazeSegment segment = it.next();
            if (segment.isOffScreen()) {
                it.remove();
            }
        }

        while (currentX < 2.0f) { // CHANGED: Keep generating to the right
            addSegment();
        }
    }

    private void addSegment() {

        float topY = random.nextFloat() * 0.3f + 0.2f; // CHANGED: randomized height (20%–50%)
        float bottomY = random.nextFloat() * 0.3f + 0.2f;
        MazeSegment segment = new MazeSegment(currentX, topY, bottomY); // CHANGED
        segments.add(segment);
        currentX += MazeSegment.WIDTH;
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
