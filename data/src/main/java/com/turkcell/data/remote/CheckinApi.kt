package com.turkcell.data.remote

import com.turkcell.data.dto.checkin.CheckinRequestDto
import com.turkcell.data.dto.checkin.CheckinResultDto
import retrofit2.http.Body
import retrofit2.http.POST

interface CheckinApi {
    @POST("/checkin/scan")
    suspend fun scan(@Body body: CheckinRequestDto): CheckinResultDto
}