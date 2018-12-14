package com.hooitis.hoo.hooitis.ui

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.databinding.ItemResultBinding
import com.hooitis.hoo.hooitis.model.quiz.Quiz
import com.hooitis.hoo.hooitis.vm.QuizItemVM


class QuizResultListAdapter: RecyclerView.Adapter<QuizResultListAdapter.ViewHolder>() {
    private lateinit var quizList: List<Quiz>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemResultBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_result,
                parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(quizList[position])
    }

    fun updateResultList(quizList: List<Quiz>){
        this.quizList = quizList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if(::quizList.isInitialized) quizList.size else 0

    class ViewHolder(private val binding: ItemResultBinding): RecyclerView.ViewHolder(binding.root){
        private val quizVM = QuizItemVM()

        fun bind(quiz: Quiz){
            quizVM.bind(quiz)
            binding.viewModel = quizVM
        }
    }
}
