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
  val homepageUrl: String,
  val shortLabel: String,
  val brandColorHex: Long
) {
  GOOGLE("Google", "https://www.google.com/search?q=", "https://www.google.com", "G", 0xFF4285F4),
  BING("Microsoft Bing", "https://www.bing.com/search?q=", "https://www.bing.com", "B", 0xFF008373),
  DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=", "https://duckduckgo.com", "DDG", 0xFFDE5833),
  YAHOO("Yahoo", "https://search.yahoo.com/search?p=", "https://search.yahoo.com", "Y!", 0xFF6001D2),
  BRAVE("Brave Search", "https://search.brave.com/search?q=", "https://search.brave.com", "🦁", 0xFFFB542B),
  ECOSIA("Ecosia", "https://www.ecosia.org/search?q=", "https://www.ecosia.org", "🌿", 0xFF009688),
  YANDEX("Yandex", "https://yandex.com/search/?text=", "https://yandex.com", "Я", 0xFFFC3F1D),
  YOUTUBE("YouTube", "https://www.youtube.com/results?search_query=", "https://www.youtube.com", "▶", 0xFFFF0000),
  WIKIPEDIA("Wikipedia", "https://en.wikipedia.org/wiki/Special:Search?search=", "https://en.wikipedia.org", "W", 0xFF24292F)
}
