package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.browser.BrowserSettings
import com.example.browser.ResolutionPreset
import com.example.browser.SearchEngine
import com.example.browser.WindowsPreset
import com.example.ui.theme.AdBlockGreen
import com.example.ui.theme.WinBlue

@Composable
fun SettingsDialog(
  settings: BrowserSettings,
  onUpdateSettings: (BrowserSettings) -> Unit,
  onClearData: (clearCookies: Boolean, clearCache: Boolean, clearHistory: Boolean) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showClearDialog by remember { mutableStateOf(false) }
  var clearCookiesCheck by remember { mutableStateOf(true) }
  var clearCacheCheck by remember { mutableStateOf(true) }
  var clearHistoryCheck by remember { mutableStateOf(true) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .height(650.dp)
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(18.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Browser Settings",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Section 1: Windows Spoofing Core Controls
          item {
            Text(
              text = "WINDOWS DESKTOP SPOOFING",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = WinBlue
            )
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                // Main Desktop Spoofing Toggle
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(
                      imageVector = Icons.Default.DesktopWindows,
                      contentDescription = null,
                      tint = WinBlue,
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = "Windows Desktop Mode",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                      )
                      Text(
                        text = "Spoofs complete Windows desktop browser identity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                  Switch(
                    checked = settings.isDesktopSpoofing,
                    onCheckedChange = { onUpdateSettings(settings.copy(isDesktopSpoofing = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = WinBlue),
                    modifier = Modifier.testTag("setting_desktop_spoofing_switch")
                  )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Windows Preset Selection
                Text(
                  text = "Windows Browser Preset",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                WindowsPreset.entries.forEach { preset ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { onUpdateSettings(settings.copy(windowsPreset = preset)) }
                      .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    RadioButton(
                      selected = settings.windowsPreset == preset,
                      onClick = { onUpdateSettings(settings.copy(windowsPreset = preset)) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                      Text(
                        text = preset.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (settings.windowsPreset == preset) FontWeight.Bold else FontWeight.Normal
                      )
                      Text(
                        text = preset.browserName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Screen Resolution Emulation
                Text(
                  text = "Emulated Desktop Resolution",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                ResolutionPreset.entries.forEach { res ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { onUpdateSettings(settings.copy(resolutionPreset = res)) }
                      .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    RadioButton(
                      selected = settings.resolutionPreset == res,
                      onClick = { onUpdateSettings(settings.copy(resolutionPreset = res)) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = res.displayName,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = if (settings.resolutionPreset == res) FontWeight.Bold else FontWeight.Normal
                    )
                  }
                }
              }
            }
          }

          // Section 2: Ad-Blocker, Popup Blocker & Adult Content Filter
          item {
            Text(
              text = "PROTECTION & FILTERING",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = AdBlockGreen
            )
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                // Strict Adult Content Filter (Permanently Locked ON)
                var showAdultLockedInfo by remember { mutableStateOf(false) }

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { showAdultLockedInfo = true }
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDC2626).copy(alpha = 0.15f)),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(22.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = "Adult Content Filter",
                          fontWeight = FontWeight.Bold,
                          style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = Color(0xFFDC2626).copy(alpha = 0.2f)
                        ) {
                          Text(
                            text = "🔒 LOCKED ON",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                          )
                        }
                      }
                      Text(
                        text = "এডাল্ট ও ক্ষতিকর কন্টেন্ট স্থায়ীভাবে ব্লক (অফ করা যাবে না)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFDC2626)
                      )
                      Text(
                        text = "Filters 200+ adult tube networks, live cams, NSFW & enforces SafeSearch",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                  Switch(
                    checked = true,
                    onCheckedChange = { showAdultLockedInfo = true },
                    colors = SwitchDefaults.colors(
                      checkedThumbColor = Color(0xFFDC2626),
                      checkedTrackColor = Color(0xFFDC2626).copy(alpha = 0.4f),
                      disabledCheckedThumbColor = Color(0xFFDC2626),
                      disabledCheckedTrackColor = Color(0xFFDC2626).copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("setting_adult_filter_switch")
                  )
                }

                if (showAdultLockedInfo) {
                  AlertDialog(
                    onDismissRequest = { showAdultLockedInfo = false },
                    icon = {
                      Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(36.dp)
                      )
                    },
                    title = {
                      Text(
                        text = "অ্যাডাল্ট ফিল্টার স্থায়ীভাবে সুরক্ষিত",
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                      )
                    },
                    text = {
                      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                          text = "🔒 এই অ্যাডাল্ট ফিল্টার সুইচটি স্থায়ীভাবে চালু (LOCKED ON) রাখা হয়েছে এবং এটি কোনোভাবেই অফ বা বন্ধ করা যাবে না।",
                          fontWeight = FontWeight.SemiBold,
                          color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                          text = "এটি অত্যন্ত শক্তিশালী এবং স্বয়ংক্রিয়ভাবে সমস্ত পর্নোগ্রাফিক ওয়েবসাইট, অ্যাডাল্ট লাইভ ক্যাম, এসকর্ট পোর্টাল এবং ক্ষতিকর অ্যাডাল্ট কন্টেন্ট ব্লক করে এবং Google, Bing ও DuckDuckGo-তে বাধ্যতামূলক SafeSearch নিশ্চিত করে।",
                          style = MaterialTheme.typography.bodySmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                      }
                    },
                    confirmButton = {
                      Button(
                        onClick = { showAdultLockedInfo = false },
                        colors = ButtonDefaults.buttonColors(containerColor = WinBlue)
                      ) {
                        Text("বুঝেছি (OK)")
                      }
                    }
                  )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Ad Blocker Switch
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Shield,
                      contentDescription = null,
                      tint = AdBlockGreen,
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = "Built-in Ad Blocker",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                      )
                      Text(
                        text = "Blocks trackers, banners & video advertisements",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                  Switch(
                    checked = settings.isAdBlockerEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isAdBlockerEnabled = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = AdBlockGreen),
                    modifier = Modifier.testTag("setting_adblocker_switch")
                  )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Cosmetic Ad Collapse Switch
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Cosmetic Element Hiding",
                      fontWeight = FontWeight.SemiBold,
                      style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                      text = "Collapses blank empty ad containers & placeholders",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Switch(
                    checked = settings.isCosmeticAdBlockingEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isCosmeticAdBlockingEnabled = it)) }
                  )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Pop-up Blocker Switch
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Block,
                      contentDescription = null,
                      tint = WinBlue,
                      modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = "Pop-up Window Blocker",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                      )
                      Text(
                        text = "Blocks unwanted window.open pop-ups and new tab redirects",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                  Switch(
                    checked = settings.isPopupBlockerEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isPopupBlockerEnabled = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = WinBlue),
                    modifier = Modifier.testTag("setting_popupblocker_switch")
                  )
                }
              }
            }
          }

          // Section 3: Advanced Spoofing Parameters
          item {
            Text(
              text = "ADVANCED HARDWARE & GPU SPOOFING",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                // WebGL GPU Spoofing
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "WebGL GPU Spoofing (NVIDIA Direct3D)",
                      fontWeight = FontWeight.SemiBold,
                      style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                      text = "Masks mobile GPU with desktop NVIDIA RTX Direct3D renderer",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Switch(
                    checked = settings.isWebGlSpoofingEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isWebGlSpoofingEnabled = it)) }
                  )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Touch Points Spoofing
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Mouse / Touch Points Spoofing",
                      fontWeight = FontWeight.SemiBold,
                      style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                      text = "Reports 0 touch points to mimic desktop pointer hardware",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Switch(
                    checked = settings.isTouchPointsSpoofingEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isTouchPointsSpoofingEnabled = it)) }
                  )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Hardware Cores & RAM
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Hardware Footprint Spoofing",
                      fontWeight = FontWeight.SemiBold,
                      style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                      text = "Reports 16 CPU cores & 16 GB device memory",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                  Switch(
                    checked = settings.isHardwareSpoofingEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isHardwareSpoofingEnabled = it)) }
                  )
                }
              }
            }
          }

          // Section 4: General Browser Settings & Data
          item {
            Text(
              text = "GENERAL & SEARCH ENGINE",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                // Search Engine
                Text(
                  text = "Default Search Engine",
                  fontWeight = FontWeight.SemiBold,
                  style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                SearchEngine.entries.forEach { engine ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { onUpdateSettings(settings.copy(searchEngine = engine)) }
                      .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    RadioButton(
                      selected = settings.searchEngine == engine,
                      onClick = { onUpdateSettings(settings.copy(searchEngine = engine)) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = engine.displayName, style = MaterialTheme.typography.bodyMedium)
                  }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // JavaScript & Cookies
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "JavaScript Support", style = MaterialTheme.typography.bodyMedium)
                  Switch(
                    checked = settings.isJavaScriptEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isJavaScriptEnabled = it)) }
                  )
                }

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = "Accept Cookies", style = MaterialTheme.typography.bodyMedium)
                  Switch(
                    checked = settings.isCookiesEnabled,
                    onCheckedChange = { onUpdateSettings(settings.copy(isCookiesEnabled = it)) }
                  )
                }
              }
            }
          }

          // Section 5: Clear Browsing Data Action
          item {
            OutlinedButton(
              onClick = { showClearDialog = true },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("clear_browsing_data_button"),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Clear Browsing Data (Cookies, Cache, History)")
            }
          }

          item {
            Spacer(modifier = Modifier.height(16.dp))
          }
        }
      }
    }
  }

  if (showClearDialog) {
    AlertDialog(
      onDismissRequest = { showClearDialog = false },
      title = { Text("Clear Browsing Data") },
      text = {
        Column {
          Text("Select the data you want to remove:")
          Spacer(modifier = Modifier.height(10.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = clearCookiesCheck, onCheckedChange = { clearCookiesCheck = it })
            Text("Cookies & Site data")
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = clearCacheCheck, onCheckedChange = { clearCacheCheck = it })
            Text("Cached images and files")
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = clearHistoryCheck, onCheckedChange = { clearHistoryCheck = it })
            Text("Browsing history")
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onClearData(clearCookiesCheck, clearCacheCheck, clearHistoryCheck)
            showClearDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Clear Now")
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
