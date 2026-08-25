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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
  var searchQuery by remember { mutableStateOf("") }
  val fidelityScore = remember(tab.isDesktopMode, settings) {
    if (tab.isDesktopMode) settings.calculateWindowsFidelityScore() else 0
  }

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

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    // Windows Fidelity Status Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .clickable { onOpenSheet(ActiveSheet.WINDOWS_FIDELITY) }
        .testTag("fidelity_banner_card"),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              colors = if (tab.isDesktopMode) {
                listOf(
                  MaterialTheme.colorScheme.surfaceVariant,
                  WinBlue.copy(alpha = 0.15f)
                )
              } else {
                listOf(
                  MaterialTheme.colorScheme.surfaceVariant,
                  MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                )
              }
            )
          )
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .background(if (tab.isDesktopMode) WinBlue else MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (tab.isDesktopMode) Icons.Default.DesktopWindows else Icons.Default.PhoneAndroid,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = if (tab.isDesktopMode) "Windows Mode Active" else "Mobile View Active",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (tab.isDesktopMode) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = WinCyan,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Text(
              text = if (tab.isDesktopMode) {
                "${settings.windowsPreset.displayName} • ${settings.resolutionPreset.width}x${settings.resolutionPreset.height}"
              } else {
                "Standard Android User-Agent & Viewport"
              },
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        // Score Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (tab.isDesktopMode) WinBlue else MaterialTheme.colorScheme.surface,
          shadowElevation = 2.dp
        ) {
          Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (tab.isDesktopMode) "$fidelityScore%" else "0%",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = if (tab.isDesktopMode) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Fidelity",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              color = if (tab.isDesktopMode) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Search Input Field with integrated search engine logo dropdown
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      modifier = Modifier
        .fillMaxWidth()
        .testTag("home_search_input"),
      placeholder = {
        Text("Search with ${settings.searchEngine.displayName} or enter URL...")
      },
      leadingIcon = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(start = 8.dp, end = 4.dp)
        ) {
          SearchEngineDropdownSelector(
            selectedEngine = settings.searchEngine,
            onEngineSelected = onSelectSearchEngine
          )
        }
      },
      trailingIcon = {
        if (searchQuery.isNotBlank()) {
          IconButton(
            onClick = {
              onNavigate(searchQuery)
              searchQuery = ""
            },
            modifier = Modifier.testTag("home_search_submit_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      },
      singleLine = true,
      shape = RoundedCornerShape(28.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface
      ),
      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
      keyboardActions = KeyboardActions(
        onSearch = {
          if (searchQuery.isNotBlank()) {
            onNavigate(searchQuery)
            searchQuery = ""
          }
        }
      )
    )

    Spacer(modifier = Modifier.height(24.dp))

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

    Spacer(modifier = Modifier.height(16.dp))

    // Quick View Toggle Action Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(14.dp))
        .clickable { onToggleDesktop() }
        .testTag("quick_view_toggle_card"),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
      )
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = if (tab.isDesktopMode) "Switch to Mobile View" else "Switch to Windows Desktop View",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
          )
          Text(
            text = if (tab.isDesktopMode) "Click to test standard mobile layout" else "Click to spoof Windows 11 Desktop",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
          )
        }
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primary
        ) {
          Text(
            text = if (tab.isDesktopMode) "Mobile" else "Windows",
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))
  }
}
