package tw.futuremedialab.mycall.data

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import android.util.LruCache
import androidx.core.net.toUri
import tw.futuremedialab.mycall.di.IODispatcher
import tw.futuremedialab.mycall.domain.entity.Contact
import tw.futuremedialab.mycall.domain.repo.ContactDetailProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactDetailProviderImpl @Inject constructor(
    @ApplicationContext context: Context,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : ContactDetailProvider {

    private val contentResolver = context.contentResolver

    /**
     * Cache both FOUND and NOT FOUND results.
     * null values are cached explicitly.
     */
    private val cache = LruCache<String, Contact?>(100)

    override suspend fun getContactByPhone(phone: String): Contact? =
        withContext(ioDispatcher) {

            // 1. Cache hit (including null)
            if (cache.snapshot().containsKey(phone)) {
                return@withContext cache[phone]
            }

            val contact = queryContact(phone)
            if (contact != null) {
                cache.put(phone, contact)
            }
            return@withContext contact
        }

    // ----------------------------
    // Internal query (single lookup)
    // ----------------------------

    private fun queryContact(phone: String): Contact? {

        val lookupUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phone)
        )

        val projection = arrayOf(
            ContactsContract.PhoneLookup._ID,
            ContactsContract.PhoneLookup.DISPLAY_NAME,
            ContactsContract.PhoneLookup.NUMBER,
            ContactsContract.PhoneLookup.PHOTO_URI
        )

        contentResolver.query(
            lookupUri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                return Contact(
                    id = cursor.getLong(0),
                    name = cursor.getString(1) ?: phone,
                    phone = cursor.getString(2) ?: phone,
                    image = cursor.getString(3)?.toUri()
                )
            }
        }

        return null
    }
}
