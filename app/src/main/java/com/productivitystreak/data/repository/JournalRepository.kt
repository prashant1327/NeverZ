package com.productivitystreak.data.repository

import com.productivitystreak.data.local.dao.JournalDao
import com.productivitystreak.data.local.entity.JournalEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JournalRepository(private val journalDao: JournalDao) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun getAllEntries(): Flow<List<JournalEntity>> = journalDao.getAllEntries()

    fun getRecentEntries(limit: Int = 7): Flow<List<JournalEntity>> = 
        journalDao.getRecentEntries(limit)

    suspend fun getTodayEntry(): JournalEntity? {
        val today = LocalDate.now().format(dateFormatter)
        return journalDao.getEntryByDate(today)
    }

    suspend fun saveEntry(
        whatDidWell: String,
        whereLackedDiscipline: String,
        whatWillDoBetter: String
    ) {
        val today = LocalDate.now().format(dateFormatter)
        val existingEntry = journalDao.getEntryByDate(today)
        
        val entry = if (existingEntry != null) {
            // Update existing entry for today
            existingEntry.copy(
                whatDidWell = whatDidWell,
                whereLackedDiscipline = whereLackedDiscipline,
                whatWillDoBetter = whatWillDoBetter
            )
        } else {
            // Create new entry
            JournalEntity(
                date = today,
                whatDidWell = whatDidWell,
                whereLackedDiscipline = whereLackedDiscipline,
                whatWillDoBetter = whatWillDoBetter
            )
        }
        
        journalDao.insertEntry(entry)
    }

    suspend fun getEntryCount(): Int = journalDao.getEntryCount()

    suspend fun deleteEntry(entry: JournalEntity) = journalDao.deleteEntry(entry)
}
