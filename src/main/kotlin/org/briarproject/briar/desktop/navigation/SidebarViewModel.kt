package org.briarproject.briar.desktop.navigation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.identity.IdentityManager
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.briar.desktop.ui.UiMode
import javax.inject.Inject

class SidebarViewModel
@Inject
constructor(
    private val identityManager: IdentityManager,
) {

    private var _uiMode = mutableStateOf(UiMode.CONTACTS)
    private var _account = mutableStateOf<LocalAuthor?>(null)

    val uiMode: State<UiMode> = _uiMode
    val account: State<LocalAuthor?> = _account

    fun setUiMode(uiMode: UiMode) {
        _uiMode.value = uiMode
    }

    fun loadAccountInfo() {
        _account.value = identityManager.localAuthor
    }
}