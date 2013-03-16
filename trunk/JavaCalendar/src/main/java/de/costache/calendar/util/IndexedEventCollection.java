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
import de.costache.calendar.events.ModelChangedEvent;
import de.costache.calendar.events.ModelChangedListener;
import de.costache.calendar.events.SelectionChangedEvent;
import de.costache.calendar.events.SelectionChangedListener;
import de.costache.calendar.model.CalendarEvent;
import de.costache.calendar.model.CalendarEvent.Property;

/**
 * 
 * @author theodorcostache
 * 
 */
class IndexedEventCollection extends Observable implements Observer, EventCollection {

	private final MultiHashMap indexedEvents;

	private final List<ModelChangedListener> collectionChangedListeners;

	private final List<SelectionChangedListener> selectionChangedListeners;

	private final Set<CalendarEvent> selectedEvents;

	private final JCalendar parent;

	/**
	 * Creates a new instance of {@link IndexedEventCollection}
	 */
	public IndexedEventCollection(JCalendar parent) {
		this.parent = parent;
		this.indexedEvents = new MultiHashMap();
		this.collectionChangedListeners = new ArrayList<ModelChangedListener>();
		this.selectionChangedListeners = new ArrayList<SelectionChangedListener>();
		this.selectedEvents = new HashSet<CalendarEvent>();
	}

	@Override
	public void add(final CalendarEvent calendarEvent) {
		calendarEvent.addObserver(this);
		final Collection<Date> dates = CalendarUtil.getDates(calendarEvent.getStart(), calendarEvent.getEnd());
		for (final Date date : dates) {

			if (!indexedEvents.containsValue(calendarEvent)) {
				indexedEvents.put(date, calendarEvent);
			}
		}
		notifyObservers();

		ModelChangedEvent event = new ModelChangedEvent(parent, calendarEvent);
		for (ModelChangedListener listener : collectionChangedListeners) {
			listener.eventAdded(event);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void remove(final CalendarEvent calendarEvent) {
		calendarEvent.deleteObserver(this);
		selectedEvents.remove(calendarEvent);

		for (final Object key : new HashSet<Object>(indexedEvents.keySet())) {
			final Collection<CalendarEvent> events = indexedEvents.getCollection(key);
			if (events.contains(calendarEvent))
				indexedEvents.remove(key, calendarEvent);
		}

		notifyObservers();

		ModelChangedEvent event = new ModelChangedEvent(parent, calendarEvent);
		for (ModelChangedListener listener : collectionChangedListeners) {
			listener.eventRemoved(event);
		}
	}

	@Override
	public void addSelected(final CalendarEvent calendarEvent) {
		selectedEvents.add(calendarEvent);
		SelectionChangedEvent event = new SelectionChangedEvent(calendarEvent);
		for (SelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(event);
		}
	}

	@Override
	public void removeSelected(final CalendarEvent calendarEvent) {
		boolean remove = selectedEvents.remove(calendarEvent);
		if (remove) {
			SelectionChangedEvent event = new SelectionChangedEvent(calendarEvent);
			for (SelectionChangedListener listener : selectionChangedListeners) {
				listener.selectionChanged(event);
			}
		}

	}

	@Override
	public void clearSelected(CalendarEvent toIgnore, boolean notifyListeners) {
		for (CalendarEvent event : selectedEvents) {
			if (event != toIgnore) {
				event.setSelected(false);
			}
		}
		selectedEvents.clear();

		if (notifyListeners) {
			SelectionChangedEvent event = new SelectionChangedEvent(null);
			for (SelectionChangedListener listener : selectionChangedListeners) {
				listener.selectionChanged(event);
			}
		}
	}

	@Override
	public Collection<CalendarEvent> getSelectedEvents() {
		return Collections.unmodifiableSet(new HashSet<CalendarEvent>(selectedEvents));
	}

	@Override
	public Collection<CalendarEvent> getEvents(final Date date) {
		@SuppressWarnings("rawtypes")
		final Collection events = indexedEvents.getCollection(CalendarUtil.stripTime(date));
		if (events == null)
			return new ArrayList<CalendarEvent>();
		@SuppressWarnings("unchecked")
		final List<CalendarEvent> result = new ArrayList<CalendarEvent>(events);
		Collections.sort(result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(final Observable o, final Object arg) {
		if (o instanceof CalendarEvent) {
			final CalendarEvent calendarEvent = (CalendarEvent) o;
			final Property property = (Property) arg;
			switch (property) {
			case START:
			case END:

				for (final Object key : new HashSet<Object>(indexedEvents.keySet())) {
					indexedEvents.remove(key, calendarEvent);
				}

				final Collection<Date> dates = CalendarUtil.getDates(calendarEvent.getStart(), calendarEvent.getEnd());
				for (final Date date : dates) {
					indexedEvents.put(date, calendarEvent);
				}

				notifyObservers(calendarEvent);

				ModelChangedEvent event = new ModelChangedEvent(parent, calendarEvent);
				for (ModelChangedListener listener : collectionChangedListeners) {
					listener.eventChanged(event);
				}

			default:
				parent.invalidate();
				parent.repaint();
				break;
			}
		}
	}

	@Override
	public void addCollectionChangedListener(ModelChangedListener listener) {
		this.collectionChangedListeners.add(listener);
	}

	@Override
	public void removeCollectionChangedListener(ModelChangedListener listener) {
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

	public int size() {
		return indexedEvents.size();
	}
}
