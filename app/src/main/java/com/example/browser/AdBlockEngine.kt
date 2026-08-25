package com.example.browser

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object AdBlockEngine {

  private val AD_DOMAINS = setOf(
    "doubleclick.net",
    "googlesyndication.com",
    "googleadservices.com",
    "pagead2.googlesyndication.com",
    "adservice.google.com",
    "adservice.google.co.in",
    "taboola.com",
    "outbrain.com",
    "criteo.com",
    "criteo.net",
    "rubiconproject.com",
    "pubmatic.com",
    "openx.net",
    "smartadserver.com",
    "adroll.com",
    "media.net",
    "revcontent.com",
    "mgid.com",
    "popads.net",
    "popcash.net",
    "propellerads.com",
    "exoclick.com",
    "adzerk.net",
    "amazon-adsystem.com",
    "scorecardresearch.com",
    "quantserve.com",
    "adcolony.com",
    "applovin.com",
    "vungle.com",
    "admob.com",
    "flurry.com",
    "zedo.com",
    "adnxs.com",
    "advertising.com",
    "adtechus.com",
    "bidswitch.net",
    "casalemedia.com",
    "yieldmo.com",
    "moatads.com",
    "adsystem.com",
    "ad-delivery.net",
    "adlightning.com",
    "adblade.com",
    "advertisement.com",
    "admob.google.com",
    "ads-twitter.com",
    "exponential.com",
    "tribalfusion.com",
    "infolinks.com",
    "buysellads.com",
    "sovnr.com",
    "chitika.com",
    "zergnet.com",
    "popunder.net",
    "trafficjunky.com",
    "juicyads.com",
    "adreactor.com",
    "adserver.com",
    "adsterra.com"
  )

  private val AD_KEYWORDS = listOf(
    "/ads/pagead",
    "/show_ads.js",
    "/pagead/js/adsbygoogle.js",
    "/ad_script.js",
    "/google_ads",
    "/adzerk/",
    "/popunder.js",
    "/ad_banner",
    "/advertisement",
    "/banner_ad"
  )

  fun shouldBlockUrl(urlString: String?): Boolean {
    if (urlString.isNullOrBlank()) return false
    return try {
      val uri = Uri.parse(urlString)
      val host = uri.host?.lowercase() ?: return false

      // Check host and parent domains
      for (domain in AD_DOMAINS) {
        if (host == domain || host.endsWith(".$domain")) {
          return true
        }
      }

      // Check path keywords
      val path = uri.path?.lowercase() ?: ""
      for (keyword in AD_KEYWORDS) {
        if (path.contains(keyword)) {
          return true
        }
      }

      false
    } catch (e: Exception) {
      false
    }
  }

  fun createEmptyBlockedResponse(): WebResourceResponse {
    return WebResourceResponse(
      "text/plain",
      "UTF-8",
      200,
      "OK",
      mapOf("Access-Control-Allow-Origin" to "*"),
      ByteArrayInputStream(ByteArray(0))
    )
  }

  fun getCosmeticAdBlockCss(): String {
    return """
      (function() {
        try {
          var styleId = '__winview_adblock_css__';
          if (!document.getElementById(styleId)) {
            var css = `
              .ad, .ads, .ad-banner, .advertisement,
              [id*="google_ads"], [class*="google_ads"],
              [id*="div-gpt-ad"], [class*="sponsored-post"],
              [data-ad-client], .adsbygoogle, .ad-box,
              .sidebar-ads, .banner-ad, .header-ad,
              iframe[src*="doubleclick"], iframe[src*="googlesyndication"] {
                display: none !important;
                visibility: hidden !important;
                height: 0 !important;
                width: 0 !important;
                opacity: 0 !important;
                pointer-events: none !important;
              }
            `;
            var style = document.createElement('style');
            style.id = styleId;
            style.type = 'text/css';
            style.appendChild(document.createTextNode(css));
            (document.head || document.documentElement).appendChild(style);
          }
        } catch(e) {}
      })();
    """.trimIndent()
  }
}
