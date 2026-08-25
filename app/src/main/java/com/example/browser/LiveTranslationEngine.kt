package com.example.browser

enum class TranslationState(
  val code: String,
  val labelBn: String,
  val badge: String,
  val targetLangName: String
) {
  ORIGINAL("original", "মূল ভাষা", "Org", "Original"),
  BANGLA("bn", "বাংলা", "বাং", "বাংলা (Bengali)"),
  ENGLISH("en", "English", "EN", "English");

  fun next(): TranslationState {
    return when (this) {
      ORIGINAL -> BANGLA
      BANGLA -> ENGLISH
      ENGLISH -> ORIGINAL
    }
  }
}

object LiveTranslationEngine {

  fun getTranslationScript(targetState: TranslationState): String {
    val langCode = targetState.code
    return """
      (function() {
        try {
          var targetLang = '$langCode';

          if (targetLang === 'original') {
            // Clear Google translate cookies across all domain levels
            var cookies = ['googtrans'];
            var hostname = window.location.hostname;
            var parts = hostname.split('.');
            for (var i = 0; i < parts.length; i++) {
              var d = parts.slice(i).join('.');
              document.cookie = 'googtrans=; path=/; domain=' + d + '; expires=Thu, 01 Jan 1970 00:00:00 GMT';
              document.cookie = 'googtrans=; path=/; domain=.' + d + '; expires=Thu, 01 Jan 1970 00:00:00 GMT';
            }
            document.cookie = 'googtrans=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT';

            // Reset banner frame if present
            var banner = document.querySelector('.goog-te-banner-frame');
            if (banner) {
              banner.style.display = 'none';
            }
            document.body.style.top = '0px';
            document.body.style.position = 'static';

            // Reload to restore original pure content
            window.location.reload();
            return 'Restored to original';
          }

          // Set cookies for translation
          var cookieVal = '/auto/' + targetLang;
          document.cookie = 'googtrans=' + cookieVal + '; path=/;';
          document.cookie = 'googtrans=' + cookieVal + '; path=/; domain=' + window.location.hostname + ';';

          var container = document.getElementById('custom_google_translate_element');
          if (!container) {
            container = document.createElement('div');
            container.id = 'custom_google_translate_element';
            container.style.display = 'none';
            document.body.appendChild(container);
          }

          window.googleTranslateElementInit = function() {
            try {
              if (window.google && window.google.translate) {
                new google.translate.TranslateElement({
                  pageLanguage: 'auto',
                  includedLanguages: 'bn,en,hi,ar,es,fr,de,ja,zh-CN,ru',
                  autoDisplay: false,
                  layout: google.translate.TranslateElement.InlineLayout.SIMPLE
                }, 'custom_google_translate_element');
              }
            } catch(e) {}

            var checkCount = 0;
            var interval = setInterval(function() {
              checkCount++;
              var select = document.querySelector('.goog-te-combo');
              if (select) {
                select.value = targetLang;
                select.dispatchEvent(new Event('change'));
                clearInterval(interval);
              }
              if (checkCount > 25) {
                clearInterval(interval);
              }
            }, 200);
          };

          if (!window.google || !window.google.translate) {
            var existingScript = document.getElementById('google-translate-script-tag');
            if (!existingScript) {
              var script = document.createElement('script');
              script.id = 'google-translate-script-tag';
              script.type = 'text/javascript';
              script.src = 'https://translate.google.com/translate_a/element.js?cb=googleTranslateElementInit';
              document.head.appendChild(script);
            }
          } else {
            var select = document.querySelector('.goog-te-combo');
            if (select) {
              select.value = targetLang;
              select.dispatchEvent(new Event('change'));
            } else {
              window.googleTranslateElementInit();
            }
          }
          return 'Translation requested: ' + targetLang;
        } catch(err) {
          console.error("Translation script error:", err);
          return 'Error: ' + err;
        }
      })();
    """.trimIndent()
  }
}
