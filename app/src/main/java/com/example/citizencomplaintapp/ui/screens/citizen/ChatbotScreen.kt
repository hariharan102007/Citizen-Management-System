package com.example.citizencomplaintapp.ui.screens.citizen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.citizencomplaintapp.ui.theme.BackgroundLight
import com.example.citizencomplaintapp.ui.theme.SecondaryBlue
import com.example.citizencomplaintapp.ui.theme.TextGrey

@Composable
fun ChatbotScreen() {
    var message by remember { mutableStateOf("") }
    val chatHistory = remember { mutableStateListOf(
        ChatMessage("Hello! I'm your Civic Assistant. How can I help you today?", false),
        ChatMessage("You can ask about complaint status, departments, or how to report an issue.", false)
    ) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundLight)) {
        Box(modifier = Modifier.fillMaxWidth().background(SecondaryBlue).padding(20.dp)) {
            Text("AI Assistant", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatHistory) { msg ->
                ChatBubble(msg)
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Ask me anything...", fontSize = 14.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SecondaryBlue,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        if (message.isNotBlank()) {
                            chatHistory.add(ChatMessage(message, true))
                            val response = getBotResponse(message)
                            chatHistory.add(ChatMessage(response, false))
                            message = ""
                        }
                    },
                    containerColor = SecondaryBlue,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isUser) SecondaryBlue else Color.White
    val textColor = if (message.isUser) Color.White else Color.Black
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = bgColor,
            shape = shape,
            shadowElevation = 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = textColor,
                fontSize = 14.sp
            )
        }
    }
}

fun getBotResponse(input: String): String {
    val lower = input.lowercase()
    return when {
        lower.contains("status") -> "Please provide your Complaint ID (e.g., CMP123) to check the real-time status."
        lower.contains("pothole") -> "Roads & Potholes complaints are handled by the Public Works Department. Would you like to start a report?"
        lower.contains("hello") || lower.contains("hi") -> "Hi there! How can I assist you with civic issues today?"
        else -> "I'm still learning! You can contact support at 1800-CIVIC-APP for more complex queries."
    }
}

data class ChatMessage(val text: String, val isUser: Boolean)