package org.briarproject.briar.desktop.conversation

import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp

class Chat {

    val messages: MutableList<SimpleMessage> = ArrayList()

    fun appendMessage(local: Boolean, timestamp: Long, messageText: String?) {
        val author = if (local) "You" else "Other"
        val formattedTimestamp = getFormattedTimestamp(timestamp)
        messages.add(SimpleMessage(local, author, messageText!!, formattedTimestamp, true))
    }
}