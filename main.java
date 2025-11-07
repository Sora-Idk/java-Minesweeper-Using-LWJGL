import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.util.Random;

public class main{
    //Stores the window
    private long window;

    //To make it modular
    private static final int ROWS = 9;
    private static final int COLS = 9;
    private static final int CELL_SIZE = 50;

    //variables to store selection location(since mouse inputs are complicated)
    private int cursorX = 0;
    private int cursorY = 0;

    //booleans to check keypresses
    private boolean fKeyHeld = false;
    private boolean spaceKeyHeld = false;
    private boolean upKeyHeld = false;
    private boolean downKeyHeld = false;
    private boolean leftKeyHeld = false;
    private boolean rightKeyHeld = false;

    //game variables
    private int totalMines = 0;
    private boolean gameOver = false;
    private static final int MINE = 1;
    private static final int FLAG = 2;
    private int[][] grid = new int[ROWS][COLS];
    private boolean[][] revealed = new boolean[ROWS][COLS];
    private boolean[][] flagged = new boolean[ROWS][COLS];

    
    public static void main(String[] args) {
        new main().run();
    }

    public void run() {
        //initializing windows and other stuff
        init();

        //The program is in this loop function till game gets done
        loop();

        //terminates the window
        glfwTerminate();
    }

    private void init() {
        // try catch method
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        window = glfwCreateWindow(COLS * CELL_SIZE, ROWS * CELL_SIZE, "Minesweeper LWJGL", NULL, NULL);

        //checking if window got created, or throw error
        if (window == NULL) throw new RuntimeException("Failed to create GLFW window");

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glClearColor(0f, 0f, 0f, 1f);

        initGrid();
        printMineStats();
    }

    //main loop of the game
    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            handleInput();

            glClear(GL_COLOR_BUFFER_BIT);
            renderGrid();

