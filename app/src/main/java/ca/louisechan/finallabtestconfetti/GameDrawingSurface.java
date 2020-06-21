package ca.louisechan.finallabtestconfetti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class GameDrawingSurface extends SurfaceView implements Runnable {

    // -----------------------------------
    // ## ANDROID DEBUG VARIABLES
    // -----------------------------------

    // Android debug variables
    final static String TAG="Confetti-App";

    // -----------------------------------
    // ## SCREEN & DRAWING SETUP VARIABLES
    // -----------------------------------

    // screen size
    int screenHeight;
    int screenWidth;

    // game state
    boolean gameIsRunning;

    // threading
    Thread gameThread;

    // drawing variables
    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;

    // -----------------------------------
    // ## GAME SPECIFIC VARIABLES
    // -----------------------------------

    // ----------------------------
    // ## SPRITES
    // ----------------------------

    ArrayList<Confetti> confettis;              // points to the confettis that were created by user
    boolean throwDirectionNorth = false;        // indicates the throw direction of the confettis
    boolean initialDisplay;                     // inidicates whether display is the first animation frame

    boolean operationIsSweep = false;
    // Helper variables for drawing lines on screen
    boolean screenLocked = false;
    boolean startAnimation = false;
    boolean sweepIsComplete = false;

    // ----------------------------
    // ## GAME STATS - number of lives, score, etc
    // ----------------------------

    public GameDrawingSurface(Context context, int w, int h/*, ArrayList<Confetti> confettis*/) {
        super(context);

        this.holder = this.getHolder();

        this.paintbrush = new Paint();
        this.paintbrush.setColor(Color.BLUE);

        this.screenWidth = w;
        this.screenHeight = h;
        this.confettis = new ArrayList<>();

        this.printScreenInfo();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.screenWidth = w;
        this.screenHeight = h;

        Log.d(TAG, "onSizeChanged: Screen size changed");
    }

    public void generateRandomSpeeds() {
        Random r = new Random();

        // set the movement speed of each confetti using a speed between 15 to 30 pixels
        for (Confetti c : confettis) {
            int speed = r.nextInt(30-15+1) + 15;
            c.setSpeed(speed);
        }

    }

    public void lockScreen(boolean lockStatus) {
        this.screenLocked = lockStatus;
    }

    public void erase() {
        pauseGame();
        confettis.clear();
        startGame();
        Log.d(TAG, "erase: Confettis erased");
    }

    public void changeColor(int color) {
        Log.d(TAG, "changeColor: Paintbrush color was changed");
        paintbrush.setColor(color);
    }

    // ------------------------------
    // HELPER FUNCTIONS
    // ------------------------------

    // This function prints the screen height & width to the screen.
    private void printScreenInfo() {

        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {

        while (gameIsRunning == true) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();

            //Log.d(TAG, "run: game thread is running!");
        }

        //Log.d(TAG, "run: game thread is stopped!");
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    // 1. Tell Android the (x,y) positions of your sprites
    public void updatePositions() {
        // Update the position of the sprites
        if (startAnimation == false) {
            return;
        }

        // Initially indicate that no confettis have been thrown offscreen or swept in the center screen.
        int offScreenConfettis = 0;

        // Check if this is the initial display
        if (initialDisplay == false) {

            for (Confetti c : confettis) {

                // Check if confetti is being swept in the center or being thrown offscreen
                if (operationIsSweep == false) {
                    // Move confettis north or south
                    if (this.throwDirectionNorth == true) {
                        // check if confetti is now off the top of the y axis
                        if (c.getCenterY() - c.getSpeed() < -50) {
                            offScreenConfettis += 1;
                            Log.d(TAG, "redrawSprites: Number of confettis left is " + (confettis.size() - offScreenConfettis));
                        } else {
                            c.setCenterY(c.getCenterY() - c.getSpeed());
                        }
                    } else {
                        // check if confetti is now way below the bottom of the y axis
                        if (c.getCenterY() + c.getSpeed() > screenHeight + 50) {
                            offScreenConfettis += 1;
                            Log.d(TAG, "redrawSprites: Number of confettis left is " + (confettis.size() - offScreenConfettis));
                        } else {
                            c.setCenterY(c.getCenterY() + c.getSpeed());
                        }

                    }
                } else {
                    // Sweep confetti towards the center
                    Log.d(TAG, "updatePositions: new Center X: " + c.getCenterX() + ", new Center Y: " + c.getCenterY());
                    // check if confettis are within acceptable center coordinate range (+/-10 pixels)
                    if (Math.abs(c.getCenterX() - screenWidth/2) < 10 || Math.abs(c.getCenterY() - screenHeight/2) < 10) {
                        // adjust x,y coordinate of confetti so that it will be truly centered.
                        c.setCenterX(screenWidth/2);
                        c.setCenterY(screenHeight/2);

                        // increment counter used to see if all confetties have been center-swept
                        offScreenConfettis += 1;
                    } else {
                        // slowly move confetti towards the center.
                        moveToCenter(c);
                    }
                }
            }
        }

        // Check if all confettis are thrown offscreen or have been swept in the center.
        Log.d(TAG, "updatePositions: Number of offscreen confettis: " + offScreenConfettis);
        Log.d(TAG, "updatePositions: Number of confettis: " + confettis.size());
        if (offScreenConfettis == confettis.size()) {
            if (operationIsSweep == false) {
                Log.d(TAG, "updatePositions: Removing confettis from collection. animating = " + startAnimation);
                startAnimation = false;
                Log.d(TAG, "updatePositions: animating = " + startAnimation);
                confettis.clear();
            } else {
                sweepIsComplete = true;
            }
        }

        // Indicate that initial display is done
        initialDisplay = false;
    }

    // 2. Tell Android to DRAW the sprites at their positions
    public void redrawSprites() {
        if (this.holder.getSurface().isValid() /*&& confettis.size() != 0*/) {
            this.canvas = this.holder.lockCanvas();

            // Put all your drawing code in this section

            // configure the drawing tools
            this.canvas.drawColor(Color.argb(255,255,255,255));

            // Draw the sprites here (rectangle, circle, etc)
            for (Confetti c : confettis) {
                paintbrush.setColor(c.getColor());
                canvas.drawRect(c.getCenterX() - 25, c.getCenterY() - 25, c.getCenterX() + 25, c.getCenterY() + 25, paintbrush);
            }

            if (sweepIsComplete == true && startAnimation == true && operationIsSweep == true) {
                paintbrush.setTextSize(40);
                paintbrush.setTextAlign(Paint.Align.CENTER);
                paintbrush.setColor(Color.BLACK);
                this.canvas.drawText("Tap on canvas one more time to clear up the pile.",  screenWidth/2, screenHeight/2 + 100, paintbrush);
            }
            //----------------
            this.holder.unlockCanvasAndPost(canvas);
        }
    }

    // Sets the frame rate of the game
    public void setFPS() {
        try {
            // setting frame rate to approximately 60fps
            gameThread.sleep(17);
        }
        catch (Exception e) {

        }
    }

    public void throwConfettis(boolean throwDirectionNorth) {
        if (startAnimation == false && confettis.size() != 0) {
            generateRandomSpeeds();
            this.throwDirectionNorth = throwDirectionNorth;
            setOperationIsSweep(false);
            startAnimation = true;
            initialDisplay = true;
        }
    }

    public void sweepConfettis() {
        if (startAnimation == false && confettis.size() != 0) {
            generateRandomSpeeds();
            setOperationIsSweep(true);
            startAnimation = true;
            initialDisplay = true;
            sweepIsComplete = false;
        }
    }

    public void setThrowDirectionNorth(boolean status) {
        this.throwDirectionNorth = status;
    }


    public void setOperationIsSweep(boolean status) {
        operationIsSweep = status;
    }

    // ----------------------------------------------------------------------------------------------------------
    // Note: This code was based on the solution found in:
    //          https://gamedev.stackexchange.com/questions/50978/moving-a-sprite-towards-an-x-and-y-coordinate
    // The code will allow the x, y coordinates of the confetti to move towards the center of the canvass at a
    // specified speed. Sweeping of the x,y coordinates has a tolerance of +/-10 pixels on either the x or y axis
    // so the checking done in updatePositions() was adjusted to take care of this tolerance.
    // ----------------------------------------------------------------------------------------------------------
    public void moveToCenter(Confetti c) {
        float rotation = (float) Math.atan2(screenHeight/2 - c.getCenterY(), screenWidth/2 - c.getCenterX());

        // Move towards the player
        c.setCenterX(c.getCenterX() + (float) Math.cos(rotation) * c.getSpeed());
        c.setCenterY(c.getCenterY() + (float) Math.sin(rotation) * c.getSpeed());
    }

    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // do nothing if screen is locked
        if (sweepIsComplete == true && startAnimation == true && operationIsSweep == true) {
            startAnimation = false;
            pauseGame();
            confettis.clear();
            startGame();
            return true;
        }

        if (screenLocked == false && startAnimation == false) {
            int userAction = event.getActionMasked();

            Log.d(TAG, "onTouchEvent: touched inside gaming surface");

            if (userAction == MotionEvent.ACTION_DOWN) {
                // user pressed the screen
                // - capture the starting position where you line should be
                pauseGame();

                Confetti c = new Confetti(event.getX(), event.getY());
                confettis.add(c);
                changeColor(c.getColor());
                Log.d(TAG, "onTouchEvent: Confetti added to collection");

                startGame();

            } else if (userAction == MotionEvent.ACTION_UP) {
                // user lifted their finger
            }
        }

        return true;
    }
}
