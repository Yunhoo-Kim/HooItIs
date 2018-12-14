package com.hooitis.hoo.hooitis.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.base.BaseActivity
import com.hooitis.hoo.hooitis.databinding.ActivityStartupBinding
import com.hooitis.hoo.hooitis.model.SharedPreferenceHelper
import com.hooitis.hoo.hooitis.utils.*
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.*
import javax.inject.Inject


class QuizStartActivity: BaseActivity(){

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    private lateinit var binding: ActivityStartupBinding
    private val backButtonSubject: Subject<Long> = BehaviorSubject.createDefault(0L)

    val MIC = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_startup)
        binding.setLifecycleOwner(this)
        setContentView(binding.root)
        requestMicPermission()

        when(sharedPreferenceHelper.getInt(SharedPreferenceHelper.KEY.MIC_MODE)){
            0 -> binding.autoVoice.isChecked = true
            1 -> binding.buttonVoice.isChecked = true
            else -> binding.keyboardInput.isChecked = true
        }

        when(sharedPreferenceHelper.getInt(SharedPreferenceHelper.KEY.PLAYERS)){
            0 -> {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.PLAYERS, ONE_PLAYER)
                binding.oneP.isChecked = true
            }
            1 -> binding.oneP.isChecked = true
            2 -> binding.twoP.isChecked = true
            3 -> binding.threeP.isChecked = true
            4 -> binding.fourP.isChecked = true
            else -> binding.fiveP.isChecked = true
        }

        binding.apply {
            autoVoice.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.MIC_MODE, AUTO_MIC)
            }
            buttonVoice.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.MIC_MODE, BTN_MIC)
            }
            keyboardInput.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.MIC_MODE, KEYBOARD_MODE)
            }
            oneP.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.PLAYERS, ONE_PLAYER)
            }
            twoP.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.PLAYERS, TWO_PLAYER)
            }
            threeP.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.PLAYERS, THREE_PLAYER)
            }
            fourP.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.PLAYERS, FOUR_PLAYER)
            }
            fiveP.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.PLAYERS, FIVE_PLAYER)
            }
        }

        binding.startQuiz.setOnClickListener {
            MediaPlayer.create(this, resources.getIdentifier("zing", "raw", packageName)).start()
            val intent = Intent(applicationContext, BeforeQuizActivity::class.java).apply {
                putExtra("MODE", sharedPreferenceHelper.getInt(SharedPreferenceHelper.KEY.MIC_MODE))
            }

            startActivity(intent)
        }

        initBackPress()
    }

    private fun initBackPress(){
        backButtonSubject.toFlowable(BackpressureStrategy.BUFFER)
                .observeOn(AndroidSchedulers.mainThread())
                .buffer(2, 1)
                .map {
                    Pair<Long, Long>(it[0], it[1])
                }
                .doOnNext { t->
                    if (t != null && t.second - t.first < 1000) {
                        super.onBackPressed()
                    } else {
                        UiUtils.makeToast(binding.title, R.string.push_again_back_pressed)
                    }
                }.subscribe()
    }

    private fun requestMicPermission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), MIC)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            MIC -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestMicPermission()
                } else {
                }
            }
        }
    }

    override fun onBackPressed() {
        backButtonSubject.onNext(Calendar.getInstance().timeInMillis)
    }
}