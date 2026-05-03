package com.gyros.startchat.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gyros.startchat.data.models.ChatHistoryEntry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatHistoryDaoTest {

    private lateinit var db: StartChatDatabase
    private lateinit var dao: ChatHistoryDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            StartChatDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.chatHistoryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getAll_returnsEmptyListInitially() {
        assertTrue(dao.getAll().isEmpty())
    }

    @Test
    fun upsert_insertsEntryAndGetAllReturnsIt() {
        val entry = ChatHistoryEntry("+14155552671", 1000L)
        dao.upsert(entry)

        val result = dao.getAll()
        assertEquals(1, result.size)
        assertEquals(entry, result.first())
    }

    @Test
    fun upsert_samePhoneNumberUpdatesTimestamp() {
        dao.upsert(ChatHistoryEntry("+14155552671", 1000L))
        dao.upsert(ChatHistoryEntry("+14155552671", 2000L))

        val result = dao.getAll()
        assertEquals(1, result.size)
        assertEquals(2000L, result.first().timestamp)
    }

    @Test
    fun getAll_returnsMostRecentFirst() {
        dao.upsert(ChatHistoryEntry("+11111111111", 1000L))
        dao.upsert(ChatHistoryEntry("+22222222222", 3000L))
        dao.upsert(ChatHistoryEntry("+33333333333", 2000L))

        val result = dao.getAll()
        assertEquals("+22222222222", result[0].phoneNumber)
        assertEquals("+33333333333", result[1].phoneNumber)
        assertEquals("+11111111111", result[2].phoneNumber)
    }

    @Test
    fun getAll_capsAtFiftyEntries() {
        for (i in 1..55) {
            dao.upsert(ChatHistoryEntry("+1000000$i", i.toLong()))
        }

        assertEquals(50, dao.getAll().size)
    }
}
