package com.example.browser

enum class ResolutionPreset(
  val displayName: String,
  val width: Int,
  val height: Int
) {
  FHD_1080P("1920 × 1080 (Full HD Desktop)", 1920, 1080),
  HD_720P("1366 × 768 (Standard Laptop)", 1366, 768),
  QHD_1440P("2560 × 1440 (2K Desktop)", 2560, 1440)
}

enum class SearchEngine(
  val displayName: String,
  val searchUrl: String,
  val homepageUrl: String
) {
  GOOGLE("Google", "https://www.google.com/search?q=", "https://www.google.com"),
  DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "https://duckduckgo.com"),
  BING("Microsoft Bing", "https://www.bing.com/search?q=", "https://www.bing.com")
}
