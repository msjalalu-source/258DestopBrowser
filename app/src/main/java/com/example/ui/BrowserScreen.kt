package com.example.ui

import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.browser.ActiveSheet
import com.example.browser.BrowserViewModel
import com.example.browser.TabModel
import com.example.ui.theme.WinBlue
import kotlinx.coroutines.launch

@Composable
fun BrowserScreen(
  viewModel: BrowserViewModel,
  modifier: Modifier = Modifier
) {
  val tabs by viewModel.tabs.collectAsStateWithLifecycle()
  val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
  val settings by viewModel.settings.collectAsStateWithLifecycle()
  val activeSheet by viewModel.activeSheet.collectAsStateWithLifecycle()
  val isBookmarked by viewModel.isCurrentBookmarked.collectAsStateWithLifecycle()
  val bookmarks by viewModel.allBookmarks.collectAsStateWithLifecycle()
  val history by viewModel.allHistory.collectAsStateWithLifecycle()

  val activeTab = viewModel.activeTab ?: TabModel()
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  // Listen to snackbar events from ViewModel
  LaunchedEffect(viewModel.snackbarMessage) {
    viewModel.snackbarMessage.collect { msg ->
      snackbarHostState.showSnackbar(msg)
    }
  }

  // Handle system back button for WebView navigation
  BackHandler(enabled = activeTab.canGoBack || activeTab.url != "about:home") {
    if (activeTab.canGoBack && webViewRef != null) {
      webViewRef?.goBack()
    } else if (activeTab.url != "about:home") {
      viewModel.updateTab(activeTab.id, url = "about:home", title = "New Tab")
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      BrowserTopBar(
        tab = activeTab,
        tabCount = tabs.size,
        isBookmarked = isBookmarked,
        onNavigate = { input -> viewModel.loadUrl(input) },
        onReload = { webViewRef?.reload() },
        onToggleDesktop = { viewModel.toggleDesktopMode(activeTab.id) },
        onToggleBookmark = { viewModel.toggleBookmarkCurrentTab() },
        onOpenSheet = { sheet -> viewModel.setActiveSheet(sheet) }
      )
    },
    bottomBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        modifier = Modifier
          .fillMaxWidth()
          .navigationBarsPadding()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          // Back button
          IconButton(
            onClick = { webViewRef?.goBack() },
            enabled = activeTab.canGoBack,
            modifier = Modifier.testTag("nav_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              tint = if (activeTab.canGoBack) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
          }

          // Forward button
          IconButton(
            onClick = { webViewRef?.goForward() },
            enabled = activeTab.canGoForward,
            modifier = Modifier.testTag("nav_forward_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = "Forward",
              tint = if (activeTab.canGoForward) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
          }

          // Home button
          IconButton(
            onClick = { viewModel.updateTab(activeTab.id, url = "about:home", title = "New Tab") },
            modifier = Modifier.testTag("nav_home_button")
          ) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = "Home",
              tint = if (activeTab.url == "about:home") WinBlue else MaterialTheme.colorScheme.onSurface
            )
          }

          // Desktop/Mobile instant switch button
          IconButton(
            onClick = { viewModel.toggleDesktopMode(activeTab.id) },
            modifier = Modifier.testTag("nav_desktop_switch_button")
          ) {
            Icon(
              imageVector = if (activeTab.isDesktopMode) Icons.Default.DesktopWindows else Icons.Default.PhoneAndroid,
              contentDescription = "Toggle Desktop",
              tint = if (activeTab.isDesktopMode) WinBlue else MaterialTheme.colorScheme.onSurface
            )
          }

          // Windows Fidelity Auditor quick launcher
          IconButton(
            onClick = { viewModel.setActiveSheet(ActiveSheet.WINDOWS_FIDELITY) },
            modifier = Modifier.testTag("nav_fidelity_button")
          ) {
            Icon(
              imageVector = Icons.Default.Speed,
              contentDescription = "Windows Fidelity Score",
              tint = if (activeTab.isDesktopMode) WinBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // New Tab / Tabs switcher
          IconButton(
            onClick = { viewModel.setActiveSheet(ActiveSheet.TABS) },
            modifier = Modifier.testTag("nav_tabs_button")
          ) {
            Icon(
              imageVector = Icons.Default.Tab,
              contentDescription = "Tabs",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (activeTab.url == "about:home") {
        NewTabPage(
          tab = activeTab,
          settings = settings,
          onNavigate = { input -> viewModel.loadUrl(input) },
          onToggleDesktop = { viewModel.toggleDesktopMode(activeTab.id) },
          onOpenSheet = { sheet -> viewModel.setActiveSheet(sheet) },
          onOpenDiagnostics = { viewModel.openDiagnosticsPage() }
        )
      } else {
        BrowserWebView(
          tab = activeTab,
          settings = settings,
          onPageStarted = { url ->
            viewModel.updateTab(activeTab.id, url = url, isLoading = true, progress = 10)
          },
          onPageFinished = { url, title ->
            viewModel.updateTab(activeTab.id, url = url, title = title ?: url, isLoading = false, progress = 100)
            viewModel.onPageVisited(title ?: url, url, null)
          },
          onProgressChanged = { progress ->
            viewModel.updateTab(activeTab.id, progress = progress, isLoading = progress < 100)
          },
          onCanGoBackForwardChanged = { canBack, canForward ->
            viewModel.updateTab(activeTab.id, canGoBack = canBack, canGoForward = canForward)
          },
          onBlockedAd = {
            viewModel.incrementBlockedAd(activeTab.id)
          },
          onBlockedPopup = { host ->
            viewModel.incrementBlockedPopup(activeTab.id, host)
          },
          onBlockedAdult = { url ->
            viewModel.incrementBlockedAdult(activeTab.id, url)
          },
          onAuditResult = { report ->
            viewModel.updateTab(activeTab.id, auditReport = report)
          },
          auditTrigger = viewModel.auditTrigger,
          onWebViewCreated = { wv ->
            webViewRef = wv
          }
        )
      }
    }
  }

  // Active Bottom Sheet / Dialog Overlays
  when (activeSheet) {
    ActiveSheet.TABS -> {
      TabsSheet(
        tabs = tabs,
        activeTabId = activeTabId,
        onSelectTab = { tabId -> viewModel.selectTab(tabId) },
        onCloseTab = { tabId -> viewModel.closeTab(tabId) },
        onNewTab = { viewModel.createNewTab() },
        onDismiss = { viewModel.setActiveSheet(ActiveSheet.NONE) }
      )
    }

    ActiveSheet.BOOKMARKS_HISTORY -> {
      BookmarksHistoryDialog(
        bookmarks = bookmarks,
        history = history,
        onNavigate = { url ->
          viewModel.loadUrl(url)
          viewModel.setActiveSheet(ActiveSheet.NONE)
        },
        onDeleteBookmark = { id -> viewModel.deleteBookmark(id) },
        onDeleteHistory = { id -> viewModel.deleteHistory(id) },
        onClearAllHistory = { viewModel.clearAllHistory() },
        onDismiss = { viewModel.setActiveSheet(ActiveSheet.NONE) }
      )
    }

    ActiveSheet.WINDOWS_FIDELITY -> {
      WindowsFidelityDialog(
        tab = activeTab,
        settings = settings,
        onRunAudit = { viewModel.triggerWindowsAudit() },
        onOpenDiagnosticsPage = { viewModel.openDiagnosticsPage() },
        onDismiss = { viewModel.setActiveSheet(ActiveSheet.NONE) }
      )
    }

    ActiveSheet.SETTINGS -> {
      SettingsDialog(
        settings = settings,
        onUpdateSettings = { newSettings -> viewModel.updateSettings(newSettings) },
        onClearData = { cookies, cache, hist ->
          viewModel.clearBrowserData(cookies, cache, hist, webViewRef)
        },
        onDismiss = { viewModel.setActiveSheet(ActiveSheet.NONE) }
      )
    }

    ActiveSheet.BLOCKED_DETAILS -> {
      BlockedItemsDialog(
        tab = activeTab,
        settings = settings,
        onDismiss = { viewModel.setActiveSheet(ActiveSheet.NONE) }
      )
    }

    ActiveSheet.NONE -> {}
  }
}
