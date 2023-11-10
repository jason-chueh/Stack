package com.example.stack.user


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stack.R
import com.example.stack.data.dataclass.Workout
import com.example.stack.databinding.DialogExerciseStatsBinding
import com.example.stack.databinding.FragmentUserBinding
import com.example.stack.util.capitalizeFirstLetterOfWords
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val MAXIMUM_WEIGHT = "maximum weight"


@AndroidEntryPoint
class UserFragment : Fragment() {
    lateinit var binding: FragmentUserBinding
    val viewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater, container, false)


        viewModel.getUserExerciseData()
        viewModel.getUserWorkoutData()



        viewModel.userExerciseRecords.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                val entryList = viewModel.getExerciseEntry()
                entryList?.let { setBarChartData(it.sortedBy { it.x }) }
                viewModel.sumUpExerciseRecords()
//                setExerciseLineChartData(viewModel.maximumWeightExerciseData("barbell bench press"))
                binding.exerciseLineChart.invalidate()
            }
        }
        viewModel.weightSum.observe(viewLifecycleOwner) {
            binding.tonLifted.text = it?.toString()
        }
        viewModel.userWorkoutRecords.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.workoutCompletedNum.text = it.size.toString()
                binding.avgWorkoutTime.text = calculateAverageWorkoutTime(it)
            }
        }
        binding.filterImage.setOnClickListener {
            showFilterDialog(viewModel.getUniqueExerciseList())
        }
        return binding.root
    }

    private fun setBarChartData(entries: List<BarEntry>) {
        Log.i("bar chart", "$entries")

        val barDataSet = BarDataSet(entries, "Label for Bars") // Set a label for your dataset
        val dataSets = listOf(barDataSet)
        barDataSet.barBorderWidth =5f
        barDataSet.barBorderColor = ContextCompat.getColor(requireContext(), R.color.primaryColor)
        val barChart = binding.lineChart
        barChart.data = BarData(dataSets)

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = XAxisValueFormatter()
        xAxis.isEnabled = true

        barChart.axisLeft.isEnabled = true
        barChart.axisRight.isEnabled = true
        barChart.setScaleEnabled(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.setDrawMarkers(false)
        barChart.legend.isEnabled = false

        // Remove the description label
        barChart.description.isEnabled = false

        // Customize bar appearance as needed
        barDataSet.color = ContextCompat.getColor(requireContext(), R.color.primaryColor)

        // Animate the chart if needed
        barChart.animateXY(0, 3000)
    }

    private fun setExerciseLineChartData(entries: List<Entry>) {
        val lineDataSet = LineDataSet(entries, "")
        val dataSets = listOf(lineDataSet)
        val lineChart = binding.exerciseLineChart

        // Set the ValueFormatter for the x-axis
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = XAxisValueFormatter()

        lineChart.data = LineData(dataSets)
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillDrawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.blue_gradient)
        lineDataSet.lineWidth = 3f
        lineDataSet.circleColors =
            listOf(ContextCompat.getColor(requireContext(), R.color.primaryColor))
        lineChart.xAxis.isEnabled = true
        lineChart.axisLeft.isEnabled = true
        lineChart.axisRight.isEnabled = true
        lineChart.setScaleEnabled(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.setDrawMarkers(false)
        lineChart.legend.isEnabled = false
        lineChart.description.text = ""

    }

    private fun calculateAverageWorkoutTime(workouts: List<Workout>): String {
        if (workouts.isEmpty()) {
            return "0"
        }
        val totalDurationMillis = workouts.sumOf { it.endTime.toInt() - it.startTime.toInt() }

        val averageDurationMillis = totalDurationMillis / workouts.size

        val averageDurationMinutes =
            averageDurationMillis / (60 * 1000) // Convert milliseconds to minutes
        return "$averageDurationMinutes"
    }

    private fun showFilterDialog(exerciseNameList: List<String>?) {
        Log.i("filter", "$exerciseNameList")
        val dialog = Dialog(this.requireContext())
        val dialogBinding: DialogExerciseStatsBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_exercise_stats,
            null,
            false
        )
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)

        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.lifecycleOwner = viewLifecycleOwner

        dialogBinding.cancelImage.setOnClickListener {
            dialog.dismiss()
        }
        if (exerciseNameList != null) {
            val sortedList = exerciseNameList.sortedBy { it.length }
            for (exerciseName in sortedList) {
                val chip = layoutInflater.inflate(R.layout.customize_chip_layout, null) as Chip
                chip.id = View.generateViewId()
                chip.text = exerciseName
                dialogBinding.exerciseGroup.addView(chip)
            }
        }

        for (statsTypeName in listOf("maximum weight", "training volume")) {
            val chip = layoutInflater.inflate(R.layout.customize_chip_layout, null) as Chip
            chip.id = View.generateViewId()
            chip.text = statsTypeName
            dialogBinding.statsTypeGroup.addView(chip)
        }

        dialogBinding.applyButton.setOnClickListener {
            val selectedChipId = dialogBinding.exerciseGroup.checkedChipId
            val selectedStatsChipId = dialogBinding.statsTypeGroup.checkedChipId
            if (selectedChipId != View.NO_ID && selectedStatsChipId != View.NO_ID) {
                val selectedExerciseChip =
                    dialogBinding.exerciseGroup.findViewById<Chip>(selectedChipId)
                val selectedExerciseName = selectedExerciseChip.text.toString()

                val selectedStatsChip =
                    dialogBinding.statsTypeGroup.findViewById<Chip>(selectedStatsChipId)
                val selectedStatsName = selectedStatsChip.text.toString()
                val filteredData: List<Entry> = if (selectedStatsName == MAXIMUM_WEIGHT) {
                    viewModel.maximumWeightExerciseData(selectedExerciseName)
                } else {
                    viewModel.trainingVolumeExerciseData(selectedExerciseName)
                }
                setExerciseLineChartData(filteredData)
                binding.exerciseLineChart.invalidate()
                val titleString = "$selectedExerciseName $selectedStatsName"
                binding.exerciseTitle.text = titleString.capitalizeFirstLetterOfWords()
                dialog.dismiss()
            }
        }
    }
}

class XAxisValueFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        val timestamp = value.toLong()
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
        return sdf.format(date)
    }
}