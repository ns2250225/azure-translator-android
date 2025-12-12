package com.example.azuretranslator

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.microsoft.cognitiveservices.speech.PropertyId
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import com.microsoft.cognitiveservices.speech.audio.AudioConfig
import com.microsoft.cognitiveservices.speech.AutoDetectSourceLanguageConfig
import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class TranslationViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "TranslationVM"
    }
    
    private val prefsManager = PrefsManager(application)

    private var recognizer: TranslationRecognizer? = null
    private var synthesizer: SpeechSynthesizer? = null

    // State
    private var lastForeignTarget: String = "en" // Default to English

    // LiveData for UI
    private val _status = MutableLiveData<String>("空闲")
    val status: LiveData<String> = _status

    private val _chatMessages = MutableLiveData<ChatMessage>()
    val chatMessages: LiveData<ChatMessage> = _chatMessages

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        initializeAzure()
    }

    fun refreshConfig() {
        // Close existing instances
        recognizer?.close()
        synthesizer?.close()
        recognizer = null
        synthesizer = null
        
        initializeAzure()
    }

    private fun initializeAzure() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val speechKey = prefsManager.speechKey
                val serviceRegion = prefsManager.region
                
                val translationConfig = SpeechTranslationConfig.fromSubscription(speechKey, serviceRegion)
                
                // Add target languages
                LanguageConfig.langsSetup.forEach {
                    translationConfig.addTargetLanguage(it.targetCode)
                }

                // Auto detect config
                // val autoDetectConfig = AutoDetectSourceLanguageConfig.fromLanguages(LanguageConfig.detectCandidates)
                
                // Configure Auto Detect via Properties (Workaround for Java SDK missing constructor)
                translationConfig.setProperty(PropertyId.SpeechServiceConnection_AutoDetectSourceLanguages, LanguageConfig.detectCandidates.joinToString(","))
                translationConfig.setProperty(PropertyId.SpeechServiceConnection_LanguageIdMode, "Continuous")

                // Audio Config
                val audioConfig = AudioConfig.fromDefaultMicrophoneInput()

                // Initialize Recognizer
                recognizer = TranslationRecognizer(translationConfig, audioConfig)

                // Force Continuous mode logic (similar to tmp.py)
                // In Android SDK, we just use startContinuousRecognitionAsync

                // Set up events
                recognizer?.recognized?.addEventListener { _, evt ->
                    if (evt.result.reason == ResultReason.TranslatedSpeech) {
                        handleRecognitionResult(evt.result)
                    }
                }
                
                recognizer?.recognizing?.addEventListener { _, _ ->
                     _status.postValue("正在听...")
                }

                recognizer?.canceled?.addEventListener { _, evt ->
                    Log.e(TAG, "Canceled: ${evt.errorDetails}")
                    _status.postValue("出错: ${evt.errorDetails}")
                }

                recognizer?.sessionStarted?.addEventListener { _, _ ->
                    _status.postValue("开始监听...")
                }

                recognizer?.sessionStopped?.addEventListener { _, _ ->
                    _status.postValue("停止监听")
                }

                // Initialize Synthesizer
                val ttsConfig = SpeechConfig.fromSubscription(speechKey, serviceRegion)
                synthesizer = SpeechSynthesizer(ttsConfig)

            } catch (e: Exception) {
                Log.e(TAG, "Init failed", e)
                _error.postValue("初始化失败: ${e.message}")
            }
        }
    }

    fun startListening() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                recognizer?.startContinuousRecognitionAsync()?.get()
                _status.postValue("监听中...")
            } catch (e: Exception) {
                _error.postValue("启动失败: ${e.message}")
            }
        }
    }

    fun stopListening() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                recognizer?.stopContinuousRecognitionAsync()?.get()
                _status.postValue("处理中/停止")
            } catch (e: Exception) {
                Log.e(TAG, "Stop failed", e)
            }
        }
    }

    fun resetState() {
        lastForeignTarget = "en"
    }

    private fun handleRecognitionResult(result: com.microsoft.cognitiveservices.speech.translation.TranslationRecognitionResult) {
        // 1. Get detected language
        val detectedSrcLangFull = result.properties.getProperty(PropertyId.SpeechServiceConnection_AutoDetectSourceLanguageResult) ?: return
        val text = result.text
        
        Log.d(TAG, "Detected: $detectedSrcLangFull, Text: $text")

        // 2. Map to short code
        val currentLangCode = LanguageConfig.getTargetCode(detectedSrcLangFull)
        if (currentLangCode == null) {
            Log.w(TAG, "Unknown language code: $detectedSrcLangFull")
            return
        }

        // Logic Branching from tmp.py
        
        // --- Case A: Chinese (Self) ---
        if (currentLangCode == "zh-Hans") {
            // Check for misinterpretation (Latin text check)
            val isLatinText = Pattern.compile("[a-zA-Z]").matcher(text).find()
            val hasChineseChar = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(text).find()

            if (isLatinText && !hasChineseChar) {
                Log.w(TAG, "Ignored potential misinterpretation: $text")
                return
            }

            // Translate to last foreign target
            val targetLang = lastForeignTarget
            val transText = result.translations[targetLang] ?: ""
            
            // UI Update
            val msg = ChatMessage(text, transText, "中文", targetLang, true)
            _chatMessages.postValue(msg)
            
            // Play
            playTranslation(transText, targetLang)

        } else {
            // --- Case B: Foreign (Other) ---
            // Update state
            lastForeignTarget = currentLangCode
            
            // Translate to Chinese
            val targetLang = "zh-Hans"
            val transText = result.translations[targetLang] ?: ""

            // UI Update
            val msg = ChatMessage(text, transText, currentLangCode, "中文", false)
            _chatMessages.postValue(msg)

            // Play
            playTranslation(transText, targetLang)
        }
    }

    private fun playTranslation(text: String, languageCode: String) {
        _status.postValue("播放中 ($languageCode)...")
        val voiceName = LanguageConfig.voiceMap[languageCode] ?: "en-US-AvaNeural"
        
        // Simple SSML
        val ssml = """
            <speak version='1.0' xml:lang='$languageCode'>
                <voice name='$voiceName'>$text</voice>
            </speak>
        """.trimIndent()

        viewModelScope.launch(Dispatchers.IO) {
            synthesizer?.SpeakSsmlAsync(ssml)?.get()
            _status.postValue("空闲")
        }
    }

    override fun onCleared() {
        super.onCleared()
        recognizer?.close()
        synthesizer?.close()
    }
}
