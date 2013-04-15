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
package de.costache.calendar;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.commons.collections.collection.UnmodifiableCollection;

import de.costache.calendar.events.*;
import de.costache.calendar.format.CalendarEventFormat;
import de.costache.calendar.format.DefaultCalendarEventFormat;
import de.costache.calendar.model.CalendarEvent;
import de.costache.calendar.ui.ContentPanel;
import de.costache.calendar.ui.HeaderPanel;
import de.costache.calendar.ui.strategy.*;
import de.costache.calendar.ui.strategy.DisplayStrategy.Type;
import de.costache.calendar.util.*;

/**
 * 
 * @author theodorcostache
 * 
 */
public class JCalendar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HeaderPanel headerPane;

	private ContentPanel contentPane;

	private Config config;

	private final List<IntervalChangedListener> intervalChangedListener;

	private JPopupMenu popupMenu;

	private CalendarEventFormat formater;

	private Calendar selectedDay;

	/**
	 * Creates a new instance of {@link JCalendar}
	 */
	public JCalendar() {
		intervalChangedListener = new ArrayList<IntervalChangedListener>();
		config = new Config();
		formater = new DefaultCalendarEventFormat();
		selectedDay = Calendar.getInstance();

		initGui();
		bindListeners();

		EventCollectionRepository.register(this);
	}

	private void initGui() {
		this.setBackground(Color.white);
		headerPane = new HeaderPanel(this);
		contentPane = new ContentPanel(this);

		headerPane.getIntervalLabel().setText(contentPane.getStrategy().getDisplayInterval());
		this.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(headerPane, c);
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.9;
		c.insets = new Insets(10, 10, 10, 10);
		add(contentPane, c);
	}

	private void bindListeners() {
		final ActionListener strategyActionListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final boolean isDay = e.getSource().equals(headerPane.getDayButton());
				final boolean isWeek = e.getSource().equals(headerPane.getWeekButton());
				final DisplayStrategy.Type type = isDay ? Type.DAY : isWeek ? Type.WEEK : Type.MONTH;

				if (getDisplayStrategy() != type)
					setDisplayStrategy(type, getSelectedDay());
			}
		};

		headerPane.getDayButton().addActionListener(strategyActionListener);
		headerPane.getWeekButton().addActionListener(strategyActionListener);
		headerPane.getMonthButton().addActionListener(strategyActionListener);

		headerPane.getScrollLeft().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final DisplayStrategy strategy = contentPane.getStrategy();
				strategy.moveIntervalLeft();
				headerPane.getIntervalLabel().setText(contentPane.getStrategy().getDisplayInterval());
				final IntervalChangedEvent event = new IntervalChangedEvent(JCalendar.this, strategy.getType(), strategy
						.getIntervalStart(), strategy.getIntervalEnd());

				for (final IntervalChangedListener listener : intervalChangedListener) {
					listener.intervalChanged(event);
				}

			}
		});

		headerPane.getScrollRight().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final DisplayStrategy strategy = contentPane.getStrategy();
				strategy.moveIntervalRight();
				headerPane.getIntervalLabel().setText(contentPane.getStrategy().getDisplayInterval());
				final IntervalChangedEvent event = new IntervalChangedEvent(JCalendar.this, strategy.getType(), strategy
						.getIntervalStart(), strategy.getIntervalEnd());

				for (final IntervalChangedListener listener : intervalChangedListener) {
					listener.intervalChanged(event);
				}
			}
		});
	}

	/**
	 * Returns the selected day in the calendar
	 * 
	 * @return
	 */
	public Date getSelectedDay() {
		return selectedDay.getTime();
	}

	/**
	 * Gets the current display strategy.
	 * 
	 * @return {@link Type}
	 */
	public DisplayStrategy.Type getDisplayStrategy() {
		return contentPane.getStrategy().getType();
	}

	/**
	 * Sets the display strategy
	 * 
	 * @param strategyType
	 *           the {@link Type} of strategy to be used
	 * @param displayDate
	 *           if not null then this value will be used as a reference for calculating the interval start
	 */
	public void setDisplayStrategy(final Type strategyType, final Date displayDate) {

		final DisplayStrategy strategy = DisplayStrategyFactory.getStrategy(contentPane, strategyType);
		contentPane.setStrategy(strategy);
		if (displayDate != null) {
			setSelectedDay(displayDate);
		}
	}

	/**
	 * Returns a {@link Collection} of selected {@link CalendarEvent}
	 * 
	 * @return an {@link UnmodifiableCollection}
	 */
	public Collection<CalendarEvent> getSelectedEvents() {
		return EventCollectionRepository.get(this).getSelectedEvents();
	}

	/**
	 * Adds a {@link IntervalChangedListener}
	 * 
	 * @param listener
	 */
	public void addIntervalChangedListener(final IntervalChangedListener listener) {
		this.intervalChangedListener.add(listener);
	}

	/**
	 * Removes the given {@link IntervalChangedListener}
	 * 
	 * @param listener
	 */
	public void removeIntervalChangedListener(final IntervalChangedListener listener) {
		this.intervalChangedListener.remove(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void addCollectionChangedListener(final ModelChangedListener listener) {
		EventCollectionRepository.get(this).addCollectionChangedListener(listener);
	}

	/**
	 * 
	 * @param listener
	 */
	public void removeCollectionChangedListener(final ModelChangedListener listener) {
		EventCollectionRepository.get(this).removeCollectionChangedListener(listener);
	}

	/**
	 * 
	 * @param selectionChangedListener
	 */
	public void addSelectionChangedListener(final SelectionChangedListener selectionChangedListener) {
		EventCollectionRepository.get(this).addSelectionChangedListener(selectionChangedListener);
	}

	/**
	 * 
	 * @param selectionChangedListener
	 */
	public void removeSelectionChangedListener(final SelectionChangedListener selectionChangedListener) {
		EventCollectionRepository.get(this).removeSelectionChangedListener(selectionChangedListener);
	}

	/**
	 * 
	 * @param intervalSelectionListener
	 */
	public void addIntervalSelectionListener(final IntervalSelectionListener intervalSelectionListener) {
		EventRepository.get().addIntervalSelectionListener(this, intervalSelectionListener);
	}

	/**
	 * 
	 * @param intervalSelectionListener
	 */
	public void removeIntervalSelectionListener(final IntervalSelectionListener intervalSelectionListener) {
		EventRepository.get().removeIntervalSelectionListener(this, intervalSelectionListener);
	}

	/**
	 * 
	 * @param event
	 */
	public void addCalendarEvent(final CalendarEvent event) {
		EventCollectionRepository.get(this).add(event);
		validate();
		repaint();
	}

	/**
	 * 
	 * @param event
	 */
	public void removeCalendarEvent(final CalendarEvent event) {
		EventCollectionRepository.get(this).remove(event);
		validate();
		repaint();
	}

	/**
	 * 
	 * @param date
	 */
	public void setSelectedDay(final Date date) {
		selectedDay = CalendarUtil.getCalendar(date, true);
		final DisplayStrategy strategy = contentPane.getStrategy();
		strategy.setIntervalStart(date);
		headerPane.getIntervalLabel().setText(strategy.getDisplayInterval());
		final IntervalChangedEvent event = new IntervalChangedEvent(JCalendar.this, strategy.getType(),
				strategy.getIntervalStart(), strategy.getIntervalEnd());

		for (final IntervalChangedListener listener : intervalChangedListener) {
			listener.intervalChanged(event);
		}
	}

	/**
	 * 
	 * @return
	 */
	public Config getConfig() {
		return config;
	}

	/**
	 * 
	 * @param config
	 */
	public void setConfig(final Config config) {
		this.config = config;
		validate();
		repaint();
	}

	/**
	 * 
	 * @return
	 */
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	/**
	 * 
	 * @param popupMenu
	 */
	public void setJPopupMenu(final JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	/**
	 * 
	 * @return
	 */
	public CalendarEventFormat getTooltipFormater() {
		return formater;
	}

	/**
	 * 
	 * @param formater
	 */
	public void setTooltipFormater(final CalendarEventFormat formater) {
		this.formater = formater;
	}
}
