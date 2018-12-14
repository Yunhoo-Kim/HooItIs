package com.hooitis.hoo.hooitis.ui

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.base.BaseFragment
import com.hooitis.hoo.hooitis.databinding.FragmentResultBinding
import com.hooitis.hoo.hooitis.di.ViewModelFactory
import com.hooitis.hoo.hooitis.vm.MainVM
import javax.inject.Inject

class QuizResultFragment: BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: MainVM
    private lateinit var binding: FragmentResultBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        MobileAds.initialize(this.activity, getString(R.string.admob))
        val adRequest = AdRequest.Builder().build()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_result, container, false)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(MainVM::class.java)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(activity)


        binding.resultList.apply {
            layoutManager = GridLayoutManager(context, 3)
        }

        viewModel.quizResultListAdapter.updateResultList(viewModel.quizResultList)
        binding.adView.loadAd(adRequest)
        return binding.root
    }

    companion object {
        fun newInstance(args: Bundle?): QuizResultFragment{
            return QuizResultFragment().apply {
                this.arguments = args
            }
        }
    }

}
