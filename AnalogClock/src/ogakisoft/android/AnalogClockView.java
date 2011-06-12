/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package android.widget;
package ogakisoft.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.RemoteViews.RemoteView;

/**
 * This widget display an analog clock with two hands for hours and minutes.
 */
@RemoteView
public class AnalogClockView extends AbstractSurfaceView {
    private Time mCalendar;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;
    private Drawable mDial;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mAttached;
    private float mSeconds;
    private float mMinutes;
    private float mHour;
    private boolean mChanged;
    private boolean mReverse;

    public AnalogClockView(Context context) {
	this(context, null);
    }

    public AnalogClockView(Context context, AttributeSet attrs) {
	this(context, attrs, 0);
    }

    public AnalogClockView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	Resources r = context.getResources();
	if (mDial == null) {
	    mDial = r.getDrawable(R.drawable.clock_dial);
	}
	if (mHourHand == null) {
	    mHourHand = r.getDrawable(R.drawable.clock_hour);
	}
	if (mMinuteHand == null) {
	    mMinuteHand = r.getDrawable(R.drawable.clock_minute);
	}
	if (mSecondHand == null) {
	    mSecondHand = r.getDrawable(R.drawable.clock_second);
	}
	mCalendar = new Time();
	mDialWidth = mDial.getIntrinsicWidth();
	mDialHeight = mDial.getIntrinsicHeight();
	mReverse = false;
    }

    @Override
    protected void onAttachedToWindow() {
	super.onAttachedToWindow();
	if (!mAttached) {
	    mAttached = true;
	}
    }

    @Override
    protected void onDetachedFromWindow() {
	super.onDetachedFromWindow();
	if (mAttached) {
	    mAttached = false;
	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	float hScale = 1.0f;
	float vScale = 1.0f;

	if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
	    hScale = (float) widthSize / (float) mDialWidth;
	}

	if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
	    vScale = (float) heightSize / (float) mDialHeight;
	}

	float scale = Math.min(hScale, vScale);

	setMeasuredDimension(
		resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
		resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);
	mChanged = true;
    }

    @Override
    public void draw(Canvas canvas) {
	mCalendar = new Time();
	mCalendar.setToNow();
	mHour = mCalendar.hour;
	mMinutes = mCalendar.minute;
	mSeconds = mCalendar.second;

	boolean changed = mChanged;
	if (changed) {
	    mChanged = false;
	}

	int availableWidth = getRight() - getLeft(); // mRight - mLeft;
	int availableHeight = getBottom() - getTop(); // mBottom - mTop;

	int x = availableWidth / 2;
	int y = availableHeight / 2;

	final Drawable dial = mDial;
	int w = dial.getIntrinsicWidth();
	int h = dial.getIntrinsicHeight();

	boolean scaled = false;

	if (availableWidth < w || availableHeight < h) {
	    scaled = true;
	    float scale = Math.min((float) availableWidth / (float) w,
		    (float) availableHeight / (float) h);
	    canvas.save();
	    canvas.scale(scale, scale, x, y);
	}

	if (changed) {
	    dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
	}
	dial.draw(canvas);

	canvas.save();
	if (!mReverse) {
	    canvas.rotate(
		    (mHour * 60.0f + mMinutes) * 360.0f / (12.0f * 60.0f), x, y);
	} else {
	    canvas.rotate(((12 - mHour) * 60.0f + (60 - mMinutes)) * 360.0f
		    / (12.0f * 60.0f), x, y);
	}
	final Drawable hourHand = mHourHand;
	if (changed) {
	    w = hourHand.getIntrinsicWidth();
	    h = hourHand.getIntrinsicHeight();
	    hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
		    + (h / 2));
	}
	hourHand.draw(canvas);
	canvas.restore();

	canvas.save();
	if (!mReverse)
	    canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
	else
	    canvas.rotate(360.0f - (mMinutes / 60.0f * 360.0f), x, y);
	final Drawable minuteHand = mMinuteHand;
	if (changed) {
	    w = minuteHand.getIntrinsicWidth();
	    h = minuteHand.getIntrinsicHeight();
	    minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
		    + (h / 2));
	}
	minuteHand.draw(canvas);
	canvas.restore();

	canvas.save();
	if (!mReverse)
	    canvas.rotate(mSeconds / 60.0f * 360.0f, x, y);
	else
	    canvas.rotate(360.0f - (mSeconds / 60.0f * 360.0f), x, y);
	final Drawable secondHand = mSecondHand;
	if (changed) {
	    w = secondHand.getIntrinsicWidth();
	    h = secondHand.getIntrinsicHeight();
	    secondHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
		    + (h / 2));
	}
	secondHand.draw(canvas);
	canvas.restore();

	if (scaled) {
	    canvas.restore();
	}
    }
}