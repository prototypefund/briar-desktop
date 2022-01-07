package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.conversation.PrivateMessageScreen
import org.briarproject.briar.desktop.navigation.BriarSidebar
import org.briarproject.briar.desktop.navigation.SidebarViewModel
import org.briarproject.briar.desktop.settings.PlaceHolderSettingsView
import org.briarproject.briar.desktop.viewmodel.viewModel

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun MainScreen(
    isDark: Boolean,
    setDark: (Boolean) -> Unit,
    viewModel: SidebarViewModel = viewModel(),
) {
    Row {
        BriarSidebar(
            viewModel.account.value,
            viewModel.uiMode.value,
            viewModel::setUiMode,
        )
        VerticalDivider()
        when (viewModel.uiMode.value) {
            UiMode.CONTACTS -> PrivateMessageScreen()
            UiMode.SETTINGS -> PlaceHolderSettingsView(isDark, setDark)
            else -> UiPlaceholder()
        }
    }
}
