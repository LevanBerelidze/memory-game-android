package com.example.android.memorygame;

import java.util.Random;

public class RandomCircleGenerator {

    private static final int MIN_RADIUS_RATIO = 18;
    private static final int MAX_RADIUS_RATIO = 8;
    private static final int DEFAULT_OFFSET = 30;

    private Random rgen;
    private int layoutWidth;
    private int layoutHeight;
    private int minRadius;
    private int maxRadius;
    private int offset;

    public RandomCircleGenerator(int layoutWidth, int layoutHeight) {
        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
        minRadius = (layoutHeight < layoutWidth ? layoutHeight : layoutWidth) / MIN_RADIUS_RATIO;
        maxRadius = (layoutHeight < layoutWidth ? layoutHeight : layoutWidth) / MAX_RADIUS_RATIO;
        offset = DEFAULT_OFFSET;
        rgen = new Random();
    }

    public Circle getRandomCircle() {
        int radius = rgen.nextInt(maxRadius - minRadius) + minRadius;
        int x = rgen.nextInt(layoutWidth - 2*(offset + radius) ) + (offset + radius);
        int y = rgen.nextInt(layoutHeight - 2*(2*offset + radius) ) + (offset + radius);
        return new Circle(radius, x, y);
    }

}
