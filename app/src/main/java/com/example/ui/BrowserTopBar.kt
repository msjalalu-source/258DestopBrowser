package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.ActiveSheet
import com.example.browser.TabModel
import com.example.ui.theme.AdBlockGreen
import com.example.ui.theme.WinBlue

@Composable
fun BrowserTopBar(
  tab: TabModel,
  tabCount: Int,
  isBookmarked: Boolean,
  onNavigate: (String) -> Unit,
  onReload: () -> Unit,
  onToggleDesktop: () -> Unit,
  onToggleBookmark: () -> Unit,
  onOpenSheet: (ActiveSheet) -> Unit,
  modifier: Modifier = Modifier
) {
  var isEditingUrl by remember { mutableStateOf(false) }
  var urlText by remember { mutableStateOf("") }
  var showMenu by remember { mutableStateOf(false) }

  val focusManager = LocalFocusManager.current
  val focusRequester = remember { FocusRequester() }

  // Sync url when tab changes or finishes loading
  LaunchedEffect(tab.url, isEditingUrl) {
    if (!isEditingUrl) {
      urlText = if (tab.url == "about:home") "" else tab.url
    }
  }

  val animatedProgress by animateFloatAsState(
    targetValue = if (tab.isLoading) (tab.progress / 100f).coerceIn(0.1f, 1f) else 0f,
    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
    label = "progress"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface)
      .statusBarsPadding()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      // Desktop / Mobile Mode Quick Switch Badge
      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onToggleDesktop() }
          .testTag("topbar_desktop_toggle"),
        color = if (tab.isDesktopMode) WinBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (tab.isDesktopMode) Icons.Default.DesktopWindows else Icons.Default.PhoneAndroid,
            contentDescription = if (tab.isDesktopMode) "Windows Desktop View" else "Mobile View",
            tint = if (tab.isDesktopMode) WinBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (tab.isDesktopMode) "Win" else "Mob",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (tab.isDesktopMode) WinBlue else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Address / Search Input Box
      Surface(
        modifier = Modifier
          .weight(1f)
          .height(44.dp),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (tab.url.startsWith("https://")) Icons.Default.Lock else Icons.Default.Search,
            contentDescription = null,
            tint = if (tab.url.startsWith("https://")) AdBlockGreen else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
          )

          Spacer(modifier = Modifier.width(8.dp))

          Box(modifier = Modifier.weight(1f)) {
            if (urlText.isEmpty() && !isEditingUrl) {
              Text(
                text = "Search or type URL",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
              )
            }

            BasicTextField(
              value = urlText,
              onValueChange = { urlText = it },
              modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                  isEditingUrl = focusState.isFocused
                  if (focusState.isFocused && tab.url != "about:home") {
                    urlText = tab.url
                  }
                }
                .testTag("url_address_input"),
              textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
              ),
              cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
              singleLine = true,
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
              keyboardActions = KeyboardActions(
                onGo = {
                  if (urlText.isNotBlank()) {
                    onNavigate(urlText)
                    focusManager.clearFocus()
                  }
                }
              )
            )
          }

          if (isEditingUrl && urlText.isNotEmpty()) {
            IconButton(
              onClick = { urlText = "" },
              modifier = Modifier.size(24.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
            }
          } else if (tab.url != "about:home") {
            IconButton(
              onClick = onReload,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // Bookmark Button
      IconButton(
        onClick = onToggleBookmark,
        modifier = Modifier
          .size(38.dp)
          .testTag("bookmark_toggle_button")
      ) {
        Icon(
          imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = "Bookmark",
          tint = if (isBookmarked) WinBlue else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
      }

      // Tab Switcher Button with count
      Surface(
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onOpenSheet(ActiveSheet.TABS) }
          .testTag("tab_switcher_button"),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = tabCount.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }

      // Overflow Menu
      Box {
        IconButton(
          onClick = { showMenu = true },
          modifier = Modifier
            .size(38.dp)
            .testTag("overflow_menu_button")
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
        }

        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false },
          modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
          DropdownMenuItem(
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = null,
                  tint = WinBlue,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text("Windows Fidelity Score", fontWeight = FontWeight.SemiBold)
                  Text("Check % spoofing accuracy", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            },
            onClick = {
              showMenu = false
              onOpenSheet(ActiveSheet.WINDOWS_FIDELITY)
            }
          )

          DropdownMenuItem(
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = null,
                  tint = AdBlockGreen,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text("Protection Shield & Stats", fontWeight = FontWeight.SemiBold)
                  Text("${tab.blockedAdultCount} adult, ${tab.blockedAdsCount} ads, ${tab.blockedPopupsCount} popups blocked", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            },
            onClick = {
              showMenu = false
              onOpenSheet(ActiveSheet.BLOCKED_DETAILS)
            }
          )

          HorizontalDivider()

          DropdownMenuItem(
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Bookmark,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Bookmarks & History")
              }
            },
            onClick = {
              showMenu = false
              onOpenSheet(ActiveSheet.BOOKMARKS_HISTORY)
            }
          )

          DropdownMenuItem(
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (tab.isDesktopMode) Icons.Default.PhoneAndroid else Icons.Default.DesktopWindows,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(if (tab.isDesktopMode) "Switch to Mobile View" else "Switch to Windows View")
              }
            },
            onClick = {
              showMenu = false
              onToggleDesktop()
            }
          )

          HorizontalDivider()

          DropdownMenuItem(
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Settings,
                  contentDescription = null,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Browser Settings")
              }
            },
            onClick = {
              showMenu = false
              onOpenSheet(ActiveSheet.SETTINGS)
            }
          )
        }
      }
    }

    // Animated Loading Progress Bar
    if (tab.isLoading && animatedProgress > 0f) {
      LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
          .fillMaxWidth()
          .height(2.5.dp),
        color = WinBlue,
        trackColor = Color.Transparent
      )
    }
  }
}
