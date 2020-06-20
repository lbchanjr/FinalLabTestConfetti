package ca.louisechan.finallabtestconfetti;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class CustomDrawingSurface extends View {
    private static final String TAG = "CustomDrawingSurface";

    // 1. the ability to detect toucches
    // 2. the ability to draw
    // - paintbursh, canvas, bitmap + c(colors come later)
    Paint paintbrush;
    Bitmap bitmap;
    Canvas canvas;

    private int canvasWidth;
    private int canvasHeight;

    // Helper variables for drawing lines on screen
    boolean screenLocked = false;
    private ArrayList<Confetti> confettis = new ArrayList<>();
    float touchX, touchY;


    public CustomDrawingSurface(Context context) {
        super(context, null);

        // configure out paintbursh
        this.paintbrush = new Paint();
        this.paintbrush.setStrokeWidth(5);    // initialize width of the pen
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d(TAG, "onSizeChanged: Screen size changed");

        // initialize our bitmap and canvas here
        this.bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        // Pass the bitmap to the canvas
        this.canvas = new Canvas(this.bitmap);

        // OPTIONALLY: Maybe you can set the background color of the screen
        this.canvas.drawColor(Color.WHITE);

        this.canvasWidth = w;
        this.canvasHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: Calling onDraw function");
        super.onDraw(canvas);
        // force the screen to draw your canvas
        canvas.drawBitmap(this.bitmap, 0, 0, this.paintbrush);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float mouseX = event.getX();
        float mouseY = event.getY();

        // do nothing if screen is locked
        if (screenLocked == true) {
            return super.onTouchEvent(event);
        }

        Log.d(TAG, "onTouchEvent: User touched the screen: (" + mouseX + ", " + mouseY + ")");

        // detect what specific type of gesteure was detected --> flight, swipe, pan --> GestureDetector
        // - FINGER IS DOWN
        // - FINGER IS UP
        // - FINGER HAS MOVED

        int action = event.getActionMasked();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
         //       Log.d(TAG, "onTouchEvent: Finger is down: (" + mouseX + ", " + mouseY + ")");
                // @TODO: write logic to handle when mouse goes down
                // - capture the starting position where you line should be
                touchX = event.getX();
                touchY = event.getY();
                Confetti c = new Confetti(touchX, touchY);
                confettis.add(c);
                changeColor(c.getColor());
                canvas.drawRect(c.getCenterX()-25, c.getCenterY()-25, c.getCenterX()+25, c.getCenterY()+25, paintbrush);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
             //   Log.d(TAG, "onTouchEvent: Finger is moving: (" + mouseX + ", " + mouseY + ")");
                // @TODO: write logic to handle when mouse moves
                return true;
            case MotionEvent.ACTION_UP:
           //     Log.d(TAG, "onTouchEvent: Finger is up: (" + mouseX + ", " + mouseY + ")");
                // @TODO: write logic to handle when mouse goes up
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }


    public void lockScreen(boolean lockStatus) {
        this.screenLocked = lockStatus;
    }

    public void erase() {

        // Only erase canvas if confettis have been drawn
        if (confettis.size() != 0) {
            canvas.drawColor(Color.WHITE);
            invalidate();
            confettis.clear();
            Log.d(TAG, "erase: Canvas erased");
        }
    }

    public void changeColor(int color) {
        Log.d(TAG, "changeColor: Paintbrush color was changed");
        paintbrush.setColor(color);
    }

    public ArrayList<Confetti> getConfettis() {
        return confettis;
    }

    public int getCanvasWidth() {
        return this.canvasWidth;
    }

    public int getCanvasHeight() {
        return this.canvasHeight;
    }


}
