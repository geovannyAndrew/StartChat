package com.gyros.startchat.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import javax.inject.Inject

class UrlOpenerImpl @Inject constructor(
    private val context: Context
) : UrlOpener {

    override fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
