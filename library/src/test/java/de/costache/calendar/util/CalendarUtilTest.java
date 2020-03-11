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

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author theodorcostache
 * 
 */
public class CalendarUtilTest {

	/**
	 * Test method for
	 * {@link de.costache.calendar.util.CalendarUtil#isSameMonth(java.util.Calendar, java.util.Calendar)}
	 * .
	 */
	@Test
	public void testIsSameMonth() {
		boolean result = CalendarUtil.isSameMonth(Calendar.getInstance(), Calendar.getInstance());
		Assert.assertTrue(result);
	}

	/**
	 * Test method for
	 * {@link de.costache.calendar.util.CalendarUtil#copyCalendar(java.util.Calendar, boolean)}
	 * .
	 */
	@Test
	public void testCopyCalendar() {
		Date date = CalendarUtil.createDate(2012, 2, 1, 10, 30, 40, 150);
		Calendar original = CalendarUtil.getCalendar(date, false);
		Calendar copy = CalendarUtil.copyCalendar(original, false);

		Assert.assertTrue(original.getTime().compareTo(copy.getTime()) == 0);

	}

	/**
	 * Test method for
	 * {@link de.costache.calendar.util.CalendarUtil#getTotalSeconds(java.util.Date)}
	 * .
	 */
	@Test
	public void testGetTotalSeconds() {
		Date date = CalendarUtil.createDate(2013, 10, 10, 20, 45, 50, 0);
		long actual = CalendarUtil.getTotalSeconds(date);
		long expected = 74750;

		Assert.assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link de.costache.calendar.util.CalendarUtil#secondsToPixels(java.util.Date, int)}
	 * .
	 */
	@Test
	public void testSecondsToPixels() {
		Date date = CalendarUtil.createDate(2013, 10, 10, 20, 45, 50, 0);
		int actual = CalendarUtil.secondsToPixels(date, 1440);
		int expected = 1246;

		Assert.assertEquals(expected, actual);
	}

    @Test
    public void testRoundToHalfAnHour(){
        Date dateActual = CalendarUtil.roundDateToHalfAnHour(CalendarUtil.createDate(2013, 10, 10, 20, 45, 50, 0),true);
        Date dateExpected = CalendarUtil.createDate(2013, 10, 10, 21, 0, 0, 0);

        Assert.assertEquals(dateActual,dateExpected);

        dateActual = CalendarUtil.roundDateToHalfAnHour(CalendarUtil.createDate(2013, 10, 10, 20, 15, 0, 0),false);
        dateExpected = CalendarUtil.createDate(2013, 10, 10, 20, 0, 0, 0);

        Assert.assertEquals(dateActual,dateExpected);

        dateActual = CalendarUtil.roundDateToHalfAnHour(CalendarUtil.createDate(2013, 10, 10, 20, 25, 0, 0),true);
        dateExpected = CalendarUtil.createDate(2013, 10, 10, 20, 30, 0, 0);

        Assert.assertEquals(dateActual,dateExpected);
    }

}
