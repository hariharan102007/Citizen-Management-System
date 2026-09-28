package com.example.citizencomplaintapp.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citizencomplaintapp.R
import com.example.citizencomplaintapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("citizen@test.com") }
    var password by remember { mutableStateOf("........") }
    var selectedRole by remember { mutableStateOf("Citizen") }
    var showSocialOptions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Logo Container
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(SecondaryBlue, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Logo",
                tint = PrimaryOrange,
                modifier = Modifier.size(32.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Welcome Back",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = SecondaryBlue
        )
        
        Text(
            text = "Sign in to your CivicResolve account",
            fontSize = 14.sp,
            color = TextGrey,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Demo Mode Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE6F0FF), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = Color(0xFF1A73E8),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "DEMO MODE — Social auth enabled",
                fontSize = 12.sp,
                color = Color(0xFF1A73E8),
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Form Fields
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Email Address",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryBlue,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Password",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryBlue,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Sign In Button
        Button(
            onClick = { 
                if (!showSocialOptions) {
                    showSocialOptions = true
                } else {
                    onLoginSuccess(email) 
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }

        if (showSocialOptions) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                Text(
                    text = "OR CONTINUE WITH",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGrey
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialLoginButton(
                    painter = painterResource(id = R.drawable.ic_google),
                    label = "Google",
                    onClick = { onLoginSuccess("google_user@test.com") }
                )
                SocialLoginButton(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    label = "Facebook",
                    onClick = { onLoginSuccess("fb_user@test.com") }
                )
                SocialLoginButton(
                    painter = painterResource(id = R.drawable.ic_apple),
                    label = "Apple",
                    onClick = { onLoginSuccess("apple_user@test.com") }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Demo Accounts Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Account Types",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryBlue,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            DemoAccountCard(
                role = "Citizen",
                demoEmail = "citizen@test.com",
                isSelected = selectedRole == "Citizen",
                onClick = {
                    selectedRole = "Citizen"
                    email = "citizen@test.com"
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            DemoAccountCard(
                role = "Super Officer",
                demoEmail = "officer@test.com",
                isSelected = selectedRole == "Officer",
                onClick = {
                    selectedRole = "Officer"
                    email = "officer@test.com"
                }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Footer
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = TextGrey, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Your data is protected by government-grade encryption",
                fontSize = 11.sp,
                color = TextGrey
            )
        }
    }
}

@Composable
fun SocialLoginButton(
    painter: Painter,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Image(
                    painter = painter,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryBlue)
    }
}

@Composable
fun DemoAccountCard(
    role: String,
    demoEmail: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isSelected) BorderStroke(1.5.dp, PrimaryOrange) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = if (isSelected) PrimaryOrange else SecondaryBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = role,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) PrimaryOrange else SecondaryBlue
                )
                Text(
                    text = demoEmail,
                    fontSize = 13.sp,
                    color = TextGrey
                )
            }
        }
    }
}