            glfwSwapBuffers(window);
        }
    }

    private void initGrid() {
        //using to randomize location of mines
        Random random = new Random();

        totalMines = 0;
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                //random.nextDouble() generates number between 0 and 1, so, chance of a block being a mine
                if (random.nextDouble() < 0.15) {
                    grid[i][j] = MINE;
                    totalMines++;
                }
                else{
                    grid[i][j] = 0;
                }
                revealed[i][j] = false;
            }
        }
    }

    //handles keypresses
    private void handleInput() {
        if (gameOver){
            return;
        }

        // Cursor movement - single-tap
        
        //checks if KEY UP(up arrow) is pressed, if pressed cursor is moved up
        if (glfwGetKey(window, GLFW_KEY_UP) == GLFW_PRESS){
            if (!upKeyHeld){
                cursorY = Math.max(0, cursorY - 1); 
                //to stop the cursor from moving multiple boxes when key is held down
                upKeyHeld = true; 
            }
        } 
        else{
            upKeyHeld = false; 
        }

        //same as the above one but for ,down, left and right respectively
        if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS){
            if (!downKeyHeld){
                cursorY = Math.min(ROWS - 1, cursorY + 1); downKeyHeld = true; 
            }
        } 
        else{
            downKeyHeld = false; 
        }

        if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS){
            if (!leftKeyHeld){
                cursorX = Math.max(0, cursorX - 1); 
                leftKeyHeld = true; 
            }
        } 
        else{
            leftKeyHeld = false; 
        }

        if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS){
            if (!rightKeyHeld){
                cursorX = Math.min(COLS - 1, cursorX + 1);
                rightKeyHeld = true; 
            }
        } 
        else{ 
            rightKeyHeld = false; 
        }

        // Flag toggle stores flagged positon in the flagged array(2d array)
        if (glfwGetKey(window, GLFW_KEY_F) == GLFW_PRESS){
            if (!fKeyHeld) {
                fKeyHeld = true;
                if (!revealed[cursorY][cursorX]){
                    flagged[cursorY][cursorX] = !flagged[cursorY][cursorX];
                }

            }
        } else { fKeyHeld = false; }

        // Dig
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS){
            if (!spaceKeyHeld) {
                spaceKeyHeld = true;
                if (!revealed[cursorY][cursorX] && grid[cursorY][cursorX] != FLAG){
                    revealed[cursorY][cursorX] = true;
                    if (grid[cursorY][cursorX] == MINE){
                        System.out.println("\nBOOM! You hit a mine. Game Over!");
                        gameOver = true;
                    }
                    else if (countAdjacentMines(cursorY, cursorX) == 0){
                        floodReveal(cursorY, cursorX);
                    }
                }
            
            }
        } 
        else{ 
            spaceKeyHeld = false; 
        }

        //chcking if the current move/interaction made the player win the game or not
        checkWin();

            
        
    }

    // Flood-fill to auto-reveal empty neighbors similar to flood fill from cg but different restrictions
    private void floodReveal(int row, int col) {
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                int r = row + i, c = col + j;
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS && !revealed[r][c] && grid[r][c] != FLAG){
                    revealed[r][c] = true;
                    if (countAdjacentMines(r, c) == 0){
                        floodReveal(r, c);
                    }
                }
            }
        }
    }

    //does the couting of mines around a revealed tile
    private int countAdjacentMines(int row, int col) {
        int count = 0;
        for (int i = -1; i <= 1; i++){
            for (int j = -1; j <= 1; j++){
                int r = row + i, c = col + j;
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS){
                    if (grid[r][c] == MINE) count++;
                }
            }
        }
        return count;
    }

    //checking if user won, gets called after every interaction like flag, dig, unflag
    private void checkWin() {
        int unrevealedSafe = 0;
        for (int y = 0; y < ROWS; y++){
            for (int x = 0; x < COLS; x++){
                if (!revealed[y][x] && grid[y][x] != MINE){
                    unrevealedSafe++;
                }
            }
        }
        if (unrevealedSafe == 0 && !gameOver) {
            System.out.println("\nCongratulations! You revealed all safe cells! You win!");
            gameOver = true;
        }
    }
    
    private void printMineStats() {
        int flags = 0;
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLS; x++) {
                if (grid[y][x] == FLAG){
                    flags++;
                }
            }
        }
        
        int remaining = totalMines - flags;

        //can also use concat to print this string but i think typespecifiers is cleaner
        System.out.printf("Total Mines: %d |Flags: %d | Remaining: %d%n", totalMines, flags, remaining);
    }

    //Drawing the different game objects
    private void renderGrid(){
        for (int y = 0; y < ROWS; y++){
            for (int x = 0; x < COLS; x++){
                float r = 0.3f, g = 0.3f, b = 0.3f;
                if (revealed[y][x]){
                    if (grid[y][x] == MINE){
                        r = 1f; g = 0f; b = 0f;
                    }
                    else{
                        r = 0.7f; g = 0.7f; b = 0.7f;
                    }
                }
                drawSquare(x, y, r, g, b);

                if (flagged[y][x] && !revealed[y][x]){
                    drawMine(x, y, 0.9f, 0f, 0f);
                }

                if (revealed[y][x] && grid[y][x] != MINE){
                    int n = countAdjacentMines(y, x);
                    if (n > 0){
                        drawDots(x, y, n);
                    }
                }
            }
        }

        drawOutline(cursorX, cursorY, 1f, 1f, 0f);
    }

    private void drawSquare(int x, int y, float r, float g, float b) {
        //setting the color to be drawn
        glColor3f(r, g, b);

        //calculaing the vertices of the square
        float left = x * 2.0f / COLS - 1f;
        float right = (x + 1) * 2.0f / COLS - 1f;
        float top = 1f - y * 2.0f / ROWS;
        float bottom = 1f - (y + 1) * 2.0f / ROWS;

        //using glBegin we can make custom shapes by providing vertices in opengl
        //parameters tells it that we are drawing quads
        glBegin(GL_QUADS);
        glVertex2f(left, top);
        glVertex2f(right, top);
        glVertex2f(right, bottom);
        glVertex2f(left, bottom);
        //stops drawing
        glEnd();
        
    }


    //same function as before just instead of drawing a filled quad, we are drawing a line loop
    private void drawOutline(int x, int y, float r, float g, float b) {
        glColor3f(r, g, b);
        float left = x * 2.0f / COLS - 1f;
        float right = (x + 1) * 2.0f / COLS - 1f;
        float top = 1f - y * 2.0f / ROWS;
        float bottom = 1f - (y + 1) * 2.0f / ROWS;

        glBegin(GL_LINE_LOOP);
        glVertex2f(left, top);
        glVertex2f(right, top);
        glVertex2f(right, bottom);
        glVertex2f(left, bottom);
        glEnd();
    }

    // Draws a smaller square "mine" inside the current cell
    private void drawMine(int x, int y, float r, float g, float b) {
        // Set mine color
        glColor3f(r, g, b);

        // Calculate center of the cell
        float cellLeft = x * 2.0f / COLS - 1f;
        float cellRight = (x + 1) * 2.0f / COLS - 1f;
        float cellTop = 1f - y * 2.0f / ROWS;
        float cellBottom = 1f - (y + 1) * 2.0f / ROWS;

        // Shrink the square slightly so it appears centered and smaller
        float inset = 0.25f * (2.0f / COLS); // smaller size ratio
        float left = cellLeft + inset;
        float right = cellRight - inset;
        float top = cellTop - inset;
        float bottom = cellBottom + inset;

        // Draw the small square (mine)
        glBegin(GL_QUADS);
        glVertex2f(left, top);
        glVertex2f(right, top);
        glVertex2f(right, bottom);
        glVertex2f(left, bottom);
        glEnd();
    }


    //instead of drawing text, which i couldnt figure out (Was running out of time) We will just display dots to represent no. of mines around a cell
    // Draws small blue boxes in a row to indicate adjacent mine count
    private void drawDots(int x, int y, int n) {
        glColor3f(0f, 0f, 1f); // Blue color for count indicator

        // Calculate center of the cell
        float cx = (x + 0.5f) * 2.0f / COLS - 1f;
        float cy = 1f - (y + 0.5f) * 2.0f / ROWS;

        // Define spacing and size for each dot box
        float spacing = 1.5f * (2.0f / COLS) * 0.1f;
        float size = 1.0f / COLS * 0.1f;
        float start = cx - ((n - 1) * spacing / 2f);

        // Draw n small squares side by side
        for (int i = 0; i < n; i++) {
            float dx = start + i * spacing;

            float left = dx - size;
            float right = dx + size;
            float top = cy + size;
            float bottom = cy - size;

            glBegin(GL_QUADS);
            glVertex2f(left, top);
            glVertex2f(right, top);
            glVertex2f(right, bottom);
            glVertex2f(left, bottom);
            glEnd();
        }
    }



    /*Exception handlling @f init() . 2d array @fhandleInput near the end*/
    /*To run it we need lwjgl(light weight java graphics library to be downloaded) and be in the same root location
    "./lwjgl" use the command "javac -cp ".;lwjgl/*" main.java" to compile it and "java -cp ".;lwjgl/*" main" to run it*/

}
