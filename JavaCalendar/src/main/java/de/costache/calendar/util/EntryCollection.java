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

import java.util.Collection;
import java.util.Date;

import de.costache.calendar.events.CollectionChangedListener;
import de.costache.calendar.events.SelectionChangedListener;
import de.costache.calendar.model.JCalendarEntry;

/**
 * 
 * @author theodorcostache
 * 
 */
public interface EntryCollection {

	void add(JCalendarEntry entry);

	void remove(JCalendarEntry entry);

	void addSelected(JCalendarEntry entry);

	void removeSelected(JCalendarEntry entry);

	void clearSelected(JCalendarEntry entry, boolean b);

	Collection<JCalendarEntry> getSelectedEntries();

	Collection<JCalendarEntry> getCalendarEntries(Date date);

	void addCollectionChangedListener(CollectionChangedListener listener);

	void removeCollectionChangedListener(CollectionChangedListener listener);

	void addSelectionChangedListener(SelectionChangedListener selectionChangedListener);

	void removeSelectionChangedListener(SelectionChangedListener selectionChangedListener);
}
