package de.costache.calendar;

import de.costache.calendar.events.*;
import de.costache.calendar.model.CalendarEvent;
import de.costache.calendar.model.EventType;
import de.costache.calendar.ui.strategy.DisplayStrategy;
import de.costache.calendar.util.CalendarUtil;

import javax.imageio.ImageIO;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * @author costache
 */
public class JCalendarFrameDemo extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss:SSS");
    private final Random r = new Random();
    private final String[] names = new String[]{"Team meeting", "Code review", "Project review",
            "Telephone conference"};
    private JMenuItem exitMenuItem;
    private JCalendar jCalendar;
    private JTextArea description;
    private JPopupMenu popup;
    private JMenuItem removeMenuItem;
    private JButton removeButton;
    private JButton addButton;

    public JCalendarFrameDemo() {

        initGui();
        initData();
        bindListeners();

    }

    public static void main(final String[] args) throws MalformedObjectNameException, NullPointerException,
            InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        Locale.setDefault(Locale.GERMAN);

        final JCalendarFrameDemo fr = new JCalendarFrameDemo();
        fr.setVisible(true);
    }

    private void initGui() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        exitMenuItem = new JMenuItem("Exit");

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JToolBar toolBar = new JToolBar("Controls");
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");

        Image addImg = null;
        Image removeImg = null;
        try {
            addImg = ImageIO.read(getClass().getResource("resources/add-icon.png"));
            removeImg = ImageIO.read(getClass().getResource("resources/remove-icon.png"));
            addButton.setIcon(new ImageIcon(addImg));
            removeButton.setIcon(new ImageIcon(removeImg));
        } catch (final Exception e) {
            System.err.println(e.toString());
        }
        toolBar.add(addButton);
        toolBar.add(removeButton);

        removeMenuItem = new JMenuItem("Remove");
        //removeMenuItem.setIcon(new ImageIcon(removeImg));

        popup = new JPopupMenu();
        popup.add(removeMenuItem);
        popup.add(new JSeparator());

        description = new JTextArea();
        description.setLineWrap(true);
        description.setRows(10);
        jCalendar = new JCalendar();
        jCalendar.setPreferredSize(new Dimension(1024, 768));
        jCalendar.setJPopupMenu(popup);
        jCalendar.getConfig().setAllDayPanelVisible(false);
        JSplitPane content = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        content.add(jCalendar);
        content.add(new JScrollPane(description));

        this.getContentPane().setLayout(new BorderLayout(10, 10));
        this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
        this.getContentPane().add(content, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

    }

    private void initData() {

        final EventType type1 = new EventType();

        final EventType type2 = new EventType();
        type2.setBackgroundColor(new Color(255, 103, 0, 128));

        final EventType type3 = new EventType();
        type3.setBackgroundColor(new Color(165, 103, 230, 128));

        final EventType[] types = new EventType[3];
        types[0] = type1;
        types[1] = type2;
        types[2] = type3;

        CalendarEvent calendarEvent;
        for (int i = 0; i < 10000; i++) {
            int hour = r.nextInt(19);
            hour = hour > 17 ? 17 : hour;
            hour = hour < 8 ? 8 : hour;
            final int min = r.nextInt(59);
            final int day = r.nextInt(28);
            final int month = r.nextInt(11);
            final int year = 2010 + r.nextInt(6);
            final Date start = CalendarUtil.createDate(year, month, day, hour, min, 0, 0);
            final Date end = CalendarUtil.createDate(year, month, day, hour + 1 + r.nextInt(4), r.nextInt(59), 0, 0);
            calendarEvent = new CalendarEvent(names[r.nextInt(3)], start, end);
            calendarEvent.setType(types[r.nextInt(3)]);
            calendarEvent.setAllDay(i % 2 == 0);
            jCalendar.addCalendarEvent(calendarEvent);
        }

        Date start = CalendarUtil.createDate(2013, 1, 31, 12, 45, 0, 0);
        Date end = CalendarUtil.createDate(2013, 1, 31, 16, 35, 0, 0);
        calendarEvent = new CalendarEvent("Overlapping", start, end);
        jCalendar.addCalendarEvent(calendarEvent);

        start = CalendarUtil.createDate(2013, 4, 25, 8, 45, 0, 0);
        end = CalendarUtil.createDate(2013, 4, 25, 15, 35, 0, 0);
        calendarEvent = new CalendarEvent("Overlapping 2", start, end);
        calendarEvent.setHoliday(true);
        jCalendar.addCalendarEvent(calendarEvent);

    }

    private void bindListeners() {
        exitMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                System.exit(0);
            }
        });

        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {

                final int hour = r.nextInt(19);
                final int min = r.nextInt(59);
                final int day = r.nextInt(28);
                final int month = r.nextInt(11);
                final int year = 2010 + r.nextInt(8);
                final Date start = CalendarUtil.createDate(year, month, day, hour, min, 0, 0);
                final Date end = CalendarUtil.createDate(year, month, day, hour + 1 + r.nextInt(4), r.nextInt(59), 0, 0);
                final CalendarEvent calendarEvent = new CalendarEvent("Added ", start, end);

                jCalendar.addCalendarEvent(calendarEvent);
                jCalendar.setDisplayStrategy(DisplayStrategy.Type.DAY, start);
            }
        });

        removeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                final Collection<CalendarEvent> selected = jCalendar.getSelectedCalendarEvents();
                for (final CalendarEvent event : selected) {
                    jCalendar.removeCalendarEvent(event);
                }
            }
        });

        removeMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final Collection<CalendarEvent> selected = jCalendar.getSelectedCalendarEvents();
                for (final CalendarEvent event : selected) {
                    jCalendar.removeCalendarEvent(event);
                }
            }
        });

        jCalendar.addCollectionChangedListener(new ModelChangedListener() {

            @Override
            public void eventRemoved(final ModelChangedEvent event) {
                description.append("Event removed " + event.getCalendarEvent() + "\n");
            }

            @Override
            public void eventChanged(final ModelChangedEvent event) {
                description.append("Event changed " + event.getCalendarEvent() + "\n");
            }

            @Override
            public void eventAdded(final ModelChangedEvent event) {
                description.append("Event added " + event.getCalendarEvent() + "\n");
            }
        });

        jCalendar.addSelectionChangedListener(new SelectionChangedListener() {

            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                if (event.getCalendarEvent() != null) {
                    if (event.getCalendarEvent().isSelected()) {
                        description.append("Event selected " + event.getCalendarEvent());
                    } else {
                        description.append("Event deselected " + event.getCalendarEvent());
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

        jCalendar.addIntervalSelectionListener(new IntervalSelectionListener() {

            @Override
            public void intervalSelected(IntervalSelectionEvent event) {
                description.append("Interval selection changed " + sdf.format(event.getIntervalStart()) + " "
                        + sdf.format(event.getIntervalEnd()) + "\n");
            }
        });

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
                removeMenuItem.setEnabled(jCalendar.getSelectedCalendarEvents().size() > 0);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent arg0) {

            }
        });
    }

}
