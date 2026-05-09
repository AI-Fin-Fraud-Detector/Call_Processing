package tw.futuremedialab.mycall.domain.repo

import tw.futuremedialab.mycall.domain.entity.CallLogData
import tw.futuremedialab.mycall.domain.entity.Contact

interface CallLogRepository {
    suspend fun getCallLogsPaged(limit: Int, offset: Int): List<CallLogData>
    suspend fun getCallLogsByPhone(phone: String): List<CallLogData>
    suspend fun deleteCallLogsByPhone(phone: String): Int
    suspend fun deleteAllCallLogs(): Int
    suspend fun getFrequentCalledContacts(): List<Contact>
    suspend fun searchCallLogContacts(query: String, limit: Int, offset: Int): List<Contact>
}