package com.hooitis.hoo.hooitis.vm

import android.arch.lifecycle.MutableLiveData
import com.hooitis.hoo.hooitis.base.BaseViewModel
import com.hooitis.hoo.hooitis.model.quiz.Quiz


@Suppress("unused")
class QuizItemVM: BaseViewModel() {
    val imageUrl: MutableLiveData<String> = MutableLiveData()
    val origin: MutableLiveData<String> = MutableLiveData()
    val result: MutableLiveData<String> = MutableLiveData()

    fun bind(quiz: Quiz){
        this.imageUrl.value = quiz.imageUrl
        this.origin.value = "출처 : Daum"
        this.result.value = quiz.answerList.first()
    }
}