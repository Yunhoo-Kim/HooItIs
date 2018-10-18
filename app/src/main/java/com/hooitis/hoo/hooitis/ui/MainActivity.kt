package com.hooitis.hoo.hooitis.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.base.BaseActivity
import com.hooitis.hoo.hooitis.databinding.ActivityMainBinding
import com.hooitis.hoo.hooitis.di.ViewModelFactory
import com.hooitis.hoo.hooitis.vm.MainVM
import java.util.*
import javax.inject.Inject


class MainActivity: BaseActivity(), RecognitionListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var viewModel: MainVM
    private lateinit var binding: ActivityMainBinding
    private var listening: Boolean = false

    val MIC = 1234

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainVM::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        binding.voice.setOnTouchListener { _, event ->
            when(event.action){
                MotionEvent.ACTION_UP -> { stopListeningSpeech()
                    return@setOnTouchListener true }
                MotionEvent.ACTION_DOWN -> { listeningSpeech()
                    return@setOnTouchListener true }
                else -> return@setOnTouchListener true
            }
        }

        binding.quizList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

            addOnItemTouchListener(object: RecyclerView.OnItemTouchListener{
                override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean { return true }
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {} })

            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(binding.quizList)
            isLayoutFrozen = true
        }
        setContentView(binding.root)


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), MIC)
        else{
            viewModel.loadQuizData()
        }
    }

    private fun stopListeningSpeech() {
        Log.d("Speech", "stop")
        if(listening){
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            listening = false
        }
    }

    private fun listeningSpeech(){

        if(listening)
            return
        listening = true

        Log.d("Speech", "listening")

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(this@MainActivity)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.KOREAN)
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizer.startListening(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            MIC -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Snackbar.make(th, R.string.allow_grant, Snackbar.LENGTH_SHORT).show()
                } else {
                    listeningSpeech()
                }
            }
        }
    }

    override fun onBeginningOfSpeech() {
        Log.d("Speech", "Begin")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
    }

    override fun onEndOfSpeech() {
        Log.d("Speech", "end")
    }

    override fun onError(error: Int) {
        var message: String = ""

        message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
    }

    override fun onPartialResults(partialResults: Bundle?) {
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d("Speech", "onReadyForSpeech")
    }

    override fun onResults(results: Bundle?) {
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return

        for(m in matches){
            Log.d("Speech", m)
        }
        viewModel.showNextQuiz()
    }

    override fun onRmsChanged(rmsdB: Float) {
    }
}