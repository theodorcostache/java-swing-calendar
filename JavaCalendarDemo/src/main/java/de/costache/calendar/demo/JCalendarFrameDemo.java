package de.costache.calendar.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.costache.calendar.JCalendar;
import de.costache.calendar.events.CollectionChangedEvent;
import de.costache.calendar.events.CollectionChangedListener;
import de.costache.calendar.events.IntervalChangedEvent;
import de.costache.calendar.events.IntervalChangedListener;
import de.costache.calendar.events.SelectionChangedEvent;
import de.costache.calendar.events.SelectionChangedListener;
import de.costache.calendar.model.JCalendarEntry;
import de.costache.calendar.model.JCalendarEntryType;
import de.costache.calendar.ui.strategy.DisplayStrategy.Type;
import de.costache.calendar.util.CalendarUtil;

/**
 * @author costache
 * 
 */
public class JCalendarFrameDemo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JMenuBar menuBar;
	private final JMenu fileMenu;
	private final JMenuItem exitMenuItem;
	private final JCalendar jCalendar;
	private final JSplitPane content;
	private final JToolBar toolBar;

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss:SSS");

	private final JTextArea description;

	private final JPopupMenu popup;

	private final String[] names = new String[] { "Meeting with John", "Shopping", "Business meeting",
			"Telephone conference" };

	public JCalendarFrameDemo() {

		final Random r = new Random();

		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);

		setJMenuBar(menuBar);

		toolBar = new JToolBar("Controls");
		final JButton add = new JButton("Add");
		final JButton remove = new JButton("Remove");

		Image addImg = null;
		Image removeImg = null;
		try {
			addImg = ImageIO.read(getClass().getResource("resources/add-icon.png"));
			removeImg = ImageIO.read(getClass().getResource("resources/remove-icon.png"));
			add.setIcon(new ImageIcon(addImg));
			remove.setIcon(new ImageIcon(removeImg));
		} catch (final Exception e) {

		}

		add.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {

				final int hour = r.nextInt(19);
				final int min = r.nextInt(59);
				final int day = r.nextInt(28);
				final int month = r.nextInt(11);
				final int year = 2010 + r.nextInt(8);
				final Date start = CalendarUtil.createDate(year, month, day, hour, min, 0, 0);
				final Date end = CalendarUtil.createDate(year, month, day, hour + 1 + r.nextInt(4), r.nextInt(59), 0, 0);
				final JCalendarEntry calendarEntry = new JCalendarEntry("Added ", start, end);

				jCalendar.addCalendarEntry(calendarEntry);
				jCalendar.setDisplayStrategy(Type.DAY, start);
			}
		});

		remove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				final Collection<JCalendarEntry> selected = jCalendar.getSelectedEntries();
				for (final JCalendarEntry entry : selected) {
					jCalendar.removeCalendarEntry(entry);
				}
			}
		});

		popup = new JPopupMenu();
		final JMenuItem removeMi = new JMenuItem("Remove");
		removeMi.setIcon(new ImageIcon(removeImg));
		removeMi.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				final Collection<JCalendarEntry> selected = jCalendar.getSelectedEntries();
				for (final JCalendarEntry entry : selected) {
					jCalendar.removeCalendarEntry(entry);
				}
			}
		});

		popup.add(removeMi);
		popup.add(new JSeparator());
		toolBar.add(add);
		toolBar.add(remove);

		description = new JTextArea();
		description.setLineWrap(true);
		description.setRows(10);
		content = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		jCalendar = new JCalendar();
		jCalendar.setPreferredSize(new Dimension(1024, 768));

		this.getContentPane().setLayout(new BorderLayout(10, 10));

		content.add(jCalendar);
		content.add(new JScrollPane(description));
		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
		this.getContentPane().add(content, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();

		final JCalendarEntryType type1 = new JCalendarEntryType();

		final JCalendarEntryType type2 = new JCalendarEntryType();
		type2.setBackgroundColor(new Color(255, 103, 0, 128));

		final JCalendarEntryType type3 = new JCalendarEntryType();
		type3.setBackgroundColor(new Color(165, 103, 230, 128));

		final JCalendarEntryType[] types = new JCalendarEntryType[3];
		types[0] = type1;
		types[1] = type2;
		types[2] = type3;

		JCalendarEntry calendarEntry;
		for (int i = 0; i < 10000; i++) {
			final int hour = r.nextInt(19);
			final int min = r.nextInt(59);
			final int day = r.nextInt(28);
			final int month = r.nextInt(11);
			final int year = 2010 + r.nextInt(6);
			final Date start = CalendarUtil.createDate(year, month, day, hour, min, 0, 0);
			final Date end = CalendarUtil.createDate(year, month, day, hour + 1 + r.nextInt(4), r.nextInt(59), 0, 0);
			calendarEntry = new JCalendarEntry(names[r.nextInt(3)], start, end);
			calendarEntry.setType(types[r.nextInt(3)]);
			calendarEntry.setAllDay(i % 2 == 0);
			jCalendar.addCalendarEntry(calendarEntry);
		}

		Date start = CalendarUtil.createDate(2013, 1, 31, 12, 45, 0, 0);
		Date end = CalendarUtil.createDate(2013, 1, 31, 16, 35, 0, 0);
		calendarEntry = new JCalendarEntry("Overlapping", start, end);
		jCalendar.addCalendarEntry(calendarEntry);

		start = CalendarUtil.createDate(2013, 1, 31, 8, 45, 0, 0);
		end = CalendarUtil.createDate(2013, 1, 31, 15, 35, 0, 0);
		calendarEntry = new JCalendarEntry("Overlapping 2", start, end);
		jCalendar.addCalendarEntry(calendarEntry);
		jCalendar.setJPopupMenu(popup);

		jCalendar.addCollectionChangedListener(new CollectionChangedListener() {

			@Override
			public void itemRemoved(final CollectionChangedEvent event) {
				description.append("Entry removed " + event.getValue() + "\n");
			}

			@Override
			public void itemChanged(final CollectionChangedEvent event) {
				description.append("Entry changed " + event.getValue() + "\n");
			}

			@Override
			public void itemAdded(final CollectionChangedEvent event) {
				description.append("Entry added " + event.getValue() + "\n");
			}
		});

		jCalendar.addSelectionChangedListener(new SelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				if (event.getEntry() != null) {
					if (event.getEntry().isSelected()) {
						description.append("Entry selected " + event.getEntry());
					} else {
						description.append("Entry deselected " + event.getEntry());
					}
				} else {
					description.append("Selection cleared");
				}
				description.append("\n");
			}
		});

		jCalendar.addIntervalChangedListener(new IntervalChangedListener() {

			@Override
			public void intervalChanged(final IntervalChangedEvent event) {
				description.append("Interval changed " + sdf.format(event.getIntervalStart()) + " "
						+ sdf.format(event.getIntervalEnd()) + "\n");
			}
		});

		jCalendar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				description.append("Mouse clicked " + e.toString() + "\n");
			}
		});

		popup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				removeMi.setEnabled(jCalendar.getSelectedEntries().size() > 0);
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public static void main(final String[] args) {
		Locale.setDefault(Locale.FRENCH);
		final JCalendarFrameDemo frameTest = new JCalendarFrameDemo();
		frameTest.setVisible(true);
	}
}
