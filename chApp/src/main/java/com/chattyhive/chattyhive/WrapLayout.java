package com.chattyhive.chattyhive;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by J.Guzmán on 24/11/2014.
 */

public class WrapLayout extends ViewGroup{

    private int counter;
    private int lineHeight;

    private Context context;

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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        int childLeft = this.getPaddingLeft();
        int childTop = this.getPaddingTop();
        int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        int childWidth = childRight - childLeft;
        int childHeight = childBottom - childTop;

        //System.out.println("LINE: "+childHeight);

        counter = 1;

        maxHeight = 0;
        curLeft = childLeft;//inicializa con el padding izquierdo
        curTop = childTop;//inicializa con el padding top


        for (int i = 0; i < count; i++) { //walk through each child, and arrange it from left to right
            View child = getChildAt(i); //cada hijo
            if (child.getVisibility() != GONE) { // si no tiene visibilidad GONE
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));//Get the maximum size of the child y lo asigna a las variables --->
                curWidth = child.getMeasuredWidth(); // ancho y alto maximo del hijo
                curHeight = child.getMeasuredHeight();
                //System.out.println("curheight "+curHeight);

                //wrap is reach to the end
                if (curLeft + curWidth >= childRight) { //si espacio ocupado a la izq hasta ahora + ancho del hijo >= ancho disponible
                    curLeft = childLeft; //se vuelve a inicializar el margen a la izq
                    curTop += maxHeight;  //se suma a la altura utilizada el alto consumido (alto de la fila de textview)
                    //System.out.println("maxHeight "+maxHeight);
                    lineHeight = curTop;
                    //System.out.println("CURTOP"+curTop);
                    maxHeight = 0; //inicializa a 0
                    counter++;
                    System.out.println("Counter: "+counter+" "+i);
                }
                lineHeight = curTop + curHeight;
                //do the layout
                //child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight); // lo posiciona dentro del layout
                if (maxHeight < curHeight) { //store the max height
                    //System.out.println("maxHeight second if before"+maxHeight);
                    maxHeight = curHeight;
                    //System.out.println("maxHeight second if after"+maxHeight);
                }
                curLeft += curWidth; //incrementa el espacio ocupado a la derecha en cada iteracion
            }
        }

        int desiredWidth = 100;
        int desiredHeight = 100;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = counter*(12 + MeasureSpec.makeMeasureSpec(40, MeasureSpec.EXACTLY)) +(getPaddingTop() + getPaddingBottom());//getMeasuredHeight(); //Math.min(desiredHeight, heightSize);
        } else if (heightMode == MeasureSpec.UNSPECIFIED){//used--> wrap content
            //Be whatever you want
            //TODO: height line
            height = counter*(12 + MeasureSpec.makeMeasureSpec(40, MeasureSpec.EXACTLY)) +(getPaddingTop() + getPaddingBottom());//desiredHeight;
            //System.out.println("Count: "+counter+" Line: "+lineHeight);
        }else{
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;


        //get the available size of child view
        int childLeft = this.getPaddingLeft();
        int childTop = this.getPaddingTop();
        int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();
        int childWidth = childRight - childLeft;
        int childHeight = childBottom - childTop;

        maxHeight = 0;
        curLeft = childLeft;//inicializa con el padding izquierdo
        curTop = childTop;//inicializa con el padding top

        for (int i = 0; i < count; i++) { //walk through each child, and arrange it from left to right
            View child = getChildAt(i); //cada hijo
            //int widthMargins = ((MarginLayoutParams)child.getLayoutParams()).leftMargin + ((MarginLayoutParams)child.getLayoutParams()).rightMargin;
            if (child.getVisibility() != GONE) { // si no tiene visibilidad GONE
                child.measure(MeasureSpec.makeMeasureSpec(childWidth , MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));//Get the maximum size of the child y lo asigna a las variables --->
                curWidth = child.getMeasuredWidth(); // ancho y alto maximo del hijo
                curHeight = child.getMeasuredHeight();
                //System.out.println("onlayout curHeight: "+curHeight);
                //lineHeight = curHeight;
                //wrap is reach to the end
                if (curLeft + curWidth >= childRight) { //si espacio ocupado a la izq hasta ahora + ancho del hijo >= ancho disponible
                    curLeft = childLeft; //se vuelve a inicializar el margen a la izq
                    curTop += maxHeight;  //se suma a la altura utilizada el alto consumido (alto de la fila de textview)
                    //System.out.println("onlayout curTop: "+curTop);
                    maxHeight = 0; //inicializa a 0
                    //counter++;
                }
                //do the layout
                child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight); // lo posiciona dentro del layout
                if (maxHeight < curHeight) //store the max height
                    maxHeight = curHeight;
                curLeft += curWidth; //incrementa el espacio ocupado a la derecha en cada iteracion
            }
        }
    }
}
