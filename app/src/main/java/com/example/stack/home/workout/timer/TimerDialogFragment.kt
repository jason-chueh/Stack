package com.example.stack.home.workout.timer

import android.Manifest
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.stack.R
import com.example.stack.databinding.DialogAddBinding
import com.example.stack.databinding.DialogTimerBinding
import com.example.stack.service.ACTION_START_SERVICE
import com.example.stack.service.TimerService

class TimerDialogFragment : AppCompatDialogFragment(), ServiceConnection {
    lateinit var binding: DialogTimerBinding
    private var mService: TimerService? = null

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as TimerService.LocalBinder

        mService = binder.getService()

        mService?.timeProgress?.observe(viewLifecycleOwner){
            binding.tvTimeLeft.text = (mService?.timeSelected?.value?.minus(it)).toString()
            binding.pbTimer.progress = (mService?.timeSelected?.value?.minus(it))!!
        }
        mService?.timeSelected?.observe(viewLifecycleOwner){
            Log.i("timer","it")
            binding.pbTimer.max = it
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        mService = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.LoginDialog)
        dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, // Set the desired width
        ViewGroup.LayoutParams.WRAP_CONTENT // Set the desired height
        )

        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val permissionState =
            ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.POST_NOTIFICATIONS)
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        if(mService == null){
            sendCommandToService(ACTION_START_SERVICE)
        }

        binding = DialogTimerBinding.inflate(inflater, container, false)
//        sendCommandToService(ACTION_START_SERVICE)
        binding.root.setOnClickListener{
            dialog?.dismiss()
        }
        binding.btnAdd.setOnClickListener {
            setTimeFunction()
        }
        binding.btnPlayPause.setOnClickListener {
            sendCommandToService(ACTION_START_SERVICE)
        }
        binding.ibReset.setOnClickListener {
            resetTime()
        }
        binding.tvAddTime.setOnClickListener {
            addExtraTime()
        }

        return binding.root
    }

    private fun sendCommandToService(action: String, bundle: Bundle? = null, autoStart: Boolean = true)=
        Intent(requireContext(), TimerService::class.java).also{ intent ->
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

    }

    private fun resetTime() {
        if (mService?.timeCountDown != null) {
            mService?.resetTime()
            val startBtn: Button = binding.btnPlayPause
            startBtn.text = getString(R.string.start)

            val progressBar = binding.pbTimer
            progressBar.progress = 0
            val timeLeftTv: TextView = binding.tvTimeLeft
            timeLeftTv.text = "0"
        }
    }

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
//                        progressBar.max = timeSet.text.toString().toInt()
                        sendCommandToService(ACTION_START_SERVICE, Bundle().apply { putInt("timeSelected", timeSet.text.toString().toInt()) }, autoStart = false)
                    }
                    else -> {
                        mService?.resetTime()
                        mService?.timeSelected?.postValue(timeSet.text.toString().toInt())
                        progressBar.max = mService?.timeSelected?.value ?: 0
                    }
                }
                timeLeftTv.text = timeSet.text
                btnStart.text = getString(R.string.start)
            }
            timeDialog.dismiss()
        }
        timeDialog.show()
    }
}
