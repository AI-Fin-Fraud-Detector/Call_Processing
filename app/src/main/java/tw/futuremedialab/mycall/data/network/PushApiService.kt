package tw.futuremedialab.mycall.data.network

import tw.futuremedialab.mycall.data.network.dto.PushSubscribeRequestDto
import tw.futuremedialab.mycall.data.network.dto.PushSubscribeResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PushApiService {

    @POST("api/push/subscribe/host_mobile")
    suspend fun subscribe(
        @Header("Authorization") authorization: String,
        @Body request: PushSubscribeRequestDto
    ): Response<PushSubscribeResponseDto>
}
