package com.example.browser

import org.json.JSONObject

object WindowsSpoofEngine {

  fun getUserAgent(settings: BrowserSettings): String {
    return if (settings.isDesktopSpoofing) {
      settings.windowsPreset.userAgent
    } else {
      WindowsPreset.MOBILE_CHROME_UA
    }
  }

  fun getCustomHeaders(settings: BrowserSettings): Map<String, String> {
    if (!settings.isDesktopSpoofing) return emptyMap()

    val preset = settings.windowsPreset
    return mapOf(
      "Sec-CH-UA" to "\"${preset.chBrand}\";v=\"${preset.chVersion}\", \"Chromium\";v=\"${preset.chVersion}\", \"Not_A Brand\";v=\"24\"",
      "Sec-CH-UA-Mobile" to "?0",
      "Sec-CH-UA-Platform" to "\"Windows\"",
      "Sec-CH-UA-Platform-Version" to "\"15.0.0\"",
      "Sec-CH-UA-Arch" to "\"x86\"",
      "Sec-CH-UA-Bitness" to "\"64\"",
      "Sec-CH-UA-Model" to "\"\"",
      "Upgrade-Insecure-Requests" to "1"
    )
  }

  fun getSpoofingJavaScript(settings: BrowserSettings): String {
    if (!settings.isDesktopSpoofing) return ""

    val preset = settings.windowsPreset
    val resolution = settings.resolutionPreset

    return """
      (function() {
        try {
          // 1. Platform spoofing
          ${if (settings.isPlatformSpoofingEnabled) """
          try {
            Object.defineProperty(navigator, 'platform', { get: () => 'Win32', configurable: true });
            Object.defineProperty(navigator, 'oscpu', { get: () => 'Windows NT 10.0; Win64; x64', configurable: true });
          } catch(e) {}
          """ else ""}

          // 2. UserAgent Data (Client Hints) spoofing
          ${if (settings.isClientHintsSpoofingEnabled) """
          try {
            const uaData = {
              brands: [
                { brand: '${preset.chBrand}', version: '${preset.chVersion}' },
                { brand: 'Chromium', version: '${preset.chVersion}' },
                { brand: 'Not_A Brand', version: '24' }
              ],
              mobile: false,
              platform: 'Windows',
              getHighEntropyValues: function(hints) {
                return Promise.resolve({
                  architecture: 'x86',
                  bitness: '64',
                  brands: [
                    { brand: '${preset.chBrand}', version: '${preset.chVersion}' },
                    { brand: 'Chromium', version: '${preset.chVersion}' },
                    { brand: 'Not_A Brand', version: '24' }
                  ],
                  fullVersionList: [
                    { brand: '${preset.chBrand}', version: '${preset.chVersion}.0.6943.98' },
                    { brand: 'Chromium', version: '${preset.chVersion}.0.6943.98' }
                  ],
                  mobile: false,
                  model: '',
                  platform: 'Windows',
                  platformVersion: '15.0.0',
                  wow64: false
                });
              },
              toJSON: function() {
                return {
                  brands: this.brands,
                  mobile: this.mobile,
                  platform: this.platform
                };
              }
            };
            Object.defineProperty(navigator, 'userAgentData', { get: () => uaData, configurable: true });
          } catch(e) {}
          """ else ""}

          // 3. Touch points spoofing (Windows Desktop has 0 primary touch points)
          ${if (settings.isTouchPointsSpoofingEnabled) """
          try {
            Object.defineProperty(navigator, 'maxTouchPoints', { get: () => 0, configurable: true });
          } catch(e) {}
          """ else ""}

          // 4. Hardware profile spoofing (16 cores, 16GB RAM)
          ${if (settings.isHardwareSpoofingEnabled) """
          try {
            Object.defineProperty(navigator, 'hardwareConcurrency', { get: () => 16, configurable: true });
            Object.defineProperty(navigator, 'deviceMemory', { get: () => 16, configurable: true });
          } catch(e) {}
          """ else ""}

          // 5. Screen resolution & Desktop properties
          try {
            Object.defineProperty(screen, 'width', { get: () => ${resolution.width}, configurable: true });
            Object.defineProperty(screen, 'height', { get: () => ${resolution.height}, configurable: true });
            Object.defineProperty(screen, 'availWidth', { get: () => ${resolution.width}, configurable: true });
            Object.defineProperty(screen, 'availHeight', { get: () => ${resolution.height - 40}, configurable: true });
            Object.defineProperty(screen, 'colorDepth', { get: () => 24, configurable: true });
            Object.defineProperty(screen, 'pixelDepth', { get: () => 24, configurable: true });
          } catch(e) {}

          // 6. WebGL GPU renderer spoofing (NVIDIA GeForce Direct3D)
          ${if (settings.isWebGlSpoofingEnabled) """
          try {
            const hookWebGL = function(proto) {
              if (!proto || !proto.getParameter) return;
              const originalGetParameter = proto.getParameter;
              proto.getParameter = function(parameter) {
                // UNMASKED_VENDOR_WEBGL = 0x9245 (37445)
                if (parameter === 37445 || parameter === 0x9245) {
                  return 'Google Inc. (NVIDIA)';
                }
                // UNMASKED_RENDERER_WEBGL = 0x9246 (37446)
                if (parameter === 37446 || parameter === 0x9246) {
                  return 'ANGLE (NVIDIA, NVIDIA GeForce RTX 4070 Direct3D11 vs_5_0 ps_5_0, D3D11)';
                }
                return originalGetParameter.apply(this, arguments);
              };
            };
            if (window.WebGLRenderingContext) hookWebGL(WebGLRenderingContext.prototype);
            if (window.WebGL2RenderingContext) hookWebGL(WebGL2RenderingContext.prototype);
          } catch(e) {}
          """ else ""}
        } catch(e) {
          console.error("Spoofing injection error:", e);
        }
      })();
    """.trimIndent()
  }

