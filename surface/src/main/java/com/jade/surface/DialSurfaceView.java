package com.jade.surface;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jade on 2018/5/10.
 */

public class DialSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    /**
     * SurfaceHolder是一个接口,类似于一个surace的监听器,可以来监听Surface的创建、销毁或者改变。
     * 1： abstract  void addCallback（SurfaceHolder.Callback callback );为SurfaceHolder添加一个SurfaceHolder.Callback回调接口。
     * 2: abstract  Canvas lockCanvas() ;获取Surface中的Canvas对象，并锁定之。所得到的Canvas对象。
     * 3：abstract  void unlockCanvasAndPost(Canvas canvas);当修改Surface中的数据完成后，释放同步锁，并提交改变，然后将新的数据进行展示。
     */
    private SurfaceHolder mSurfaceHolder;
    //绘制所用的画板
    private Canvas mCanvas;
    //SurfaceView的特点,在单独的线程中刷新自己
    private Thread mThread;
    //线程控制开关
    private boolean mIsRuning;

    //背景图
    private int mBgWidth;
    private int mBgHeight;
    private RectF mBackground = new RectF();
    private Bitmap mBg;

    //鸟
    private Bird mBird;
    private Bitmap mBirdBitmap;

    //管道
    private Bitmap mPillarTop;
    private Bitmap mPillarBottom;
    private RectF mPillarRect;
    private int mPillarWidth;

    //地板
    private Paint mPaint;
    private Floor mFloor;
    private Bitmap mFloorBg;
    private int mSpeed;

    //管道的宽度 60dp
    private static final int PIPE_WIDTH = 60;
    private List<Pillar> mPillars = new ArrayList<>();

    //分数
    private int mGrade = 0;
    private final int[] mNums = new int[]{
            R.mipmap.zero,
            R.mipmap.one,
            R.mipmap.two,
            R.mipmap.three,
            R.mipmap.four,
            R.mipmap.five,
            R.mipmap.six,
            R.mipmap.seven,
            R.mipmap.eight,
            R.mipmap.nine
    };
    private Bitmap[] mNumBitmap;
    //单个数字的高度的1/15
    private static final float RADIO_SINGLE_NUM_HEIGHT = 1 / 15f;
    //单个数字的宽度
    private int mSingleGradeWidth;
    //单个数字的高度
    private int mSingleGradeHeight;
    //单个数字的范围
    private RectF mSingleNumRectF;

    /****************************************/

    //触摸上升的高度
    private final int BIRD_UP_DIS_SIZE = Util.dp2px(getContext(), TOUCH_UP_SIZE);
    //自由下落速度
    private final int AUTO_DOWN_SPEED = Util.dp2px(getContext(), 2);
    //游戏状态
    private GameStatus mGameStaus = GameStatus.RUNNING;
    private int mTmpBirdDis;
    //触摸上升的距离。
    private static final int TOUCH_UP_SIZE = -16;
    //将上升的距离转化为px；
    private final int mBirdUpDis = Util.dp2px(getContext(), TOUCH_UP_SIZE);

    //两个管道间距离
    private final int PIPE_DIS_BETWEEN_TWO = Util.dp2px(getContext(), 100);
    //记录移动的距离，达到 PIPE_DIS_BETWEEN_TWO 则生成一个管道
    private int mTmpMoveDistance;
    //记录需要移除的管道
    private List<Pillar> mNeedRemovePillar = new ArrayList<>();

    //鸟自动下落的距离
    private final int mAutoDownSpeed = Util.dp2px(getContext(), 2);
    private int mRemovedPillar;

    /**
     * 构造方法组
     */
    public DialSurfaceView(Context context) {
        this(context, null);
    }

    public DialSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //拿到管理器,通过这个东西来控制surfaceView
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        //设置画布背景透明
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        //设置可以获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常亮
        setKeepScreenOn(true);

        //初始化画笔
        mPaint = new Paint();
        //抗锯齿
        mPaint.setAntiAlias(true);
        //防抖动
        mPaint.setDither(true);

        initBitMap();

        mSpeed = Util.dp2px(getContext(), 2);

        mPillarWidth = Util.dp2px(getContext(), PIPE_WIDTH);
    }

    public DialSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DialSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 生成bitmap
     */
    private void initBitMap() {

        mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.game_bg);
        mBirdBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_bird);
        mFloorBg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_floor_two);
        mPillarTop = BitmapFactory.decodeResource(getResources(), R.mipmap.img_top_pipe);
        mPillarBottom = BitmapFactory.decodeResource(getResources(), R.mipmap.img_bottom_pipe);
        //加载分数bitmap
        mNumBitmap = new Bitmap[mNums.length];
        for (int i = 0; i < mNumBitmap.length; i++) {
            mNumBitmap[i] = BitmapFactory.decodeResource(getResources(), mNums[i]);
        }
    }

    /**
     * 在控件大小发生改变时会调用
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBgWidth = w;
        mBgHeight = h;
        mBackground.set(0, 0, w, h);
        //小鸟和地板
        mBird = new Bird(getContext(), mBgWidth, mBgHeight, mBirdBitmap);
        mFloor = new Floor(mBgWidth, mBgHeight, mFloorBg);
        // 初始化管道范围
        mPillarRect = new RectF(0, 0, mPillarWidth, mBgHeight);
        Pillar pillar = new Pillar(w, h, mPillarTop, mPillarBottom);
        mPillars.add(pillar);
        // 初始化分数
        mSingleGradeHeight = (int) (h * RADIO_SINGLE_NUM_HEIGHT);
        mSingleGradeWidth = (int) (mSingleGradeHeight * 1.0f / mNumBitmap[0].getHeight() * mNumBitmap[0].getWidth());
        mSingleNumRectF = new RectF(0, 0, mSingleGradeWidth, mSingleGradeHeight);
    }

    /**
     * Runnable接口start
     */
    @Override
    public void run() {
        while (mIsRuning) {
            long start = System.currentTimeMillis();
            //游戏运行时,每个物体的运动逻辑
            gameStatus();
            //实际绘制方法
            draw();

            long end = System.currentTimeMillis();

            int interval = 30;
            long consumeTime = end - start;
            if (consumeTime < interval) {
                SystemClock.sleep(interval - (consumeTime));
            }
        }
    }

    /**
     * 游戏状态
     */
    private void gameStatus() {

        switch (mGameStaus) {

            case RUNNING:
                //重置分数
                mGrade = 0;
                //管道逻辑


                // 管道移动 以及计算即将被移除的管道
                for (Pillar pillar : mPillars) {
                    if (pillar.getPillarX() < -mPillarWidth) {
                        mNeedRemovePillar.add(pillar);
                        mRemovedPillar++;
                        continue;
                    }
                    pillar.setPillarX(pillar.getPillarX() - mSpeed);
                }
                //移除管道
                mPillars.removeAll(mNeedRemovePillar);
                // 管道
                mTmpMoveDistance += mSpeed;
                // 生成一个管道
                Log.w("123456", "mTmpMoveDistance = " + mTmpMoveDistance);
                Log.w("123456", "mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO = " + (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO));
                if (mTmpMoveDistance >= PIPE_DIS_BETWEEN_TWO) {
                    Pillar pillar = new Pillar(getWidth(), getHeight(), mPillarTop, mPillarBottom);
                    mPillars.add(pillar);
                    mTmpMoveDistance = 0;
                }

                Log.e("123456", "现存管道数量：" + mPillars.size());
                // 更新我们地板绘制的x坐标，地板向左移动
                mFloor.setFloorX(mFloor.getFloorX() - mSpeed);

                //默认下落，点击时瞬间上升
                mTmpBirdDis += mAutoDownSpeed;
                Log.i("jade","mTmpBirdDis--->" + mTmpBirdDis);
                mBird.setBirdY(mBird.getBirdY() + mTmpBirdDis);
                //计算分数
                mGrade += mRemovedPillar;
                for (Pillar pillar : mPillars) {
                    if (pillar.getPillarX() + mPillarWidth < mBird.getBirdX()) {
                        mGrade++;
                    }
                }
//                checkGameOver();
                break;

            case OVER:
                // 鸟落下
                if (mBird.getBirdY() < mFloor.getFloorY() - mBird.getWidth()) {
                    mTmpBirdDis += mAutoDownSpeed;
                    mBird.setBirdY(mBird.getBirdY() + mTmpBirdDis);
                } else {
                    mGameStaus = GameStatus.WAITING;
                    resetPos();
                }
                break;

            default:
                break;
        }

    }

    private void checkGameOver() {

        // 如果触碰地板，就挂了
        if (mBird.getBirdY() > mFloor.getFloorX() - mBird.getHeight()) {
            mGameStaus = GameStatus.OVER;
        }

        // 如果撞到管道
        for (Pillar pillar : mPillars) {

            //已经穿过的
            if (pillar.getPillarX() + mPillarWidth < mBird.getBirdY()) {
                continue;
            }
            if (pillar.touchBird(mBird)) {
                mGameStaus = GameStatus.OVER;
                break;
            }
        }
    }

    /**
     * 重置鸟的位置等数据
     */
    private void resetPos() {
        mPillars.clear();
        mNeedRemovePillar.clear();
        //重置鸟的位置
        mBird.setBirdY(mBgHeight * 2 / 3);
        //重置下落速度
        mTmpBirdDis = 0;
        //        mTmpMoveDistance = 0;
        //重置已通过的管道数
        mRemovedPillar = 0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            switch (mGameStaus) {
                case WAITING:
                    mGameStaus = GameStatus.RUNNING;
                    break;
                case RUNNING:
                    mTmpBirdDis = mBirdUpDis;
                    break;
            }

        }
        return true;
    }

    /**
     * callback接口start
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //开启线程
        mIsRuning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    /**
     * callback接口end
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRuning = false;
    }


    /**
     * Runnable接口end
     */
    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null) {
                drawBg();
                drawBird();
                drawFloor();
                // 更新我们地板绘制的x坐标
                mFloor.setFloorX(mFloor.getFloorX() - mSpeed);
                //绘制管道
                drawPillars();
                drawGrades();
            }
        } catch (Exception e) {
            Log.e("123456", "e = " + e.getMessage());
        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 绘制背景
     */
    private void drawBg() {
        mCanvas.drawBitmap(mBg, null, mBackground, null);
    }

    /**
     * 绘制鸟儿
     */
    private void drawBird() {
        if (mBird != null) {
            mBird.draw(mCanvas);
        }
    }

    /**
     * 绘制地板
     */
    private void drawFloor() {
        mFloor.draw(mCanvas, mPaint);
    }

    /**
     * 绘制地板
     */
    private void drawPillars() {
        for (Pillar pillar : mPillars) {
            pillar.setPillarX(pillar.getPillarX() - mSpeed);
            pillar.draw(mCanvas, mPillarRect);
        }
    }

    /**
     * 绘制分数
     */
    private void drawGrades() {
        String grade = String.valueOf(mGrade);
        mCanvas.save(Canvas.MATRIX_SAVE_FLAG);
        mCanvas.translate(mBgWidth / 2 - grade.length() * mSingleGradeWidth / 2, 1f / 8 * mBgHeight);
        // draw single num one by one
        for (int i = 0; i < grade.length(); i++) {
            String numStr = grade.substring(i, i + 1);
            int num = Integer.valueOf(numStr);
            mCanvas.drawBitmap(mNumBitmap[num], null, mSingleNumRectF, null);
            mCanvas.translate(mSingleGradeWidth, 0);
        }
        mCanvas.restore();
    }
}
