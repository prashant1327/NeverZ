package com.productivitystreak.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class StreakReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Your work here
        return Result.success()
    }

    companion object {
        fun createInputData(categories: Set<String>, userName: String): Data {
            return Data.Builder()
                .putStringArray("categories", categories.toTypedArray())
                .putString("userName", userName)
                .build()
        }
    }
}
