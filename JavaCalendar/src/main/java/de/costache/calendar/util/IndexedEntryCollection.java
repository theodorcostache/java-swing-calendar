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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.apache.commons.collections.MultiHashMap;

import de.costache.calendar.JCalendar;
import de.costache.calendar.events.CollectionChangedEvent;
import de.costache.calendar.events.CollectionChangedListener;
import de.costache.calendar.events.SelectionChangedEvent;
import de.costache.calendar.events.SelectionChangedListener;
import de.costache.calendar.model.JCalendarEntry;
import de.costache.calendar.model.JCalendarEntry.Property;

/**
 * 
 * @author theodorcostache
 * 
 */
class IndexedEntryCollection extends Observable implements Observer, EntryCollection {

	private final MultiHashMap indexedEntries;

	private final List<CollectionChangedListener> collectionChangedListeners;

	private final List<SelectionChangedListener> selectionChangedListeners;

	private final Set<JCalendarEntry> selectedEntries;

	private final JCalendar parent;

	/**
	 * Creates a new instance of {@link IndexedEntryCollection}
	 */
	public IndexedEntryCollection(JCalendar parent) {
		this.parent = parent;
		this.indexedEntries = new MultiHashMap();
		this.collectionChangedListeners = new ArrayList<CollectionChangedListener>();
		this.selectionChangedListeners = new ArrayList<SelectionChangedListener>();
		this.selectedEntries = new HashSet<JCalendarEntry>();
	}

	@Override
	public void add(final JCalendarEntry calendarEntry) {
		calendarEntry.addObserver(this);
		final Collection<Date> dates = CalendarUtil.getDates(calendarEntry.getStart(), calendarEntry.getEnd());
		for (final Date date : dates) {

			if (!indexedEntries.containsValue(calendarEntry)) {
				indexedEntries.put(date, calendarEntry);
			}
		}
		notifyObservers();

		CollectionChangedEvent event = new CollectionChangedEvent(parent, calendarEntry);
		for (CollectionChangedListener listener : collectionChangedListeners) {
			listener.itemAdded(event);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void remove(final JCalendarEntry calendarEntry) {
		calendarEntry.deleteObserver(this);
		selectedEntries.remove(calendarEntry);

		for (final Object key : new HashSet<Object>(indexedEntries.keySet())) {
			final Collection<JCalendarEntry> entries = indexedEntries.getCollection(key);
			if (entries.contains(calendarEntry))
				indexedEntries.remove(key, calendarEntry);
		}

		notifyObservers();

		CollectionChangedEvent event = new CollectionChangedEvent(parent, calendarEntry);
		for (CollectionChangedListener listener : collectionChangedListeners) {
			listener.itemRemoved(event);
		}
	}

	@Override
	public void addSelected(final JCalendarEntry entry) {
		selectedEntries.add(entry);
		SelectionChangedEvent event = new SelectionChangedEvent(entry);
		for (SelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(event);
		}
	}

	@Override
	public void removeSelected(final JCalendarEntry entry) {
		boolean remove = selectedEntries.remove(entry);
		if (remove) {
			SelectionChangedEvent event = new SelectionChangedEvent(entry);
			for (SelectionChangedListener listener : selectionChangedListeners) {
				listener.selectionChanged(event);
			}
		}

	}

	@Override
	public void clearSelected(JCalendarEntry toIgnore, boolean notifyListeners) {
		for (JCalendarEntry entry : selectedEntries) {
			if (entry != toIgnore) {
				entry.setSelected(false);
			}
		}
		selectedEntries.clear();

		if (notifyListeners) {
			SelectionChangedEvent event = new SelectionChangedEvent(null);
			for (SelectionChangedListener listener : selectionChangedListeners) {
				listener.selectionChanged(event);
			}
		}
	}

	@Override
	public Collection<JCalendarEntry> getSelectedEntries() {
		return Collections.unmodifiableSet(new HashSet<JCalendarEntry>(selectedEntries));
	}

	@Override
	public Collection<JCalendarEntry> getCalendarEntries(final Date date) {
		@SuppressWarnings("rawtypes")
		final Collection entries = indexedEntries.getCollection(CalendarUtil.stripTime(date));
		if (entries == null)
			return new ArrayList<JCalendarEntry>();
		@SuppressWarnings("unchecked")
		final List<JCalendarEntry> result = new ArrayList<JCalendarEntry>(entries);
		Collections.sort(result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(final Observable o, final Object arg) {
		if (o instanceof JCalendarEntry) {
			final JCalendarEntry entry = (JCalendarEntry) o;
			final Property property = (Property) arg;
			switch (property) {
			case START:
			case END:

				for (final Object key : new HashSet<Object>(indexedEntries.keySet())) {
					indexedEntries.remove(key, entry);
				}

				final Collection<Date> dates = CalendarUtil.getDates(entry.getStart(), entry.getEnd());
				for (final Date date : dates) {
					indexedEntries.put(date, entry);
				}

				notifyObservers(entry);

				CollectionChangedEvent event = new CollectionChangedEvent(parent, entry);
				for (CollectionChangedListener listener : collectionChangedListeners) {
					listener.itemChanged(event);
				}

			default:
				parent.invalidate();
				parent.repaint();
				break;
			}
		}
	}

	@Override
	public void addCollectionChangedListener(CollectionChangedListener listener) {
		this.collectionChangedListeners.add(listener);
	}

	@Override
	public void removeCollectionChangedListener(CollectionChangedListener listener) {
		this.collectionChangedListeners.remove(listener);
	}

	@Override
	public void addSelectionChangedListener(SelectionChangedListener listener) {
		this.selectionChangedListeners.add(listener);
	}

	@Override
	public void removeSelectionChangedListener(SelectionChangedListener listener) {
		this.selectionChangedListeners.remove(listener);
	}
}