  fun getLiveAuditScript(): String {
    return """
      (function() {
        try {
          var ua = navigator.userAgent || "";
          var platform = navigator.platform || "";
          var touch = (navigator.maxTouchPoints !== undefined) ? navigator.maxTouchPoints : "undefined";
          var cores = navigator.hardwareConcurrency || "unknown";
          var ram = navigator.deviceMemory || "unknown";
          var scr = (window.screen ? screen.width + "x" + screen.height : "unknown");
          
          var chPlatform = "N/A";
          var chMobile = "N/A";
          if (navigator.userAgentData) {
            chPlatform = navigator.userAgentData.platform || "N/A";
            chMobile = navigator.userAgentData.mobile ? "true" : "false";
          }
          
          var glRenderer = "Not available";
          try {
            var canvas = document.createElement('canvas');
            var gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');
            if (gl) {
              var dbgRenderInfo = gl.getExtension('WEBGL_debug_renderer_info');
              if (dbgRenderInfo) {
                glRenderer = gl.getParameter(dbgRenderInfo.UNMASKED_RENDERER_WEBGL);
              }
            }
          } catch(err) {
            glRenderer = "Error: " + err;
          }

          var result = {
            userAgent: ua,
            platform: platform,
            touchPoints: touch,
            cores: cores,
            ram: ram,
            screen: scr,
            chPlatform: chPlatform,
            chMobile: chMobile,
            webGlRenderer: glRenderer,
            isWindowsUA: ua.indexOf("Windows NT") !== -1,
            isWin32Platform: platform === "Win32"
          };
          return JSON.stringify(result);
        } catch(e) {
          return JSON.stringify({ error: e.toString() });
        }
      })();
    """.trimIndent()
  }

  fun parseAuditResult(rawJson: String?): LiveAuditReport? {
    if (rawJson.isNullOrBlank()) return null
    return try {
      val cleanJson = if (rawJson.startsWith("\"") && rawJson.endsWith("\"")) {
        // Escaped JSON string from evaluateJavascript
        JSONObject("{\"data\":" + rawJson + "}").getString("data")
      } else {
        rawJson
      }
      val json = JSONObject(cleanJson)
      if (json.has("error")) return null

      val ua = json.optString("userAgent", "Unknown")
      val platform = json.optString("platform", "Unknown")
      val touch = json.optString("touchPoints", "Unknown")
      val cores = json.optString("cores", "Unknown")
      val ram = json.optString("ram", "Unknown")
      val scr = json.optString("screen", "Unknown")
      val chPlatform = json.optString("chPlatform", "N/A")
      val chMobile = json.optString("chMobile", "N/A")
      val webgl = json.optString("webGlRenderer", "Unknown")

      var score = 0
      if (ua.contains("Windows NT 10.0") || ua.contains("Win64")) score += 20
      if (platform == "Win32") score += 20
      if (chPlatform == "Windows" || chMobile == "false") score += 20
      if (touch == "0") score += 10
      if (cores == "16" || ram == "16") score += 10
      if (webgl.contains("NVIDIA") || webgl.contains("Direct3D") || webgl.contains("ANGLE")) score += 20

      LiveAuditReport(
        overallScore = score.coerceIn(0, 100),
        userAgentReport = ua,
        platformReport = platform,
        clientHintsReport = "Platform: $chPlatform | Mobile: $chMobile",
        resolutionReport = scr,
        touchPointsReport = "$touch points",
        hardwareReport = "$cores CPU Cores, ${ram}GB RAM",
        webGlReport = webgl
      )
    } catch (e: Exception) {
      null
    }
  }

