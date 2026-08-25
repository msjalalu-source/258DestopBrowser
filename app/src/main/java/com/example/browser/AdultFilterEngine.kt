package com.example.browser

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object AdultFilterEngine {

  // True by default and permanently locked ON
  const val IS_PERMANENTLY_LOCKED: Boolean = true

  private val ADULT_DOMAINS = setOf(
    // Major Adult Video & Tube Networks
    "pornhub.com",
    "xvideos.com",
    "xnxx.com",
    "redtube.com",
    "youporn.com",
    "tube8.com",
    "spankbang.com",
    "eporner.com",
    "tnaflix.com",
    "xhamster.com",
    "xhamster.desi",
    "xhamster.one",
    "xhamster2.com",
    "xhamster3.com",
    "porntrex.com",
    "porndig.com",
    "sunporno.com",
    "txxx.com",
    "tubepornclassic.com",
    "porn300.com",
    "hdpornz.xxx",
    "pornhd.com",
    "porn.com",
    "sex.com",
    "beeg.com",
    "daftsex.com",
    "nuvid.com",
    "pornmd.com",
    "4tube.com",
    "empflix.com",
    "hclips.com",
    "palimas.com",
    "vjav.com",
    "hqporner.com",
    "mypornvid.fun",
    "tblop.com",
    "badjojo.com",
    "tktube.com",
    "xtapes.to",

    // Premium Studios & Networks
    "brazzers.com",
    "naughtyamerica.com",
    "bangbros.com",
    "realitykings.com",
    "mofos.com",
    "wicked.com",
    "twistys.com",
    "vixen.com",
    "blacked.com",
    "tushy.com",
    "deeper.com",
    "bravoteens.com",
    "babesnetwork.com",
    "teamskeet.com",
    "evilangel.com",
    "digitalplayground.com",
    "kink.com",
    "milehighmedia.com",
    "sweetheartvideo.com",
    "girlsway.com",
    "transfixed.com",
    "slr.com",
    "wankzvr.com",
    "vrporn.com",
    "babevr.com",
    "sexlikereal.com",
    "czechvr.com",

    // Live Adult Cams & Streaming
    "chaturbate.com",
    "chaturbate.co",
    "stripchat.com",
    "bongacams.com",
    "livejasmin.com",
    "cam4.com",
    "camsoda.com",
    "myfreecams.com",
    "flirt4free.com",
    "camster.com",
    "imlive.com",
    "jerkmate.com",
    "streamate.com",
    "cams.com",
    "webcamromance.com",

    // Adult Creator & Subscription Platforms
    "onlyfans.com",
    "fansly.com",
    "manyvids.com",
    "clips4sale.com",
    "loyalfans.com",
    "pocketstars.com",
    "admireme.vip",
    "modelhub.com",
    "fapello.com",
    "coomer.party",
    "coomer.su",
    "kemono.party",
    "kemono.su",
    "erome.com",
    "redgifs.com",
    "pornpics.com",

    // Asian / JAV & Regional Adult
    "javlibrary.com",
    "javbus.com",
    "javdb.com",
    "missav.com",
    "missav.ai",
    "jable.tv",
    "netflav.com",
    "supjav.com",
    "javdoe.is",
    "7mmtv.tv",
    "javmenu.com",
    "javcl.com",
    "hpjav.tv",
    "sexdunia.com",
    "desipapa.com",
    "antarvasna.com",
    "savita.com",

    // Hentai / Anime NSFW
    "hentaihaven.xxx",
    "hentaihaven.red",
    "hanime.tv",
    "rule34.xxx",
    "rule34.paheal.net",
    "e-hentai.org",
    "nhentai.net",
    "nhentai.to",
    "luscious.net",
    "gelbooru.com",
    "danbooru.donmai.us",
    "sankakucomplex.com",
    "yande.re",
    "fakku.net",
    "tsumino.com",
    "hitomi.la",
    "pururin.io",
    "simply-hentai.com",
    "8muses.com",
    "multporn.net",
    "hentai2read.com",
    "erofus.com",
    "hentaicity.com",
    "hentai-foundry.com",
    "asmhentai.com",

    // Extreme & Aggregator Sites
    "heavy-r.com",
    "motherless.com",
    "planetsuzy.org",
    "vintage-erotica-forum.com",
    "shemalez.com",
    "transerotic.com",
    "gayporno.tv",
    "boyfriendtv.com",
    "men.com",
    "seancody.com",
    "nakedsword.com",
    "nextdoorstudios.com",

    // Adult Dating & Escort
    "adultfriendfinder.com",
    "ashley-madison.com",
    "fetlife.com",
    "alt.com",
    "bedpage.com",
    "skipthegames.com",
    "eros.com",
    "listcrawler.com",
    "cityvibe.com",
    "escortdirectory.com",

    // Adult Industry Directories & Forums
    "avn.com",
    "freeones.com",
    "boobpedia.com",
    "iafd.com",
    "indexxx.com",
    "babepedia.com"
  )

  private val ADULT_KEYWORDS = listOf(
    "pornhub",
    "xvideos",
    "xnxx",
    "redtube",
    "youporn",
    "chaturbate",
    "stripchat",
    "bongacams",
    "onlyfans",
    "hentai",
    "brazzers",
    "naughtyamerica",
    "spankbang",
    "eporner",
    "fapello",
    "erome",
    "camsoda",
    "livejasmin",
    "camgirl",
    "webcamsex",
    "sexvideo",
    "adultvideo",
    "hardcoreporn",
    "softcoreporn",
    "pornstars",
    "tube8",
    "nuvid",
    "tnaflix",
    "xhamster",
    "porndig",
    "porntrex",
    "javlibrary",
    "missav",
    "jable.tv",
    "hanime",
    "rule34",
    "nhentai",
    "e-hentai",
    "hitomi.la",
    "tsumino",
    "adultfriendfinder",
    "fetlife",
    "escortservice",
    "shemalez",
    "transerotic",
    "gayporno",
    "boyfriendtv"
  )

  private val SENSITIVE_TOKEN_SUBSTRINGS = listOf(
    "porn",
    "xxx",
    "adult-tube",
    "sex-tube",
    "hardcore-sex",
    "free-porn",
    "cam-girls",
    "live-sex",
    "porn-video",
    "erotic-video",
    "hentai-manga",
    "nsfw-video",
    "incest-porn",
    "milf-porn",
    "gangbang-video",
    "creampie-video",
    "blowjob-video",
    "cumshot-video",
    "deepthroat-video",
    "erotic-cams"
  )

  /**
   * Checks if a URL points to adult content.
   */
  fun isAdultContent(urlString: String?): Boolean {
    if (urlString.isNullOrBlank()) return false
    if (urlString.startsWith("about:") || urlString.startsWith("data:") || urlString.startsWith("file:")) return false

    return try {
      val uri = Uri.parse(urlString)
      val host = uri.host?.lowercase() ?: return false
      val fullUrl = urlString.lowercase()

      // 1. Direct Domain matching & Subdomain checks
      for (domain in ADULT_DOMAINS) {
        if (host == domain || host.endsWith(".$domain")) {
          return true
        }
      }

      // 2. Hostname keyword search
      for (keyword in ADULT_KEYWORDS) {
        if (host.contains(keyword)) {
          return true
        }
      }

      // 3. Path & Query sensitive token search
      val pathAndQuery = ((uri.path ?: "") + " " + (uri.query ?: "")).lowercase()
      for (token in SENSITIVE_TOKEN_SUBSTRINGS) {
        if (pathAndQuery.contains(token)) {
          return true
        }
      }

      // 4. Standalone 'xxx' or 'porn' in host domain parts
      val hostParts = host.split(".")
      if (hostParts.any { it == "xxx" || it == "porn" || it == "adult" || it == "sex" || it == "hentai" || it == "nsfw" }) {
        return true
      }

      false
    } catch (e: Exception) {
      false
    }
  }

  /**
   * Enforces SafeSearch parameter on search engine queries.
   */
  fun enforceSafeSearch(url: String): String {
    if (url.startsWith("about:") || url.startsWith("data:")) return url

    return try {
      val uri = Uri.parse(url)
      val host = uri.host?.lowercase() ?: return url

      when {
        // Google Search SafeSearch
        host.contains("google.") && uri.path?.contains("/search") == true -> {
          if (!url.contains("safe=active") && !url.contains("safe=strict")) {
            if (url.contains("?")) "$url&safe=active" else "$url?safe=active"
          } else {
            url
          }
        }

        // DuckDuckGo Strict SafeSearch
        host.contains("duckduckgo.com") -> {
          if (!url.contains("kp=1")) {
            if (url.contains("?")) "$url&kp=1" else "$url?kp=1"
          } else {
            url
          }
        }

        // Bing Strict SafeSearch
        host.contains("bing.com") && uri.path?.contains("/search") == true -> {
          if (!url.contains("adlt=strict")) {
            if (url.contains("?")) "$url&adlt=strict" else "$url?adlt=strict"
          } else {
            url
          }
        }

        // YouTube Restricted Mode
        host.contains("youtube.com") -> {
          if (!url.contains("has_verified=1")) {
            url
          } else {
            url
          }
        }

        else -> url
      }
    } catch (e: Exception) {
      url
    }
  }

  /**
   * Generates a high-security blocked page HTML when adult content is requested.
   */
  fun getBlockedAdultHtml(blockedUrl: String): String {
    val cleanUrl = blockedUrl.take(80)
    return """
      <!DOCTYPE html>
      <html lang="en">
      <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
        <title>Access Blocked | Safe Shield Active</title>
        <style>
          * { margin: 0; padding: 0; box-sizing: border-box; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
          body {
            background: linear-gradient(135deg, #0F172A 0%, #1E1B4B 50%, #0F172A 100%);
            color: #F8FAFC;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
            text-align: center;
          }
          .card {
            background: rgba(30, 41, 59, 0.85);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(239, 68, 68, 0.3);
            border-radius: 24px;
            padding: 36px 24px;
            max-width: 480px;
            width: 100%;
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5), 0 0 30px rgba(239, 68, 68, 0.2);
            animation: fadeIn 0.4s ease-out;
          }
          @keyframes fadeIn {
            from { opacity: 0; transform: translateY(16px); }
            to { opacity: 1; transform: translateY(0); }
          }
          .shield-icon {
            width: 80px;
            height: 80px;
            background: rgba(239, 68, 68, 0.15);
            border: 2px solid rgba(239, 68, 68, 0.5);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 20px;
            font-size: 38px;
          }
          .badge {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            background: rgba(239, 68, 68, 0.2);
            color: #FCA5A5;
            padding: 6px 14px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 0.5px;
            margin-bottom: 16px;
            border: 1px solid rgba(239, 68, 68, 0.3);
          }
          h1 {
            font-size: 22px;
            font-weight: 800;
            color: #FFFFFF;
            margin-bottom: 12px;
            line-height: 1.3;
          }
          p.bengali {
            font-size: 15px;
            color: #E2E8F0;
            margin-bottom: 12px;
            font-weight: 500;
            line-height: 1.5;
          }
          p.desc {
            font-size: 13px;
            color: #94A3B8;
            margin-bottom: 24px;
            line-height: 1.5;
          }
          .blocked-url-box {
            background: rgba(15, 23, 42, 0.8);
            border: 1px dashed rgba(148, 163, 184, 0.3);
            border-radius: 12px;
            padding: 10px 14px;
            font-family: monospace;
            font-size: 12px;
            color: #F87171;
            word-break: break-all;
            margin-bottom: 24px;
          }
          .btn-container {
            display: flex;
            flex-direction: column;
            gap: 12px;
          }
          .btn-primary {
            background: #0078D4;
            color: #FFFFFF;
            border: none;
            padding: 14px 20px;
            border-radius: 14px;
            font-size: 15px;
            font-weight: 700;
            cursor: pointer;
            text-decoration: none;
            display: block;
            box-shadow: 0 4px 14px rgba(0, 120, 212, 0.4);
            transition: transform 0.15s, background 0.15s;
          }
          .btn-primary:active {
            transform: scale(0.98);
            background: #005A9E;
          }
          .lock-notice {
            margin-top: 20px;
            font-size: 11px;
            color: #64748B;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 4px;
          }
        </style>
      </head>
      <body>
        <div class="card">
          <div class="shield-icon">🛡️</div>
          <div class="badge">🔒 STRICT SAFETY SHIELD ACTIVE</div>
          <h1>অ্যাডাল্ট কন্টেন্ট ব্লক করা হয়েছে</h1>
          <p class="bengali">নিরাপদ এবং সুস্থ পারিবারিক ইন্টারনেট পরিবেশ নিশ্চিত করতে এই সাইটটি স্থায়ীভাবে ফিল্টার করা হয়েছে।</p>
          <div class="blocked-url-box">Blocked: $cleanUrl</div>
          <p class="desc">This website has been identified as adult/explicit material. The Adult Content Filter is permanently locked ON and cannot be disabled.</p>
          
          <div class="btn-container">
            <button class="btn-primary" onclick="window.history.length > 1 ? window.history.back() : location.href='about:home'">Go Back to Safety / ফিরে যান</button>
          </div>

          <div class="lock-notice">
            <span>🔒</span> Permanent Strict Adult Filtering Engine • Always Protected
          </div>
        </div>
      </body>
      </html>
    """.trimIndent()
  }

  fun createBlockedAdultResponse(blockedUrl: String): WebResourceResponse {
    val html = getBlockedAdultHtml(blockedUrl)
    return WebResourceResponse(
      "text/html",
      "UTF-8",
      200,
      "OK",
      mapOf("Access-Control-Allow-Origin" to "*"),
      ByteArrayInputStream(html.toByteArray(Charsets.UTF_8))
    )
  }
}
