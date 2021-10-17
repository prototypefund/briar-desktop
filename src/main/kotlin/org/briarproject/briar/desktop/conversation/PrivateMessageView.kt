package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState.MakeIntro
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactsViewModel
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.ui.VerticalDivider

@Composable
fun PrivateMessageView(
    contacts: ContactsViewModel,
) {
    val (dropdownExpanded, setExpanded) = remember { mutableStateOf(false) }
    val (infoDrawer, setInfoDrawer) = remember { mutableStateOf(false) }
    val (contactDrawerState, setDrawerState) = remember { mutableStateOf(MakeIntro) }
    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(contacts)
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            contacts.selectedContact.value?.also { selectedContact ->
                Conversation(
                    selectedContact.contact,
                    contacts.contactList.map { c -> c.contact },
                    dropdownExpanded,
                    setExpanded,
                    infoDrawer,
                    setInfoDrawer,
                    contactDrawerState
                )
            } ?: UiPlaceholder()
        }
    }
}
