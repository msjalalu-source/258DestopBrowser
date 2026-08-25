package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.ActiveSheet
import com.example.browser.BrowserSettings
import com.example.browser.SearchEngine
import com.example.browser.TabModel
import com.example.ui.theme.AdBlockGreen
import com.example.ui.theme.WinBlue
import com.example.ui.theme.WinCyan

data class ShortcutItem(
  val title: String,
  val url: String,
  val category: String,
  val icon: ImageVector,
  val color: Color
)

@Composable
fun NewTabPage(
  tab: TabModel,
  settings: BrowserSettings,
  onNavigate: (String) -> Unit,
  onSelectSearchEngine: (SearchEngine) -> Unit = {},
  onToggleDesktop: () -> Unit,
  onOpenSheet: (ActiveSheet) -> Unit,
  onOpenDiagnostics: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shortcuts = remember {
    listOf(
      ShortcutItem(
        title = "Windows Tester",
        url = "internal:diagnostics",
        category = "Audit",
        icon = Icons.Default.Speed,
        color = Color(0xFF0078D4)
      ),
      ShortcutItem(
        title = "WhatIsMyBrowser",
        url = "https://www.whatismybrowser.com",
        category = "Verification",
        icon = Icons.Default.Language,
        color = Color(0xFF0284C7)
      ),
      ShortcutItem(
        title = "BrowserLeaks",
        url = "https://browserleaks.com/javascript",
        category = "Diagnostics",
        icon = Icons.Default.Security,
        color = Color(0xFF8B5CF6)
      ),
      ShortcutItem(
        title = "Google",
        url = "https://www.google.com",
        category = "Search",
        icon = Icons.Default.Search,
        color = Color(0xFFEA4335)
      ),
      ShortcutItem(
        title = "YouTube",
        url = "https://www.youtube.com",
        category = "Media",
        icon = Icons.Default.Language,
        color = Color(0xFFFF0000)
      ),
      ShortcutItem(
        title = "GitHub",
        url = "https://github.com",
        category = "Developer",
        icon = Icons.Default.DesktopWindows,
        color = Color(0xFF24292F)
      ),
      ShortcutItem(
        title = "Reddit",
        url = "https://www.reddit.com",
        category = "Community",
        icon = Icons.Default.Language,
        color = Color(0xFFFF4500)
      ),
      ShortcutItem(
        title = "Wikipedia",
        url = "https://www.wikipedia.org",
        category = "Reference",
        icon = Icons.Default.Language,
        color = Color(0xFF0A66C2)
      )
    )
  }

  val focusManager = LocalFocusManager.current
  var homeSearchQuery by remember { mutableStateOf("") }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Browser / Engine Logo & Hero Title
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center,
      modifier = Modifier.padding(vertical = 12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(
            Brush.linearGradient(
              colors = listOf(WinBlue, WinCyan)
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.DesktopWindows,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = "WinBrowse Pro",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Windows 11 Engine & Safe Filter",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Central Home Page Search Bar
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp),
      shape = RoundedCornerShape(27.dp),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
      shadowElevation = 2.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Engine selector badge
        SearchEngineDropdownSelector(
          selectedEngine = settings.searchEngine,
          onEngineSelected = onSelectSearchEngine
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Center Input field
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.CenterStart
        ) {
          if (homeSearchQuery.isEmpty()) {
            Text(
              text = "Search with ${settings.searchEngine.displayName} or enter URL...",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          androidx.compose.foundation.text.BasicTextField(
            value = homeSearchQuery,
            onValueChange = { newText ->
              if (newText.contains("\n") || newText.contains("\r")) {
                val clean = newText.replace("\n", "").replace("\r", "").trim()
                if (clean.isNotBlank()) {
                  homeSearchQuery = ""
                  focusManager.clearFocus()
                  onNavigate(clean)
                }
              } else {
                homeSearchQuery = newText
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                  if (homeSearchQuery.isNotBlank()) {
                    val query = homeSearchQuery.trim()
                    homeSearchQuery = ""
                    focusManager.clearFocus()
                    onNavigate(query)
                    true
                  } else {
                    false
                  }
                } else {
                  false
                }
              }
              .testTag("home_page_search_input"),
            textStyle = androidx.compose.ui.text.TextStyle(
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 15.sp
            ),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
              imeAction = ImeAction.Search,
              keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
              onSearch = {
                if (homeSearchQuery.isNotBlank()) {
                  val query = homeSearchQuery.trim()
                  homeSearchQuery = ""
                  focusManager.clearFocus()
                  onNavigate(query)
                }
              },
              onGo = {
                if (homeSearchQuery.isNotBlank()) {
                  val query = homeSearchQuery.trim()
                  homeSearchQuery = ""
                  focusManager.clearFocus()
                  onNavigate(query)
                }
              },
              onDone = {
                if (homeSearchQuery.isNotBlank()) {
                  val query = homeSearchQuery.trim()
                  homeSearchQuery = ""
                  focusManager.clearFocus()
                  onNavigate(query)
                }
              },
              onSend = {
                if (homeSearchQuery.isNotBlank()) {
                  val query = homeSearchQuery.trim()
                  homeSearchQuery = ""
                  focusManager.clearFocus()
                  onNavigate(query)
                }
              }
            )
          )
        }

        // Action Buttons: Clear & Submit Search
        if (homeSearchQuery.isNotEmpty()) {
          IconButton(
            onClick = { homeSearchQuery = "" },
            modifier = Modifier.size(30.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Clear",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        IconButton(
          onClick = {
            if (homeSearchQuery.isNotBlank()) {
              val query = homeSearchQuery.trim()
              homeSearchQuery = ""
              focusManager.clearFocus()
              onNavigate(query)
            }
          },
          modifier = Modifier
            .size(36.dp)
            .testTag("home_page_search_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Quick Shortcuts Grid
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Quick Verification & Sites",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = "Instant Desktop Spoofing",
        style = MaterialTheme.typography.labelSmall,
        color = WinBlue
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyVerticalGrid(
      columns = GridCells.Fixed(4),
      modifier = Modifier
        .fillMaxWidth()
        .height(190.dp),
      contentPadding = PaddingValues(vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(shortcuts) { item ->
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              if (item.url == "internal:diagnostics") {
                onOpenDiagnostics()
              } else {
                onNavigate(item.url)
              }
            }
            .padding(4.dp)
            .testTag("shortcut_${item.title.lowercase().replace(" ", "_")}")
        ) {
          Box(
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(item.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = item.title,
              tint = item.color,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Protection & Spoofing Feature Cards
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Ad Blocker Status Card
      Card(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(14.dp))
          .clickable { onOpenSheet(ActiveSheet.BLOCKED_DETAILS) }
          .testTag("adblock_status_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Shield,
              contentDescription = null,
              tint = if (settings.isAdBlockerEnabled) AdBlockGreen else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(22.dp)
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (settings.isAdBlockerEnabled) AdBlockGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
            ) {
              Text(
                text = if (settings.isAdBlockerEnabled) "ACTIVE" else "OFF",
                style = MaterialTheme.typography.labelSmall,
                color = if (settings.isAdBlockerEnabled) AdBlockGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Ad & Tracker Shield",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (settings.isAdBlockerEnabled) "Blocking banner & tracker ads" else "Ad blocking disabled",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Popup Blocker Status Card
      Card(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(14.dp))
          .clickable { onOpenSheet(ActiveSheet.SETTINGS) }
          .testTag("popup_status_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = if (settings.isPopupBlockerEnabled) WinBlue else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(22.dp)
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (settings.isPopupBlockerEnabled) WinBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
            ) {
              Text(
                text = if (settings.isPopupBlockerEnabled) "ACTIVE" else "OFF",
                style = MaterialTheme.typography.labelSmall,
                color = if (settings.isPopupBlockerEnabled) WinBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Pop-up Blocker",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = if (settings.isPopupBlockerEnabled) "Blocking window.open popups" else "Popups allowed",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
