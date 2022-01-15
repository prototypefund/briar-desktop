/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import org.briarproject.bramble.api.FeatureFlags
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState.RUNNING
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.desktop.DesktopFeatureFlags
import org.briarproject.briar.desktop.expiration.ExpirationBanner
import org.briarproject.briar.desktop.login.StartupScreen
import org.briarproject.briar.desktop.settings.SettingsViewModel
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.ui.Screen.MAIN
import org.briarproject.briar.desktop.ui.Screen.STARTUP
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.ViewModelProvider
import org.briarproject.briar.desktop.viewmodel.viewModel
import java.awt.Dimension
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

enum class Screen {
    STARTUP,
    MAIN
}

interface BriarUi {

    @Composable
    fun start(onClose: () -> Unit)

    fun stop()
}

val LocalWindowScope = staticCompositionLocalOf<FrameWindowScope?> { null }
val LocalViewModelProvider = staticCompositionLocalOf<ViewModelProvider?> { null }
val LocalCoreFeatureFlags = staticCompositionLocalOf<FeatureFlags?> { null }
val LocalDesktopFeatureFlags = staticCompositionLocalOf<DesktopFeatureFlags?> { null }

@Immutable
@Singleton
internal class BriarUiImpl
@Inject
constructor(
    private val lifecycleManager: LifecycleManager,
    private val eventBus: EventBus,
    private val viewModelProvider: ViewModelProvider,
    private val featureFlags: FeatureFlags,
    private val desktopFeatureFlags: DesktopFeatureFlags,
) : BriarUi, EventListener {

    private var screenState by mutableStateOf(
        if (lifecycleManager.lifecycleState == RUNNING) MAIN
        else STARTUP
    )

    override fun eventOccurred(e: Event?) {
        if (e is LifecycleEvent && e.lifecycleState == RUNNING)
            screenState = MAIN
    }

    override fun stop() {
        if (lifecycleManager.lifecycleState == RUNNING) {
            lifecycleManager.stopServices()
            lifecycleManager.waitForShutdown()
        }
        eventBus.removeListener(this)
    }

    @Composable
    override fun start(onClose: () -> Unit) {
        val title = i18n("main.title")
        eventBus.addListener(this)
        Window(
            title = title,
            onCloseRequest = onClose,
            icon = painterResource("images/logo_circle.svg")
        ) {
            window.minimumSize = Dimension(800, 600)
            CompositionLocalProvider(
                LocalWindowScope provides this,
                LocalViewModelProvider provides viewModelProvider,
                LocalCoreFeatureFlags provides featureFlags,
                LocalDesktopFeatureFlags provides desktopFeatureFlags
            ) {
                val settingsViewModel: SettingsViewModel = viewModel()
                BriarTheme(isDarkTheme = settingsViewModel.isDarkMode.value) {
                    Column(Modifier.fillMaxSize()) {
                        ExpirationBanner(onExpired = { screenState = STARTUP })
                        when (screenState) {
                            STARTUP -> StartupScreen()
                            MAIN -> MainScreen(settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}
