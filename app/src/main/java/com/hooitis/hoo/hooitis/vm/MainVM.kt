package com.hooitis.hoo.hooitis.vm

import android.arch.lifecycle.MutableLiveData
import com.hooitis.hoo.hooitis.base.BaseViewModel
import com.hooitis.hoo.hooitis.model.hooitis.VersionsRepository
import com.hooitis.hoo.hooitis.ui.QuizImageListAdapter
import javax.inject.Inject


@Suppress("unused")
class MainVM @Inject constructor(
        private val versionsRepository: VersionsRepository
): BaseViewModel() {

    var index = 0
    val quizIndex:MutableLiveData<Int> = MutableLiveData()

    val quizImageListAdapter: QuizImageListAdapter by lazy {
        QuizImageListAdapter()
    }

    init {
        index = 0
        quizIndex.value = index
    }

    fun loadQuizData(){
        val quizList = listOf("A", "b", "C")
        quizImageListAdapter.updateQuizList(quizList)
    }

    fun showNextQuiz(){
        quizIndex.value = index + 1
        index++
//        quizIndex.value!!.plus(1)
    }

//    fun getVersion() = versionsRepository.checkVersion()
}