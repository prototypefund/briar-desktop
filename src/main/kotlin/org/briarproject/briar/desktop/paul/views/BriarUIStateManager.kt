package org.briarproject.briar.desktop.paul.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.desktop.paul.theme.briarBlack

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun BriarUIStateManager(
    contacts: List<Contact>,
    conversationManager: ConversationManager,
    messagingManager: MessagingManager
) {
    // current selected mode, changed using the sidebar buttons
    val (uiMode, onModeChange) = remember { mutableStateOf("Contacts") }
    // current selected contact
    val uiContact: MutableState<Contact> = remember { mutableStateOf(contacts[0]) }
    // current selected private message
    val (uiPrivateMsg, onPMSelect) = remember { mutableStateOf(0) }
    // current selected forum
    val (uiForum, onForumSelect) = remember { mutableStateOf(0) }
    // current blog state
    val (uiBlog, onBlogSelect) = remember { mutableStateOf(0) }
    // current transport state
    val (uiTransports, onTransportSelect) = remember { mutableStateOf(0) }
    // current settings state
    val (uiSettings, onSettingSelect) = remember { mutableStateOf(0) }
    // current profile
    var Profile: String
    // Other global state that we need to track should go here also
    Row() {
        BriarSidebar(uiMode, onModeChange)
        when (uiMode) {
            "Contacts" -> PrivateMessageView(
                contacts,
                uiContact,
                conversationManager,
                messagingManager
            )
            else -> Box(modifier = Modifier.fillMaxSize().background(briarBlack)) {
                Text("TBD", modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
        }
    }
}