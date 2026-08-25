package com.example.browser

data class BrowserSettings(
  val isDesktopSpoofing: Boolean = true,
  val windowsPreset: WindowsPreset = WindowsPreset.WIN11_CHROME,
  val resolutionPreset: ResolutionPreset = ResolutionPreset.FHD_1080P,
  val searchEngine: SearchEngine = SearchEngine.GOOGLE,
  val isAdBlockerEnabled: Boolean = true,
  val isPopupBlockerEnabled: Boolean = true,
  val isWebGlSpoofingEnabled: Boolean = true,
  val isTouchPointsSpoofingEnabled: Boolean = true,
  val isHardwareSpoofingEnabled: Boolean = true,
  val isClientHintsSpoofingEnabled: Boolean = true,
  val isPlatformSpoofingEnabled: Boolean = true,
  val isJavaScriptEnabled: Boolean = true,
  val isCookiesEnabled: Boolean = true,
  val isCosmeticAdBlockingEnabled: Boolean = true,
  val isAdultFilterEnabled: Boolean = true
) {
  fun calculateWindowsFidelityScore(): Int {
    if (!isDesktopSpoofing) return 0

    var score = 0
    // 1. User-Agent
    score += 15
    // 2. Platform (Win32)
    if (isPlatformSpoofingEnabled) score += 15
    // 3. Client Hints (Windows platform, mobile=false)
    if (isClientHintsSpoofingEnabled) score += 15
    // 4. Desktop Resolution & Viewport
    score += 15
    // 5. Touch Points (maxTouchPoints=0 for true desktop profile)
    if (isTouchPointsSpoofingEnabled) score += 10
    // 6. Hardware Concurrency & Memory
    if (isHardwareSpoofingEnabled) score += 10
    // 7. WebGL GPU unmasked renderer
    if (isWebGlSpoofingEnabled) score += 10
    // 8. Headers & Client Protocol
    score += 10

    return score.coerceIn(0, 100)
  }

  fun getFidelityCriteriaList(): List<FidelityCriterion> {
    val isDesktop = isDesktopSpoofing
    return listOf(
      FidelityCriterion(
        id = "ua",
        title = "User-Agent Header",
        description = "Windows NT 10.0; Win64; x64 identifier spoofing",
        weightPercent = 15,
        isEnabled = isDesktop,
        expectedValue = if (isDesktop) windowsPreset.userAgent else WindowsPreset.MOBILE_CHROME_UA
      ),
      FidelityCriterion(
        id = "platform",
        title = "navigator.platform Spoofing",
        description = "Returns 'Win32' matching genuine Windows installation",
        weightPercent = 15,
        isEnabled = isDesktop && isPlatformSpoofingEnabled,
        expectedValue = if (isDesktop && isPlatformSpoofingEnabled) "Win32" else "Linux armv8l"
      ),
      FidelityCriterion(
        id = "client_hints",
        title = "navigator.userAgentData (Client Hints)",
        description = "Modern Sec-CH-UA client hints: Windows, non-mobile, x86 architecture",
        weightPercent = 15,
        isEnabled = isDesktop && isClientHintsSpoofingEnabled,
        expectedValue = if (isDesktop && isClientHintsSpoofingEnabled) "platform: 'Windows', mobile: false" else "platform: 'Android', mobile: true"
      ),
      FidelityCriterion(
        id = "resolution",
        title = "Desktop Viewport & Screen DPI",
        description = "Emulates ${resolutionPreset.displayName} with desktop overview",
        weightPercent = 15,
        isEnabled = isDesktop,
        expectedValue = if (isDesktop) "${resolutionPreset.width}x${resolutionPreset.height}, ratio 1.0" else "Native Mobile DPI"
      ),
      FidelityCriterion(
        id = "touch",
        title = "navigator.maxTouchPoints Profile",
        description = "Spoofs 0 touch points to mimic classic Windows mouse/pointer hardware",
        weightPercent = 10,
        isEnabled = isDesktop && isTouchPointsSpoofingEnabled,
        expectedValue = if (isDesktop && isTouchPointsSpoofingEnabled) "0 (Mouse/Desktop Pointer)" else "5 (Multi-touch Mobile)"
      ),
      FidelityCriterion(
        id = "hardware",
        title = "Hardware Profile (CPU & RAM)",
        description = "Spoofs 16 CPU threads and 16 GB desktop RAM footprint",
        weightPercent = 10,
        isEnabled = isDesktop && isHardwareSpoofingEnabled,
        expectedValue = if (isDesktop && isHardwareSpoofingEnabled) "16 Cores, 16 GB Memory" else "Device Native Hardware"
      ),
      FidelityCriterion(
        id = "webgl",
        title = "WebGL GPU Renderer (Direct3D)",
        description = "Spoofs NVIDIA GeForce RTX GPU Direct3D unmasked renderer",
        weightPercent = 10,
        isEnabled = isDesktop && isWebGlSpoofingEnabled,
        expectedValue = if (isDesktop && isWebGlSpoofingEnabled) "ANGLE (NVIDIA GeForce RTX 4070 Direct3D11)" else "Qualcomm/Mali Mobile GPU"
      ),
      FidelityCriterion(
        id = "headers",
        title = "Sec-CH-UA Protocol Headers",
        description = "Windows desktop security headers transmitted to web servers",
        weightPercent = 10,
        isEnabled = isDesktop,
        expectedValue = if (isDesktop) "?0 (Desktop Mode Active)" else "?1 (Mobile Request)"
      )
    )
  }
}
