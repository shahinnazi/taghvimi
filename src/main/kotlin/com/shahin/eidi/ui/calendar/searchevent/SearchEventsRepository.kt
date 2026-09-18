package com.shahin.eidi.ui.calendar.searchevent

import android.content.Context
import com.shahin.eidi.entities.CalendarEvent
import com.shahin.eidi.entities.Jdn
import com.shahin.eidi.global.eventsRepository
import com.shahin.eidi.utils.getAllEnabledAppointments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchEventsRepository(private val context: Context) {
    private var store: SearchEventsStore? = null

    private suspend fun createStore(context: Context): SearchEventsStore {
        return withContext(Dispatchers.IO) {
            SearchEventsStore(
                context.getAllEnabledAppointments() +
                        // Hopefully we can get rid of this global variable someday
                        (eventsRepository?.getEnabledEvents(Jdn.today()) ?: emptyList())
            )
        }
    }

    // encapsulate store in repository
    suspend fun findEvent(query: CharSequence): List<CalendarEvent<*>> =
        (store ?: createStore(context).also { store = it }).query(query)
}
