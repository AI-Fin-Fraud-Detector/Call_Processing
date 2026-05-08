package com.talsk.amadz.core

import android.content.Context

object AutoAnswerPrefs {
    const val KEY_AUTO_ANSWER_ENABLED = "auto_answer_enabled"
    const val DEFAULT_DELAY_MS = 1500L

    fun isEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(DtmfTonePrefs.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_AUTO_ANSWER_ENABLED, true)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(DtmfTonePrefs.PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_AUTO_ANSWER_ENABLED, enabled).apply()
    }
}
