import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import maze.CoralManager;
import maze.MazeGenerator;
import maze.ProceduralSettings;

import java.nio.*;
import graphics.Texture;

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

    float coralSize = 1.0f; // Uniform scale
    float coralSpawnInterval = 2.0f; // Seconds

    float submarineWidth = 0.1f; // Width of the submarine
    float submarineHeight = 0.1f;

    private float speed = 0.02f; // Speed of movement
    float scale = 1.0f;
    float offsetX = 0f;

    private float inputCooldown = 0f;
    private long lastFrameTime = System.nanoTime();

    private Texture submarineTexture;
    private Texture backgroundTexture;
    private float bgOffset = 0f;

    private MazeGenerator maze;
    private CoralManager coralManager;

    private boolean gameOver = false;

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

        glfwMakeContextCurrent(window); // Make the OpenGL context current
        GL.createCapabilities(); // Initialize OpenGL capabilities

        maze = new MazeGenerator();
        maze.init();
        coralManager = new CoralManager(); // Fixed CoralManager instantiation
        submarineTexture = new Texture("res/submarine.png"); // texture inst
        backgroundTexture = new Texture("res/ocean.png");

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSwapInterval(1);
        glfwShowWindow(window);
        glViewport(0, 0, width, height); // Set viewport size to window size

        // Add framebuffer size callback to adjust OpenGL viewport on resize
        glfwSetFramebufferSizeCallback(window, (window, newWidth, newHeight) -> {
            glViewport(0, 0, newWidth, newHeight);
        });
    }

    private void loop() {

        long now = System.nanoTime();
        deltaTime = (now - lastFrameTime) / 1_000_000_000.0f;
        lastFrameTime = now;
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

            float cooldownTime = 0.2f; // 200ms delay
            inputCooldown -= deltaTime;

            if (inputCooldown <= 0f) {
                if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS) {
                    float current = ProceduralSettings.getCoralTrashRatio();
                    ProceduralSettings.setCoralTrashRatio(Math.min(1.0f, current + 0.05f));
                    System.out.println("Trash ratio increased to: " + ProceduralSettings.getCoralTrashRatio());
                }
                if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS) {
                    float current = ProceduralSettings.getCoralTrashRatio();
                    ProceduralSettings.setCoralTrashRatio(Math.max(0.0f, current - 0.05f));
                    System.out.println("Trash ratio decreased to: " + ProceduralSettings.getCoralTrashRatio());
                }

            }

            // Limit vertical movement within window height range
            submarineX = Math.max(-1.0f, Math.min(1.0f, submarineX)); // Clamp horizontally
            submarineY = Math.max(-1.0f, Math.min(1.0f, submarineY)); // Clamp value

            bgOffset -= 0.0005f;

            renderBackground();
            renderSubmarine(submarineX, submarineY);
            renderMaze(scale);
            mazeHeight = maze.getLatestGapHeight();

            // Update and render the coral obstacles
            coralManager.update(deltaTime, mazeHeight, scale); // Pass both deltaTime and
            // mazeHeight
            // coralManager.update(deltaTime, mazeHeight, coralSpawnInterval, coralSize);
            coralManager.render(); // Render the coral obstacles
            // coralManager.render(offsetX);

            // Check for collisions between the submarine and coral obstacles
            if (coralManager.checkCollisions(submarineX, submarineY, submarineWidth, submarineHeight)) {
                System.out.println("Collision detected! Game Over.");
                // Handle game over logic (you can stop the game, reduce health, etc.)
            }

            if (!gameOver && coralManager.checkCollisions(submarineX, submarineY, submarineWidth, submarineHeight)) {
                gameOver = true;
                System.out.println("Collision detected! Game Over.");
            }
            if (!gameOver) {
                if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
                    submarineY += speed;
                if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
                    submarineY -= speed;
            }

            glfwSwapBuffers(window); // Swap buffers for next frame
            glfwPollEvents(); // Handle events (keyboard, mouse, etc.)
        }
    }

    private void renderBackground() {
        backgroundTexture.bind();

        glColor3f(1f, 1f, 1f);
        glBegin(GL_QUADS);
        glTexCoord2f(bgOffset, 0);
        glVertex2f(-1f, -1f);
        glTexCoord2f(bgOffset + 1, 0);
        glVertex2f(1f, -1f);
        glTexCoord2f(bgOffset + 1, 1);
        glVertex2f(1f, 1f);
        glTexCoord2f(bgOffset, 1);
        glVertex2f(-1f, 1f);
        glEnd();

        backgroundTexture.unbind();
    }

    private void renderSubmarine(float x, float y) {
        glPushMatrix();
        glTranslatef(0f, y, 0f);

        float scale = 4.0f; // Scale factor to enlarge the submarine
        glScalef(scale, scale, 1f);

        submarineTexture.bind();

        glColor3f(1f, 1f, 1f);
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(-submarineWidth / 2, -submarineHeight / 2);
        glTexCoord2f(1, 1);
        glVertex2f(submarineWidth / 2, -submarineHeight / 2);
        glTexCoord2f(1, 0);
        glVertex2f(submarineWidth / 2, submarineHeight / 2);
        glTexCoord2f(0, 0);
        glVertex2f(-submarineWidth / 2, submarineHeight / 2);
        glEnd();

        submarineTexture.unbind();

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

    // private boolean collision() {
    // for (int i = 0; i < 5 * 2; i++) {
    // float bx = -bgOffset * 0.05f;
    // float by = submarineY;
    // }

    // }

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception e) {
            e.printStackTrace(); // See what's going wrong
        }
    }
}