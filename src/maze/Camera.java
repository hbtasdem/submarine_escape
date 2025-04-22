package maze;

import org.lwjgl.opengl.GL11;

public class Camera {
    private float offsetX = 0.0f; // X position of the camera
    private float scale = 1.0f; // Zoom factor for the camera

    public Camera() {
        // Don't call OpenGL functions here.
    }

    // Initialize camera settings (called after context is created)
    public void init() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(-1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f); // Default camera setup
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
    }

    // Update the camera's X offset (scrolling the world)
    public void updateCamera(float delta) {
        offsetX -= delta; // Move the camera to the left by a small amount
    }

    // Set the camera's zoom level (scaling)
    public void setScale(float scale) {
        this.scale = scale;
    }

    // Apply the camera's transformations (translation and scaling)
    public void applyTransformations() {
        GL11.glPushMatrix();
        GL11.glTranslatef(offsetX, 0, 0); // Apply X offset (scrolling)
        GL11.glScalef(scale, scale, 1.0f); // Apply zoom scaling
    }

    // Reset the transformations after rendering
    public void resetTransformations() {
        GL11.glPopMatrix();
    }
}
