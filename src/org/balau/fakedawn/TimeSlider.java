/**
 *   Copyright 2012 Francesco Balducci
 *
 *   This file is part of FakeDawn.
 *
 *   FakeDawn is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   FakeDawn is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with FakeDawn.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.balau.fakedawn;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author francesco
 *
 */
public class TimeSlider extends IntervalSlider {

	private Listener m_listener;
	private int m_color = 0xFFFFFFFF;
	private Paint m_paint;
	
	private DawnTime m_startTime;
	private DawnTime m_leftTime;
	private DawnTime m_rightTime;
	private int m_spanMinutes;
	
	private void construct()
	{
		m_startTime = new DawnTime(0);
		m_leftTime = new DawnTime(0);
		m_rightTime = new DawnTime(0);
		m_spanMinutes = 30;
		
		m_listener = new Listener();
		setOnClickListener(m_listener);
		setOnCursorsMovedListener(m_listener);
		
		m_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		m_paint.setStyle(Paint.Style.FILL_AND_STROKE);
		m_paint.setStrokeWidth(0);
	}
	
	public TimeSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		construct();
	}

	public TimeSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		construct();
	}

	public TimeSlider(Context context) {
		super(context);
		construct();
	}

	private void updateView()
	{
		m_listener.onCursorsMoved(this, 0.0F, 0.0F);
	}
	
	public void setRectColor(int color)
	{
		m_color = color;
		updateView();
	}
	
	public int setStartTime(int hour, int minute)
	{
		int minutes = minute + hour*60;
		int minMinutes = m_leftTime.getMinutes();
		if(minutes < minMinutes)
			minutes = minMinutes;
		m_startTime = new DawnTime(minutes);
		
		int minSpan = m_rightTime.getMinutes() - minutes;
		if(m_spanMinutes < minSpan)
			m_spanMinutes = minSpan;
		
		updateView();

		return minutes;
	}
	
	public int setSpanTime(int minutes)
	{
		int minSpan = m_rightTime.getMinutes() - m_leftTime.getMinutes();
		if(minutes < minSpan)
			minutes = minSpan;
		m_spanMinutes = minutes;
		
		int minStart = m_rightTime.getMinutes() - minutes;
		if(m_startTime.getMinutes() < minStart)
			m_startTime = new DawnTime(minStart);

		updateView();

		return minutes;
	}
	
	public void setLeftTime(int hour, int minute)
	{
		m_leftTime = new DawnTime(hour, minute);
		int leftMinutes = m_leftTime.getMinutes();
		if(m_rightTime.getMinutes() < leftMinutes)
			m_rightTime = new DawnTime(leftMinutes);
		if(leftMinutes < m_startTime.getMinutes())
			m_startTime = new DawnTime(leftMinutes);
		if(m_rightTime.getMinutes() > m_startTime.getMinutes() + m_spanMinutes)
			m_spanMinutes = m_rightTime.getMinutes() - m_startTime.getMinutes();
		setRightPos(
				((float)(m_rightTime.getMinutes()-m_startTime.getMinutes())) /
				((float)m_spanMinutes));
		setLeftPos(
				((float)(leftMinutes-m_startTime.getMinutes())) /
				((float)m_spanMinutes));
		updateView();
	}
	
	public void setRightTime(int hour, int minute)
	{
		m_rightTime = new DawnTime(hour, minute);
		int rightMinutes = m_rightTime.getMinutes();
		if(m_leftTime.getMinutes() > rightMinutes)
			m_leftTime = new DawnTime(rightMinutes);
		if(m_leftTime.getMinutes() < m_startTime.getMinutes())
			m_startTime = new DawnTime(m_leftTime.getMinutes());
		if(rightMinutes > m_startTime.getMinutes() + m_spanMinutes)
			m_spanMinutes = rightMinutes - m_startTime.getMinutes();
		setLeftPos(
				((float)(m_leftTime.getMinutes()-m_startTime.getMinutes())) /
				((float)m_spanMinutes));
		setRightPos(
				((float)(rightMinutes-m_startTime.getMinutes())) /
				((float)m_spanMinutes));
		updateView();
	}
	
	private class Listener implements OnClickListener, OnCursorsMovedListener {

		public void onClick(View v) {

		}

		public void onCursorsMoved(IntervalSlider i, float leftMovement,
				float rightMovement) {
			
			m_leftTime = new DawnTime(
					m_startTime.getMinutes() +
					(int)Math.round(m_spanMinutes*i.getLeftPos()));
			m_rightTime = new DawnTime(
					m_startTime.getMinutes() +
					(int)Math.round(m_spanMinutes*i.getRightPos()));
			setLeftText(m_leftTime.toString());
			setRightText(m_rightTime.toString());
			
			int colors[] = new int[] {0xFF000000, 0xFF000000, m_color, m_color};
			Shader s = new SweepGradient(0, 0, colors, null);
			s = new LinearGradient(
					getLeftPos()*getWidth()-0.1F, 0,
					getRightPos()*getWidth()+0.1F, 0, 
					0xFF000000, m_color,
					Shader.TileMode.CLAMP);
			m_paint.setShader(s);
			setRectPaint(m_paint);
		}

	}

	/* (non-Javadoc)
	 * @see com.balau.helloandroid.IntervalSlider#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		updateView();
	}
	
	private class DawnTime
	{
		public DawnTime(int hour, int minute)
		{
			m_hour = hour;
			m_minute = minute;
		}
		
		public DawnTime(int minutes)
		{
			m_hour = minutes/60;
			m_minute = minutes - m_hour*60;
		}
		
		private int m_hour;
		private int m_minute;
		
		public int getMinute()
		{
			return m_minute;
		}
		
		public int getMinutes()
		{
			return m_minute + 60*m_hour;
		}
		
		public int getHour()
		{
			return m_hour;
		}
		
		public int getHourOfDay()
		{
			return m_hour % 24;
		}
		
		@Override
		public String toString()
		{
			return String.format("%02d:%02d", getHourOfDay(), getMinute());
		}
	}
}