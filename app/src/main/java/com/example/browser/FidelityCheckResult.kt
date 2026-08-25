package com.example.browser

data class FidelityCriterion(
  val id: String,
  val title: String,
  val description: String,
  val weightPercent: Int,
  val isEnabled: Boolean,
  val expectedValue: String,
  val actualValue: String? = null,
  val isPassed: Boolean = isEnabled
)

data class LiveAuditReport(
  val overallScore: Int,
  val userAgentReport: String,
  val platformReport: String,
  val clientHintsReport: String,
  val resolutionReport: String,
  val touchPointsReport: String,
  val hardwareReport: String,
  val webGlReport: String,
  val testedAt: Long = System.currentTimeMillis()
)
