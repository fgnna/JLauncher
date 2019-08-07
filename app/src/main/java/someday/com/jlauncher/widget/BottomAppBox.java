package someday.com.jlauncher.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import someday.com.jlauncher.R;

public class BottomAppBox extends FrameLayout
{

    private final String TAG = "BottomAppBox";

    private final int hideBoxHight_DP = 80;
    private final float showBoxHightScale = 0.8f;
    private final float showBoxThresholdScale = showBoxHightScale - 0.2f;

    private float displayHeight;
    private float maxY;
    private float minY;
    private float showBoxThreshold_Y;
    private float hideBoxQuadToY = 0;
    private boolean isShow = false;

    private View root;


    private Drawable mBackground = new Drawable()
    {
        private Paint mPaint = new Paint();


        @Override
        public void draw(@NonNull Canvas canvas)
        {
            mPaint.setColor(0xFFFFFFFF);
            mPaint.setStrokeWidth(6);
//
//            Log.d(TAG, "Drawable  w:" + canvas.getWidth());
//            Log.d(TAG, "Drawable  h:" + canvas.getHeight());
//            Log.d(TAG, "Drawable  y:" + root.getTranslationY() + "  minY:"+minY + "  maxY:"+maxY);



//            canvas.drawLine(0,0,canvas.getWidth(),0,mPaint);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            Path path = new Path();
            path.moveTo(0,0);
            path.quadTo(canvas.getWidth()/2,( 1 - maxY / root.getTranslationY()) * hideBoxQuadToY,canvas.getWidth(),0);
            canvas.drawPath(path,mPaint);


        }

        @Override
        public void setAlpha(int alpha)
        {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter)
        {

        }

        @Override
        public int getOpacity()
        {
            return PixelFormat.UNKNOWN;
        }

        @Override
        protected void onBoundsChange(Rect bounds)
        {

            Log.d(TAG, "Drawable  onBoundsChange" );
        }
    };
    private OnTranslationYChangeListener onTranslationYChangeListener;


    public BottomAppBox(Context context)
    {
        super(context);
        init();
    }

    public BottomAppBox(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public BottomAppBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init()
    {

        root = inflate(getContext(), R.layout.widget_bottom_app_box, null);
        addView(root);
        displayHeight = getResources().getDisplayMetrics().heightPixels;
        maxY = displayHeight * (1 - showBoxHightScale);
        minY = displayHeight - dpToPx(hideBoxHight_DP, getContext());
        hideBoxQuadToY =  dpToPx(60, getContext());
        showBoxThreshold_Y = displayHeight * (1 - showBoxThresholdScale);
        root.setTranslationY(minY);
        root.setBackground(mBackground);

    }


    private float initY = 0;
    private float lastRawY = 0;
    private int lastMoveModel = -1;
    private float lastMoveY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        float rawY = event.getRawY();
        int action = event.getAction();

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG, "ACTION_DOWN");
                initY = event.getY();
                if(isShow && initY < maxY  )
                    return false;
                else if(!isShow && initY < minY  )
                    return false;
                else
                    break;
            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "ACTION_UP");
                switch (lastMoveModel)
                {
                    case MOVE_UP:
//                        Log.d(TAG, "ACTION_MOVE:   MOVE_UP");
                    case MOVE_DOWN:
//                        Log.d(TAG, "ACTION_MOVE:   MOVE_DOWN");

                        if (root.getTranslationY() >= showBoxThreshold_Y)
                            hideBox();
                        else
                            showBox();
                        break;
                    case MOVE_FAST_UP:
                        showBox();
//                        Log.d(TAG, "ACTION_MOVE:   MOVE_FAST_UP");
                        break;
                    case MOVE_FAST_DOWN:
                        hideBox();
//                        Log.d(TAG, "ACTION_MOVE:   MOVE_FAST_DOWN");
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                lastMoveModel = checkMoveDirection(rawY, lastRawY);

                float moveY = root.getTranslationY() - (lastRawY-rawY);
                if (moveY < maxY)
                {
                    updateTranslationY(maxY);
                } else if (moveY > minY)
                {
                    updateTranslationY(minY);
                } else
                {
                    updateTranslationY(moveY);
                }
                break;

        }
        lastRawY = rawY;
        return true;
    }


    private void showBox()
    {
        float distance = root.getTranslationY() - maxY;

        if (0 == distance)
            return;

        int duration = (int) (200 + (lastMoveY < -150 ? -150 : lastMoveY));

        ValueAnimator anim = ValueAnimator.ofFloat(root.getTranslationY(), maxY);
        anim.setDuration(duration);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                updateTranslationY((Float) animation.getAnimatedValue());
            }
        });
        anim.start();
        isShow = true;
    }


    private void hideBox()
    {

        float distance = minY - root.getTranslationY();

        if (0 == distance)
            return;

//        Log.d(TAG, "hideBox:" + lastMoveY);
        int duration = (int) (200 - (lastMoveY > 150 ? 150 : lastMoveY));

        ValueAnimator anim = ValueAnimator.ofFloat(root.getTranslationY(), minY);
        anim.setDuration(duration);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                updateTranslationY((Float) animation.getAnimatedValue());
            }
        });
        anim.start();
        isShow = false;

    }


    /**
     * 统一调用的抽屉移位方法，方便做一些统一回调的操作
     * @param translationY
     */
    private void updateTranslationY(float translationY)
    {
        root.setTranslationY(translationY);
        mBackground.invalidateSelf();
        if(null != onTranslationYChangeListener)
            onTranslationYChangeListener.onChange(translationY,lastMoveY);
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener)
    {
        super.setOnHierarchyChangeListener(listener);
    }



    public void setOnTranslationYChangeListener(OnTranslationYChangeListener onTranslationYChangeListener)
    {
        this.onTranslationYChangeListener = onTranslationYChangeListener;
    }

    public interface OnTranslationYChangeListener
    {
        void onChange(float translationY,float moveY);
    }





    private static final int MOVE_UP = 1;
    private static final int MOVE_DOWN = 2;
    private static final int MOVE_FAST_UP = 3;
    private static final int MOVE_FAST_DOWN = 4;

    /**
     * 获取手指移动方向
     * 相对于上一移动的方向
     *
     * @return
     */
    private int checkMoveDirection(float currentRawY, float lastRawY)
    {
        float move = currentRawY - lastRawY;
        lastMoveY = move;
        if (move >= 0)
        {
            return move > 10 ? MOVE_FAST_DOWN : MOVE_DOWN;
        } else
        {
            return move < -10 ? MOVE_FAST_UP : MOVE_UP;
        }

    }


    /**
     * DP转PX
     */
    public static int dpToPx(float dp, Context context)
    {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }

}
