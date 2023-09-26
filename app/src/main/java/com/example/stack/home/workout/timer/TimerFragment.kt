package com.example.stack.home.workout.timer

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.example.stack.R
import com.example.stack.databinding.DialogAddBinding
import com.example.stack.databinding.DialogTimerBinding
import com.example.stack.service.ACTION_ADD_TIME
import com.example.stack.service.ACTION_RESET
import com.example.stack.service.ACTION_START_SERVICE
import com.example.stack.service.INT_EXTRA_KEY
import com.example.stack.service.TimerService

class TimerFragment : AppCompatDialogFragment(), ServiceConnection {
    lateinit var binding: DialogTimerBinding
//    private var timeSelected: Int = 0
//    private var timeCountDown: CountDownTimer? = null
//    private var timeProgress = 0
//    private var pauseOffSet: Long = 0
//    private var isStart = true

    var mService: TimerService? = null


    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as TimerService.LocalBinder

        mService = binder.getService()

        mService?.timeProgress?.observe(viewLifecycleOwner){
            binding.tvTimeLeft.text = (mService?.timeSelected?.value?.minus(it)).toString()
            binding.pbTimer.progress = (mService?.timeSelected?.value?.minus(it))!!
        }
        mService?.timeSelected?.observe(viewLifecycleOwner){
            binding.pbTimer.max = it
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {

        mService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTimerBinding.inflate(inflater, container, false)
//        sendCommandToService(ACTION_START_SERVICE)

        binding.btnAdd.setOnClickListener {
            setTimeFunction()
        }
        binding.btnPlayPause.setOnClickListener {
            sendCommandToService(ACTION_START_SERVICE)

//            startTimerSetup()
        }
        binding.ibReset.setOnClickListener {
            resetTime()
        }
        binding.tvAddTime.setOnClickListener {
            addExtraTime()
        }

        return binding.root
    }

    private fun sendCommandToService(action: String, bundle: Bundle? = null, autoStart: Boolean = true)= Intent(requireContext(), TimerService::class.java).also{ intent ->
        intent.action = action
        bundle?.let {
            intent.putExtras(it)
        }

        when(mService) {
            null -> {
                requireContext().startService(intent)
                if(autoStart) requireContext().bindService(intent, this, Context.BIND_AUTO_CREATE)
            } else -> {
                if(autoStart) mService?.startTimerSetup()
            }
        }
    }

    private fun addExtraTime() {
        val progressBar: ProgressBar = binding.pbTimer
        mService?.addExtraTime()
        progressBar.max = mService?.timeSelected?.value ?: 0
//        if (timeSelected != 0) {
//            timeSelected += 15
//            progressBar.max = timeSelected
//            timePause()
//            startTimer(pauseOffSet)
//            Toast.makeText(this.requireContext(), "15 sec added", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun resetTime() {
        if (mService?.timeCountDown != null) {
            mService?.resetTime()
            val startBtn: Button = binding.btnPlayPause
            startBtn.text = "Start"

            val progressBar = binding.pbTimer
            progressBar.progress = 0
            val timeLeftTv: TextView = binding.tvTimeLeft
            timeLeftTv.text = "0"
        }
    }

    private fun timePause() {
            mService?.timePause()
        }
    private fun startTimerSetup() {
        mService?.startTimerSetup()
//        val startBtn: Button = binding.btnPlayPause
//        if (timeSelected > timeProgress) {
//            if (isStart) {
//                startBtn.text = "Pause"
//                startTimer(pauseOffSet)
//                isStart = false
//            } else {
//                isStart = true
//                startBtn.text = "Resume"
//                timePause()
//            }
//        } else {
//            Toast.makeText(this.requireContext(), "Enter Time", Toast.LENGTH_SHORT).show()
//        }
    }

//    private fun startTimer(pauseOffSetL: Long) {
//        val progressBar = binding.pbTimer
//        progressBar.progress = timeProgress
//        timeCountDown = object : CountDownTimer(
//            (timeSelected * 1000).toLong() - pauseOffSetL * 1000, 1000
//        ) {
//            override fun onTick(p0: Long) {
//                timeProgress++
//                pauseOffSet = timeSelected.toLong() - p0 / 1000
//                progressBar.progress = timeSelected - timeProgress
//                val timeLeftTv: TextView = binding.tvTimeLeft
//                timeLeftTv.text = (timeSelected - timeProgress).toString()
//            }
//
//            override fun onFinish() {
//                resetTime()
//                Toast.makeText(this@TimerFragment.requireContext(), "Times Up!", Toast.LENGTH_SHORT).show()
//            }
//
//        }.start()
//    }


    private fun setTimeFunction() {
        val timeDialog = Dialog(this.requireContext())

        val dialogAddBinding = DialogAddBinding.inflate(
            layoutInflater, null, false
        )

        timeDialog.setContentView(dialogAddBinding.root)
        val timeSet = dialogAddBinding.etGetTime
        val timeLeftTv: TextView = binding.tvTimeLeft
        val btnStart: Button = binding.btnPlayPause
        val progressBar = binding.pbTimer
        timeDialog.findViewById<Button>(R.id.btnOk).setOnClickListener {
            if (timeSet.text.isEmpty()) {
                Toast.makeText(this.requireContext(), "Enter Time Duration", Toast.LENGTH_SHORT).show()
            } else {
                when (mService) {
                    null -> {
                        sendCommandToService(ACTION_START_SERVICE, Bundle().apply { putInt("timeSelected", timeSet.text.toString().toInt()) }, autoStart = false)
                    }
                    else -> {
                        mService?.resetTime()
                        mService?.timeSelected?.postValue(timeSet.text.toString().toInt())
                        progressBar.max = mService?.timeSelected?.value ?: 0
                    }
                }
                timeLeftTv.text = timeSet.text
                btnStart.text = "Start"

            }
            timeDialog.dismiss()
        }
        timeDialog.show()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        if (timeCountDown != null) {
//            timeCountDown?.cancel()
//            timeProgress = 0
//        }
//    }


}
