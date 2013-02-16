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

import java.util.HashMap;
import java.util.Map;

import de.costache.calendar.JCalendar;

/**
 * 
 * @author theodorcostache
 * 
 */
public class EntryCollectionRepository {

	private final Map<JCalendar, IndexedEntryCollection> repository;

	private static final EntryCollectionRepository instance = new EntryCollectionRepository();

	private EntryCollectionRepository() {
		repository = new HashMap<JCalendar, IndexedEntryCollection>();
	}

	public static void register(final JCalendar calendar) {
		instance.repository.put(calendar, new IndexedEntryCollection(calendar));
	}

	public static EntryCollection get(final JCalendar calendar) {
		IndexedEntryCollection entryCollection = instance.repository.get(calendar);
		if (entryCollection == null)
			throw new IllegalArgumentException(
					"Calendar not registered. Please register calendar before calling this method");
		return entryCollection;
	}
}
