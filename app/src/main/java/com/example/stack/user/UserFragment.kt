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
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.example.stack.databinding.FragmentUserBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val MAXIMUM_WEIGHT = "maximum weight"
const val TRAINING_VOLUME = "training volume"


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

//        binding.logout.setOnClickListener {
//            UserManager.clear()
//        }
        viewModel.getUserExerciseData()
        viewModel.getUserWorkoutData()


        val dataList = listOf(Entry(1f, 2f), Entry(3f, 4f), Entry(5f, 6f))
//        setLineChartData(dataList)

        viewModel.userExerciseRecords.observe(viewLifecycleOwner) { it ->
            if (it != null) {
                val entryList = viewModel.getExerciseEntry()
                entryList?.let { setBarChartData(it.sortedBy { it.x }) }
                viewModel.sumUpExerciseRecords()
                setExerciseLineChartData(viewModel.maximumWeightExerciseData("barbell bench press"))
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

        Log.i("line chart", "$entries")

        //feed data
        val lineDataSet = BarDataSet(entries, "")
        val dataSets = listOf(lineDataSet)
        binding.lineChart.data = BarData(dataSets)
        //set line chart style
//        lineDataSet.setDrawFilled(true)
//        lineDataSet.fillDrawable =
//            ContextCompat.getDrawable(requireContext(), R.drawable.blue_gradient)
//        lineDataSet.lineWidth = 3f
//        lineDataSet.circleColors =
//            listOf(ContextCompat.getColor(requireContext(), R.color.darkestBlue))

        binding.lineChart.xAxis.isEnabled = true
        binding.lineChart.axisLeft.isEnabled = true
        binding.lineChart.axisRight.isEnabled = true
        binding.lineChart.setScaleEnabled(false)
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.setDrawMarkers(false)
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.description.text = ""

//        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.darkestBlue)
        binding.lineChart.animateXY(0, 3000)
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
            listOf(ContextCompat.getColor(requireContext(), R.color.darkestBlue))
        lineChart.xAxis.isEnabled = true
        lineChart.axisLeft.isEnabled = true
        lineChart.axisRight.isEnabled = true
        lineChart.setScaleEnabled(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.setDrawMarkers(false)
        lineChart.legend.isEnabled = false
        lineChart.description.text = ""

    }

    fun calculateAverageWorkoutTime(workouts: List<Workout>): String {
        if (workouts.isEmpty()) {
            return "No data"
        }
        val totalDurationMillis = workouts.sumBy { it.endTime.toInt() - it.startTime.toInt() }

        val averageDurationMillis = totalDurationMillis / workouts.size

        val averageDurationMinutes =
            averageDurationMillis / (60 * 1000) // Convert milliseconds to minutes
        return "$averageDurationMinutes"
    }

    fun showFilterDialog(exerciseNameList: List<String>?) {
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
        dialogBinding.exerciseGroup.setChipSpacingHorizontal(resources.getDimensionPixelSize(R.dimen.chip_spacing))
        if (exerciseNameList != null) {
            val sortedList = exerciseNameList.sortedBy { it.length }
            for (exerciseName in sortedList) {
                // Create a new Chip
                val chip = Chip(this.requireContext()) // "this" should be your activity or context
                // Set Chip attributes
                chip.id = View.generateViewId() // Assign a unique ID to each Chip
                chip.text = exerciseName
                chip.isCheckable = true
                chip.setChipDrawable(
                    ChipDrawable.createFromAttributes(
                        this.requireContext(),
                        null,
                        0,
                        R.style.Widget_Material3_Chip_Filter_Elevated
                    )
                )
                dialogBinding.exerciseGroup.addView(chip)
            }
        }
        dialogBinding.statsTypeGroup.chipSpacingHorizontal
        for (statsTypeName in listOf("maximum weight", "training volume")) {
            val chip = Chip(this.requireContext()) // "this" should be your activity or context
            // Set Chip attributes
            chip.id = View.generateViewId() // Assign a unique ID to each Chip
            chip.text = statsTypeName
            chip.isCheckable = true
            chip.setChipDrawable(
                ChipDrawable.createFromAttributes(
                    this.requireContext(),
                    null,
                    0,
                    R.style.Widget_Material3_Chip_Filter_Elevated
                )
            )
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
                val titleString = "$selectedExerciseName (${selectedStatsName})"
                binding.exerciseTitle.text = titleString
                dialog.dismiss()
            }
        }
    }
}

class XAxisValueFormatter : IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        // Convert the value (timestamp) to the desired format "MM/dd"
        val timestamp = value.toLong()
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("MM/dd", Locale.getDefault())
        return sdf.format(date)
    }
}