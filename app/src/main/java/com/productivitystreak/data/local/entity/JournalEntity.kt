package com.productivitystreak.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "journal_entries",
    indices = [
        Index(value = ["date"]),
        Index(value = ["createdAt"])
    ]
)
data class JournalEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val date: String,                    // YYYY-MM-DD format
    val whatDidWell: String,             // "What did I do well today?"
    val whereLackedDiscipline: String,   // "Where did I lack discipline?"
    val whatWillDoBetter: String,        // "What will I do better tomorrow?"
    val createdAt: Long = System.currentTimeMillis()
)
