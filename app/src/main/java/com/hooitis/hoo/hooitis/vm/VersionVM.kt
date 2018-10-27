package com.hooitis.hoo.hooitis.vm

import android.arch.lifecycle.MutableLiveData
import com.hooitis.hoo.hooitis.base.BaseViewModel
import com.hooitis.hoo.hooitis.model.hooitis.VersionsRepository
import javax.inject.Inject


@Suppress("unused")
class VersionVM @Inject constructor(
        val versionsRepository: VersionsRepository
): BaseViewModel() {

    val beforeQuizText: MutableLiveData<String> = MutableLiveData()

    init {
//        checkVersion()
    }

    fun checkVersion() = versionsRepository.checkVersion()
}