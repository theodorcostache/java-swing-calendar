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
package de.costache.calendar.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JToolTip;

import de.costache.calendar.Config;
import de.costache.calendar.JCalendar;
import de.costache.calendar.model.JCalendarEntry;
import de.costache.calendar.ui.strategy.DisplayStrategy.Type;
import de.costache.calendar.util.CalendarUtil;
import de.costache.calendar.util.EntryCollection;
import de.costache.calendar.util.EntryCollectionRepository;
import de.costache.calendar.util.GraphicsUtil;

/**
 * 
 * @author theodorcostache
 * 
 */
public class DayContentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	private final DayPanel owner;

	private final JToolTip toolTip = new JToolTip();

	/**
	 * Creates a new instance of {@link DayContentPanel}
	 */
	public DayContentPanel(final DayPanel owner) {
		super(true);
		setOpaque(false);
		this.owner = owner;

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				super.mouseClicked(e);
				final JCalendar calendar = DayContentPanel.this.owner.getOwner();
				final boolean isSelectedStrategyMonth = calendar.getDisplayStrategy() == Type.MONTH;
				final JCalendarEntry entry = isSelectedStrategyMonth ? getEntryForMonth(e.getX(), e.getY())
						: getNotMonthEntry(e.getX(), e.getY());

				if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON1) {

					final EntryCollection entries = EntryCollectionRepository.get(calendar);

					if (!e.isControlDown()) {
						entries.clearSelected(entry, true);
					}
					if (entry != null) {
						entry.setSelected(!entry.isSelected());
						if (entry.isSelected()) {
							entries.addSelected(entry);
						} else {
							entries.removeSelected(entry);
						}
					}

					calendar.validate();
					calendar.repaint();

				}

				for (MouseListener ml : DayContentPanel.this.owner.getOwner().getMouseListeners()) {
					ml.mouseClicked(e);
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				super.mouseReleased(e);
				final JCalendar calendar = DayContentPanel.this.owner.getOwner();
				if (e.isPopupTrigger() && calendar.getPopupMenu() != null) {
					calendar.getPopupMenu().show(DayContentPanel.this, e.getX(), e.getY());
				}
				for (MouseListener ml : DayContentPanel.this.owner.getOwner().getMouseListeners()) {
					ml.mouseReleased(e);
				}
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				super.mousePressed(e);
				final JCalendar calendar = DayContentPanel.this.owner.getOwner();
				if (e.isPopupTrigger() && calendar.getPopupMenu() != null) {
					calendar.getPopupMenu().show(DayContentPanel.this, e.getX(), e.getY());
				}
				for (MouseListener ml : DayContentPanel.this.owner.getOwner().getMouseListeners()) {
					ml.mousePressed(e);
				}
			}

		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);

				final JCalendar calendar = DayContentPanel.this.owner.getOwner();
				final boolean isSelectedStrategyMonth = calendar.getDisplayStrategy() == Type.MONTH;
				final JCalendarEntry entry = isSelectedStrategyMonth ? getEntryForMonth(e.getX(), e.getY())
						: getNotMonthEntry(e.getX(), e.getY());
				if (entry != null) {
					setToolTipText(calendar.getFormater().format(entry));
				} else {
					setToolTipText(null);
				}
			}
		});
	}

	/**
	 * returns the owner
	 * 
	 * @return
	 */
	public DayPanel getOwner() {
		return owner;
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		drawBackground((Graphics2D) g);
		if (owner.getOwner().getDisplayStrategy() != Type.MONTH) {
			drawCalendarEntries((Graphics2D) g);
		} else {
			drawCalendarEntriesMonth((Graphics2D) g);
		}
	}

	private void drawBackground(final Graphics2D graphics2d) {
		final int height = getHeight();
		final int width = getWidth();
		final JCalendar calendar = owner.getOwner();
		final Config config = calendar.getConfig();
		final Color outsideWorkingHoursColor = config.getOutsideWorkingHoursColor();
		final Color dayDisableBackgroundColor = config.getDayDisabledBackgroundColor();
		final int workingHoursRectHeight = config.getWorkingHoursStart() * 60;
		final int workingHoursEndRectYStart = config.getWorkingHoursEnd() * 60;
		final int workingHoursEndHeight = height - config.getWorkingHoursEnd() * 60;
		final boolean isSelectedStrategyMonth = calendar.getDisplayStrategy() == Type.MONTH;

		if (isEnabled()) {
			if (!isSelectedStrategyMonth) {
				graphics2d.setColor(outsideWorkingHoursColor);
				graphics2d.fillRect(0, 0, width, workingHoursRectHeight);
				graphics2d.fillRect(0, workingHoursEndRectYStart, width, workingHoursEndHeight);
			}
		} else {
			if (isSelectedStrategyMonth) {
				graphics2d.setColor(dayDisableBackgroundColor);
				graphics2d.fillRect(0, 0, width, height);
			}
		}

		graphics2d.setColor(config.getLineColor());
		graphics2d.drawRect(0, 0, width, height);

		if (!isSelectedStrategyMonth) {
			int y = 0;

			for (int i = 0; i < 24; i++) {
				y += 60;
				graphics2d.setColor(config.getMiddleLineColor());
				graphics2d.drawLine(0, y - 30, getWidth(), y - 30);
				graphics2d.setColor(config.getLineColor());
				graphics2d.drawLine(0, y, getWidth(), y);
			}
		}

	}

	private void drawCalendarEntries(final Graphics2D graphics2d) {

		final EntryCollection entryCollection = EntryCollectionRepository.get(owner.getOwner());
		final Collection<JCalendarEntry> entries = entryCollection.getCalendarEntries(owner.getDate());

		final Map<JCalendarEntry, List<JCalendarEntry>> conflictingEntries = CalendarUtil.getConflicting(entries);

		final Config config = owner.getOwner().getConfig();
		if (entries.size() > 0) {
			for (final JCalendarEntry entry : entries) {
				if (entry.isAllDay())
					continue;
				Color bgColor = entry.getType().getBackgroundColor();
				bgColor = bgColor == null ? config.getEntryDefaultBackgroundColor() : bgColor;
				Color fgColor = entry.getType().getForegroundColor();
				fgColor = fgColor == null ? config.getEntryDefaultForegroundColor() : fgColor;

				graphics2d.setColor(!entry.isSelected() ? bgColor : bgColor.darker().darker());
				int entryYStart = 0;

				final boolean isSameStartDay = CalendarUtil.isSameDay(entry.getStart(), owner.getDate());
				if (isSameStartDay) {
					entryYStart = CalendarUtil.secondsToPixels(entry.getStart(), getHeight());
				}

				int entryYEnd = getHeight();
				if (CalendarUtil.isSameDay(entry.getEnd(), owner.getDate())) {
					entryYEnd = CalendarUtil.secondsToPixels(entry.getEnd(), getHeight());
				}

				final int conflictIndex = conflictingEntries.get(entry).indexOf(entry);
				final int conflictingEntriesSize = conflictingEntries.get(entry).size();

				graphics2d.fillRoundRect(conflictIndex * (getWidth() - 4) / conflictingEntriesSize, entryYStart,
						(getWidth() - 4) / conflictingEntriesSize - 2, entryYEnd - entryYStart, 12, 12);
				final String entryString = sdf.format(entry.getStart()) + " " + sdf.format(entry.getEnd()) + " "
						+ entry.getSummary();

				graphics2d.setFont(new Font("Verdana", Font.BOLD, 9));
				graphics2d.setColor(!entry.isSelected() ? fgColor : Color.white);

				GraphicsUtil.drawString(graphics2d, entryString, conflictIndex * (getWidth() - 4)
						/ conflictingEntriesSize + 3, entryYStart + 11, (getWidth() - 4) / conflictingEntriesSize - 3,
						entryYEnd - entryYStart);

			}
		}
	}

	private JCalendarEntry getNotMonthEntry(final int x, final int y) {

		final EntryCollection entryCollection = EntryCollectionRepository.get(owner.getOwner());
		final Collection<JCalendarEntry> entries = entryCollection.getCalendarEntries(owner.getDate());

		final Map<JCalendarEntry, List<JCalendarEntry>> conflictingEntries = CalendarUtil.getConflicting(entries);

		if (entries.size() > 0) {
			for (final JCalendarEntry entry : entries) {
				if (entry.isAllDay())
					continue;
				int entryYStart = 0;

				final boolean isSameStartDay = CalendarUtil.isSameDay(entry.getStart(), owner.getDate());
				if (isSameStartDay) {
					entryYStart = CalendarUtil.secondsToPixels(entry.getStart(), getHeight());
				}

				int entryYEnd = getHeight();
				if (CalendarUtil.isSameDay(entry.getEnd(), owner.getDate())) {
					entryYEnd = CalendarUtil.secondsToPixels(entry.getEnd(), getHeight());
				}

				final int conflictIndex = conflictingEntries.get(entry).indexOf(entry);
				final int conflictingEntriesSize = conflictingEntries.get(entry).size();

				final int rectXStart = conflictIndex * (getWidth() - 4) / conflictingEntriesSize;
				final int rectYStart = entryYStart;

				final int rectWidth = (getWidth() - 4) / conflictingEntriesSize - 2;

				final int rectHeight = entryYEnd - entryYStart;

				final Rectangle r = new Rectangle(rectXStart, rectYStart, rectWidth, rectHeight);
				if (r.contains(x, y)) {
					return entry;
				}
			}
		}
		return null;
	}

	private void drawCalendarEntriesMonth(final Graphics2D graphics2d) {

		final EntryCollection entryCollection = EntryCollectionRepository.get(owner.getOwner());
		final Collection<JCalendarEntry> entries = entryCollection.getCalendarEntries(owner.getDate());
		int pos = 2;
		if (entries.size() > 0) {
			final Config config = owner.getOwner().getConfig();
			for (final JCalendarEntry entry : entries) {

				Color bgColor = entry.getType().getBackgroundColor();
				bgColor = bgColor == null ? config.getEntryDefaultBackgroundColor() : bgColor;
				Color fgColor = entry.getType().getForegroundColor();
				fgColor = fgColor == null ? config.getEntryDefaultForegroundColor() : fgColor;
				graphics2d.setColor(!entry.isSelected() ? bgColor : bgColor.darker().darker());
				graphics2d.fillRect(2, pos, getWidth() - 4, 15);

				final String entryString = sdf.format(entry.getStart()) + " " + sdf.format(entry.getEnd()) + " "
						+ entry.getSummary();
				int fontSize = Math.round(getHeight() * 0.5f);
				fontSize = fontSize > 9 ? 9 : fontSize;

				final Font font = new Font("Verdana", Font.BOLD, fontSize);
				final FontMetrics metrics = graphics2d.getFontMetrics(font);
				graphics2d.setFont(font);

				graphics2d.setColor(!entry.isSelected() ? fgColor : Color.white);
				GraphicsUtil.drawTrimmedString(graphics2d, entryString, 6,
						pos + (13 / 2 + metrics.getHeight() / 2) - 2, getWidth());

				pos += 17;
			}
		}
	}

	private JCalendarEntry getEntryForMonth(final int x, final int y) {

		final EntryCollection entryCollection = EntryCollectionRepository.get(owner.getOwner());
		final Collection<JCalendarEntry> entries = entryCollection.getCalendarEntries(owner.getDate());

		int pos = 2;
		if (entries.size() > 0) {
			for (final JCalendarEntry entry : entries) {

				final int rectXStart = 2;
				final int rectYStart = pos;

				final int rectWidth = getWidth() - 4;

				final int rectHeight = 15;

				final Rectangle r = new Rectangle(rectXStart, rectYStart, rectWidth, rectHeight);
				if (r.contains(x, y)) {
					return entry;
				}

				pos += 17;

			}
		}
		return null;
	}

}
