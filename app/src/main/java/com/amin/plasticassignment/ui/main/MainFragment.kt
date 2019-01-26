package com.amin.plasticassignment.ui.main


import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.amin.plasticassignment.R
import com.amin.plasticassignment.api.WebServicesVolley
import com.amin.plasticassignment.model.TimeResponseModel
import com.amin.plasticassignment.utils.Constants.BASE_URL
import com.github.florent37.viewanimator.ViewAnimator
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*


class MainFragment : Fragment(), View.OnTouchListener {


    private lateinit var rootView: View

    private lateinit var rotate: RotateAnimation
    private lateinit var root: ViewGroup
    private lateinit var timeTextView: TextView
    private lateinit var grayArea: View
    private val api = WebServicesVolley()

    private var xDelta: Int = 0
    private var yDelta: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        timeTextView = rootView.findViewById(R.id.timeTv)
        grayArea = rootView.findViewById(R.id.grayArea)
        root = rootView.findViewById(R.id.root)
        return rootView
    }

    @Suppress("UNUSED_VARIABLE")
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTimer()


        setSquareIntPosition()

        startAnimation()

        //region Drag Solution for ConstraintLayout, which will
        val listener = View.OnTouchListener(function = { touchView, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_MOVE) {

                touchView.y = motionEvent.rawY - touchView.height / 2
                touchView.x = motionEvent.rawX - touchView.width / 2
            }
            true
        })
        //endregion

        val listener2 = View.OnTouchListener(function = { touchView, motionEvent ->

            coordinates.text = "move x = ${motionEvent.rawX} \nmove y = ${motionEvent.rawY} " +
                    "\nX: ${timeTextView.x} \nY: ${timeTextView.y} \n" +
                    "Y gray: ${grayArea.y} "
            if (motionEvent != null && touchView != null) {
                val x = motionEvent.rawX.toInt()
                val y = motionEvent.rawY.toInt()
                when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        val lParams = touchView.layoutParams as RelativeLayout.LayoutParams
                        xDelta = x - lParams.leftMargin
                        yDelta = y - lParams.topMargin
                        ViewAnimator.animate(grayArea).translationY(-dpToPx(170f))
                            .duration(200).decelerate().start()
                    }
                    MotionEvent.ACTION_UP -> {
                        if (grayArea.y - timeTextView.y > dpToPx(80f)) {
                            setSquareIntPosition()
                            ViewAnimator.animate(grayArea).translationY(0f).dp()
                                .duration(200).decelerate().start()
                        } else {
                            setSquareInGrayArea()
                        }
                    }
                    MotionEvent.ACTION_POINTER_DOWN -> {
                    }
                    MotionEvent.ACTION_POINTER_UP -> {
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val layoutParams = touchView.layoutParams as RelativeLayout.LayoutParams
                        layoutParams.leftMargin = x - xDelta
                        layoutParams.topMargin = y - yDelta
                        layoutParams.rightMargin = -250
                        layoutParams.bottomMargin = -250
                        touchView.layoutParams = layoutParams
                    }
                }
                root.invalidate()
            }
            true
        })
        timeTextView.setOnTouchListener(listener2)
    }

    private fun setSquareIntPosition() {
        val layoutParams = timeTextView.layoutParams as RelativeLayout.LayoutParams
        val margin = getInitialMargin()
        layoutParams.leftMargin = margin.toInt()
        layoutParams.rightMargin = margin.toInt()
        layoutParams.topMargin = dpToPx(100f).toInt()
        timeTextView.layoutParams = layoutParams
    }

    private fun setSquareInGrayArea() {
        val layoutParams = timeTextView.layoutParams as RelativeLayout.LayoutParams
        val margin = getInitialMargin()
        layoutParams.leftMargin = margin.toInt()
        layoutParams.rightMargin = margin.toInt()
        layoutParams.topMargin = (grayArea.y + ((grayArea.height - dpToPx(100f)) / 2)).toInt()
        timeTextView.layoutParams = layoutParams
    }

    private fun getInitialMargin(): Float {
        val display = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        val width = size.x
        return (width - dpToPx(120f)) / 2
    }

    private fun startAnimation() {

//        val rotation = AnimationUtils.loadAnimation(activity, R.anim.rotate);
//        rotation.fillAfter = true;
//        timeTextView.startAnimation(rotation);

        rotate = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.interpolator = LinearInterpolator()

        rotate.duration = 2000
        rotate.repeatCount = Animation.INFINITE
        rotate.fillAfter = true
        timeTextView.startAnimation(rotate)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {

        if (event != null && view != null) {
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    val lParams = view.layoutParams as RelativeLayout.LayoutParams
                    xDelta = x - lParams.leftMargin
                    yDelta = y - lParams.topMargin
                }
                MotionEvent.ACTION_UP -> {
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                }
                MotionEvent.ACTION_POINTER_UP -> {
                }
                MotionEvent.ACTION_MOVE -> {
                    val layoutParams = view.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.leftMargin = x - xDelta
                    layoutParams.topMargin = y - yDelta
                    layoutParams.rightMargin = -250
                    layoutParams.bottomMargin = -250
                    view.layoutParams = layoutParams
                }
            }
            root.invalidate()
        }
        return true
    }

    private fun startTimer() {
        val t = Timer()
        t.scheduleAtFixedRate(
            object : TimerTask() {

                @SuppressLint("CheckResult")
                override fun run() {

                    //region Retrofit 2
                    //                    WebServices.create().getCurrentTime().subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe({ model: TimeResponseModel? ->
//                            timeTextView.text = model?.getCurrentTime()
//                        },{ _ ->
//                            timeTextView.text = getString(R.string.time_error)
//                        })
                    //endregion

                    api.getCurrentTime(BASE_URL, object : WebServicesVolley.NetworkListeners {
                        override fun onResponse(model: TimeResponseModel) {
                            timeTextView.text = model.getCurrentTime()
                        }

                        override fun onError(message: String) {
                            timeTextView.text = message
                        }
                    })
                }

            },
            500,
            1
        )
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

}
