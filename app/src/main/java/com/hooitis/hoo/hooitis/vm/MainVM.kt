package com.hooitis.hoo.hooitis.vm

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.hooitis.hoo.hooitis.base.BaseViewModel
import com.hooitis.hoo.hooitis.model.SharedPreferenceHelper
import com.hooitis.hoo.hooitis.model.quiz.Quiz
import com.hooitis.hoo.hooitis.model.quiz.QuizRepository
import com.hooitis.hoo.hooitis.ui.QuizImageListAdapter
import com.hooitis.hoo.hooitis.ui.QuizResultListAdapter
import com.hooitis.hoo.hooitis.utils.COUNTDOWN
import com.hooitis.hoo.hooitis.utils.KEYBOARD_COUNTDOWN
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


@Suppress("unused")
class MainVM @Inject constructor(
        private val quizRepository: QuizRepository,
        val sharedPreferenceHelper: SharedPreferenceHelper
): BaseViewModel() {

    private var index = 0
    private lateinit var timer: Timer
    private lateinit var quizList: List<Quiz>
    var quizResultList: MutableList<Quiz>
    private var playerList: MutableList<Boolean>

    private lateinit var countDownValue: String

    private val numOfPlayer: Int by lazy {
        sharedPreferenceHelper.getInt(SharedPreferenceHelper.KEY.PLAYERS)
    }

    val wrong: MutableLiveData<Boolean> = MutableLiveData()
    val pTurn: MutableLiveData<Int> = MutableLiveData()
    val quizIndex: MutableLiveData<Int> = MutableLiveData()
    val countDown: MutableLiveData<String> = MutableLiveData()

    val quizImageListAdapter: QuizImageListAdapter by lazy {
        QuizImageListAdapter()
    }

    val quizResultListAdapter: QuizResultListAdapter by lazy {
        QuizResultListAdapter()
    }

    init {
        wrong.value = false
        index = 0
        pTurn.value = 0
        quizIndex.value = index
        countDownValue = COUNTDOWN
        playerList = mutableListOf()
        quizResultList = mutableListOf()

        for (i in 0 until numOfPlayer){
           playerList.add(false)
        }

    }

    fun setKeyboardCountDown(){
        countDownValue = KEYBOARD_COUNTDOWN
    }

    fun loadQuizData(){
        countDown.value = countDownValue
        quizList = quizRepository.getQuizzes().blockingFirst().shuffled()
        quizImageListAdapter.updateQuizList(quizList)
        resetTimer()
    }

    private fun getNextTurn(): Int{
        var turn = -1
        val pt = pTurn.value!!

        for(i in (pt+1)..(playerList.size + pt)){
            if(!playerList[i % playerList.size]) {
                turn = i
                pTurn.postValue(i % playerList.size)
                break
            }
        }

        return turn
    }

    private fun resetTimer() {
        countDown.postValue(countDownValue)

        if(::timer.isInitialized) {
            timer.cancel()
            timer.purge()
        }

        timer = Timer("Count", false)
        timer.schedule(1000, 1000){

            try {
                val cnt = countDown.value!!.toInt() - 1
                if(cnt < 0){
                    playerList[pTurn.value!!] = true
                    val quiz = quizList[index]
                    quizResultList.add(quiz)
                    showNextQuiz()
                }else {
                    countDown.postValue(cnt.toString())
                }
            }catch(e: NumberFormatException){
                wrong.postValue(true)
                cancel()
            }
        }
    }

    private fun showNextQuiz(){
        if(wrong.value!!)
            return

        val next = getNextTurn()
        if(next < 0){
            timer.purge()
            timer.cancel()
            wrong.postValue(true)
        }else {
            quizIndex.postValue(index + 1)
            index++
            resetTimer()
        }
    }

    fun checkResult(results: List<String>): Boolean{
        var found = false

        val quiz = quizList[index]
        quiz.answerList.forEachIndexed { _, value ->
            Log.d("Quiz Answer", value)
            if(results.contains(value)){
                found = true
                return@forEachIndexed
            }
        }
        if (found){
            wrong.value = false
        }
        else{
            playerList[pTurn.value!!] = true
            quizResultList.add(quiz)
        }

        showNextQuiz()
        return found
    }

    fun checkResult(result: String): Boolean{
        var found = false

        val quiz = quizList[index]
        quiz.answerList.forEachIndexed { _, value ->
            if(value.contains(result)){
                found = true
                return@forEachIndexed
            }
        }

        if (found){
            wrong.value = false
        }
        else{
            playerList[pTurn.value!!] = true
            quizResultList.add(quiz)
        }

        showNextQuiz()
        return found
    }

}