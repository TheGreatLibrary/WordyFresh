package com.sinya.projects.wordle.presentation.profile.subscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.sinya.projects.wordle.BuildConfig.WEB_CLIENT_ID
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.profile.ProfileEvent
import com.sinya.projects.wordle.presentation.profile.ProfileViewModel
import com.sinya.projects.wordle.ui.features.RoundedButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun ProfileOutAccount(
    viewModel: ProfileViewModel,
    navigateTo: (ScreenRoute) -> Unit,
    onEvent: (ProfileEvent) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.launchGoogleSignIn.collect {
            try {

                val credentialManager = CredentialManager.create(context)
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(
                        GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(WEB_CLIENT_ID)
                            .build()
                    )
                    .build()
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                val token = if (credential is GoogleIdTokenCredential) {
                    credential.idToken
                } else if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    GoogleIdTokenCredential.createFrom(credential.data).idToken
                } else {
                    onEvent(ProfileEvent.GoogleSignInFailed(Exception("Unknown credential type")))
                    return@collect
                }
                onEvent(ProfileEvent.GoogleIdTokenReceived(token))
            } catch (e: NoCredentialException) {
                onEvent(ProfileEvent.GoogleSignInFailed(e))
            } catch (e: Exception) {
                onEvent(ProfileEvent.GoogleSignInFailed(e))
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 550.dp)
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(9.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.sync_to_save_stat),
                    color = WordyColor.colors.textPrimary,
                    style = WordyTypography.titleLarge,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.play_with_friends),
                    color = WordyColor.colors.textCardSecondary,
                    style = WordyTypography.bodyMedium,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkI),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { navigateTo(ScreenRoute.Register) }
                ) {
                    Text(
                        stringResource(R.string.sign_up),
                        fontSize = 16.sp,
                        color = WordyColor.colors.textForActiveBtnMkI,
                        style = WordyTypography.bodyMedium
                    )
                }
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { navigateTo(ScreenRoute.Login) }
                ) {
                    Text(
                        stringResource(R.string.sign_in),
                        fontSize = 16.sp,
                        color = WordyColor.colors.textForActiveBtnMkII,
                        style = WordyTypography.bodyMedium
                    )
                }
                RoundedButton(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = WordyColor.colors.backgroundActiveBtnMkII),
                    contentPadding = PaddingValues(vertical = 0.dp, horizontal = 10.dp),
                    onClick = { onEvent(ProfileEvent.SignInWithGoogle) }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_google),
                            contentDescription = "",
                            modifier = Modifier.size(18.dp),
                        )
                        Text(
                            stringResource(R.string.sign_in_with_google),
                            fontSize = 16.sp,
                            color = WordyColor.colors.textForActiveBtnMkII,
                            style = WordyTypography.bodyMedium
                        )
                    }
                }
            }

            Spacer(Modifier)
        }
    }
}