package com.turkcell.core.domain

import com.turkcell.core.domain.event.Event

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
}