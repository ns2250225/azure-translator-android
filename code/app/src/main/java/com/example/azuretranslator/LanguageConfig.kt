package com.example.azuretranslator

data class ChatMessage(
    val originalText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val isFromMe: Boolean // True if source is zh-CN
)

data class LanguageInfo(
    val sourceCode: String,
    val targetCode: String,
    val voiceName: String
)

object LanguageConfig {
    // 格式: (识别语言代码, 翻译目标代码, 语音合成人名称)
    val langsSetup = listOf(
        LanguageInfo("zh-CN", "zh-Hans", "zh-CN-XiaoxiaoNeural"), // 中文
        LanguageInfo("en-US", "en",      "en-US-AvaNeural"),      // 英语
        LanguageInfo("ja-JP", "ja",      "ja-JP-NanamiNeural"),   // 日语
        LanguageInfo("ko-KR", "ko",      "ko-KR-SunHiNeural"),    // 韩语
        LanguageInfo("fr-FR", "fr",      "fr-FR-DeniseNeural"),   // 法语
        LanguageInfo("es-ES", "es",      "es-ES-ElviraNeural"),   // 西班牙语
        LanguageInfo("de-DE", "de",      "de-DE-KatjaNeural"),    // 德语
        LanguageInfo("ru-RU", "ru",      "ru-RU-SvetlanaNeural"), // 俄语
        LanguageInfo("it-IT", "it",      "it-IT-ElsaNeural"),     // 意大利语
        LanguageInfo("pt-BR", "pt",      "pt-BR-FranciscaNeural") // 葡萄牙语
    )

    val detectCandidates: List<String> = langsSetup.map { it.sourceCode }
    
    val srcToTargetMap: Map<String, String> = langsSetup.associate { it.sourceCode to it.targetCode }
    val voiceMap: Map<String, String> = langsSetup.associate { it.targetCode to it.voiceName }

    fun getTargetCode(sourceFull: String): String? {
        // Azure might return "ja-JP" or similar, need to match with our list
        return langsSetup.find { sourceFull.contains(it.sourceCode) }?.targetCode
    }
}
