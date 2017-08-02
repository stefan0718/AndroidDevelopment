package project.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class StrokedTextView extends TextView {

    private TextView textView;

    public StrokedTextView(Context context) {
        super(context);
        textView = new TextView(context);
        initialize();
    }

    public StrokedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textView = new TextView(context, attrs);
        initialize();
    }

    public StrokedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        textView = new TextView(context, attrs, defStyle);
        initialize();
    }

    //initialize when create a new stroke maker
    public void initialize() {
        TextPaint paint = textView.getPaint();
        paint.setStrokeWidth(10);    //set stroke width
        paint.setStyle(Paint.Style.STROKE);
        textView.setTextColor(Color.parseColor("#ffffff"));  //set stroke color
        textView.setGravity(getGravity());
    }

    @Override
    //set textview parameters inside its parent layout
    public void setLayoutParams (ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        textView.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        CharSequence outlineText = textView.getText();  //set text
        if (outlineText == null || !outlineText.equals(this.getText())) {
            textView.setText(getText());
            postInvalidate();  //refresh textView
        }
        textView.measure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    //set left, top, right and bottom of the textView inside its parent layout
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        textView.layout(left, top, right, bottom);
    }

    @Override
    //draw stroke
    protected void onDraw(Canvas canvas) {
        textView.draw(canvas);
        super.onDraw(canvas);
    }
}
