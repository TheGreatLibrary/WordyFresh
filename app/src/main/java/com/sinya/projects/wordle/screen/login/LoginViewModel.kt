package com.sinya.projects.wordle.screen.login

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinya.projects.wordle.data.local.dao.ProfilesDao
import com.sinya.projects.wordle.data.remote.supabase.SupabaseClientHolder
import com.sinya.projects.wordle.data.remote.supabase.SupabaseSyncManager
import com.sinya.projects.wordle.domain.model.entity.Profiles
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class LoginViewModel(private val supabase: SupabaseClient, private val profilesDao: ProfilesDao) : ViewModel() {
    var emailValue = mutableStateOf("")
    var passwordValue = mutableStateOf("")

    var isEmailError = mutableStateOf(false)
    var isPasswordError = mutableStateOf(false)

    companion object {
        fun provideFactory(
            profilesDao: ProfilesDao,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(supabase, profilesDao) as T
                }
            }
        }
    }

    private fun validationForm() : Boolean {
        emailValue.value = emailValue.value.trim()
        passwordValue.value = passwordValue.value.trim()

        isPasswordError.value = passwordValue.value.length<6
        isEmailError.value = emailValue.value.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue.value).matches()

        if (isPasswordError.value && isEmailError.value) return false
        else return true
    }

    fun loginUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (validationForm()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    supabase.auth.signInWith(Email) {
                        this.email = emailValue.value
                        this.password = passwordValue.value
                    }
                    if (supabase.auth.currentUserOrNull() != null) {
                        val profile = supabase
                            .from("profiles")
                            .select(columns = Columns.list("*")) {
                                filter {
                                    eq("id", supabase.auth.currentUserOrNull()!!.id)
                                }
                            }
                            .decodeSingle<Profiles>()

                        profilesDao.insertProfile(
                            Profiles(
                                id = profile.id,
                                nickname = profile.nickname,
                                avatarUrl = profile.avatarUrl,
                                createdAt = profile.createdAt
                            )
                        )
                    }
                    withContext(Dispatchers.Main) { onSuccess() }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { onError(e.localizedMessage ?: "Ошибка входа") }
                }
            }
        }
    }
}
