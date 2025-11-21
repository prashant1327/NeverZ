package com.productivitystreak.notifications

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration

class GhostNotificationScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    fun schedule() {
        val workRequest = PeriodicWorkRequestBuilder<GhostNotificationWorker>(Duration.ofDays(1))
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    fun cancel() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "ghost_notifications_work"
    }
}
