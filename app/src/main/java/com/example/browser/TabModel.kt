package com.example.browser

import java.util.UUID

data class TabModel(
  val id: String = UUID.randomUUID().toString(),
  val title: String = "New Tab",
  val url: String = "about:home",
  val favicon: String? = null,
  val isLoading: Boolean = false,
  val progress: Int = 0,
  val canGoBack: Boolean = false,
  val canGoForward: Boolean = false,
  val isDesktopMode: Boolean = true,
  val blockedAdsCount: Int = 0,
  val blockedPopupsCount: Int = 0,
  val blockedAdultCount: Int = 0,
  val lastAuditReport: LiveAuditReport? = null,
  val translationState: TranslationState = TranslationState.ORIGINAL
)
