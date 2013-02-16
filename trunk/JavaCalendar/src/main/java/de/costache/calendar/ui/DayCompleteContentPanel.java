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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import de.costache.calendar.Config;
import de.costache.calendar.JCalendar;
import de.costache.calendar.model.JCalendarEntry;
import de.costache.calendar.util.EntryCollection;
import de.costache.calendar.util.EntryCollectionRepository;
import de.costache.calendar.util.GraphicsUtil;

/**
 * s
 * 
 * @author theodorcostache
 * 
 */
public class DayCompleteContentPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DayPanel owner;

	public DayCompleteContentPanel(DayPanel owner) {
		this.owner = owner;
		setOpaque(false);
		setBorder(BorderFactory.createLineBorder(owner.getOwner().getConfig().getLineColor()));

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				super.mouseClicked(e);
				final JCalendar calendar = DayCompleteContentPanel.this.owner.getOwner();
				final JCalendarEntry entry = getEntry(e.getX(), e.getY());

				if (e.getClickCount() == 1) {

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

				for (MouseListener ml : DayCompleteContentPanel.this.owner.getOwner().getMouseListeners()) {
					ml.mouseClicked(e);
				}

				e.consume();
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				super.mouseReleased(e);
				final JCalendar calendar = DayCompleteContentPanel.this.owner.getOwner();
				if (e.isPopupTrigger() && calendar.getPopupMenu() != null) {
					calendar.getPopupMenu().show(DayCompleteContentPanel.this, e.getX(), e.getY());
				}
				for (MouseListener ml : DayCompleteContentPanel.this.owner.getOwner().getMouseListeners()) {
					ml.mouseReleased(e);
				}

				e.consume();
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				super.mousePressed(e);
				final JCalendar calendar = DayCompleteContentPanel.this.owner.getOwner();
				if (e.isPopupTrigger() && calendar.getPopupMenu() != null) {
					calendar.getPopupMenu().show(DayCompleteContentPanel.this, e.getX(), e.getY());
				}
				for (MouseListener ml : DayCompleteContentPanel.this.owner.getOwner().getMouseListeners()) {
					ml.mousePressed(e);
				}
				e.consume();
			}

		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				final JCalendar calendar = DayCompleteContentPanel.this.owner.getOwner();
				final JCalendarEntry entry = getEntry(e.getX(), e.getY());
				if (entry != null) {
					setToolTipText(calendar.getFormater().format(entry));
				} else {
					setToolTipText(null);
				}
			}
		});

	}

	public DayPanel getOwner() {
		return owner;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		drawFullDayEntries((Graphics2D) g);
	}

	private void drawFullDayEntries(final Graphics2D graphics2d) {

		final EntryCollection entryCollection = EntryCollectionRepository.get(owner.getOwner());
		final Collection<JCalendarEntry> entries = entryCollection.getCalendarEntries(owner.getDate());
		int pos = 2;
		if (entries.size() > 0) {

			final Config config = owner.getOwner().getConfig();

			for (final JCalendarEntry entry : entries) {
				if (!entry.isAllDay())
					continue;
				Color bgColor = entry.getType().getBackgroundColor();
				bgColor = bgColor == null ? config.getEntryDefaultBackgroundColor() : bgColor;
				Color fgColor = entry.getType().getForegroundColor();
				fgColor = fgColor == null ? config.getEntryDefaultForegroundColor() : fgColor;
				graphics2d.setColor(!entry.isSelected() ? bgColor : bgColor.darker().darker());
				graphics2d.fillRect(2, pos, getWidth() - 4, 15);

				final String entryString = entry.getSummary();
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
			setPreferredSize(new Dimension(0, pos));
			revalidate();
		}
	}

	private JCalendarEntry getEntry(final int x, final int y) {

		final EntryCollection entryCollection = EntryCollectionRepository.get(owner.getOwner());
		final Collection<JCalendarEntry> entries = entryCollection.getCalendarEntries(owner.getDate());

		int pos = 2;
		if (entries.size() > 0) {
			for (final JCalendarEntry entry : entries) {
				if (!entry.isAllDay())
					continue;
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
