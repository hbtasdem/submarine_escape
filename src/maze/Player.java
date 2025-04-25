package maze;

public class Player {
    private float x, y;
    private static final float WIDTH = 0.1f; // Assuming player width
    private static final float HEIGHT = 0.1f; // Assuming player height

    public Player(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return WIDTH;
    }

    public float getHeight() {
        return HEIGHT;
    }
}
