package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.briar.desktop.contact.add.remote.AddContactDialog
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.CONTACTLIST_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE

@Composable
fun ContactList(
    contactList: List<ContactItem>,
    isSelected: (ContactId) -> Boolean,
    selectContact: (ContactId) -> Unit,
    filterBy: String,
    setFilterBy: (String) -> Unit,
) {
    var isContactDialogVisible by remember { mutableStateOf(false) }
    if (isContactDialogVisible) AddContactDialog(onClose = { isContactDialogVisible = false })
    Scaffold(
        modifier = Modifier.fillMaxHeight().width(CONTACTLIST_WIDTH),
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp),
            ) {
                SearchTextField(
                    filterBy,
                    onValueChange = setFilterBy,
                    onContactAdd = { isContactDialogVisible = true },
                    icon = Icons.Filled.PersonAdd,
                )
            }
        },
        content = {
            LazyColumn {
                items(contactList) { contactItem ->
                    ContactCard(
                        contactItem,
                        { selectContact(contactItem.contact.id) },
                        isSelected(contactItem.contact.id)
                    )
                }
            }
        },
    )
}
