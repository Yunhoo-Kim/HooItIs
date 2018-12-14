package com.hooitis.hoo.hooitis.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.base.BaseActivity
import com.hooitis.hoo.hooitis.databinding.ActivitySplashBinding
import com.hooitis.hoo.hooitis.di.ViewModelFactory
import com.hooitis.hoo.hooitis.vm.VersionVM
import javax.inject.Inject


class SplashActivity: BaseActivity(){
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val SPLASH_DELAY: Long = 500
    private val mDelayHandler: Handler by lazy {
        Handler()
    }
    private lateinit var viewModel: VersionVM
    private lateinit var binding: ActivitySplashBinding



    private val mRunnable: Runnable = Runnable {
        if(!isFinishing){
            val intent = Intent(applicationContext, QuizStartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(VersionVM::class.java)

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setContentView(binding.root)

        val cm: ConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo

        if(!(networkInfo != null && networkInfo.isConnectedOrConnecting)){
            val dialog = AlertDialog.Builder(this).apply {
                setMessage(R.string.need_network)
                        .setPositiveButton(R.string.confirm) { _, _ ->
                            finish()
                        }
            }

            dialog.create()
            dialog.show()
            return
        }

        viewModel.checkVersion()
                .subscribe({serverVersion ->
                    val localVersion = viewModel.versionsRepository.loadLocalVersions()
                    if(serverVersion.dbVersion != localVersion.dbVersion){
                        viewModel.versionsRepository.quizRepository.loadQuizFromStore().subscribe {
                            mDelayHandler.postDelayed(mRunnable, SPLASH_DELAY)
                        }
                    }
                    else
                        mDelayHandler.postDelayed(mRunnable, SPLASH_DELAY)
                    if(serverVersion.appVersion != localVersion.appVersion){
                        // update application from store
                    }
                    viewModel.versionsRepository.versionsDao.deleteAll()
                    viewModel.versionsRepository.versionsDao.insert(serverVersion)
                }, {
                })

    }
}