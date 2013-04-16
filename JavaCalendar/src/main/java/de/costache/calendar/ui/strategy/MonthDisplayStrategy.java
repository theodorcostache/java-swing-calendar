/**
 * Copyright 2013 Theodor Costache
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. 
 */
package de.costache.calendar.ui.strategy;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;

import de.costache.calendar.ui.ContentPanel;
import de.costache.calendar.ui.DayPanel;
import de.costache.calendar.util.CalendarUtil;

/**
 * 
 * @author theodorcostache
 * 
 */
class MonthDisplayStrategy implements DisplayStrategy {

	private Calendar start;
	private final ContentPanel parent;
	private final SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
	private final DayPanel[] days = new DayPanel[35];
	private JPanel displayPanel;

	/**
	 * Creates a new instance of MonthDisplayStrategy
	 * 
	 * @param parent
	 */
	public MonthDisplayStrategy(final ContentPanel parent) {
		this.parent = parent;
		init();
	}

	@Override
	public void init() {
		if (start == null)
			start = CalendarUtil.getCalendar(
					parent.getOwner().getSelectedDay(), true);
		start.set(Calendar.DAY_OF_MONTH, 1);

		displayPanel = new JPanel(true);
		displayPanel.setOpaque(false);
		displayPanel.setLayout(new GridLayout(5, 7));
		final Calendar c = CalendarUtil.copyCalendar(start, true);
		final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, -dayOfWeek + c.getFirstDayOfWeek());
		for (int i = 0; i < 35; i++) {
			days[i] = new DayPanel(parent.getOwner(), c.getTime(), 0.1f);
			days[i].setEnabled(CalendarUtil.isSameMonth(start, c));
			displayPanel.add(days[i].layout());
			c.add(Calendar.DATE, 1);
		}

	}

	@Override
	public void display() {
		parent.removeAll();
		parent.setLayout(new BorderLayout());
		parent.add(displayPanel, BorderLayout.CENTER);
		parent.validate();
		parent.repaint();
	}

	@Override
	public void moveIntervalLeft() {
		Date currentDay = parent.getOwner().getSelectedDay();
		parent.getOwner().setSelectedDay(
				CalendarUtil.createInMonths(currentDay, -1));
		start.setTime(CalendarUtil.createInMonths(currentDay, -1));
		start.set(Calendar.DAY_OF_MONTH, 1);
		final Calendar c = CalendarUtil.copyCalendar(start, true);
		final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, -dayOfWeek + start.getFirstDayOfWeek());
		for (int i = 0; i < 35; i++) {
			days[i].setDate(c.getTime());
			days[i].setEnabled(CalendarUtil.isSameMonth(start, c));
			c.add(Calendar.DATE, 1);
		}

		parent.validate();
		parent.repaint();
	}

	@Override
	public void moveIntervalRight() {
		Date currentDay = parent.getOwner().getSelectedDay();
		parent.getOwner().setSelectedDay(
				CalendarUtil.createInMonths(currentDay, 1));
		start.setTime(CalendarUtil.createInMonths(currentDay, 1));
		start.set(Calendar.DAY_OF_MONTH, 1);
		final Calendar c = CalendarUtil.copyCalendar(start, true);
		final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, -dayOfWeek + start.getFirstDayOfWeek());

		for (int i = 0; i < 35; i++) {
			days[i].setDate(c.getTime());
			days[i].setEnabled(CalendarUtil.isSameMonth(start, c));
			c.add(Calendar.DATE, 1);
		}

		parent.validate();
		parent.repaint();
	}

	@Override
	public void setIntervalStart(Date date) {

		if (date.compareTo(getIntervalStart()) < 0) {
			start = CalendarUtil.getCalendar(date, true);
			start.set(Calendar.DAY_OF_MONTH, 1);
			final Calendar c = CalendarUtil.copyCalendar(start, true);
			final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			c.add(Calendar.DATE, -dayOfWeek + c.getFirstDayOfWeek());
			for (int i = 0; i < 35; i++) {
				days[i].setDate(c.getTime());
				days[i].setEnabled(CalendarUtil.isSameMonth(start, c));
				c.add(Calendar.DATE, 1);
			}

			parent.validate();
			parent.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mediamarkt.calendar.strategy.DisplayStrategy#getDisplayInterval()
	 */
	@Override
	public String getDisplayInterval() {
		return sdf.format(start.getTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mediamarkt.calendar.strategy.DisplayStrategy#getType()
	 */
	@Override
	public Type getType() {
		return Type.MONTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mediamarkt.calendar.strategy.DisplayStrategy#getIntervalStart()
	 */
	@Override
	public Date getIntervalStart() {
		final Calendar c = CalendarUtil.copyCalendar(start, true);
		final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, -dayOfWeek + c.getFirstDayOfWeek());
		return c.getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mediamarkt.calendar.strategy.DisplayStrategy#getIntervalEnd()
	 */
	@Override
	public Date getIntervalEnd() {
		final Calendar c = CalendarUtil.copyCalendar(start, true);
		final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DATE, -dayOfWeek + c.getFirstDayOfWeek());
		c.add(Calendar.DATE, 35);
		c.add(Calendar.SECOND, -1);
		return c.getTime();
	}
}
