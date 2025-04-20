import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

//import org.lwjgl.opengl.ContextAttribs.*;

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
    private float submarineY = 0.0f; // Submarine's vertical position
    private float speed = 0.02f; // Speed of movement

    public void run() {

        // example usage of shader for now //int shader =
        // shaderUtils.load("shaders/shader.vert", "shaders/shader.frag");
        init();
        loop();

        String osName = System.getProperty("os.name");

        /*
         * ContextAttribs attribs = new ContextAttribs(3, 2);
         * if (osName.contains("Mac"))
         * context = new ContextAttribs(3, 2);
         */

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
        glfwDefaultWindowHints(); // clear the default settings
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // set window invisible to setup position, context wo showing
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL); // window is a long variable storing the memory
                                                                     // address (ID) of game window
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Center the window
        // using try is important to ensure that the memory leak doesn't happen bc it
        // returns it after the block ends
        try (MemoryStack stack = stackPush()) { // allocates native memory temporarily
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight); // fills the buffers with current width/height of the window
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor()); // gets the monitor resolution

            // moves the window to the center of the screen
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        // Context is the state machine openGl uses to draw things: buffers, shaders,
        // textures etc
        // You can only create and use the OpenGL context on the main thread.
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1); // game wait 1s refresh before swapping buffers so new frames doesn't get drawn
                             // during a refresh
        // Make the window visible
        glfwShowWindow(window); // undo the invisible mode from glfwWindowHint
    }

    private void loop() {
        GL.createCapabilities(); // init OpenGL

        // Set clear color to simulate the ocean
        glClearColor(0f, 0.5f, 1f, 1f); // ocean blue

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the frame buffer

            // Handle keyboard input for vertical movement
            if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS) {
                submarineY += speed; // Move up
            }

            if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) {
                submarineY -= speed; // Move down
            }

            // Limit vertical movement within window height range
            submarineY = Math.max(-1.0f, Math.min(1.0f, submarineY)); // Clamping the value

            // Render the submarine (placeholder for now)
            renderSubmarine();

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents(); // processes all pending events (keyboard, mouse etc) to react
        }
    }

    private void renderSubmarine() {
        // TO DO: Render a simple rectangle as a placeholder for the submarine
        glPushMatrix();
        glTranslatef(0f, submarineY, 0f); // Move submarine to the current vertical position

        glBegin(GL_QUADS); // Draw rectangle
        glColor3f(1f, 0f, 0f); // Red color for the submarine
        glVertex2f(-0.05f, 0.05f); // Top left
        glVertex2f(0.05f, 0.05f); // Top right
        glVertex2f(0.05f, -0.05f); // Bottom right
        glVertex2f(-0.05f, -0.05f); // Bottom left
        glEnd();

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
