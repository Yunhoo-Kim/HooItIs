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
import com.hooitis.hoo.hooitis.utils.AUTO_MIC
import com.hooitis.hoo.hooitis.utils.BTN_MIC
import com.hooitis.hoo.hooitis.utils.UiUtils
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

        Log.d("MICMODE", sharedPreferenceHelper.getInt(SharedPreferenceHelper.KEY.MIC_MODE).toString())

        when(sharedPreferenceHelper.getInt(SharedPreferenceHelper.KEY.MIC_MODE)){
            0 -> binding.autoVoice.isChecked = true
            else -> binding.buttonVoice.isChecked = true
        }

        binding.apply {
            autoVoice.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.MIC_MODE, AUTO_MIC)
            }
            buttonVoice.setOnClickListener {
                sharedPreferenceHelper.setInt(SharedPreferenceHelper.KEY.MIC_MODE, BTN_MIC)
            }
        }

        binding.startQuiz.setOnClickListener {
            MediaPlayer.create(this, resources.getIdentifier("zing", "raw", packageName)).start()
            val intent = Intent(applicationContext, BeforeQuizActivity::class.java)
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