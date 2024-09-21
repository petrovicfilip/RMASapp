package com.example.aplikacijazasportsketerene.UserInterface.LogIn

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplikacijazasportsketerene.R
import com.example.aplikacijazasportsketerene.Screen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Composable
fun LogInScreen(
    navController: NavController,
    context: Context,
    logInViewModel: LogInViewModel = LogInViewModel.getInstance(context)
) {
    val email by logInViewModel.email.collectAsState()
    val password by logInViewModel.password.collectAsState()
    val passwordVisible by logInViewModel.passwordVisible.collectAsState()

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
                painter = painterResource(id = R.mipmap.img2),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    //.padding(bottom = 32.dp)
                    .border(
                        border = BorderStroke(2.dp, Color.Cyan),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(8.dp),
                ,colors = CardColors(containerColor = CardDefaults.cardColors().containerColor,
                    contentColor = Color.Black, disabledContentColor = MaterialTheme.colorScheme.primary, disabledContainerColor = Color.Gray
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        logInViewModel.updateEmail(it)
                    },
                    label = { Text("E-mail") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_email_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        logInViewModel.updatePassword(it)
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
                            logInViewModel.togglePasswordVisibility()
                        }) {
                            Icon(painter = image, contentDescription = null)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(autoCorrect = false)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        logInViewModel.onSignInClick({
                            navController.popBackStack(Screen.LogIn.name, inclusive = true)
                            navController.navigate(Screen.Home.name)
                        }, {
                            navController.navigate(Screen.Loading.name)
                        }, {
                            navController.popBackStack(Screen.Loading.name, inclusive = true)
                        })
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text("Prijavite se")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { navController.navigate(Screen.SignIn.name) },
            ) {
                Text("Napravite nalog")
            }
        }
    }
}
