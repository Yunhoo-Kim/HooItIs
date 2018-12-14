package com.hooitis.hoo.hooitis.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.base.BaseActivity
import com.hooitis.hoo.hooitis.databinding.ActivityMainBinding
import com.hooitis.hoo.hooitis.di.ViewModelFactory
import com.hooitis.hoo.hooitis.model.SharedPreferenceHelper
import com.hooitis.hoo.hooitis.utils.AUTO_MIC
import com.hooitis.hoo.hooitis.utils.AdCount
import com.hooitis.hoo.hooitis.utils.BTN_MIC
import com.hooitis.hoo.hooitis.utils.UiUtils
import com.hooitis.hoo.hooitis.vm.MainVM
import java.lang.Exception
import java.util.*
import javax.inject.Inject


class MainActivity: BaseActivity(), RecognitionListener {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var viewModel: MainVM
    private lateinit var binding: ActivityMainBinding
    private lateinit var mInterstitialAd:InterstitialAd
    private var listening: Boolean = false

    val MIC = 1234

    private val DELAY: Long = 1500
    private val STOP_DELAY: Long = 250
    private val vibrator: Vibrator by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    private val mDelayHandler: Handler by lazy {
        Handler()
    }

    private val mStopListeningDelayHandler: Handler by lazy {
        Handler()
    }

    private val mAdCount: AdCount by lazy {
        AdCount.getInstance()
    }

    private val mShowAdView: Runnable = Runnable {
        if(!isFinishing){
            if(mInterstitialAd.isLoaded)
                mInterstitialAd.show()
            else
                showResultActivity()
        }else
            showResultActivity()
    }

    private val mStopListening: Runnable = Runnable {
        stopListeningSpeech()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainVM::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)

        MobileAds.initialize(this, getString(R.string.admob))
        mInterstitialAd = InterstitialAd(this).apply {
            adUnitId = getString(R.string.admob_id)
            loadAd(AdRequest.Builder().build())
            adListener = object : AdListener(){
                override fun onAdClosed() {
                    super.onAdClosed()
                    showResultActivity()
                    Log.d("Resulta", "Closed")
                }
            }
        }

        viewModel.wrong.observe(this, android.arch.lifecycle.Observer {
            if(it!!){
                stopListeningSpeech()
                binding.voice.isClickable = false
                viewModel.countDown.postValue(getString(R.string.wrong))
                mDelayHandler.postDelayed(mShowAdView, DELAY)
            }else{
                mDelayHandler.removeCallbacks(mShowAdView)
            }
        })

        if(viewModel.sharedPreferenceHelper.getInt(SharedPreferenceHelper.KEY.MIC_MODE) == BTN_MIC){
            binding.voice.setOnTouchListener { _, event ->
                when(event.action){
                    MotionEvent.ACTION_UP -> {
                        mStopListeningDelayHandler.removeCallbacks(mStopListening)
                        mStopListeningDelayHandler.postDelayed(mStopListening, STOP_DELAY)
                        return@setOnTouchListener true }
                    MotionEvent.ACTION_DOWN -> { listeningSpeech()
                        return@setOnTouchListener true }
                    else -> return@setOnTouchListener true
                }
            }
        }else{
            viewModel.quizIndex.observe(this, android.arch.lifecycle.Observer {
                if (!viewModel.wrong.value!!) {
                    stopListeningSpeech()
                    try {
                        listeningSpeech()
                    }catch (e: Exception){
                    }
                }
            })
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


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), MIC)
        else{
        }
        viewModel.loadQuizData()
    }

    private fun showResultActivity(){
        try {
            UiUtils.replaceNewFragment(this, QuizResultFragment.newInstance(Bundle()), R.id.container_main)
        }catch (e: IllegalStateException){
            finish()
        }
    }

    private fun stopListeningSpeech() {
        if(listening){
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            listening = false
        }
    }

    private fun listeningSpeech(){
        if(listening) return
        listening = true
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(this@MainActivity)
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.KOREAN)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 4500)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 500)

        val pulseAnim = AnimationUtils.loadAnimation(this, R.anim.pulse)
        binding.voice.startAnimation(pulseAnim)
        vibrator.vibrate(100)
        speechRecognizer.startListening(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            MIC -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                } else {
                    listeningSpeech()
                }
            }
        }
    }

    override fun onBeginningOfSpeech() {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}

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

        UiUtils.makeToast(binding.root, R.string.retry)
    }

    override fun onEvent(eventType: Int, params: Bundle?) {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onReadyForSpeech(params: Bundle?) {}

    override fun onResults(results: Bundle?) {
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: return
        viewModel.checkResult(matches)
        listening = false
    }
    override fun onRmsChanged(rmsdB: Float) {}

    override fun onDestroy() {
        stopListeningSpeech()
        super.onDestroy()
    }
}