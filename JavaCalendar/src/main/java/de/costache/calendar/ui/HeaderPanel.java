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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.costache.calendar.JCalendar;

/**
 * 
 * @author theodorcostache
 * 
 */
public class HeaderPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JButton scrollFarLeftButton;
	
	private JButton scrollLeftButton;

	private JButton scrollRightButton;

	private JButton scrollFarRightButton;

	private JLabel intervalLabel;

	private JButton dayButton;

	private JButton weekButton;

	private JButton monthButton;

	private final JCalendar owner;

	/**
	 * Creates a new instance of {@link HeaderPanel}
	 */
	public HeaderPanel(JCalendar owner) {
		super(true);
		setOpaque(false);
		this.owner = owner;
		init();
	}

	/**
	 * returns the owner
	 * 
	 * @return
	 */
	public JCalendar getOwner() {
		return owner;
	}

	private void init() {
		scrollFarLeftButton = setupButton(new JButton());
		scrollLeftButton = setupButton(new JButton());
		scrollRightButton = setupButton(new JButton());
		scrollFarRightButton = setupButton(new JButton());

		try {
			scrollFarLeftButton.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("de/costache/calendar/resources/far-left.png"))));
			scrollLeftButton.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("de/costache/calendar/resources/left.png"))));
			scrollRightButton.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("de/costache/calendar/resources/right.png"))));
			scrollFarRightButton.setIcon(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResource("de/costache/calendar/resources/far-right.png"))));
		} catch (final Exception e) {
			scrollFarLeftButton.setText("<<");
			scrollLeftButton.setText("<");
			scrollRightButton.setText(">");
			scrollFarRightButton.setText(">>");
		}

		intervalLabel = new JLabel();

		String strDay = ResourceBundle.getBundle("calendar", getLocale()).getString("day");
		String strWeek = ResourceBundle.getBundle("calendar", getLocale()).getString("week");
		String strMonth = ResourceBundle.getBundle("calendar", getLocale()).getString("month");
		dayButton = new JButton(strDay);
		weekButton = new JButton(strWeek);
		monthButton = new JButton(strMonth);

		dayButton.setOpaque(false);
		weekButton.setOpaque(false);
		monthButton.setOpaque(false);

		setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;

		c.gridx = 0;
		c.gridy = 0;
		add(scrollFarLeftButton, c);

		c.gridx = 1;
		c.gridy = 0;
		add(scrollLeftButton, c);

		c.gridx = 2;
		c.gridy = 0;
		add(scrollRightButton, c);

		c.gridx = 3;
		c.gridy = 0;
		add(scrollFarRightButton, c);

		c.gridx = 4;
		c.gridy = 0;
		c.weightx = 1.0;
		c.insets = new Insets(10, 10, 10, 10);
		add(intervalLabel, c);

		c.gridx = 5;
		c.gridy = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 10, 0);
		add(dayButton, c);

		c.gridx = 6;
		c.gridy = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 0, 10, 0);
		add(weekButton, c);

		c.gridx = 7;
		c.gridy = 0;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 0, 10, 10);
		add(monthButton, c);
	}

	/**
	 * @return the scrollFarLeft
	 */
	public JButton getScrollFarLeft() {
		return scrollFarLeftButton;
	}

	/**
	 * @return the scrollLeft
	 */
	public JButton getScrollLeft() {
		return scrollLeftButton;
	}

	/**
	 * @return the scrollRight
	 */
	public JButton getScrollRight() {
		return scrollRightButton;
	}

	/**
	 * @return the scrollFarRight
	 */
	public JButton getScrollFarRight() {
		return scrollFarRightButton;
	}

	/**
	 * @return the dayButton
	 */
	public JButton getDayButton() {
		return dayButton;
	}

	/**
	 * @return the weekButton
	 */
	public JButton getWeekButton() {
		return weekButton;
	}

	/**
	 * @return the monthButton
	 */
	public JButton getMonthButton() {
		return monthButton;
	}

	/**
	 * @return the intervalLabel
	 */
	public JLabel getIntervalLabel() {
		return intervalLabel;
	}

	private JButton setupButton(JButton button) {
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);

		return button;
	}
}
