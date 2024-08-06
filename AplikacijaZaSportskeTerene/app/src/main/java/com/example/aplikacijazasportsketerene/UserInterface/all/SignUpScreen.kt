//( navController: NavController, context: Context)
package com.example.aplikacijazasportsketerene.UserInterface.signup

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SignUpScreen(
    navController: NavController,
    context: Context,
    signUpViewModel: SignUpViewModel = SignUpViewModel.getClassInstance(context)) {
    val firstName by signUpViewModel.firstName.collectAsState()
    val lastName by signUpViewModel.lastName.collectAsState()
    val username by signUpViewModel.username.collectAsState()
    val email by signUpViewModel.email.collectAsState()
    val password by signUpViewModel.password.collectAsState()
    val confirmPassword by signUpViewModel.confirmPassword.collectAsState()
    val phoneNumber by signUpViewModel.phoneNumber.collectAsState()
    val passwordVisible by signUpViewModel.passwordVisible.collectAsState()
    val confirmPasswordVisible by signUpViewModel.confirmPasswordVisible.collectAsState()

    Scaffold(){ _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_sports_basketball_24),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(128.dp)
                    .padding(bottom = 32.dp)
                    .padding(top = 15.dp)
            )
            OutlinedTextField(
                value = firstName,
                onValueChange = {
                    signUpViewModel.updateFirstName(it)
                },
                label = { Text("Ime") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(17.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = {
                    signUpViewModel.updateLastName(it)
                },
                label = { Text("Prezime") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(17.dp))
            OutlinedTextField(
                value = username,
                onValueChange = {
                    signUpViewModel.updateUsername(it)
                },
                label = { Text("Korisničko ime") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_person_outline_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(17.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    signUpViewModel.updateEmail(it)
                },
                label = { Text("E-mail") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_email_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(17.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    signUpViewModel.updatePhoneNumber(it)
                },
                label = { Text("Broj telefona") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_phone_24),
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(35.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    signUpViewModel.updatePassword(it)
                },
                label = { Text("Šifra") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_password_24),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    val image = if (passwordVisible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)

                    IconButton(onClick = {
                        signUpViewModel.togglePasswordVisibility()
                    }) {
                        Icon(painter = image, contentDescription = null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false)
            )
            Spacer(modifier = Modifier.height(17.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    signUpViewModel.updateConfirmPassword(it)
                },
                label = { Text("Potvrdi šifru") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_password_24),
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)

                    IconButton(onClick = {
                        signUpViewModel.toggleConfirmPasswordVisibility()
                    }) {
                        Icon(painter = image, contentDescription = null)
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { signUpViewModel.onSignUpClick { navController.popBackStack(Screen.SignIn.name, inclusive = true) } },
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
            ) {
                Text("Napravite nalog")
            }
        }
    }
}