  fun getDiagnosticTestHtml(): String {
    return """
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Windows Browser Fidelity Auditor</title>
        <style>
          body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0b132b; color: #e0e2e8; margin: 0; padding: 20px; }
          .container { max-width: 800px; margin: 0 auto; }
          .card { background: #161b24; border: 1px solid #242a36; border-radius: 12px; padding: 20px; margin-bottom: 16px; }
          .badge { display: inline-block; padding: 4px 10px; border-radius: 6px; font-weight: bold; font-size: 12px; }
          .badge-success { background: #10b981; color: #064e3b; }
          .badge-warn { background: #f59e0b; color: #78350f; }
          .score-box { text-align: center; padding: 24px; background: linear-gradient(135deg, #004e8c, #0078d4); border-radius: 16px; margin-bottom: 24px; }
          .score-num { font-size: 56px; font-weight: 800; color: #fff; margin: 8px 0; }
          table { width: 100%; border-collapse: collapse; margin-top: 12px; }
          td, th { padding: 12px; text-align: left; border-bottom: 1px solid #242a36; font-size: 14px; }
          th { color: #9ccaff; font-weight: 600; }
          .val-code { font-family: monospace; color: #71f7e6; word-break: break-all; }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="score-box">
            <h2 style="margin:0; font-size: 22px; color: #d0e4ff;">Windows Spoofing Fidelity Test</h2>
            <div class="score-num" id="score">--%</div>
            <p style="margin:0; color: #e0e2e8; font-size: 14px;" id="status-text">Analyzing browser environment...</p>
          </div>

          <div class="card">
            <h3 style="margin-top:0; color: #9ccaff;">Live Environmental Inspection</h3>
            <table>
              <thead>
                <tr>
                  <th>Test Parameter</th>
                  <th>Observed Value</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody id="test-rows">
              </tbody>
            </table>
          </div>
        </div>

        <script>
          function runAudit() {
            var ua = navigator.userAgent;
            var platform = navigator.platform;
            var touch = navigator.maxTouchPoints;
            var cores = navigator.hardwareConcurrency;
            var ram = navigator.deviceMemory;
            var scr = screen.width + "x" + screen.height;
            var chPlatform = navigator.userAgentData ? navigator.userAgentData.platform : "N/A";
            var chMobile = navigator.userAgentData ? navigator.userAgentData.mobile : "N/A";
            
            var glRenderer = "Unavailable";
            try {
              var c = document.createElement('canvas');
              var gl = c.getContext('webgl') || c.getContext('experimental-webgl');
              if (gl) {
                var ext = gl.getExtension('WEBGL_debug_renderer_info');
                if (ext) glRenderer = gl.getParameter(ext.UNMASKED_RENDERER_WEBGL);
              }
            } catch(e) {}

            var isWinUA = ua.indexOf("Windows NT") !== -1;
            var isWinPlatform = platform === "Win32";
            var isWinCH = chPlatform === "Windows";
            var isZeroTouch = touch === 0;
            var isWinGPU = glRenderer.indexOf("NVIDIA") !== -1 || glRenderer.indexOf("Direct3D") !== -1 || glRenderer.indexOf("ANGLE") !== -1;

            var score = 0;
            if (isWinUA) score += 20;
            if (isWinPlatform) score += 20;
            if (isWinCH) score += 20;
            if (isZeroTouch) score += 10;
            if (cores >= 8) score += 10;
            if (isWinGPU) score += 20;

            document.getElementById('score').innerText = score + "%";
            document.getElementById('status-text').innerText = score >= 80 ? "Genuine Windows Profile Active" : "Partial or Mobile Profile";

            var tests = [
              { name: "User-Agent Header", val: ua, pass: isWinUA },
              { name: "navigator.platform", val: platform, pass: isWinPlatform },
              { name: "Client Hints Platform", val: chPlatform + " (mobile: " + chMobile + ")", pass: isWinCH },
              { name: "Touch Points", val: touch + " points", pass: isZeroTouch },
              { name: "CPU & Memory", val: cores + " Cores / " + ram + "GB RAM", pass: cores >= 8 },
              { name: "Screen Resolution", val: scr, pass: true },
              { name: "WebGL GPU Renderer", val: glRenderer, pass: isWinGPU }
            ];

            var tbody = document.getElementById('test-rows');
            tbody.innerHTML = "";
            tests.forEach(function(t) {
              var tr = document.createElement('tr');
              tr.innerHTML = "<td><b>" + t.name + "</b></td>" +
                             "<td class='val-code'>" + t.val + "</td>" +
                             "<td><span class='badge " + (t.pass ? "badge-success" : "badge-warn") + "'>" + (t.pass ? "MATCH" : "MISMATCH") + "</span></td>";
              tbody.appendChild(tr);
            });
          }
          window.onload = runAudit;
        </script>
      </body>
      </html>
    """.trimIndent()
  }
}
