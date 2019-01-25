package com.amin.plasticassignment.ui.main


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.amin.plasticassignment.R
import com.amin.plasticassignment.api.WebServices
import com.amin.plasticassignment.model.TimeResponseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class MainFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var timeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        timeTextView = rootView.findViewById(com.amin.plasticassignment.R.id.timeTv)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTimer()
    }

    private fun startTimer() {
        val t = Timer()
        t.scheduleAtFixedRate(
            object : TimerTask() {

                @SuppressLint("CheckResult")
                override fun run() {
                    WebServices.create().getCurrentTime().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ model: TimeResponseModel? ->
                            timeTextView.text = model?.getCurrentTime()
                        },{ _ ->
                            timeTextView.text = getString(R.string.time_error)
                        })
                }

            },
            500,
            1
        )
    }


}
