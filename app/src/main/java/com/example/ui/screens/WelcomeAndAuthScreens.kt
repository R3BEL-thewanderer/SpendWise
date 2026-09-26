package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.state.AppScreen
import com.example.state.SpendWiseViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.GlassCard
import com.example.ui.components.PrimaryButton
import com.example.ui.components.SecondaryButton
import com.example.ui.components.formatCurrency
import com.example.ui.components.getIconForType
import com.example.ui.theme.SpendWiseTheme

@Composable
fun SpendWiseLogo(modifier: Modifier = Modifier, size: Int = 36) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            Color(0xFFEF9C8D),
                            Color(0xFFF4C7B5),
                            Color(0xFFC9B8FF),
                            Color(0xFF9CC9FF),
                            Color(0xFFEF9C8D)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size((size * 0.5f).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.85f))
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "SpendWise",
            fontSize = (size * 0.6f).sp,
            fontWeight = FontWeight.Bold,
            color = SpendWiseTheme.colors.textPrimary
        )
    }
}

@Composable
fun WelcomeScreen(viewModel: SpendWiseViewModel) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        // Ambient background pastel blobs
        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SpendWiseTheme.colors.lavender.copy(alpha = 0.25f),
                            SpendWiseTheme.colors.softBlue.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SpendWiseTheme.colors.softPeach.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SpendWiseLogo(size = 38)

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Understand\nyour money.",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SpendWiseTheme.colors.textPrimary,
                lineHeight = 46.sp
            )
            Text(
                text = "Build better\nhabits.",
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SpendWiseTheme.colors.lavender,
                lineHeight = 46.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Track expenses, set budgets, and turn your spending into meaningful insights.",
                fontSize = 16.sp,
                color = SpendWiseTheme.colors.textSecondary,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Floating Glass Hero Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                elevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Spending",
                            fontSize = 13.sp,
                            color = SpendWiseTheme.colors.textSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF34C759).copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = Color(0xFF34C759),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "12%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF34C759)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "₹ 24,580",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SpendWiseTheme.colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylized sparkline simulation in card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val heights = listOf(14.dp, 24.dp, 18.dp, 32.dp, 26.dp, 38.dp, 22.dp)
                        heights.forEachIndexed { i, h ->
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(h)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (i == 5) SpendWiseTheme.colors.lavender
                                        else SpendWiseTheme.colors.lavender.copy(alpha = 0.35f)
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Get Started",
                onClick = { viewModel.navigateTo(AppScreen.SIGN_UP) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "welcome_get_started"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Sign In",
                onClick = { viewModel.navigateTo(AppScreen.SIGN_IN) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "welcome_sign_in"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SignUpScreen(viewModel: SpendWiseViewModel) {
    var fullName by remember { mutableStateOf("Ashish Singh") }
    var email by remember { mutableStateOf("ashish.singh@example.com") }
    var password by remember { mutableStateOf("••••••••") }
    var confirmPassword by remember { mutableStateOf("••••••••") }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SpendWiseTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SpendWiseLogo(size = 32)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create your\naccount",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SpendWiseTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Start your journey towards smarter money habits.",
                fontSize = 15.sp,
                color = SpendWiseTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                placeholder = { Text("Enter your full name") },
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_name_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("Enter your email address") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_email_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Create a strong password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = SpendWiseTheme.colors.textSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_password_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                placeholder = { Text("Confirm your password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signup_confirm_password_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Create Account",
                onClick = {
                    viewModel.userProfile = viewModel.userProfile.copy(name = fullName, email = email)
                    viewModel.navigateTo(AppScreen.PROFILE_SETUP)
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "signup_create_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Continue with Google",
                onClick = {
                    viewModel.navigateTo(AppScreen.PROFILE_SETUP)
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "signup_google_button"
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = SpendWiseTheme.colors.textSecondary
                )
                Text(
                    text = "Sign In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpendWiseTheme.colors.lavender,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.SIGN_IN) }
                        .testTag("signup_goto_signin")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SignInScreen(viewModel: SpendWiseViewModel) {
    var email by remember { mutableStateOf("ashish.singh@example.com") }
    var password by remember { mutableStateOf("••••••••") }
    var rememberMe by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SpendWiseTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SpendWiseLogo(size = 32)

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Welcome ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SpendWiseTheme.colors.textPrimary
                )
                Text(
                    text = "back",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SpendWiseTheme.colors.lavender
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sign in to continue your journey towards smarter spending.",
                fontSize = 15.sp,
                color = SpendWiseTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("Enter your email address") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signin_email_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Enter your password") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = SpendWiseTheme.colors.textSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("signin_password_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = SpendWiseTheme.colors.lavender)
                    )
                    Text(
                        text = "Remember me",
                        fontSize = 14.sp,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                }

                Text(
                    text = "Forgot password?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SpendWiseTheme.colors.lavender,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.FORGOT_PASSWORD) }
                        .testTag("signin_forgot_password")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Sign In",
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "signin_submit_button"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Continue with Google",
                onClick = { viewModel.navigateTo(AppScreen.HOME) },
                modifier = Modifier.fillMaxWidth(),
                testTag = "signin_google_button"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = SpendWiseTheme.colors.textSecondary
                )
                Text(
                    text = "Create Account",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpendWiseTheme.colors.lavender,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.SIGN_UP) }
                        .testTag("signin_goto_signup")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ForgotPasswordScreen(viewModel: SpendWiseViewModel) {
    var email by remember { mutableStateOf("ashish.singh@example.com") }
    var linkSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = SpendWiseTheme.colors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SpendWiseLogo(size = 32)

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Forgot your ",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SpendWiseTheme.colors.textPrimary
                )
                Text(
                    text = "password?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SpendWiseTheme.colors.softCoral
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No worries! Enter your email address and we'll send you a reset link.",
                fontSize = 15.sp,
                color = SpendWiseTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                placeholder = { Text("Enter your email address") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = SpendWiseTheme.colors.textSecondary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forgot_email_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Send Reset Link",
                onClick = { linkSent = true },
                modifier = Modifier.fillMaxWidth(),
                testTag = "forgot_send_button"
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(visible = linkSent) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SpendWiseTheme.colors.softBlue.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = SpendWiseTheme.colors.softBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "A reset link will be sent to your email",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SpendWiseTheme.colors.textPrimary
                            )
                            Text(
                                text = "Please check your inbox or spam folder.",
                                fontSize = 12.sp,
                                color = SpendWiseTheme.colors.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = SpendWiseTheme.colors.lavender
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Back to Sign In",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SpendWiseTheme.colors.lavender,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.SIGN_IN) }
                        .testTag("forgot_goto_signin")
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(viewModel: SpendWiseViewModel) {
    var name by remember { mutableStateOf(viewModel.userProfile.name) }
    var incomeText by remember { mutableStateOf("45000") }
    var budgetText by remember { mutableStateOf("30000") }
    val selectedCats = remember {
        mutableStateListOf("Food", "Transport", "Shopping", "Bills")
    }

    val availableCats = listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Health", "Education", "Travel")
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SpendWiseTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SpendWiseTheme.colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Step 1 of 3",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SpendWiseTheme.colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { 0.33f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SpendWiseTheme.colors.softBlue,
                        trackColor = SpendWiseTheme.colors.softSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Let's set up\nyour SpendWise",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SpendWiseTheme.colors.textPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tell us a bit about yourself to personalize your experience.",
                fontSize = 15.sp,
                color = SpendWiseTheme.colors.textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name") },
                placeholder = { Text("Ashish Singh") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_name_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Currency Selector
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Currency", fontSize = 12.sp, color = SpendWiseTheme.colors.textSecondary)
                        Text(text = "INR ₹", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SpendWiseTheme.colors.textPrimary)
                    }
                    Text(text = "Change", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SpendWiseTheme.colors.lavender)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = incomeText,
                onValueChange = { incomeText = it.filter { c -> c.isDigit() } },
                label = { Text("Monthly Income (₹)") },
                placeholder = { Text("Enter your monthly income") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_income_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = budgetText,
                onValueChange = { budgetText = it.filter { c -> c.isDigit() } },
                label = { Text("Monthly Spending Budget (₹)") },
                placeholder = { Text("Enter your monthly budget") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_budget_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SpendWiseTheme.colors.surface,
                    unfocusedContainerColor = SpendWiseTheme.colors.surface
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Preferred Spending Categories",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = SpendWiseTheme.colors.textPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableCats.forEach { cat ->
                    val isSelected = selectedCats.contains(cat)
                    CategoryChip(
                        name = cat,
                        icon = getIconForType(cat),
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) selectedCats.remove(cat)
                            else selectedCats.add(cat)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Continue",
                onClick = {
                    val inc = incomeText.toDoubleOrNull() ?: 45000.0
                    val bud = budgetText.toDoubleOrNull() ?: 30000.0
                    viewModel.userProfile = viewModel.userProfile.copy(
                        name = name,
                        monthlyIncome = inc,
                        monthlyBudget = bud,
                        preferredCategories = selectedCats.toList()
                    )
                    viewModel.monthlyIncome = inc
                    viewModel.navigateTo(AppScreen.HOME)
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "setup_continue_button"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
