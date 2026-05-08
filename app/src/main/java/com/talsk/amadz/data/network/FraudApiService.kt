package com.talsk.amadz.data.network

import com.talsk.amadz.data.network.dto.FraudReportRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FraudApiService {

    @POST("api/fraud/incoming-call")
    suspend fun reportIncomingCall(
        @Header("Authorization") authorization: String,
        @Body request: FraudReportRequestDto
    ): Response<Unit>
}
