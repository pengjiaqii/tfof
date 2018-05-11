package com.jade.surface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

/**
 * Created by Jade on 2018/5/10.
 */

public class Bird {

    //鸟儿占屏幕的高度
    private final static float RADIO_POS_HEIGHT = 1 / 3f;

    //鸟儿的大小
    private final static int BIRD_SIZE = 30;

    //鸟儿的坐标
    private int mBirdX;
    private int mBirdY;

    //鸟儿的宽高
    private int mBirdWidth;
    private int mBirdHeight;

    //鸟儿图片
    private Bitmap mBirdBitmap;
    //图片所在的区域
    private RectF mBirdArea = new RectF();

    public Bird(Context context,int gameWidth,int gameHeight,Bitmap birdBitmap) {
        mBirdBitmap = birdBitmap;
        mBirdX = gameWidth / 2 - mBirdBitmap.getWidth()/2;
        mBirdY = (int) (gameHeight * RADIO_POS_HEIGHT);

        mBirdWidth = Util.dp2px(context,BIRD_SIZE);

        mBirdHeight = mBirdWidth / (mBirdBitmap.getWidth()/mBirdBitmap.getHeight());
    }

    public void draw(Canvas canvas) {
        mBirdArea.set(mBirdX,mBirdY,mBirdX+mBirdWidth,mBirdY+ mBirdHeight);
        canvas.drawBitmap(mBirdBitmap,null, mBirdArea,null);
    }

    public int getBirdY() {
        return mBirdY;
    }

    public void setBirdY(int y) {
       mBirdY = y;
    }

    public int getBirdX() {
        return mBirdX;
    }

    public void setBirdX(int x) {
        this.mBirdX = x;
    }

    public int getWidth() {
        return mBirdWidth;
    }

    public int getHeight() {
        return mBirdHeight;
    }

}
