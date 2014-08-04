package com.chattyhive.chattyhive.framework;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Jonathan on 03/08/2014.
 */
public class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    public SquareImageView(Context context,AttributeSet attrs,int defStyle) {
        super(context,attrs,defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int measuredExpectedHeight = super.getMeasuredHeight();
        int measuredExpectedWidth = super.getMeasuredWidth();

        int paddingLeft = this.getPaddingLeft();
        int paddingRight = this.getPaddingRight();
        int paddingTop = this.getPaddingTop();
        int paddingBottom = this.getPaddingBottom();

        int imageHeight = measuredExpectedHeight - paddingBottom - paddingTop;
        int imageWidth = measuredExpectedWidth - paddingLeft - paddingRight;

        if (imageHeight == imageWidth) return;

        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();

        int performOperation = 0; //0 -> set width equal to height. 1 -> set height equal to width.

        if (((layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) || (layoutParams.height == 0)) && (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT)) {
            performOperation = 0;
        } else if ((layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) && ((layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) || (layoutParams.width == 0))) {
            performOperation = 1;
        } else if (((layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) || (layoutParams.height == 0)) && ((layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) || (layoutParams.width == 0))) {
            if (imageHeight > imageWidth) performOperation = 1;
            else performOperation = 0;
        } else if ((layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) && (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT)) {
            if (imageHeight > imageWidth) performOperation = 0;
            else performOperation = 1;
        }

        int newHeightMeasureSpec = heightMeasureSpec;
        int newWidthMeasureSpec = widthMeasureSpec;

        switch (performOperation) {
            case 0:
                newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(imageHeight+paddingLeft+paddingRight,MeasureSpec.EXACTLY);
                break;
            case 1:
                newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(imageWidth+paddingBottom+paddingTop,MeasureSpec.EXACTLY);
                break;
        }

        super.onMeasure(newWidthMeasureSpec,newHeightMeasureSpec);
    }
}
