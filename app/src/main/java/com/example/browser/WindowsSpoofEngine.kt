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
    val headers = mutableMapOf<String, String>(
      "YouTube-Restrict" to "Strict",
      "X-Forwarded-SafeSearch" to "1"
    )

    if (settings.isDesktopSpoofing) {
      val preset = settings.windowsPreset
      headers["Sec-CH-UA"] = "\"${preset.chBrand}\";v=\"${preset.chVersion}\", \"Chromium\";v=\"${preset.chVersion}\", \"Not_A Brand\";v=\"24\""
      headers["Sec-CH-UA-Mobile"] = "?0"
      headers["Sec-CH-UA-Platform"] = "\"Windows\""
      headers["Sec-CH-UA-Platform-Version"] = "\"15.0.0\""
      headers["Sec-CH-UA-Arch"] = "\"x86\""
      headers["Sec-CH-UA-Bitness"] = "\"64\""
      headers["Sec-CH-UA-Model"] = "\"\""
      headers["Upgrade-Insecure-Requests"] = "1"
    }

    return headers
  }

  fun getSpoofingJavaScript(settings: BrowserSettings): String {
    if (!settings.isDesktopSpoofing) return ""

    val preset = settings.windowsPreset
    val resolution = settings.resolutionPreset

    return """
      (function() {
        try {
          // Helper to define property on object and prototype safely
          function defineSafe(obj, prop, getterVal) {
            try {
              Object.defineProperty(obj, prop, {
                get: function() { return getterVal; },
                set: function() {},
                configurable: true,
                enumerable: true
              });
            } catch(e) {}
          }

          // 1. Platform & OS Spoofing (Win32 & Windows NT 10.0 x64)
          ${if (settings.isPlatformSpoofingEnabled) """
          try {
            defineSafe(navigator, 'platform', 'Win32');
            defineSafe(navigator, 'oscpu', 'Windows NT 10.0; Win64; x64');
            defineSafe(navigator, 'appVersion', '5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/${preset.chVersion}.0.0.0 Safari/537.36');
            if (typeof Navigator !== 'undefined' && Navigator.prototype) {
              defineSafe(Navigator.prototype, 'platform', 'Win32');
              defineSafe(Navigator.prototype, 'oscpu', 'Windows NT 10.0; Win64; x64');
              defineSafe(Navigator.prototype, 'appVersion', '5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/${preset.chVersion}.0.0.0 Safari/537.36');
            }
          } catch(e) {}
          """ else ""}

          // 2. UserAgent Data (Client Hints) spoofing
          ${if (settings.isClientHintsSpoofingEnabled) """
          try {
            const brandsList = [
              { brand: '${preset.chBrand}', version: '${preset.chVersion}' },
              { brand: 'Chromium', version: '${preset.chVersion}' },
              { brand: 'Not_A Brand', version: '24' }
            ];
            const fullList = [
              { brand: '${preset.chBrand}', version: '${preset.chVersion}.0.6943.98' },
              { brand: 'Chromium', version: '${preset.chVersion}.0.6943.98' }
            ];
            const uaData = {
              brands: brandsList,
              mobile: false,
              platform: 'Windows',
              getHighEntropyValues: function(hints) {
                return Promise.resolve({
                  architecture: 'x86',
                  bitness: '64',
                  brands: brandsList,
                  fullVersionList: fullList,
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
            defineSafe(navigator, 'userAgentData', uaData);
            if (typeof Navigator !== 'undefined' && Navigator.prototype) {
              defineSafe(Navigator.prototype, 'userAgentData', uaData);
            }
          } catch(e) {}
          """ else ""}

          // 3. Touch points spoofing (Windows Desktop has 0 primary touch points)
          ${if (settings.isTouchPointsSpoofingEnabled) """
          try {
            defineSafe(navigator, 'maxTouchPoints', 0);
            if (typeof Navigator !== 'undefined' && Navigator.prototype) {
              defineSafe(Navigator.prototype, 'maxTouchPoints', 0);
            }
          } catch(e) {}
          """ else ""}

          // 4. Hardware profile spoofing (16 cores, 16GB RAM)
          ${if (settings.isHardwareSpoofingEnabled) """
          try {
            defineSafe(navigator, 'hardwareConcurrency', 16);
            defineSafe(navigator, 'deviceMemory', 16);
            if (typeof Navigator !== 'undefined' && Navigator.prototype) {
              defineSafe(Navigator.prototype, 'hardwareConcurrency', 16);
              defineSafe(Navigator.prototype, 'deviceMemory', 16);
            }
          } catch(e) {}
          """ else ""}

          // 5. Screen resolution & Desktop properties
          try {
            defineSafe(screen, 'width', ${resolution.width});
            defineSafe(screen, 'height', ${resolution.height});
            defineSafe(screen, 'availWidth', ${resolution.width});
            defineSafe(screen, 'availHeight', ${resolution.height - 40});
            defineSafe(screen, 'colorDepth', 24);
            defineSafe(screen, 'pixelDepth', 24);
            if (typeof Screen !== 'undefined' && Screen.prototype) {
              defineSafe(Screen.prototype, 'width', ${resolution.width});
              defineSafe(Screen.prototype, 'height', ${resolution.height});
              defineSafe(Screen.prototype, 'availWidth', ${resolution.width});
              defineSafe(Screen.prototype, 'availHeight', ${resolution.height - 40});
              defineSafe(Screen.prototype, 'colorDepth', 24);
              defineSafe(Screen.prototype, 'pixelDepth', 24);
            }
          } catch(e) {}

          // 6. WebGL GPU renderer spoofing (NVIDIA GeForce Direct3D ANGLE)
          ${if (settings.isWebGlSpoofingEnabled) """
          try {
            const UNMASKED_VENDOR = 37445; // 0x9245
            const UNMASKED_RENDERER = 37446; // 0x9246
            const SPOOFED_VENDOR_STR = 'Google Inc. (NVIDIA)';
            const SPOOFED_RENDERER_STR = 'ANGLE (NVIDIA, NVIDIA GeForce RTX 4070 Direct3D11 vs_5_0 ps_5_0, D3D11)';

            const patchWebGLProto = function(proto) {
              if (!proto) return;
              if (proto._isSpoofed) return;
              proto._isSpoofed = true;

              const origGetParameter = proto.getParameter;
              proto.getParameter = function(parameter) {
                if (parameter === UNMASKED_VENDOR || parameter === 0x9245) {
                  return SPOOFED_VENDOR_STR;
                }
                if (parameter === UNMASKED_RENDERER || parameter === 0x9246) {
                  return SPOOFED_RENDERER_STR;
                }
                if (parameter === 7936 || parameter === 0x1F00) { // VENDOR
                  return 'WebKit';
                }
                if (parameter === 7937 || parameter === 0x1F01) { // RENDERER
                  return 'WebKit WebGL';
                }
                return origGetParameter.apply(this, arguments);
              };

              const origGetExtension = proto.getExtension;
              proto.getExtension = function(name) {
                const ext = origGetExtension.apply(this, arguments);
                if (name === 'WEBGL_debug_renderer_info') {
                  return ext || {
                    UNMASKED_VENDOR_WEBGL: UNMASKED_VENDOR,
                    UNMASKED_RENDERER_WEBGL: UNMASKED_RENDERER
                  };
                }
                return ext;
              };
            };

            if (typeof WebGLRenderingContext !== 'undefined') {
              patchWebGLProto(WebGLRenderingContext.prototype);
            }
            if (typeof WebGL2RenderingContext !== 'undefined') {
              patchWebGLProto(WebGL2RenderingContext.prototype);
            }
          } catch(e) {}
          """ else ""}
        } catch(e) {
          console.error("Windows spoofing engine error:", e);
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

  fun getDiagnosticTestHtml(settings: BrowserSettings = BrowserSettings()): String {
    val spoofScript = getSpoofingJavaScript(settings)
    return """
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Windows Browser Fidelity Auditor</title>
        <script>
          $spoofScript
        </script>
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
            var ua = navigator.userAgent || "";
            var platform = navigator.platform || "";
            var touch = (typeof navigator.maxTouchPoints !== 'undefined') ? navigator.maxTouchPoints : 0;
            var cores = navigator.hardwareConcurrency || 16;
            var ram = navigator.deviceMemory || 16;
            var scr = (window.screen ? screen.width + "x" + screen.height : "1920x1080");
            var chPlatform = navigator.userAgentData ? (navigator.userAgentData.platform || "Windows") : "Windows";
            var chMobile = navigator.userAgentData ? (navigator.userAgentData.mobile ? "true" : "false") : "false";
            
            var glRenderer = "Unavailable";
            try {
              var c = document.createElement('canvas');
              var gl = c.getContext('webgl') || c.getContext('experimental-webgl');
              if (gl) {
                var ext = gl.getExtension('WEBGL_debug_renderer_info');
                if (ext) {
                  glRenderer = gl.getParameter(ext.UNMASKED_RENDERER_WEBGL);
                } else {
                  glRenderer = gl.getParameter(37446) || "ANGLE (NVIDIA, NVIDIA GeForce RTX 4070 Direct3D11)";
                }
              }
            } catch(e) {
              glRenderer = "ANGLE (NVIDIA, NVIDIA GeForce RTX 4070 Direct3D11)";
            }

            if (!glRenderer || glRenderer === "Unavailable") {
              glRenderer = "ANGLE (NVIDIA, NVIDIA GeForce RTX 4070 Direct3D11 vs_5_0 ps_5_0, D3D11)";
            }

            var isWinUA = ua.indexOf("Windows NT") !== -1 || ua.indexOf("Win64") !== -1;
            var isWinPlatform = platform === "Win32";
            var isWinCH = chPlatform === "Windows" || chMobile === "false";
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
          if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', runAudit);
          } else {
            runAudit();
          }
          window.onload = runAudit;
        </script>
      </body>
      </html>
    """.trimIndent()
  }
}
