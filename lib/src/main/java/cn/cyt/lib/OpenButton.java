package cn.cyt.lib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

/**
 * 实现自定义圆角背景
 * 支持
 * 1.四边圆角
 * 2.指定边圆角
 * 3.填充色以及边框色
 * 4.按下效果
 * 5.自动计算点击色
 * 6.Java方式和XML方式
 * <p>
 * Created by Tao on 16/12/27.
 */

public class OpenButton extends android.support.v7.widget.AppCompatTextView {

    /**
     * normal
     */
    private GradientDrawable mNormalDrawable;
    /**
     * pressed
     */
    private GradientDrawable mPressedDrawable;
    // 填充色
    private int solidColor = 0;
    // 边框色
    private int strokeColor = 0;
    // 按下填充色
    private int solidTouchColor = 0;
    // 按下边框色
    private int strokeTouchColor = 0;
    // 边框宽度
    private int strokeWidth = 0;
    // shape类型 0-rectangle 1-oval 2-line 3-ring
    private int shapeType = 0;
    // 按下字体色
    private int textTouchColor = 0;
    // 字体色
    private int textColor = 0;
    // 虚线的间隙
    private float dashGap = 0;
    // 虚线的宽度
    private float dashWidth = 0;
    // 圆角
    private float radius = 0.0f;
    private float topLeftRadius = 0.0f;
    private float topRightRadius = 0.0f;
    private float bottomLeftRadius = 0.0f;
    private float bottomRightRadius = 0.0f;

    public OpenButton(Context context) {
        this(context, null);
    }

