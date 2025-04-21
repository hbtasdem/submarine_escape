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

    private float currentZ = 0.0f; // Tracks the front of the maze
    private Random random;

    public MazeGenerator() {
        segments = new ArrayList<>();
        random = new Random();

        // Initial segments
        for (int i = 0; i < 5; i++) {
            addSegment();
        }
    }

    public void update(float playerZ) {
        // Add new segments if player is approaching the end
        while (currentZ - playerZ < spawnDistance) {
            addSegment();
        }

        // Remove segments far behind
        Iterator<MazeSegment> it = segments.iterator();
        while (it.hasNext()) {
            MazeSegment segment = it.next();
            if (playerZ - segment.getPosition().z > despawnDistance) {
                it.remove();
            }
        }
    }

    private void addSegment() {
        // Optional: randomize width/height later
        MazeSegment segment = new MazeSegment(currentZ, 10.0f, 10.0f);
        segments.add(segment);
        currentZ += segmentLength;
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
