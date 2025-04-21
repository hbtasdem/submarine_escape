package maze;

import math.Vector3f;

public class MazeSegment {

    private Vector3f position;
    public float x; // X position (scrolling)
    public float topY; // Ceiling height
    public float bottomY; // Floor height
    public static final float WIDTH = 1.0f; // Width of the segment

    public Vector3f getPosition() {
        return position;
    }

    public MazeSegment(float x, float topY, float bottomY) {
        this.x = x;
        this.topY = topY;
        this.bottomY = bottomY;
    }

    public void update(float speed) {
        x -= speed; // Move segment to the left
    }

    public boolean isOffScreen() {
        return x + WIDTH < -1.0f; // Off left side of screen (adjust to camera)
    }
}
