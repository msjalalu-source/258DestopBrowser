package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.browser.BrowserSettings
import com.example.browser.FidelityCriterion
import com.example.browser.LiveAuditReport
import com.example.browser.TabModel
import com.example.ui.theme.AdBlockGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WinBlue
import com.example.ui.theme.WinCyan

@Composable
fun WindowsFidelityDialog(
  tab: TabModel,
  settings: BrowserSettings,
  onRunAudit: () -> Unit,
  onOpenDiagnosticsPage: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isAuditing by remember { mutableStateOf(false) }

  val baseScore = remember(tab.isDesktopMode, settings) {
    if (tab.isDesktopMode) settings.calculateWindowsFidelityScore() else 0
  }

  val displayScore = tab.lastAuditReport?.overallScore ?: baseScore

  val animatedScoreProgress by animateFloatAsState(
    targetValue = displayScore / 100f,
    animationSpec = tween(600),
    label = "score_progress"
  )

  val criteriaList = remember(tab.isDesktopMode, settings) {
    settings.getFidelityCriteriaList()
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .height(640.dp)
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(WinBlue.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = null,
                tint = WinBlue,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Windows Fidelity Score",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Windows Browser Spoofing Auditor",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Score Gauge Card
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("fidelity_score_gauge_card"),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  listOf(
                    MaterialTheme.colorScheme.surfaceVariant,
                    if (displayScore >= 80) WinBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                  )
                )
              )
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Circular Progress Indicator with Score
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier.size(88.dp)
            ) {
              CircularProgressIndicator(
                progress = { animatedScoreProgress },
                modifier = Modifier.size(88.dp),
                color = if (displayScore >= 80) WinBlue else if (displayScore > 0) WarningAmber else MaterialTheme.colorScheme.outline,
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
              )
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "$displayScore%",
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = if (displayScore >= 80) "OPTIMAL" else if (displayScore > 0) "PARTIAL" else "MOBILE",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (displayScore >= 80) AdBlockGreen else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Explanation Text
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (tab.isDesktopMode) {
                  if (displayScore >= 90) "Full Windows 11 Profile" else "Custom Windows Spoofing"
                } else {
                  "Standard Mobile Environment"
                },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (tab.isDesktopMode) {
                  "All targeted web servers and scripts detect this browser as a genuine ${settings.windowsPreset.browserName} running on 64-bit Windows."
                } else {
                  "Mobile view active. Web servers will serve phone-optimized responsive layouts."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: Run Live In-Page Test & Open Tester Page
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Button(
            onClick = {
              isAuditing = true
              onRunAudit()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("run_live_audit_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = WinBlue),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Live DOM Audit", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }

          OutlinedButton(
            onClick = {
              onOpenDiagnosticsPage()
              onDismiss()
            },
            modifier = Modifier
              .weight(1f)
              .testTag("open_test_page_btn"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Default.OpenInBrowser,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Diagnostic Page", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Live In-Page Audit Results Banner (if tested)
        if (tab.lastAuditReport != null) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = "Live In-Page JavaScript Evaluation",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                  text = "Verified on current page",
                  style = MaterialTheme.typography.labelSmall,
                  color = AdBlockGreen,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "GPU: ${tab.lastAuditReport.webGlReport}",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "Platform: ${tab.lastAuditReport.platformReport} | Touch: ${tab.lastAuditReport.touchPointsReport} | RAM: ${tab.lastAuditReport.hardwareReport}",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        Text(
          text = "Fidelity Breakdown Checklist",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of Criteria
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(criteriaList, key = { it.id }) { item ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Icon(
                  imageVector = if (item.isEnabled) Icons.Default.CheckCircle else Icons.Default.Warning,
                  contentDescription = null,
                  tint = if (item.isEnabled) AdBlockGreen else WarningAmber,
                  modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Text(
                      text = item.title,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = FontWeight.SemiBold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = if (item.isEnabled) WinBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ) {
                      Text(
                        text = "+${item.weightPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isEnabled) WinBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(2.dp))

                  Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                  )

                  Text(
                    text = "Expected: ${item.expectedValue}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
