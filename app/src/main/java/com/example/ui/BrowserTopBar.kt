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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.browser.ActiveSheet
import com.example.browser.SearchEngine
import com.example.browser.TabModel
import com.example.ui.theme.AdBlockGreen
import com.example.ui.theme.WinBlue

@Composable
fun SearchEngineIcon(
  engine: SearchEngine,
  size: Dp = 22.dp,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .size(size)
      .clip(CircleShape)
      .background(Color(engine.brandColorHex)),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = engine.shortLabel,
      color = Color.White,
      fontSize = if (engine.shortLabel.length > 2) (size.value * 0.42f).sp else (size.value * 0.58f).sp,
      fontWeight = FontWeight.ExtraBold,
      lineHeight = (size.value * 0.6f).sp
    )
  }
}

@Composable
fun SearchEngineDropdownSelector(
  selectedEngine: SearchEngine,
  onEngineSelected: (SearchEngine) -> Unit,
  modifier: Modifier = Modifier
) {
  var showDropdown by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    Surface(
      modifier = Modifier
        .clip(RoundedCornerShape(14.dp))
        .clickable { showDropdown = true }
        .testTag("addressbar_search_engine_trigger"),
      color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
      shape = RoundedCornerShape(14.dp),
      tonalElevation = 1.dp
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        SearchEngineIcon(engine = selectedEngine, size = 18.dp)
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
          imageVector = Icons.Default.ArrowDropDown,
          contentDescription = "Change Search Engine",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(16.dp)
        )
      }
    }

    DropdownMenu(
      expanded = showDropdown,
      onDismissRequest = { showDropdown = false },
      modifier = Modifier
        .background(MaterialTheme.colorScheme.surface)
        .width(230.dp)
    ) {
      Text(
        text = "Choose Search Engine",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
      )
      HorizontalDivider()

      SearchEngine.entries.forEach { engine ->
        DropdownMenuItem(
          text = {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              SearchEngineIcon(engine = engine, size = 22.dp)
              Spacer(modifier = Modifier.width(10.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = engine.displayName,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = if (engine == selectedEngine) FontWeight.Bold else FontWeight.Normal,
                  color = if (engine == selectedEngine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
              }
              if (engine == selectedEngine) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          },
          onClick = {
            onEngineSelected(engine)
            showDropdown = false
          },
          modifier = Modifier.testTag("search_engine_${engine.name.lowercase()}")
        )
      }
    }
  }
}

@Composable
fun BrowserTopBar(
  tab: TabModel,
  tabCount: Int,
  isBookmarked: Boolean,
  searchEngine: SearchEngine = SearchEngine.GOOGLE,
  onSelectSearchEngine: (SearchEngine) -> Unit = {},
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
      // Integrated Address / Search Input Box with Dropdown Search Engine
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
            .padding(start = 6.dp, end = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Search Engine Logo with Dropdown Selector
          SearchEngineDropdownSelector(
            selectedEngine = searchEngine,
            onEngineSelected = onSelectSearchEngine
          )

          Spacer(modifier = Modifier.width(6.dp))

          // Center: Text Input Area
          Box(
            modifier = Modifier
              .weight(1f)
              .clickable { focusRequester.requestFocus() },
            contentAlignment = Alignment.CenterStart
          ) {
            if (urlText.isEmpty() && !isEditingUrl) {
              Text(
                text = "Search with ${searchEngine.displayName} or enter URL",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                .onKeyEvent { keyEvent ->
                  if ((keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) && keyEvent.type == KeyEventType.KeyUp) {
                    if (urlText.isNotBlank()) {
                      val queryToSearch = urlText.trim()
                      isEditingUrl = false
                      focusManager.clearFocus()
                      onNavigate(queryToSearch)
                      true
                    } else {
                      false
                    }
                  } else {
                    false
                  }
                }
                .testTag("url_address_input"),
              textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
              ),
              cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
              ),
              keyboardActions = KeyboardActions(
                onSearch = {
                  if (urlText.isNotBlank()) {
                    val queryToSearch = urlText.trim()
                    isEditingUrl = false
                    focusManager.clearFocus()
                    onNavigate(queryToSearch)
                  }
                },
                onGo = {
                  if (urlText.isNotBlank()) {
                    val queryToSearch = urlText.trim()
                    isEditingUrl = false
                    focusManager.clearFocus()
                    onNavigate(queryToSearch)
                  }
                },
                onDone = {
                  if (urlText.isNotBlank()) {
                    val queryToSearch = urlText.trim()
                    isEditingUrl = false
                    focusManager.clearFocus()
                    onNavigate(queryToSearch)
                  }
                },
                onSend = {
                  if (urlText.isNotBlank()) {
                    val queryToSearch = urlText.trim()
                    isEditingUrl = false
                    focusManager.clearFocus()
                    onNavigate(queryToSearch)
                  }
                }
              )
            )
          }

          // Right action buttons (Clear / Search submit / Reload)
          if (urlText.isNotBlank() && (isEditingUrl || tab.url == "about:home")) {
            if (urlText.isNotEmpty()) {
              IconButton(
                onClick = { urlText = "" },
                modifier = Modifier.size(26.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Clear",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            IconButton(
              onClick = {
                if (urlText.isNotBlank()) {
                  val queryToSearch = urlText.trim()
                  isEditingUrl = false
                  focusManager.clearFocus()
                  onNavigate(queryToSearch)
                }
              },
              modifier = Modifier
                .size(28.dp)
                .testTag("address_bar_search_action_button")
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
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
