package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.ai.model.AssistantMessage
import com.example.state.ActiveModal
import com.example.state.SpendWiseViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.SpendWiseTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantModal(viewModel: SpendWiseViewModel) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val textSecondary = SpendWiseTheme.colors.textSecondary
    val isDark = SpendWiseTheme.colors.isDark
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.assistantMessages.size, viewModel.isAssistantThinking) {
        if (viewModel.assistantMessages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.assistantMessages.size - 1)
        }
    }

    ModalBottomSheet(
        onDismissRequest = { viewModel.activeModal = ActiveModal.NONE },
        sheetState = sheetState,
        containerColor = SpendWiseTheme.colors.background,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(SpendWiseTheme.colors.textMuted.copy(alpha = 0.5f))
            )
        },
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SpendWiseTheme.colors.lavender, SpendWiseTheme.colors.softBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Sparkle",
                            tint = Color(0xFF171717),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SpendWise AI",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Grounded in your verified numbers",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.activeModal = ActiveModal.NONE },
                    modifier = Modifier.testTag("close_ai_assistant_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Suggestions Carousel
            val suggestions = listOf(
                "Where did most of my money go?",
                "How much on food?",
                "Am I spending more than last month?",
                "How is my budget?",
                "What is my balance?"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { suggestion ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isDark) Color(0x22FFFFFF) else Color.White.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SpendWiseTheme.colors.glassBorder),
                        modifier = Modifier
                            .clickable {
                                viewModel.askAssistant(suggestion)
                            }
                            .testTag("ai_suggestion_${suggestion.take(10)}")
                    ) {
                        Text(
                            text = suggestion,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = textPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conversation Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.assistantMessages) { message ->
                    MessageBubble(message = message)
                }

                if (viewModel.isAssistantThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = SpendWiseTheme.colors.lavender,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Analyzing verified domain metrics...",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                    }
                }
            }

            // Input Bar
            Surface(
                color = if (isDark) Color(0xFF1E2126) else Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Ask about your finances...", fontSize = 14.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_assistant_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = if (isDark) Color(0x22FFFFFF) else Color(0xFFF7F6F2),
                            unfocusedContainerColor = if (isDark) Color(0x22FFFFFF) else Color(0xFFF7F6F2),
                            focusedBorderColor = SpendWiseTheme.colors.lavender,
                            unfocusedBorderColor = SpendWiseTheme.colors.glassBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val query = inputText
                                inputText = ""
                                viewModel.askAssistant(query)
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SpendWiseTheme.colors.lavender)
                            .testTag("ai_assistant_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color(0xFF171717),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: AssistantMessage) {
    val textPrimary = SpendWiseTheme.colors.textPrimary
    val isDark = SpendWiseTheme.colors.isDark

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (message.isUser) {
            Surface(
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                color = SpendWiseTheme.colors.lavender,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = Color(0xFF171717),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            GlassCard(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp),
                modifier = Modifier.widthIn(max = 320.dp),
                elevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = textPrimary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
