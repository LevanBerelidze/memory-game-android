package com.example.android.memorygame;

import android.animation.Animator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {

    Button startButton;
    Button instructionsButton;
    Button quitButton;
    LinearLayout startButtonText;
    LinearLayout instructionsButtonText;
    LinearLayout quitButtonText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton = (Button) findViewById(R.id.start_button);
        instructionsButton = (Button) findViewById(R.id.instr_button);
        quitButton = (Button) findViewById(R.id.quit_button);

        startButtonText = (LinearLayout) findViewById(R.id.start_button_text);
        instructionsButtonText = (LinearLayout) findViewById(R.id.instr_button_text);
        quitButtonText = (LinearLayout) findViewById(R.id.quit_button_text);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();

        AlphaAnimation alpha = new AlphaAnimation(0.0F, 1.0F);
        alpha.setDuration(600);
        startButton.startAnimation(alpha);
        instructionsButton.startAnimation(alpha);
        quitButton.startAnimation(alpha);

        startButton.animate().scaleX(1f).scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
        instructionsButton.animate().scaleX(1f).scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
        quitButton.animate().scaleX(1f).scaleY(1f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(400);
    }

    public void viewInstructions(View view) {
        final ViewPropertyAnimator animator = instructionsButton.animate();
        animator.scaleX(10f).scaleY(10f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(650);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                instructionsButton.bringToFront();
                instructionsButtonText.bringToFront();
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Game Instructions");
                builder.setMessage(getResources().getString(R.string.instruction));
                instructionsButton.bringToFront();
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        animator.setListener(null);
                        onResume();
                        instructionsButtonText.bringToFront();
                    }
                });
                builder.show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void startGame(View view) {
        final ViewPropertyAnimator animator = startButton.animate();
        animator.scaleX(8f).scaleY(8f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(800);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                startButton.bringToFront();
                startButtonText.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(gameIntent);
                animator.setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void quitGame(View view) {
        final ViewPropertyAnimator animator = quitButton.animate();
        animator.scaleX(20f).scaleY(20f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                quitButton.bringToFront();
                quitButtonText.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                animator.setListener(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

}
