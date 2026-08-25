package com.example.browser

import android.app.Application
import android.content.Context
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BrowserRepository
import com.example.data.entity.BookmarkEntity
import com.example.data.entity.HistoryEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ActiveSheet {
  NONE,
  TABS,
  BOOKMARKS_HISTORY,
  SETTINGS,
  WINDOWS_FIDELITY,
  BLOCKED_DETAILS
}

class BrowserViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: BrowserRepository

  private val _tabs = MutableStateFlow<List<TabModel>>(listOf(TabModel()))
  val tabs: StateFlow<List<TabModel>> = _tabs.asStateFlow()

  private val _activeTabId = MutableStateFlow<String>(_tabs.value.first().id)
  val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

  private val _settings = MutableStateFlow(BrowserSettings())
  val settings: StateFlow<BrowserSettings> = _settings.asStateFlow()

  private val _activeSheet = MutableStateFlow(ActiveSheet.NONE)
  val activeSheet: StateFlow<ActiveSheet> = _activeSheet.asStateFlow()

  private val _snackbarMessage = MutableSharedFlow<String>()
  val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

  private val _auditTrigger = MutableSharedFlow<String>()
  val auditTrigger: SharedFlow<String> = _auditTrigger.asSharedFlow()

  val allBookmarks: StateFlow<List<BookmarkEntity>>
  val allHistory: StateFlow<List<HistoryEntity>>

  private val _isCurrentBookmarked = MutableStateFlow(false)
  val isCurrentBookmarked: StateFlow<Boolean> = _isCurrentBookmarked.asStateFlow()

  init {
    val db = AppDatabase.getDatabase(application)
    repository = BrowserRepository(db.bookmarkDao(), db.historyDao())

    val bookmarksFlow = MutableStateFlow<List<BookmarkEntity>>(emptyList())
    val historyFlow = MutableStateFlow<List<HistoryEntity>>(emptyList())
    allBookmarks = bookmarksFlow
    allHistory = historyFlow

    viewModelScope.launch {
      repository.allBookmarks.collect { list ->
        bookmarksFlow.value = list
        checkActiveBookmark()
      }
    }

    viewModelScope.launch {
      repository.allHistory.collect { list ->
        historyFlow.value = list
      }
    }
  }

  val activeTab: TabModel?
    get() = _tabs.value.find { it.id == _activeTabId.value } ?: _tabs.value.firstOrNull()

  fun setActiveSheet(sheet: ActiveSheet) {
    _activeSheet.value = sheet
  }

  fun createNewTab(initialUrl: String = "about:home") {
    val newTab = TabModel(
      url = initialUrl,
      isDesktopMode = _settings.value.isDesktopSpoofing
    )
    _tabs.update { it + newTab }
    _activeTabId.value = newTab.id
    _activeSheet.value = ActiveSheet.NONE
    checkActiveBookmark()
  }

  fun selectTab(tabId: String) {
    _activeTabId.value = tabId
    _activeSheet.value = ActiveSheet.NONE
    checkActiveBookmark()
  }

  fun closeTab(tabId: String) {
    val currentTabs = _tabs.value
    if (currentTabs.size <= 1) {
      // Reset single tab to home
      _tabs.value = listOf(TabModel(url = "about:home", isDesktopMode = _settings.value.isDesktopSpoofing))
      _activeTabId.value = _tabs.value.first().id
      return
    }

    val index = currentTabs.indexOfFirst { it.id == tabId }
    val newTabs = currentTabs.filter { it.id != tabId }
    _tabs.value = newTabs

    if (_activeTabId.value == tabId) {
      val nextIndex = if (index >= newTabs.size) newTabs.size - 1 else index
      _activeTabId.value = newTabs[nextIndex].id
    }
    checkActiveBookmark()
  }

  fun updateTab(
    tabId: String,
    url: String? = null,
    title: String? = null,
    favicon: String? = null,
    isLoading: Boolean? = null,
    progress: Int? = null,
    canGoBack: Boolean? = null,
    canGoForward: Boolean? = null,
    isDesktopMode: Boolean? = null,
    blockedAdsCount: Int? = null,
    blockedPopupsCount: Int? = null,
    blockedAdultCount: Int? = null,
    auditReport: LiveAuditReport? = null
  ) {
    _tabs.update { list ->
      list.map { tab ->
        if (tab.id == tabId) {
          tab.copy(
            url = url ?: tab.url,
            title = title ?: tab.title,
            favicon = favicon ?: tab.favicon,
            isLoading = isLoading ?: tab.isLoading,
            progress = progress ?: tab.progress,
            canGoBack = canGoBack ?: tab.canGoBack,
            canGoForward = canGoForward ?: tab.canGoForward,
            isDesktopMode = isDesktopMode ?: tab.isDesktopMode,
            blockedAdsCount = blockedAdsCount ?: tab.blockedAdsCount,
            blockedPopupsCount = blockedPopupsCount ?: tab.blockedPopupsCount,
            blockedAdultCount = blockedAdultCount ?: tab.blockedAdultCount,
            lastAuditReport = auditReport ?: tab.lastAuditReport
          )
        } else {
          tab
        }
      }
    }
    if (tabId == _activeTabId.value && url != null) {
      checkActiveBookmark()
    }
  }

  fun toggleDesktopMode(tabId: String) {
    val tab = _tabs.value.find { it.id == tabId } ?: return
    val newMode = !tab.isDesktopMode
    updateTab(tabId, isDesktopMode = newMode)
    viewModelScope.launch {
      val modeName = if (newMode) "Windows Desktop (Spoofed)" else "Mobile View"
      _snackbarMessage.emit("Switched to $modeName")
    }
  }

  fun incrementBlockedAd(tabId: String) {
    val tab = _tabs.value.find { it.id == tabId } ?: return
    updateTab(tabId, blockedAdsCount = tab.blockedAdsCount + 1)
  }

  fun incrementBlockedPopup(tabId: String, host: String) {
    val tab = _tabs.value.find { it.id == tabId } ?: return
    updateTab(tabId, blockedPopupsCount = tab.blockedPopupsCount + 1)
    viewModelScope.launch {
      _snackbarMessage.emit("Blocked popup from $host")
    }
  }

  fun incrementBlockedAdult(tabId: String, url: String = "") {
    val tab = _tabs.value.find { it.id == tabId } ?: return
    updateTab(tabId, blockedAdultCount = tab.blockedAdultCount + 1)
    viewModelScope.launch {
      _snackbarMessage.emit("🛡️ অ্যাডাল্ট কন্টেন্ট ব্লক করা হয়েছে (Adult Content Blocked)")
    }
  }

  fun showAdultFilterLockedMessage() {
    viewModelScope.launch {
      _snackbarMessage.emit("🔒 অ্যাডাল্ট ফিল্টার স্থায়ীভাবে চালু রয়েছে এবং এটি বন্ধ করা যাবে না (Adult Filter is permanently locked ON)")
    }
  }

  fun loadUrl(input: String, tabId: String? = null) {
    val targetTabId = tabId ?: _activeTabId.value
    val cleanInput = input.trim()
    if (cleanInput.isEmpty()) return

    val url = formatUrlOrSearch(cleanInput, _settings.value.searchEngine)

    if (AdultFilterEngine.isAdultContent(url)) {
      val blockedHtml = AdultFilterEngine.getBlockedAdultHtml(url)
      val dataUrl = "data:text/html;charset=utf-8," + Uri.encode(blockedHtml)
      incrementBlockedAdult(targetTabId, url)
      updateTab(targetTabId, url = dataUrl, title = "Blocked Adult Content", blockedAdsCount = 0, blockedPopupsCount = 0)
      return
    }

    val safeUrl = AdultFilterEngine.enforceSafeSearch(url)
    updateTab(targetTabId, url = safeUrl, title = "Loading...", blockedAdsCount = 0, blockedPopupsCount = 0)
  }

  private fun formatUrlOrSearch(input: String, searchEngine: SearchEngine): String {
    if (input.startsWith("about:") || input.startsWith("data:") || input.startsWith("file:")) {
      return input
    }
    val lower = input.lowercase()
    val isUrl = (lower.startsWith("http://") || lower.startsWith("https://")) ||
        (!input.contains(" ") && (input.contains(".") || input.contains(":")))

    val initialUrl = if (isUrl) {
      if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
        "https://$input"
      } else {
        input
      }
    } else {
      searchEngine.searchUrl + Uri.encode(input)
    }

    return AdultFilterEngine.enforceSafeSearch(initialUrl)
  }

  fun onPageVisited(title: String, url: String, favicon: String? = null) {
    viewModelScope.launch {
      repository.addHistory(title, url, favicon)
      checkActiveBookmark()
    }
  }

  fun toggleBookmarkCurrentTab() {
    val tab = activeTab ?: return
    if (tab.url.startsWith("about:") || tab.url.startsWith("data:")) return

    viewModelScope.launch {
      val added = repository.toggleBookmark(tab.title, tab.url, tab.favicon)
      _isCurrentBookmarked.value = added
      _snackbarMessage.emit(if (added) "Saved to Bookmarks" else "Removed from Bookmarks")
    }
  }

  fun deleteBookmark(id: Long) {
    viewModelScope.launch {
      repository.deleteBookmark(id)
      checkActiveBookmark()
    }
  }

  fun deleteHistory(id: Long) {
    viewModelScope.launch {
      repository.deleteHistory(id)
    }
  }

  fun clearAllHistory() {
    viewModelScope.launch {
      repository.clearHistory()
      _snackbarMessage.emit("Browsing history cleared")
    }
  }

  private fun checkActiveBookmark() {
    val url = activeTab?.url ?: return
    viewModelScope.launch {
      _isCurrentBookmarked.value = repository.isBookmarked(url)
    }
  }

  fun updateSettings(newSettings: BrowserSettings) {
    val old = _settings.value
    _settings.value = newSettings

    // If global desktop mode changed, apply to current active tab
    if (old.isDesktopSpoofing != newSettings.isDesktopSpoofing) {
      _activeTabId.value.let { tabId ->
        updateTab(tabId, isDesktopMode = newSettings.isDesktopSpoofing)
      }
    }
  }

  fun triggerWindowsAudit() {
    viewModelScope.launch {
      _auditTrigger.emit(_activeTabId.value)
    }
  }

  fun openDiagnosticsPage() {
    val html = WindowsSpoofEngine.getDiagnosticTestHtml()
    val dataUrl = "data:text/html;charset=utf-8," + Uri.encode(html)
    loadUrl(dataUrl)
    _activeSheet.value = ActiveSheet.NONE
  }

  fun clearBrowserData(clearCookies: Boolean, clearCache: Boolean, clearHist: Boolean, webView: WebView?) {
    viewModelScope.launch {
      if (clearCookies) {
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().flush()
      }
      if (clearCache) {
        WebStorage.getInstance().deleteAllData()
        webView?.clearCache(true)
      }
      if (clearHist) {
        repository.clearHistory()
        webView?.clearHistory()
      }
      _snackbarMessage.emit("Selected browser data cleared successfully")
    }
  }
}
