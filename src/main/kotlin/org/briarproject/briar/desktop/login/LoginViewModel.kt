package org.briarproject.briar.desktop.login

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.desktop.login.LoginViewModel.State.COMPACTING
import org.briarproject.briar.desktop.login.LoginViewModel.State.MIGRATING
import org.briarproject.briar.desktop.login.LoginViewModel.State.SIGNED_OUT
import org.briarproject.briar.desktop.login.LoginViewModel.State.STARTED
import org.briarproject.briar.desktop.login.LoginViewModel.State.STARTING
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class LoginViewModel
@Inject
constructor(
    private val accountManager: AccountManager,
    private val briarExecutors: BriarExecutors,
    private val lifecycleManager: LifecycleManager,
    eventBus: EventBus,
    db: TransactionManager,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    enum class State {
        SIGNED_OUT, STARTING, MIGRATING, COMPACTING, STARTED
    }

    private val _state = mutableStateOf(SIGNED_OUT)
    val state = _state.asState()

    private val _password = mutableStateOf("")
    val password = _password.asState()

    val buttonEnabled = derivedStateOf { password.value.isNotEmpty() }

    fun setPassword(password: String) {
        _password.value = password
    }

    override fun onInit() {
        super.onInit()
        updateState(lifecycleManager.lifecycleState)
    }

    override fun eventOccurred(e: Event?) {
        if (e is LifecycleEvent) {
            updateState(e.lifecycleState)
        }
    }

    private fun updateState(s: LifecycleState) {
        _state.value =
            if (accountManager.hasDatabaseKey()) {
                when {
                    s.isAfter(LifecycleState.STARTING_SERVICES) -> STARTED
                    s == LifecycleState.MIGRATING_DATABASE -> MIGRATING
                    s == LifecycleState.COMPACTING_DATABASE -> COMPACTING
                    else -> _state.value
                }
            } else {
                SIGNED_OUT
            }
    }

    @IoExecutor
    private fun signedIn() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }

    fun signIn() {
        if (!buttonEnabled.value) return

        _state.value = STARTING
        briarExecutors.onIoThread {
            try {
                accountManager.signIn(password.value)
                signedIn()
            } catch (e: DecryptionException) {
                // failure, try again
                briarExecutors.onUiThread {
                    _state.value = SIGNED_OUT
                    _password.value = ""
                }
            }
        }
    }
}
