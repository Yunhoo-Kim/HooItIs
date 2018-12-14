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
import android.speech.SpeechRecognizer
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.base.BaseActivity
import com.hooitis.hoo.hooitis.databinding.ActivityKeyboardModeBinding
import com.hooitis.hoo.hooitis.di.ViewModelFactory
import com.hooitis.hoo.hooitis.model.SharedPreferenceHelper
import com.hooitis.hoo.hooitis.utils.AdCount
import com.hooitis.hoo.hooitis.utils.UiUtils
import com.hooitis.hoo.hooitis.vm.MainVM
import javax.inject.Inject


class KeyboardModeActivity: BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainVM
    private lateinit var binding: ActivityKeyboardModeBinding
    private lateinit var mInterstitialAd:InterstitialAd

    private var checking = false
    private val DELAY: Long = 1500
    private val mDelayHandler: Handler by lazy {
        Handler()
    }
    private val mAdCount: AdCount by lazy {
        AdCount.getInstance()
    }
    private val mShowAdView: Runnable = Runnable {
        mAdCount.addCount()
        if(!isFinishing && (mAdCount.count % 1 == 0)){
            if(mInterstitialAd.isLoaded)
                mInterstitialAd.show()
            else
                showResultActivity()
        }else
            showResultActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_keyboard_mode)
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
                }
            }
        }

        viewModel.setKeyboardCountDown()
        viewModel.wrong.observe(this, android.arch.lifecycle.Observer {
            if(it!!){
                viewModel.countDown.postValue(getString(R.string.wrong))
                mDelayHandler.postDelayed(mShowAdView, DELAY)
            }else{
                mDelayHandler.removeCallbacks(mShowAdView)
            }
        })

        binding.quizList.apply {
            layoutManager = LinearLayoutManager(this@KeyboardModeActivity, LinearLayoutManager.HORIZONTAL, false)
            addOnItemTouchListener(object: RecyclerView.OnItemTouchListener{
                override fun onInterceptTouchEvent(rv: RecyclerView?, e: MotionEvent?): Boolean { return true }
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
                override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {} })
            val pagerSnapHelper = PagerSnapHelper()
            pagerSnapHelper.attachToRecyclerView(binding.quizList)
            isLayoutFrozen = true
        }

        binding.checkResult.setOnClickListener {
            if (checking)
                return@setOnClickListener

            checking = true
            checkResults(binding.keyboardInput.text.toString())
            binding.keyboardInput.text.clear()
            checking = false
        }

        binding.keyboardInput.setOnEditorActionListener { v, actionId, _ ->
            when(actionId){
                EditorInfo.IME_ACTION_SEND -> {
                    if (checking)
                        return@setOnEditorActionListener false

                    checking = true
                    checkResults(v.text.toString())
                    v.text = ""
                    checking = false

                    return@setOnEditorActionListener true
                }
                else -> {
                    return@setOnEditorActionListener false
                }
            }
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

    private fun checkResults(result: String) {
        viewModel.checkResult(result.replace(" ", ""))
    }

//    override fun onBackPressed() {
//        mDelayHandler.postDelayed(mShowAdView, 10)
//    }
}