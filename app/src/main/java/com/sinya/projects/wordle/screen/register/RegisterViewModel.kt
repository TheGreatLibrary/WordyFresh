package com.sinya.projects.wordle.screen.register

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Dao
import com.sinya.projects.wordle.data.local.dao.ProfilesDao
import com.sinya.projects.wordle.domain.model.entity.Profiles
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import java.util.UUID

class RegisterViewModel(private val supabase: SupabaseClient, private val profileDao: ProfilesDao) : ViewModel() {
    var emailValue = mutableStateOf("")
    var passwordValue = mutableStateOf("")
    var nicknameValue = mutableStateOf("")
    var status = mutableStateOf(false)

    var isEmailError = mutableStateOf(false)
    var isPasswordError = mutableStateOf(false)
    var isNickNameError = mutableStateOf(false)
    var isStatusError = mutableStateOf(false)

    companion object {
        fun provideFactory(
            profileDao: ProfilesDao,
            supabase: SupabaseClient
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RegisterViewModel(supabase, profileDao) as T
                }
            }
        }
    }

    fun registerUser(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (validationForm()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    supabase.auth.signUpWith(Email) {
                        this.email = emailValue.value
                        this.password = passwordValue.value
                    }
                    if (supabase.auth.currentUserOrNull() != null) {
                        supabase.from("profiles").insert(
                            Profiles(
                                id = supabase.auth.currentUserOrNull()!!.id,
                                nickname = nicknameValue.value,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        profileDao.insertProfile(
                            com.sinya.projects.wordle.domain.model.entity.Profiles(
                                id = supabase.auth.currentUserOrNull()!!.id,
                                nickname = nicknameValue.value,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                    else {
                        supabase.auth.signInWith(Email) {
                            this.email = emailValue.value
                            this.password = passwordValue.value
                        }
                        supabase.from("profiles").insert(
                            Profiles(
                                id = supabase.auth.currentUserOrNull()?.id ?: UUID.randomUUID().toString(),
                                nickname = nicknameValue.value,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        profileDao.insertProfile(
                            com.sinya.projects.wordle.domain.model.entity.Profiles(
                                id = supabase.auth.currentUserOrNull()?.id ?: UUID.randomUUID().toString(),
                                nickname = nicknameValue.value,
                                avatarUrl = "",
                                createdAt = Clock.System.now().toString()
                            )
                        )
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        onError(e.localizedMessage ?: "Ошибка регистрации")
                    }
                }
            }
        }

    }

    private fun validationForm() : Boolean {
        emailValue.value = emailValue.value.trim()
        passwordValue.value = passwordValue.value.trim()
        nicknameValue.value = nicknameValue.value.trim()

        isPasswordError.value = passwordValue.value.length<6
        isEmailError.value = emailValue.value.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailValue.value).matches()
        isNickNameError.value = nicknameValue.value.isEmpty()
        isStatusError.value = status.value == false

        if (isPasswordError.value && isEmailError.value && isNickNameError.value && isStatusError.value) return false
        else return true
    }
}
