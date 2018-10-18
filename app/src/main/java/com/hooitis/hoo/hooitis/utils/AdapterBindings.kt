package com.hooitis.hoo.hooitis.utils

import android.arch.lifecycle.MutableLiveData
import android.databinding.BindingAdapter
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.hooitis.hoo.hooitis.R
import com.hooitis.hoo.hooitis.utils.extension.getParentActivity


@BindingAdapter("adapter")
@Suppress("unused")
fun setAdapter(view: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    view.adapter = adapter
}

@BindingAdapter("itemPosition")
@Suppress("unused")
fun setItemPosition(view: RecyclerView, position: MutableLiveData<Int>?){
    Log.d("Speech", position!!.value.toString())

    if(position!!.value == 0) {
        view.smoothScrollToPosition(0)
        return
    }

    view.smoothScrollToPosition(position.value!!)
}

@BindingAdapter("android:text")
@Suppress("unused")
fun setText(view: TextView, data: MutableLiveData<Float>?){
    if (data != null)
        view.text = data.value.toString()
}

@BindingAdapter("imageRes")
@Suppress("unused")
fun setImageResource(view: ImageView, image_id: MutableLiveData<Int>?){
    if(image_id!!.value == 0){
        view.setImageResource(R.drawable.microphone)
        return
    }
    view.setImageResource(image_id.value!!)
}

@BindingAdapter("imageResUri")
@Suppress("unused")
fun setImageResourceByURI(view: ImageView, image_id: MutableLiveData<String>?){
    if(image_id!!.value.isNullOrEmpty()) {
        view.setImageResource(R.drawable.microphone)
        return
    }

    Glide.with(view.getParentActivity()!!)
            .load(FirebaseStorage.getInstance().reference.child(image_id.value!!))
//            .apply(RequestOptions.)
            .into(view)
}

//@Suppress("unused")
//@BindingAdapter("tabadapter")
//fun setTabAdapter(view: ViewPager, adapter: FragmentPagerAdapter) {
//    view.adapter = adapter
//}


