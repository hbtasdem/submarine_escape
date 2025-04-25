import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import maze.CoralManager;
import maze.MazeGenerator;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*; //useful for clean shutdown -> avoid memory leaks when ur closing the game window
import static org.lwjgl.glfw.GLFW.*; //creates windows, input handling, context, exiting etc
import static org.lwjgl.opengl.GL11.*; //color functions, also glClear clears the screen every frame
import static org.lwjgl.system.MemoryStack.*; //stack based mem, useful for performance
import static org.lwjgl.system.MemoryUtil.*; //memory allocation helper

public class Main {

    private long window;

    private int width = 1280;
    private int height = 720;
    private String title = "Submarine Escape";

    float deltaTime = 1.0f / 60.0f;
    float mazeHeight = 0.5f;

    private float submarineY = 0.0f; // Submarine's vertical position
    private float submarineX = -0.8f; // starting X position

    float submarineWidth = 0.1f; // Width of the submarine
    float submarineHeight = 0.1f;

    private float speed = 0.02f; // Speed of movement
    float scale = 1.0f;
    float offsetX = 0f;

    private MazeGenerator maze;
    private CoralManager coralManager;

    public void run() {

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Center the window
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        maze = new MazeGenerator();
        coralManager = new CoralManager(); // Fixed CoralManager instantiation

        glfwMakeContextCurrent(window); // Make the OpenGL context current
        GL.createCapabilities(); // Initialize OpenGL capabilities
        glfwSwapInterval(1);
        glfwShowWindow(window);
        glViewport(0, 0, width, height); // Set viewport size to window size

        // Add framebuffer size callback to adjust OpenGL viewport on resize
        glfwSetFramebufferSizeCallback(window, (window, newWidth, newHeight) -> {
            glViewport(0, 0, newWidth, newHeight);
        });
    }

    private void loop() {
        glClearColor(0f, 0.5f, 1f, 1f); // set the color to ocean blue

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear the screen

            // Handle keyboard input for vertical movement
            if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
                submarineY += speed; // Move up
            if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
                submarineY -= speed; // Move down
            if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS)
                submarineX -= speed; // Move left
            if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS)
                submarineX += speed; // Move right

            // Limit vertical movement within window height range
            submarineX = Math.max(-1.0f, Math.min(1.0f, submarineX)); // Clamp horizontally
            submarineY = Math.max(-1.0f, Math.min(1.0f, submarineY)); // Clamp value

            renderSubmarine(submarineX, submarineY);
            renderMaze(scale);

            // Update and render the coral obstacles
            coralManager.update(deltaTime, mazeHeight); // Pass both deltaTime and mazeHeight
            coralManager.render(); // Render the coral obstacles

            // Check for collisions between the submarine and coral obstacles
            if (coralManager.checkCollisions(submarineX, submarineY, submarineWidth, submarineHeight)) {
                System.out.println("Collision detected! Game Over.");
                // Handle game over logic (you can stop the game, reduce health, etc.)
            }

            glfwSwapBuffers(window); // Swap buffers for next frame
            glfwPollEvents(); // Handle events (keyboard, mouse, etc.)
        }
    }

    private void renderSubmarine(float x, float y) {
        // TO DO: Render a simple rectangle as a placeholder for the submarine
        glPushMatrix();
        glTranslatef(0f, y, 0f); // Move submarine to the current vertical position

        glBegin(GL_QUADS); // Draw rectangle
        glColor3f(1f, 0f, 0f); // Red color for the submarine
        glVertex2f(-0.05f, 0.05f); // Top left
        glVertex2f(0.05f, 0.05f); // Top right
        glVertex2f(0.05f, -0.05f); // Bottom right
        glVertex2f(-0.05f, -0.05f); // Bottom left
        glEnd();

        glPopMatrix();
    }

    private void renderMaze(float scale) {
        glPushMatrix();
        glScalef(scale, scale, 1.0f); // Scale down the maze

        maze.update(0.01f, offsetX); // move forward IMP
        offsetX -= 0.002f;
        glTranslatef(offsetX, 0f, 0f); // you could animate offsetX leftwards for movement
        maze.render(); // draw it

        glPopMatrix();
    }

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception e) {
            e.printStackTrace(); // See what's going wrong
        }
    }
}
