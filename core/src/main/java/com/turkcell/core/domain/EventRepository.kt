package com.turkcell.core.domain

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
}