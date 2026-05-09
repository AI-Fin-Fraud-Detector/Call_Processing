package tw.futuremedialab.mycall.domain.repo

import tw.futuremedialab.mycall.domain.entity.Contact

interface ContactDetailProvider {
    suspend fun getContactByPhone(phone: String): Contact?
}
