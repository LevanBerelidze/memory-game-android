package com.example.android.memorygame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    private static final int TIME_TO_ANSWER = 3000; // in millis
    private static final int TIME_TO_VIBRATE = 300;

    RelativeLayout gameView;
    RandomCircleGenerator rgen;
    ArrayList<Circle> circles;
    boolean gameOver;
    boolean isAnswering;
    int points;
    int currentPersonalBest;
    LinearLayout intermediateMessageView;
    TextView pointsView;
    TextView personalBestView;
    SharedPreferences sharedPref;
    Handler handler;
    Runnable runnable;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);
        gameView = (RelativeLayout) findViewById(R.id.game_view);
        pointsView = (TextView) findViewById(R.id.points_view);
        personalBestView = (TextView) findViewById(R.id.personal_best_view);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        intermediateMessageView = (LinearLayout) findViewById(R.id.intermediate_message);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                vibrate(TIME_TO_VIBRATE);
                Circle correctCircle = circles.get(circles.size() - 1);
                showCorrectCircle(correctCircle);
                gameOver = true;
                handler.removeCallbacks(this);
            }
        };

        reset();
        showGameplay();
        updatePointsDisplay();
        draw();
        handler.postDelayed(runnable, TIME_TO_ANSWER);

        gameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() != MotionEvent.ACTION_DOWN) {
                    return false;
                }

                if(gameOver) {
                    TextView continueMessage = (TextView) findViewById(R.id.continue_message);
                    continueMessage.setText(getResources().getString(R.string.game_over_message));
                    hideGameplay();
                    LinearLayout finishedOptions = (LinearLayout) findViewById(R.id.finished_options);
                    finishedOptions.setVisibility(View.VISIBLE);
                    return false;
                }

                if(isAnswering) {

                    handler.removeCallbacks(runnable);
                    double userX = event.getX();
                    double userY = event.getY();
                    if( checkCircle(circles.get(circles.size() - 1), userX, userY) ) {
                        hideGameplay();
                        points++;
                        isAnswering = false;
                    } else {
                        // size() - 1 checks all the incorrect circles
                        for(int i = 0; i < circles.size() - 1; i++) {
                            if(checkCircle(circles.get(i), userX, userY)) {
                                vibrate(TIME_TO_VIBRATE);
                                Circle correctCircle = circles.get(circles.size() - 1);
                                showCorrectCircle(correctCircle);
                                gameOver = true;

                                // Record high score if necessary
                                recordHighScore(currentPersonalBest);
                                break;
                            }
                        }
                    }
                    updatePointsDisplay();
                } else {
                    showGameplay();
                    draw();
                    handler.postDelayed(runnable, TIME_TO_ANSWER);
                    isAnswering = true;
                }

                return true;
            }
        });

    }

    private void vibrate(long t) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(t);
    }

    private void reset() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        rgen = new RandomCircleGenerator(metrics.widthPixels, metrics.heightPixels);
        circles = new ArrayList<>();
        currentPersonalBest = sharedPref.getInt(getResources().getString(R.string.personal_best_pref_key), 0);
        points = 0;
        isAnswering = true;
        gameOver = false;
    }

    private void showGameplay() {
        intermediateMessageView.setVisibility(View.INVISIBLE);
        changeTransparency(gameView, 0.0F, 1.0F, 0);
    }

    private void hideGameplay() {
        changeTransparency(gameView, 1.0F, 0.0F, 0);
        intermediateMessageView.setVisibility(View.VISIBLE);
    }

    private void recordHighScore(int currentPersonalBest) {
        int personalBest = sharedPref.getInt(getResources().getString(R.string.personal_best_pref_key), 0);
        if(currentPersonalBest > personalBest) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getResources().getString(R.string.personal_best_pref_key), currentPersonalBest);
            editor.apply();
        }
    }

    private void updatePointsDisplay() {
        if(points > currentPersonalBest) {
            currentPersonalBest = points;
        }
        String pointsStr = getResources().getString(R.string.points_template_view) + " " + points;
        String personalBestStr =
                getResources().getString(R.string.personal_best_template_view) + " " + currentPersonalBest;
        pointsView.setText(pointsStr);
        personalBestView.setText(personalBestStr);
    }

    /**
     * The method draws a new circle of different colour
     * which indicates the circle the user should have guessed.
     * @param correctCircle
     */
    private void showCorrectCircle(Circle correctCircle) {
        ImageView correctCircleView = new ImageView(GameActivity.this);
        correctCircleView.setImageResource(R.drawable.circular_shape_wrong);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                2*correctCircle.getRadius(),
                2*correctCircle.getRadius());
        params.leftMargin = correctCircle.getX() - correctCircle.getRadius();
        params.topMargin = correctCircle.getY() - correctCircle.getRadius();

        correctCircleView.setLayoutParams(params);
        correctCircleView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        gameView.addView(correctCircleView);
    }

    /**
     * The method checks whether the coordinates passed as an arguments
     * are inside the specified circle.
     * @param circle
     * @param userX
     * @param userY
     * @return
     */
    private boolean checkCircle(Circle circle, double userX, double userY) {
        double centerX = circle.getX();
        double centerY = circle.getY();
        double currentRadius = circle.getRadius();
        return Math.pow(userX - centerX, 2) + Math.pow(userY - centerY, 2) <= Math.pow(currentRadius, 2);
    }

    private void changeTransparency(View view, float from, float to, long time) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        alphaAnimation.setDuration(time);
        alphaAnimation.setFillAfter(true);
        view.startAnimation(alphaAnimation);
    }

    private void draw() {

        Circle circleToGuess;
        // generate a circle with appropriate location and size
        do {
            circleToGuess = rgen.getRandomCircle();
        } while(!checkOverlap(circleToGuess, circles));

        ImageView currentCircleView = new ImageView(this);
        currentCircleView.setImageResource(R.drawable.circular_shape);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                2*circleToGuess.getRadius(),
                2*circleToGuess.getRadius());
        params.leftMargin = circleToGuess.getX() - circleToGuess.getRadius();
        params.topMargin = circleToGuess.getY() - circleToGuess.getRadius();

        currentCircleView.setLayoutParams(params);
        currentCircleView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        gameView.addView(currentCircleView);
        circles.add(circleToGuess);

    }

    private boolean checkOverlap(Circle circle, ArrayList<Circle> circlesList) {
        for(int i = 0; i < circlesList.size(); i++) {
            Circle circleToCompare = circlesList.get(i);
            double distanceBetweenCircles = Math.sqrt( Math.pow( (circle.getX() - circleToCompare.getX()), 2) +
                    Math.pow(circle.getY() - circleToCompare.getY(), 2) );
            if(distanceBetweenCircles <= circle.getRadius() + circleToCompare.getRadius())
                return false;
        }
        return true;
    }

    public void restart(View view) {
        finish();
        startActivity(new Intent(GameActivity.this, GameActivity.class));
    }

    public void backToMenu(View view) {
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if( (keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            handler.removeCallbacks(runnable);
        }
        return super.onKeyDown(keyCode, event);

    }

}
