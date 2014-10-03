package com.chattyhive.chattyhive.framework.CustomViews.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Jonathan on 04/09/2014.
 */
public class ShapeArrow extends View {
    private int arrowPointWidth;
    public void setArrowPointWidth(int arrowPointWidth) {
        this.arrowPointWidth = arrowPointWidth;
        if (this.arrowPath != null) {
            this.invalidate();
            this.requestLayout();
        }
    }
    public int getArrowPointWidth(){
        return this.arrowPointWidth;
    }
    private int borderColor;
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }
    public int getBorderColor() { return this.borderColor; }
    private int borderThick = 0;
    public void setBorderThick(int borderThick) {
        this.borderThick = borderThick;
    }
    public int getBorderThick() {
        return this.borderThick;
    }
    private int shapeFillColor;
    public void setShapeFillColor (int shapeFillColor) {
        this.shapeFillColor = shapeFillColor;
        if (this.arrowPath != null) {
            invalidate();
            requestLayout();
        }
    }
    public int getShapeFillColor() {
        return this.shapeFillColor;
    }

    private Path arrowPath;

    public ShapeArrow(Context context) {
        super(context);
    }
    public ShapeArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ShapeArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resolvedWidth;
        int tryWidth = Math.max(this.getSuggestedMinimumWidth(), this.getLayoutParams().width);
        if ((this.getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) && (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST))
            tryWidth = MeasureSpec.getSize(widthMeasureSpec);
        resolvedWidth = this.resolveSize(tryWidth, widthMeasureSpec);

        int resolvedHeight;
        int tryHeight = Math.max(this.getSuggestedMinimumHeight(), this.getLayoutParams().height);
        if ((this.getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT) && (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST))
            tryHeight = MeasureSpec.getSize(heightMeasureSpec);
        resolvedHeight = this.resolveSize(tryHeight, heightMeasureSpec);

        this.setMeasuredDimension(resolvedWidth, resolvedHeight);

        this.computePolygon();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(0,0,0,0);

        if (this.arrowPath == null) return;

        Paint fillBrush = new Paint();
        fillBrush.setColor(this.shapeFillColor);
        fillBrush.setStyle(Paint.Style.FILL);

        canvas.drawPath(this.arrowPath, fillBrush);

        if (this.borderThick > 0) {
            Paint lineBrush = new Paint();
            lineBrush.setColor(this.borderColor);
            lineBrush.setStyle(Paint.Style.STROKE);
            canvas.drawPath(this.arrowPath,lineBrush);
        }
    }

    private void computePolygon() {
        float scale = 1;
        int finalArrowPointWidth;

        if (this.getLayoutParams() == null) return;
        if (this.getLayoutParams().width > 0)
            scale = (this.getMeasuredWidth()/(float)this.getLayoutParams().width);
        finalArrowPointWidth = Math.round((scale * this.arrowPointWidth));


        ArrayList<PointF> arrowMap = new ArrayList<PointF>();

        arrowMap.add(new PointF(0,0));
        arrowMap.add(new PointF(finalArrowPointWidth-1,(this.getMeasuredHeight())/(float)2));
        arrowMap.add(new PointF(0,this.getMeasuredHeight()));
        arrowMap.add(new PointF(this.getMeasuredWidth()-finalArrowPointWidth,this.getMeasuredHeight()));
        arrowMap.add(new PointF(this.getMeasuredWidth()-1,(this.getMeasuredHeight())/(float)2));
        arrowMap.add(new PointF(this.getMeasuredWidth()-finalArrowPointWidth,0));

        PointF finalPoint = arrowMap.get(arrowMap.size()-1);

        if (this.arrowPath == null)
            this.arrowPath = new Path();
        else
            this.arrowPath.reset();

        this.arrowPath.moveTo(finalPoint.x,finalPoint.y);
        for(PointF point : arrowMap)
            this.arrowPath.lineTo(point.x, point.y);
    }
}
