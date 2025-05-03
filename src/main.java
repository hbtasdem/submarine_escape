
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import maze.CoralManager;
import maze.MazeGenerator;
import maze.MazeSegment;
import maze.ProceduralSettings;

import java.nio.*;

import graphics.Shader;
import graphics.Texture;
import graphics.VertexArray;
import math.Vector3f;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    private long window;

    private int width = 1280;
    private int height = 720;
    private String title = "Submarine Escape";

    float deltaTime = 1.0f / 60.0f;
    float mazeHeight = 0.5f;

    // pos
    private float submarineY = 0.0f;
    private float submarineX = -0.8f;

    // dimensions
    float submarineWidth = 0.09f;
    float submarineHeight = 0.09f;

    private float speed = 0.01f;

    float scale = 1.0f;
    float offsetX = 0f;
    private float bgOffset = 0f;

    private float inputCooldown = 0f;
    private long lastFrameTime = System.nanoTime();

    private Texture submarineTexture;
    private Texture backgroundTexture;

    private MazeGenerator maze;
    private CoralManager coralManager;

    private Vector3f hitColor = new Vector3f(0f, 0f, 0f);
    private float hitIntensity = 0f;

    // logic params
    private boolean gameOver = false;
    boolean mazeCollision = false;
    private boolean coralHit = false;

    public void run() {
        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        Shader.loadAll(); // flash effect

        maze = new MazeGenerator();
        coralManager = new CoralManager();
        submarineTexture = new Texture("res/submarine.png");
        backgroundTexture = new Texture("res/ocean.png");

        maze.init();

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glfwSwapInterval(1);
        glfwShowWindow(window);
        glViewport(0, 0, width, height);

        glfwSetFramebufferSizeCallback(window, (window, newWidth, newHeight) -> {
            glViewport(0, 0, newWidth, newHeight);
        });
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            long now = System.nanoTime();
            deltaTime = (now - lastFrameTime) / 1_000_000_000.0f;
            lastFrameTime = now;

            glClearColor(0f, 0.5f, 1f, 1f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // movement controls
            // if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
            // submarineY += speed;
            // if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
            // submarineY -= speed;

            // obstacle type controls T/C
            float cooldownTime = 0.2f;
            inputCooldown -= deltaTime;
            if (inputCooldown <= 0f) {
                if (glfwGetKey(window, GLFW_KEY_T) == GLFW_PRESS) {
                    float current = ProceduralSettings.getCoralTrashRatio();
                    ProceduralSettings.setCoralTrashRatio(Math.min(1.0f, current + 0.05f));
                    System.out.println("Trash ratio increased to: " + ProceduralSettings.getCoralTrashRatio());
                    inputCooldown = cooldownTime;
                }
                if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS) {
                    float current = ProceduralSettings.getCoralTrashRatio();
                    ProceduralSettings.setCoralTrashRatio(Math.max(0.0f, current - 0.05f));
                    System.out.println("Trash ratio decreased to: " + ProceduralSettings.getCoralTrashRatio());
                    inputCooldown = cooldownTime;
                }
            }

            submarineX = Math.max(-1.0f, Math.min(1.0f, submarineX));
            submarineY = Math.max(-1.0f, Math.min(1.0f, submarineY));

            bgOffset -= 0.0005f; // bg scroll

            mazeHeight = maze.getLatestGapHeight();
            renderBackground();
            renderSubmarine(submarineX, submarineY);
            renderMaze(scale);
            coralManager.update(deltaTime, mazeHeight, scale);
            coralManager.render();

            // fix the difference between the visual submarine and rendered submarine
            float drawScale = 4.0f; // same scale used in glScalef
            float scaledSubW = submarineWidth * drawScale;
            float scaledSubH = submarineHeight * drawScale;

            // maze collision & gray flash effect
            for (MazeSegment segment : maze.getSegments()) {
                if (segment.collidesWith(submarineX, submarineY, scaledSubW, scaledSubH)) {
                    mazeCollision = true;
                    hitColor = new Vector3f(0.6f, 0.6f, 0.6f); // gray
                    hitIntensity = 0.5f;
                    // System.out.println("Maze collision!"); // Debug
                    // gameOver = true; //messes obstacle hit flash up
                    break;
                }
            }

            // hit type detector
            if (!gameOver) {
                int hitType = coralManager.getCollisionType(0f, submarineY, scaledSubW, scaledSubH);
                if (hitType == 0) { // coral
                    hitColor = new Vector3f(1f, 0.5f, 1f); // pink
                    hitIntensity = 0.5f;
                    // System.out.println("Hit coral!"); // Debug
                    // gameOver = true; //messes obstacle hit flash up
                } else if (hitType == 1) { // trash
                    hitColor = new Vector3f(0.3f, 1f, 0.3f); // green
                    hitIntensity = 0.5f;
                    // System.out.println("Hit trash!"); // Debug
                    // gameOver = true; //messes obstacle hit flash up
                }
            }

            // movement controls
            if (!gameOver) {
                if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS)
                    submarineY += speed;
                if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS)
                    submarineY -= speed;
            }

            // makes sure the flash effect fades off
            if (hitIntensity > 0f) {
                Shader.HIT.enable();
                Shader.HIT.setUniform3f("hitColor", hitColor);
                Shader.HIT.setUniform1f("intensity", hitIntensity);

                glBegin(GL_QUADS);
                glVertex2f(-1f, -1f);
                glVertex2f(1f, -1f);
                glVertex2f(1f, 1f);
                glVertex2f(-1f, 1f);
                glEnd();

                Shader.HIT.disable();
                hitIntensity -= deltaTime * 20.0f;
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
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

        glTranslatef(0, y, 0f);
        float scale = 4.0f;
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
        glScalef(scale, scale, 1.0f);
        maze.update(0.01f, offsetX);
        offsetX -= 0.002f;
        glTranslatef(offsetX, 0f, 0f);
        maze.render();
        glPopMatrix();
    }

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}