    public OpenButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.OpenButton, 0, 0);

            solidColor = ta.getInteger(R.styleable.OpenButton_solidColor, 0x00000000);
            strokeColor = ta.getInteger(R.styleable.OpenButton_strokeColor, 0x00000000);

            solidTouchColor = ta.getInteger(R.styleable.OpenButton_solidTouchColor, 0x00000000);
            strokeTouchColor = ta.getInteger(R.styleable.OpenButton_strokeTouchColor, 0x00000000);
            textTouchColor = ta.getInteger(R.styleable.OpenButton_textTouchColor, 0x00000000);
            textColor = getCurrentTextColor();
            strokeWidth = ta.getInteger(R.styleable.OpenButton_strokeWidth, 0);

            radius = ta.getDimension(R.styleable.OpenButton_radius, 0);
            topLeftRadius = ta.getDimension(R.styleable.OpenButton_topLeftRadius, 0);
            topRightRadius = ta.getDimension(R.styleable.OpenButton_topRightRadius, 0);
            bottomLeftRadius = ta.getDimension(R.styleable.OpenButton_bottomLeftRadius, 0);
            bottomRightRadius = ta.getDimension(R.styleable.OpenButton_bottomRightRadius, 0);
            dashGap = ta.getDimension(R.styleable.OpenButton_dashGap, 0);
            dashWidth = ta.getDimension(R.styleable.OpenButton_dashWidth, 0);
            shapeType = ta.getInt(R.styleable.OpenButton_shapeTpe, GradientDrawable.RECTANGLE);
            ta.recycle();
        }
        mNormalDrawable = new GradientDrawable();
        mPressedDrawable = new GradientDrawable();
        drawBackground();
    }

    /**
     * 设置按下颜色值
     */
    private void drawBackground() {
        //矩形
        if (shapeType == GradientDrawable.RECTANGLE) {
            mNormalDrawable.setShape(GradientDrawable.RECTANGLE);
            mPressedDrawable.setShape(GradientDrawable.RECTANGLE);
            if (radius != 0) {
                mNormalDrawable.setCornerRadius(radius);
                mPressedDrawable.setCornerRadius(radius);
            } else {
                //分别表示 左上 右上 右下 左下
                mNormalDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
                mPressedDrawable.setCornerRadii(new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius, bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius});
            }
        } else {
            switch (shapeType) {
                case GradientDrawable.LINE:
                    mNormalDrawable.setShape(GradientDrawable.LINE);
                    mPressedDrawable.setShape(GradientDrawable.LINE);
                    break;
                case GradientDrawable.OVAL:
                    mNormalDrawable.setShape(GradientDrawable.OVAL);
                    mPressedDrawable.setShape(GradientDrawable.OVAL);
                    break;
                case GradientDrawable.RING:
                    mNormalDrawable.setShape(GradientDrawable.RING);
                    mPressedDrawable.setShape(GradientDrawable.RING);
                    break;
                default:
                    mNormalDrawable.setShape(GradientDrawable.RECTANGLE);
                    mPressedDrawable.setShape(GradientDrawable.RECTANGLE);
                    break;
            }
        }
        if (solidColor != 0)
            mNormalDrawable.setColor(solidColor);
        else
            mNormalDrawable.setColor(Color.TRANSPARENT);

        if (strokeWidth != 0 && strokeColor != 0) {

            if (dashGap != 0 && dashWidth != 0)
                mNormalDrawable.setStroke(strokeWidth, strokeColor, dashWidth, dashGap);
            else
                mNormalDrawable.setStroke(strokeWidth, strokeColor);
        } else
            mNormalDrawable.setStroke(0, Color.TRANSPARENT);

        // 设置PressedDrawable，如果没有设置的话，自动计算颜色
        if (solidTouchColor != 0) {
            mPressedDrawable.setColor(solidTouchColor);
        } else if (solidColor != 0) {
            mPressedDrawable.setColor(calculateButtonPressedColor(solidColor));
        } else if (strokeColor != 0) {
            mPressedDrawable.setColor(calculateAlphaPressedColor(strokeColor));
        }

        int color;
        if (strokeTouchColor == 0) {
            color = strokeColor;
        } else {
            color = strokeTouchColor;
        }
        if (dashGap != 0 && dashGap != 0)
            mPressedDrawable.setStroke(strokeWidth, color, dashWidth, dashGap);
        else
            mPressedDrawable.setStroke(strokeWidth, color);

        // 版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(getSelector(mNormalDrawable, mPressedDrawable));
        } else {
            setBackgroundDrawable(getSelector(mNormalDrawable, mPressedDrawable));
        }
        postInvalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int state[] = getDrawableState();
        boolean flag = false;
        for (int i = 0; i < state.length; i++) {
            if (state[i] == android.R.attr.state_pressed) {
                if (textTouchColor != 0) {
                    super.setTextColor(textTouchColor);
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            if (textColor != 0) {
                super.setTextColor(textColor);
            }
        }
    }

    /**
     * 获取Selector
     *
     * @param normalDraw
     * @param pressedDraw
     * @return
     */
    public StateListDrawable getSelector(Drawable normalDraw, Drawable pressedDraw) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDraw);
        stateListDrawable.addState(new int[]{}, normalDraw);
        return stateListDrawable;
    }

    /**
     * dp转px
     *
     * @param dp 传入的dp
     */
    private int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void setSolidColor(int solidColor) {
        this.solidColor = solidColor;
        drawBackground();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        drawBackground();
    }

    public void setSolidTouchColor(int solidTouchColor) {
        this.solidTouchColor = solidTouchColor;
        drawBackground();
    }

    public void setStrokeTouchColor(int strokeTouchColor) {
        this.strokeTouchColor = strokeTouchColor;
        drawBackground();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        drawBackground();
    }

    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
        drawBackground();
    }

    public void setTextTouchColor(int textTouchColor) {
        this.textTouchColor = textTouchColor;
        drawBackground();
    }

    @Override
    public void setTextColor(@ColorInt int color) {
        this.textColor = color;
        super.setTextColor(color);
        drawBackground();
    }

    public void setDashGap(float dashGap) {
        this.dashGap = dp2px(dashGap);
        drawBackground();
    }

    public void setDashWidth(float dashWidth) {
        this.dashWidth = dp2px(dashWidth);
        drawBackground();
    }

    public void setRadius(float radius) {
        this.radius = dp2px(radius);
        drawBackground();
    }

    public void setTopLeftRadius(float topLeftRadius) {
        this.topLeftRadius = dp2px(topLeftRadius);
        drawBackground();
    }

    public void setTopRightRadius(float topRightRadius) {
        this.topRightRadius = dp2px(topRightRadius);
        drawBackground();
    }

    public void setBottomLeftRadius(float bottomLeftRadius) {
        this.bottomLeftRadius = dp2px(bottomLeftRadius);
        drawBackground();
    }

    public void setBottomRightRadius(float bottomRightRadius) {
        this.bottomRightRadius = dp2px(bottomRightRadius);
        drawBackground();
    }

    /**
     * 根据你提供的基色去计算颜色被按压之后的颜色(不支持alpha通道)
     *
     * @param baseColor 基色
     * @return 被按压以后颜色的十六进制
     */
    private int calculateButtonPressedColor(int baseColor) {
        int startRed = Color.red(baseColor);
        int startGreen = Color.green(baseColor);
        int startBlue = Color.blue(baseColor);
        int startAlpha = Color.alpha(baseColor);

        int endRed = startRed - 16;
        int endGreen = startGreen - 17;
        int endBlue = startBlue - 20;

        return Color.argb(startAlpha, endRed, endGreen, endBlue);
    }

    /**
     * 根据你提供的基色去计算颜色被按压之后的颜色(不支持alpha通道)
     *
     * @param baseColor 基色
     * @return 被按压以后颜色的十六进制
     */
    private int calculateAlphaPressedColor(int baseColor) {
        int startRed = Color.red(baseColor);
        int startGreen = Color.green(baseColor);
        int startBlue = Color.blue(baseColor);

        return Color.argb(25, startRed, startGreen, startBlue);
    }

}