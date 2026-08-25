package com.example.browser

enum class WindowsPreset(
  val displayName: String,
  val browserName: String,
  val osName: String,
  val userAgent: String,
  val chBrand: String,
  val chVersion: String
) {
  WIN11_CHROME(
    displayName = "Windows 11 Chrome (133.0)",
    browserName = "Google Chrome",
    osName = "Windows 11",
    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
    chBrand = "Google Chrome",
    chVersion = "133"
  ),
  WIN11_EDGE(
    displayName = "Windows 11 Microsoft Edge (133.0)",
    browserName = "Microsoft Edge",
    osName = "Windows 11",
    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36 Edg/133.0.0.0",
    chBrand = "Microsoft Edge",
    chVersion = "133"
  ),
  WIN10_FIREFOX(
    displayName = "Windows 10 Mozilla Firefox (135.0)",
    browserName = "Mozilla Firefox",
    osName = "Windows 10",
    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:135.0) Gecko/20100101 Firefox/135.0",
    chBrand = "Firefox",
    chVersion = "135"
  );

  companion object {
    val MOBILE_CHROME_UA = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Mobile Safari/537.36"
  }
}
