package com.nlinterface.viewmodels

import android.app.Application
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nlinterface.R
import com.nlinterface.utility.SpeechToTextUtility
import com.nlinterface.utility.TextToSpeechUtility
import com.nlinterface.utility.VoiceCommandHelper
import java.util.Locale

class SettingsViewModel(
    application: Application
) : AndroidViewModel(application), OnInitListener {

    private lateinit var tts: TextToSpeechUtility

    val ttsInitialized: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    private val _isListening: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isListening: LiveData<Boolean>
        get() = _isListening

    private val _command: MutableLiveData<ArrayList<String>> by lazy {
        MutableLiveData<ArrayList<String>>()
    }

    val command: LiveData<ArrayList<String>>
        get() = _command

    private val stt = SpeechToTextUtility()

    fun initTTS() {
        tts = TextToSpeechUtility(getApplication<Application>().applicationContext, this)
    }

    fun say(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (ttsInitialized.value == true) {
            tts.say(text, queueMode)
        }
    }

    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            tts.setLocale(Locale.getDefault())
            ttsInitialized.value = true
        } else {
            Log.println(Log.ERROR, "tts onInit", "Couldn't initialize TTS Engine")
        }
    }

    fun initSTT() {
        stt.createSpeechRecognizer(getApplication<Application>().applicationContext,
            onResults = {
                cancelListening()
                val matches = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.size > 0) {
                    // results are added in decreasing order of confidence to the list, so choose the first one
                    handleSpeechResult(matches[0])
                }
            }, onEndOfSpeech = {
                cancelListening()
            })
    }

    private fun handleSpeechResult(s: String) {

        Log.println(Log.DEBUG, "handleSpeechResult", s)
        val voiceCommandHelper = VoiceCommandHelper()
        _command.value = voiceCommandHelper.decodeVoiceCommand(s)

    }

    fun handleSpeechBegin() {
        stt.handleSpeechBegin()
        _isListening.value = true
    }

    fun cancelListening() {
        stt.cancelListening()
        _isListening.value = false
    }
}