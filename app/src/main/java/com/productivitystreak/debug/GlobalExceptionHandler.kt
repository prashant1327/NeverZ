package com.productivitystreak.debug

import android.content.Context
import android.content.Intent
import com.productivitystreak.ui.debug.DebugActivity
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class GlobalExceptionHandler(
    private val applicationContext: Context
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val stackTrace = StringWriter().also {
            exception.printStackTrace(PrintWriter(it))
        }.toString()

        val intent = Intent(applicationContext, DebugActivity::class.java).apply {
            putExtra(DebugActivity.EXTRA_STACK_TRACE, stackTrace)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        applicationContext.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}
