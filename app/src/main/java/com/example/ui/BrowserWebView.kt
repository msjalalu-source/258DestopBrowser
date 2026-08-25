package com.example.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.viewinterop.AndroidView
import com.example.browser.AdBlockEngine
import com.example.browser.AdultFilterEngine
import com.example.browser.BrowserSettings
import com.example.browser.LiveAuditReport
import com.example.browser.LiveTranslationEngine
import com.example.browser.TabModel
import com.example.browser.TranslationState
import com.example.browser.WindowsSpoofEngine
import kotlinx.coroutines.flow.SharedFlow

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserWebView(
  tab: TabModel,
  settings: BrowserSettings,
  onPageStarted: (String) -> Unit,
  onPageFinished: (String, String?) -> Unit,
  onProgressChanged: (Int) -> Unit,
  onCanGoBackForwardChanged: (Boolean, Boolean) -> Unit,
  onBlockedAd: () -> Unit,
  onBlockedPopup: (String) -> Unit,
  onBlockedAdult: (String) -> Unit,
  onAuditResult: (LiveAuditReport?) -> Unit,
  auditTrigger: SharedFlow<String>,
  onWebViewCreated: (WebView) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val webView = remember(tab.id) {
    WebView(context).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
      )

      val tabSettings = settings.copy(isDesktopSpoofing = tab.isDesktopMode)
      val targetUserAgent = WindowsSpoofEngine.getUserAgent(tabSettings)

      this.settings.apply {
        userAgentString = targetUserAgent
        javaScriptEnabled = settings.isJavaScriptEnabled
        domStorageEnabled = true
        databaseEnabled = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false

        if (tab.isDesktopMode) {
          useWideViewPort = true
          loadWithOverviewMode = true
          layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        } else {
          useWideViewPort = false
          loadWithOverviewMode = false
          layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        }

        setSupportMultipleWindows(true)
        javaScriptCanOpenWindowsAutomatically = !settings.isPopupBlockerEnabled
        mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
      }

      val cookieManager = CookieManager.getInstance()
      cookieManager.setAcceptCookie(settings.isCookiesEnabled)
      cookieManager.setAcceptThirdPartyCookies(this, settings.isCookiesEnabled)

      webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
          onProgressChanged(newProgress)
          if (tab.isDesktopMode && (newProgress == 10 || newProgress == 25 || newProgress == 50)) {
            val spoofJs = WindowsSpoofEngine.getSpoofingJavaScript(tabSettings)
            view?.evaluateJavascript(spoofJs, null)
          }
          onCanGoBackForwardChanged(canGoBack(), canGoForward())
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
          if (!title.isNullOrBlank() && view?.url != null && !view.url!!.startsWith("about:home")) {
            onPageFinished(view.url!!, title)
          }
        }

        override fun onCreateWindow(
          view: WebView?,
          isDialog: Boolean,
          isUserGesture: Boolean,
          resultMsg: Message?
        ): Boolean {
          val currentHost = try { Uri.parse(view?.url ?: "").host ?: "Unknown site" } catch (e: Exception) { "Unknown site" }
          if (settings.isPopupBlockerEnabled && !isUserGesture) {
            onBlockedPopup(currentHost)
            return false
          }
          return false
        }
      }

      webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
          view: WebView?,
          request: WebResourceRequest?
        ): Boolean {
          val targetUrl = request?.url?.toString() ?: ""
          if (AdultFilterEngine.isAdultContent(targetUrl)) {
            onBlockedAdult(targetUrl)
            val blockedHtml = AdultFilterEngine.getBlockedAdultHtml(targetUrl)
            val dataUrl = "data:text/html;charset=utf-8," + Uri.encode(blockedHtml)
            view?.loadUrl(dataUrl)
            return true
          }
          return false
        }

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
          val targetUrl = url ?: ""
          if (AdultFilterEngine.isAdultContent(targetUrl)) {
            onBlockedAdult(targetUrl)
            val blockedHtml = AdultFilterEngine.getBlockedAdultHtml(targetUrl)
            val dataUrl = "data:text/html;charset=utf-8," + Uri.encode(blockedHtml)
            view?.loadUrl(dataUrl)
            return true
          }
          return false
        }

        override fun shouldInterceptRequest(
          view: WebView?,
          request: WebResourceRequest?
        ): WebResourceResponse? {
          val url = request?.url?.toString() ?: ""

          // Strict Adult Content Filter
          if (AdultFilterEngine.isAdultContent(url)) {
            onBlockedAdult(url)
            return if (request?.isForMainFrame == true) {
              AdultFilterEngine.createBlockedAdultResponse(url)
            } else {
              AdBlockEngine.createEmptyBlockedResponse()
            }
          }

          // Ad & Tracker blocker
          if (settings.isAdBlockerEnabled && AdBlockEngine.shouldBlockUrl(url)) {
            onBlockedAd()
            return AdBlockEngine.createEmptyBlockedResponse()
          }

          return super.shouldInterceptRequest(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          super.onPageStarted(view, url, favicon)
          if (url != null) {
            onPageStarted(url)
            // Early injection of spoofing JS & CSS
            if (tab.isDesktopMode) {
              val spoofJs = WindowsSpoofEngine.getSpoofingJavaScript(tabSettings)
              view?.evaluateJavascript(spoofJs, null)
            }
            if (settings.isAdBlockerEnabled && settings.isCosmeticAdBlockingEnabled) {
              val adBlockCss = AdBlockEngine.getCosmeticAdBlockCss()
              view?.evaluateJavascript(adBlockCss, null)
            }
          }
          onCanGoBackForwardChanged(canGoBack(), canGoForward())
        }

        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          if (url != null && !url.startsWith("about:home")) {
            onPageFinished(url, view?.title)

            // Re-inject for dynamic single-page applications
            if (tab.isDesktopMode) {
              val spoofJs = WindowsSpoofEngine.getSpoofingJavaScript(tabSettings)
              view?.evaluateJavascript(spoofJs, null)
            }
            if (settings.isAdBlockerEnabled && settings.isCosmeticAdBlockingEnabled) {
              val adBlockCss = AdBlockEngine.getCosmeticAdBlockCss()
              view?.evaluateJavascript(adBlockCss, null)
            }

            // Re-apply live translation if tab is in translated state
            if (tab.translationState != TranslationState.ORIGINAL) {
              val translateScript = LiveTranslationEngine.getTranslationScript(tab.translationState)
              view?.evaluateJavascript(translateScript, null)
            }

            // Run silent background audit to record live spoofing state
            view?.evaluateJavascript(WindowsSpoofEngine.getLiveAuditScript()) { rawResult ->
              val report = WindowsSpoofEngine.parseAuditResult(rawResult)
              onAuditResult(report)
            }
          }
          onCanGoBackForwardChanged(canGoBack(), canGoForward())
        }
      }
    }
  }

  // Handle Audit trigger for the active tab
  LaunchedEffect(auditTrigger) {
    auditTrigger.collect { targetTabId ->
      if (targetTabId == tab.id) {
        val auditScript = WindowsSpoofEngine.getLiveAuditScript()
        webView.evaluateJavascript(auditScript) { rawResult ->
          val report = WindowsSpoofEngine.parseAuditResult(rawResult)
          onAuditResult(report)
        }
      }
    }
  }

  // Update Settings on WebView
  LaunchedEffect(tab.isDesktopMode, settings) {
    val tabSettings = settings.copy(isDesktopSpoofing = tab.isDesktopMode)
    val targetUserAgent = WindowsSpoofEngine.getUserAgent(tabSettings)

    webView.settings.apply {
      userAgentString = targetUserAgent
      javaScriptEnabled = settings.isJavaScriptEnabled
      domStorageEnabled = true
      databaseEnabled = true
      setSupportZoom(true)
      builtInZoomControls = true
      displayZoomControls = false

      if (tab.isDesktopMode) {
        useWideViewPort = true
        loadWithOverviewMode = true
        layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
      } else {
        useWideViewPort = false
        loadWithOverviewMode = false
        layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
      }

      setSupportMultipleWindows(true)
      javaScriptCanOpenWindowsAutomatically = !settings.isPopupBlockerEnabled
      mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
    }

    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(settings.isCookiesEnabled)
    cookieManager.setAcceptThirdPartyCookies(webView, settings.isCookiesEnabled)
  }

  // Load URL changes
  LaunchedEffect(tab.url) {
    if (tab.url.isNotBlank() && tab.url != "about:home" && webView.url != tab.url) {
      val tabSettings = settings.copy(isDesktopSpoofing = tab.isDesktopMode)
      val headers = WindowsSpoofEngine.getCustomHeaders(tabSettings)
      if (headers.isNotEmpty() && !tab.url.startsWith("data:") && !tab.url.startsWith("about:")) {
        webView.loadUrl(tab.url, headers)
      } else {
        webView.loadUrl(tab.url)
      }
    }
  }

  // Handle translation state changes
  LaunchedEffect(tab.translationState) {
    if (tab.url.isNotBlank() && tab.url != "about:home" && !tab.url.startsWith("data:")) {
      val translateScript = LiveTranslationEngine.getTranslationScript(tab.translationState)
      webView.evaluateJavascript(translateScript, null)
    }
  }

  DisposableEffect(tab.id) {
    onWebViewCreated(webView)
    onDispose {
      webView.stopLoading()
    }
  }

  AndroidView(
    factory = {
      webView
    },
    update = { wv ->
      if (tab.url.isNotBlank() && tab.url != "about:home" && wv.url != tab.url) {
        val tabSettings = settings.copy(isDesktopSpoofing = tab.isDesktopMode)
        val headers = WindowsSpoofEngine.getCustomHeaders(tabSettings)
        if (headers.isNotEmpty() && !tab.url.startsWith("data:") && !tab.url.startsWith("about:")) {
          wv.loadUrl(tab.url, headers)
        } else {
          wv.loadUrl(tab.url)
        }
      }
    },
    modifier = modifier
      .fillMaxSize()
      .testTag("browser_webview")
  )
}
