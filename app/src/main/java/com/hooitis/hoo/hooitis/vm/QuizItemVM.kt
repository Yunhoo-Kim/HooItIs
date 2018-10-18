package com.hooitis.hoo.hooitis.vm

import android.arch.lifecycle.MutableLiveData
import com.hooitis.hoo.hooitis.base.BaseViewModel


@Suppress("unused")
class QuizItemVM: BaseViewModel() {
    val imageUrl:MutableLiveData<String> = MutableLiveData()

    fun bind(imageUrl: String){
        this.imageUrl.value = imageUrl
    }
}