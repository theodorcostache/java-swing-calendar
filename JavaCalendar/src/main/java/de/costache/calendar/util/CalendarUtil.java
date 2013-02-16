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
package de.costache.calendar.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.costache.calendar.model.JCalendarEntry;

/**
 * 
 * @author theodorcostache
 * 
 */
public class CalendarUtil {

	public static boolean isSameDay(final Date date1, final Date date2) {
		return stripTime(date1).equals(stripTime(date2));
	}

	public static boolean isSameMonth(final Calendar c1, final Calendar c2) {
		return c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
	}

	public static boolean isToday(final Date date) {
		final Calendar now = Calendar.getInstance();
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);

		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		return calendar.getTime().equals(now.getTime());
	}

	public static Calendar copyCalendar(final Calendar calendar, final boolean stripTime) {
		final Calendar c = Calendar.getInstance();
		c.setTime(calendar.getTime());
		if (stripTime) {
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
		}
		return c;
	}

	public static Calendar getCalendar(final Date date, final boolean stripTime) {
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (stripTime) {
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
		}
		return c;
	}

	public static final Date createDate(final int year, final int month, final int day, final int hour,
			final int minutes, final int seconds, final int miliseconds) {
		final Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minutes);
		calendar.set(Calendar.SECOND, seconds);
		calendar.set(Calendar.MILLISECOND, miliseconds);
		return calendar.getTime();
	}

	public static final Date stripTime(final Date date) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public static final Date createInDays(final Date from, final int amount) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(from);
		cal.add(Calendar.DATE, amount);
		return cal.getTime();
	}

	public static final Date createInWeeks(final Date date, final int amount) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.WEEK_OF_YEAR, amount);
		return cal.getTime();
	}

	public static final Date createInMonths(final Date date, final int amount) {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, amount);
		return cal.getTime();
	}

	public static Collection<Date> getDates(final Date start, final Date end) {

		final Set<Date> result = new HashSet<Date>();
		final Date endDay = stripTime(end);
		Date date = stripTime(start);
		result.add(date);
		while ((date = stripTime(createInDays(date, 1))).before(endDay))
			result.add(date);

		result.add(endDay);
		return result;
	}

	public static long getTotalSeconds(final Date date) {
		Calendar c = CalendarUtil.getCalendar(date, false);
		long seconds = c.get(Calendar.HOUR_OF_DAY) * 60 * 60;
		seconds += c.get(Calendar.MINUTE) * 60;
		seconds += c.get(Calendar.SECOND);
		return seconds;
	}

	public static int secondsToPixels(final Date date, int maxHeight) {
		long seconds = getTotalSeconds(date);
		int pixel = Math.round(seconds * maxHeight / 86400.0f);
		return pixel;
	}

	public static Map<JCalendarEntry, List<JCalendarEntry>> getConflicting(Collection<JCalendarEntry> calendarEntries) {
		List<JCalendarEntry> clonedCollection = new ArrayList<JCalendarEntry>(calendarEntries);

		Map<JCalendarEntry, List<JCalendarEntry>> conflictingEntries = new HashMap<JCalendarEntry, List<JCalendarEntry>>();

		for (int i = 0; i < clonedCollection.size(); i++) {
			JCalendarEntry entry1 = clonedCollection.get(i);
			conflictingEntries.put(entry1, new ArrayList<JCalendarEntry>());
			for (int j = 0; j < clonedCollection.size(); j++) {
				JCalendarEntry entry2 = clonedCollection.get(j);
				if (entry2.isAllDay())
					continue;
				Date startA = entry1.getStart();
				Date endA = entry1.getEnd();
				Date startB = entry2.getStart();
				Date endB = entry2.getEnd();

				boolean isStartABeforeEndB = (startA.compareTo(endB)) < 0;
				boolean isEndAAfterStartB = (endA.compareTo(startB)) > 0;

				boolean isCurrentPairOverlap = false;

				isCurrentPairOverlap = isStartABeforeEndB && isEndAAfterStartB;

				if (isCurrentPairOverlap) {
					conflictingEntries.get(entry1).add(entry2);
				}
			}

			Collections.sort(conflictingEntries.get(entry1));
		}
		Set<JCalendarEntry> keys = new HashSet<JCalendarEntry>(conflictingEntries.keySet());
		Map<JCalendarEntry, List<JCalendarEntry>> result = new HashMap<JCalendarEntry, List<JCalendarEntry>>();

		for (JCalendarEntry entry : keys) {
			Set<JCalendarEntry> visitedEntries = new HashSet<JCalendarEntry>();
			Set<JCalendarEntry> tempSet = new HashSet<JCalendarEntry>();
			copyAll(visitedEntries, tempSet, conflictingEntries, entry);
			List<JCalendarEntry> newConflictingEntriesList = new ArrayList<JCalendarEntry>(tempSet);
			Collections.sort(newConflictingEntriesList);
			result.put(entry, newConflictingEntriesList);
		}

		return result;
	}

	private static void copyAll(Set<JCalendarEntry> visitedEntries, Set<JCalendarEntry> tempSet,
			Map<JCalendarEntry, List<JCalendarEntry>> conflictingEntries, JCalendarEntry entry) {

		for (JCalendarEntry ce : conflictingEntries.get(entry)) {

			if (!visitedEntries.contains(ce)) {
				visitedEntries.add(ce);
				tempSet.addAll(conflictingEntries.get(ce));
				copyAll(visitedEntries, tempSet, conflictingEntries, ce);
			}
		}
	}

}
