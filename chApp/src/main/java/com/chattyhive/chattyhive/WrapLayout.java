package com.chattyhive.chattyhive;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by J.Guzmán on 24/11/2014.
 */

public class WrapLayout extends ViewGroup {

    private ArrayList<ContentLine> lines;

    public WrapLayout(Context context) {
        this(context, null);
    }

    public WrapLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapLayout(Context context, AttributeSet attrs, int defStyle){
        super(context,attrs,defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //Max dimensions for parent.
        //TODO: Handle PaddingStart and PaddingEnd for compatibility with RTL languages.
        int horizontalPadding = this.getPaddingLeft()+this.getPaddingRight();
        int verticalPadding = this.getPaddingTop()+this.getPaddingBottom();

        int maxWidth = ((MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED)?Integer.MAX_VALUE:(MeasureSpec.getSize(widthMeasureSpec)));
        maxWidth -= horizontalPadding;

        //Computed required dimensions for children and temporal positioning vars.
        int requiredHeight = 0;
        int requiredWidth = 0;
        int accumulatedWidth = 0;
        int maxLineHeight = 0;

        //Child variables. We allocate them here to avoid reallocating in each loop.
        final int childCount = this.getChildCount();
        View childView;
        Child child;
        ContentLine line = new ContentLine();

        if (childCount > 0)
            this.lines = new ArrayList<ContentLine>();
        else
            this.lines = null;

        for (int i = 0; i < childCount; i++) {
            childView = this.getChildAt(i);
            if (childView.getVisibility() == View.GONE) continue; //If child's visibility is GONE then skip this child.

            child = new Child(childView,widthMeasureSpec,heightMeasureSpec);

            int childWidth = child.renderWidth + child.marginLeft + child.marginRight;
            int childHeight = child.renderHeight + child.marginTop + child.marginBottom;

            //We compute child distribution across the layout to get the real required size.
            if (((accumulatedWidth+childWidth) > maxWidth) && (maxWidth > 0)) {
                //This child does not fit in this line so we take next line.
                line.lineWidth = accumulatedWidth;
                line.lineHeight = maxLineHeight;
                this.lines.add(line);
                line = new ContentLine();

                requiredWidth = Math.max(requiredWidth,accumulatedWidth);
                requiredHeight += maxLineHeight;

                maxLineHeight = 0;
                accumulatedWidth = 0;
            }
                //Child always fits in current line.
            maxLineHeight = Math.max(maxLineHeight,childHeight);
            accumulatedWidth += childWidth;
            line.children.add(child);
        }

        //We compute the last line of the layout.
        if (accumulatedWidth > 0) {
            line.lineWidth = accumulatedWidth;
            line.lineHeight = maxLineHeight;
            this.lines.add(line);
        }

        if ((this.lines != null) && (this.lines.isEmpty()))
            this.lines = null;

        requiredWidth = Math.max(requiredWidth,accumulatedWidth);
        requiredHeight += maxLineHeight;

        //We take padding into account.
        requiredWidth += horizontalPadding;
        requiredHeight += verticalPadding;

        setMeasuredDimension(resolveSize(requiredWidth,widthMeasureSpec),resolveSize(requiredHeight,heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (this.lines == null) return; //Nothing to layout.

        //TODO: Handle compatibility with RTL languages.
        final int left = this.getPaddingLeft();

        int currentLeft = left;
        int currentTop = this.getPaddingTop();
        int currentBottom;

        for (ContentLine line : this.lines) {
            currentBottom = currentTop + line.lineHeight;
            for (Child child : line.children) {
                currentLeft += child.marginLeft;
                child.childView.layout(currentLeft,currentTop+child.marginTop,currentLeft+child.renderWidth,currentBottom-child.marginBottom);
                currentLeft += child.renderWidth + child.marginRight;
            }
            currentTop = currentBottom;
            currentLeft = left;
        }
    }

    private class ContentLine {
        int lineWidth;
        int lineHeight;

        ArrayList<Child> children;

        ContentLine() {
            this.children = new ArrayList<Child>();
        }
    }
    private class Child {
        int renderWidth;
        int renderHeight;

        int marginLeft = 0;
        int marginRight = 0;
        int marginTop = 0;
        int marginBottom = 0;

        View childView;

        Child (View childView, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
            measureChild(childView,parentWidthMeasureSpec,parentHeightMeasureSpec);
            this.childView = childView;
            this.renderWidth = this.childView.getMeasuredWidth();
            this.renderHeight = this.childView.getMeasuredHeight();

            LayoutParams layoutParams = this.childView.getLayoutParams();
            if (layoutParams instanceof MarginLayoutParams) {
                this.marginLeft = ((MarginLayoutParams) layoutParams).leftMargin;
                this.marginRight = ((MarginLayoutParams) layoutParams).rightMargin;
                this.marginBottom = ((MarginLayoutParams) layoutParams).bottomMargin;
                this.marginTop = ((MarginLayoutParams) layoutParams).topMargin;
                //TODO: Handle MarginStart and MarginEnd for compatibility with RTL languages.
            }
        }
    }
}
