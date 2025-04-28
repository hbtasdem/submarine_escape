package maze;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import graphics.Texture;

public class MazeGenerator {

    private List<MazeSegment> segments;

    private static Texture mazeTexture;

    // change these to change the total lenght of the maze
    private float spawnDistance = 100.0f; // How far ahead segments are generated
    private float despawnDistance = 100.0f; // How far behind segments are removed

    private Random random;

    private float currentX = 0.0f; // Tracks the front of the maze
    private static final float SEGMENT_WIDTH = MazeSegment.WIDTH;

    // public MazeGenerator() {
    // segments = new ArrayList<>();
    // random = new Random();
    // mazeTexture = new Texture("res/maze.png");

    // // Initial segments
    // for (int i = 0; i < 5; i++) {
    // addSegment();
    // }
    // }

    public MazeGenerator() {
        segments = new ArrayList<>();
        random = new Random();
    }

    // New method to load texture AFTER OpenGL context is ready
    public void init() {
        mazeTexture = new Texture("res/maze.png");

        for (int i = 0; i < 5; i++) {
            addSegment();
        }
    }

    public void update(float speed, float offsetX) {
        for (MazeSegment segment : segments) {
            segment.update(speed); // This likely just shifts left
        }

        // Despawn segments far behind the screen
        Iterator<MazeSegment> it = segments.iterator();
        while (it.hasNext()) {
            MazeSegment segment = it.next();
            if (segment.getX() - offsetX < -despawnDistance) {
                it.remove();
            }
        }

        // Spawn new segments ahead of visible screen
        while (currentX - offsetX < spawnDistance) {
            addSegment();
        }
    }

    private void addSegment() {

        float topY = random.nextFloat() * 0.3f + 0.2f; // CHANGED: randomized height (20%–50%)
        float bottomY = random.nextFloat() * 0.3f + 0.2f;
        MazeSegment segment = new MazeSegment(currentX, topY, bottomY, mazeTexture); // CHANGED
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
