package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.browser.AdultFilterEngine
import com.example.browser.BrowserViewModel
import com.example.browser.SearchEngine
import com.example.browser.TranslationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrowserFeaturesTest {

  private lateinit var viewModel: BrowserViewModel

  @Before
  fun setUp() {
    val app = ApplicationProvider.getApplicationContext<android.app.Application>()
    viewModel = BrowserViewModel(app)
  }

  @Test
  fun `test default search engine and search engine selection`() {
    assertEquals(SearchEngine.GOOGLE, viewModel.settings.value.searchEngine)

    // Select Bing
    viewModel.setSearchEngine(SearchEngine.BING)
    assertEquals(SearchEngine.BING, viewModel.settings.value.searchEngine)

    // Select DuckDuckGo
    viewModel.setSearchEngine(SearchEngine.DUCKDUCKGO)
    assertEquals(SearchEngine.DUCKDUCKGO, viewModel.settings.value.searchEngine)
  }

  @Test
  fun `test URL navigation with search engine query format`() {
    viewModel.setSearchEngine(SearchEngine.GOOGLE)
    val activeTabId = viewModel.activeTabId.value

    // Search query
    viewModel.loadUrl("android compose development")
    val currentTab = viewModel.tabs.value.find { it.id == activeTabId }
    assertTrue(currentTab?.url?.startsWith("https://www.google.com/search?q=") == true)

    // Direct URL
    viewModel.loadUrl("github.com")
    val updatedTab = viewModel.tabs.value.find { it.id == activeTabId }
    assertEquals("https://github.com", updatedTab?.url)
  }

  @Test
  fun `test adult filter engine detects explicit domains queries and bengali terms`() {
    // Adult domain test
    assertTrue(AdultFilterEngine.isAdultContent("https://pornhub.com/video123"))
    assertTrue(AdultFilterEngine.isAdultContent("https://sub.xhamster.com"))
    assertTrue(AdultFilterEngine.isAdultContent("https://example.xxx/watch"))
    assertFalse(AdultFilterEngine.isAdultContent("https://google.com"))
    assertFalse(AdultFilterEngine.isAdultContent("https://wikipedia.org"))

    // Adult queries
    assertTrue(AdultFilterEngine.isAdultQuery("free porn video"))
    assertTrue(AdultFilterEngine.isAdultQuery("xxx adult tube"))

    // Bengali explicit terms
    assertTrue(AdultFilterEngine.isAdultQuery("বাংলা চটি গল্প"))
    assertTrue(AdultFilterEngine.isAdultQuery("সেক্স ভিডিও"))

    // Banglish explicit terms
    assertTrue(AdultFilterEngine.isAdultQuery("bangla choti golpo"))
    assertTrue(AdultFilterEngine.isAdultQuery("desi bhabhi sex"))

    // Safe queries
    assertFalse(AdultFilterEngine.isAdultQuery("android development tutorial"))
    assertFalse(AdultFilterEngine.isAdultQuery("bangla news live"))
  }

  @Test
  fun `test adult search query triggers instant block shield`() {
    val activeTabId = viewModel.activeTabId.value

    // Attempting adult query
    viewModel.loadUrl("free adult xxx videos")
    val currentTab = viewModel.tabs.value.find { it.id == activeTabId }

    assertTrue(currentTab?.url?.startsWith("data:text/html") == true)
    assertTrue(currentTab?.title?.contains("Blocked") == true)
    assertEquals(1, currentTab?.blockedAdultCount)
  }

  @Test
  fun `test safesearch parameters enforcement`() {
    val googleUrl = "https://www.google.com/search?q=cats"
    val safeGoogle = AdultFilterEngine.enforceSafeSearch(googleUrl)
    assertTrue(safeGoogle.contains("safe=active"))
    assertTrue(safeGoogle.contains("ssui=on"))

    val bingUrl = "https://www.bing.com/search?q=cats"
    val safeBing = AdultFilterEngine.enforceSafeSearch(bingUrl)
    assertTrue(safeBing.contains("adlt=strict"))

    val ddgUrl = "https://duckduckgo.com/?q=cats"
    val safeDdg = AdultFilterEngine.enforceSafeSearch(ddgUrl)
    assertTrue(safeDdg.contains("kp=1"))
  }

  @Test
  fun `test new tab button creation`() {
    val initialCount = viewModel.tabs.value.size
    viewModel.createNewTab("about:home")
    val newCount = viewModel.tabs.value.size
    assertEquals(initialCount + 1, newCount)

    val activeTab = viewModel.tabs.value.find { it.id == viewModel.activeTabId.value }
    assertEquals("about:home", activeTab?.url)
  }

  @Test
  fun `test live translation cycle transitions`() {
    val activeTabId = viewModel.activeTabId.value
    var currentTab = viewModel.tabs.value.find { it.id == activeTabId }
    assertEquals(TranslationState.ORIGINAL, currentTab?.translationState)

    // First click -> BANGLA
    viewModel.cycleTranslation(activeTabId)
    currentTab = viewModel.tabs.value.find { it.id == activeTabId }
    assertEquals(TranslationState.BANGLA, currentTab?.translationState)

    // Second click -> ENGLISH
    viewModel.cycleTranslation(activeTabId)
    currentTab = viewModel.tabs.value.find { it.id == activeTabId }
    assertEquals(TranslationState.ENGLISH, currentTab?.translationState)

    // Third click -> ORIGINAL
    viewModel.cycleTranslation(activeTabId)
    currentTab = viewModel.tabs.value.find { it.id == activeTabId }
    assertEquals(TranslationState.ORIGINAL, currentTab?.translationState)
  }

  @Test
  fun `test desktop mobile mode toggle`() {
    val activeTabId = viewModel.activeTabId.value
    var currentTab = viewModel.tabs.value.find { it.id == activeTabId }
    val initialDesktopMode = currentTab?.isDesktopMode ?: false

    viewModel.toggleDesktopMode(activeTabId)
    currentTab = viewModel.tabs.value.find { it.id == activeTabId }
    assertNotEquals(initialDesktopMode, currentTab?.isDesktopMode)
  }
}
