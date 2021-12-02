package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.desktop.theme.noticeIn
import org.briarproject.briar.desktop.theme.noticeOut
import org.briarproject.briar.desktop.theme.privateMessageDate
import org.briarproject.briar.desktop.theme.textPrimary
import org.briarproject.briar.desktop.theme.textSecondary
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.time.Instant

fun main() = preview(
    "msgText" to "This is a long long long message that spans over several lines.\n\nIt ends here.",
    "text" to "Text of notice message.",
    "time" to Instant.now().toEpochMilli(),
    "isIncoming" to false,
    "isRead" to false,
    "isSent" to false,
    "isSeen" to true,
) {
    ConversationNoticeItemView(
        ConversationNoticeItem(
            msgText = getStringParameter("msgText"),
            text = getStringParameter("text"),
            id = MessageId(getRandomId()),
            groupId = GroupId(getRandomId()),
            time = getLongParameter("time"),
            autoDeleteTimer = 0,
            isIncoming = getBooleanParameter("isIncoming"),
            isRead = getBooleanParameter("isRead"),
            isSent = getBooleanParameter("isSent"),
            isSeen = getBooleanParameter("isSeen"),
        )
    )
}

@Composable
fun ConversationNoticeItemView(m: ConversationNoticeItem) {
    val textColor = if (m.isIncoming) MaterialTheme.colors.textPrimary else Color.White
    val noticeBackground = if (m.isIncoming) MaterialTheme.colors.noticeIn else MaterialTheme.colors.noticeOut
    val noticeColor = if (m.isIncoming) MaterialTheme.colors.textSecondary else MaterialTheme.colors.privateMessageDate
    ConversationItemView(m) {
        Column(Modifier.width(IntrinsicSize.Max)) {
            Text(
                m.msgText,
                fontSize = 16.sp,
                color = textColor,
                modifier = Modifier.padding(12.dp, 8.dp).align(Alignment.Start)
            )
            Column(
                Modifier.fillMaxWidth().background(noticeBackground).padding(12.dp, 8.dp)
            ) {
                Text(
                    text = m.text!!,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = noticeColor,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                )
                ConversationItemStatusView(m)
            }
        }
    }
}
