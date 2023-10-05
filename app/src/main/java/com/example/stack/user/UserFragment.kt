package com.example.stack.user


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.stack.R
import com.example.stack.data.dataclass.Workout

import com.example.stack.databinding.FragmentHomeBinding
import com.example.stack.databinding.FragmentUserBinding
import com.example.stack.home.HomeViewModel
import com.example.stack.login.UserManager
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserFragment: Fragment() {
    lateinit var binding: FragmentUserBinding
    val viewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)

//        binding.logout.setOnClickListener {
//            UserManager.clear()
//        }
        viewModel.getUserExerciseData()
        viewModel.getUserWorkoutData()


        val dataList = listOf(Entry(1f,2f),Entry(3f,4f),Entry(5f,6f))
//        setLineChartData(dataList)

        viewModel.userExerciseRecords.observe(viewLifecycleOwner){
            if(it!=null){
                val entryList = viewModel.getExerciseEntry()
                entryList?.let{setLineChartData(it.sortedBy { it.x })}
                viewModel.sumUpExerciseRecords()
            }
        }
        viewModel.weightSum.observe(viewLifecycleOwner){
            binding.tonLifted.text = it?.toString()
        }
        viewModel.userWorkoutRecords.observe(viewLifecycleOwner){
            if(it!=null){
                binding.workoutCompletedNum.text  = it.size.toString()
                binding.avgWorkoutTime.text = calculateAverageWorkoutTime(it)
            }
        }
        return binding.root
    }

    private fun setLineChartData(entries: List<Entry>) {

        Log.i("line chart", "$entries")

        //feed data
        val lineDataSet = LineDataSet(entries, "")
        val dataSets = listOf(lineDataSet)
        binding.lineChart.data = LineData(dataSets)


        //set line chart style
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.blue_gradient)
        lineDataSet.lineWidth = 3f
        lineDataSet.circleColors =
            listOf(ContextCompat.getColor(requireContext(), R.color.darkestBlue))

        binding.lineChart.xAxis.isEnabled = true
        binding.lineChart.axisLeft.isEnabled = true
        binding.lineChart.axisRight.isEnabled = true
        binding.lineChart.setScaleEnabled(false)
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.setDrawMarkers(false)
        binding.lineChart.legend.isEnabled = false

//        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.darkestBlue)
        binding.lineChart.animateXY(0, 3000)
    }

    fun calculateAverageWorkoutTime(workouts: List<Workout>): String {
        if (workouts.isEmpty()) {
            return "No data"
        }
        val totalDurationMillis = workouts.sumBy { it.endTime.toInt() - it.startTime.toInt() }

        val averageDurationMillis = totalDurationMillis / workouts.size

        val averageDurationMinutes = averageDurationMillis / (60 * 1000) // Convert milliseconds to minutes
        return "$averageDurationMinutes"
    }
}
