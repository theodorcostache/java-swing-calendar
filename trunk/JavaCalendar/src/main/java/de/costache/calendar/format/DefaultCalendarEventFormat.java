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
package de.costache.calendar.format;

import java.text.SimpleDateFormat;

import de.costache.calendar.model.CalendarEvent;

public class DefaultCalendarEventFormat implements CalendarEventFormat {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

	public DefaultCalendarEventFormat() {

	}

	@Override
	public String format(CalendarEvent calendarEvent) {
		StringBuilder formated = new StringBuilder();
		formated.append("<html>");
		formated.append("<table>");

		formated.append("<tr>");
		formated.append("<td><b>Summary: </b></td>");
		formated.append("<td>").append(calendarEvent.getSummary()).append("</td>");
		formated.append("</tr>");

		if (calendarEvent.getDescription() != null) {
			formated.append("<tr>");
			formated.append("<td><b>Description: </b></td>");
			formated.append("<td>").append(calendarEvent.getDescription()).append("</td>");
			formated.append("</tr>");
		}

		if (calendarEvent.getLocation() != null) {
			formated.append("<tr>");
			formated.append("<td><b>Location: </b></td>");
			formated.append("<td>").append(calendarEvent.getLocation()).append("</td>");
			formated.append("</tr>");
		}

		formated.append("<tr>");
		formated.append("<td><b>Start time: </b></td>");
		formated.append("<td>").append(sdf.format(calendarEvent.getStart())).append("</td>");
		formated.append("</tr>");

		formated.append("<tr>");
		formated.append("<td><b>End time: </b></td>");
		formated.append("<td>").append(sdf.format(calendarEvent.getEnd())).append("</td>");
		formated.append("</tr>");

		formated.append("<tr>");
		formated.append("<td><b>Priority: </b></td>");
		formated.append("<td>").append(calendarEvent.getPriority()).append("</td>");
		formated.append("</tr>");

		formated.append("</table>");
		formated.append("</html>");
		return formated.toString();
	}
}
