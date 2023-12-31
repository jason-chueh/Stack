package com.example.stack.home.template

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.R
import com.example.stack.data.dataclass.Template
import com.example.stack.databinding.DialogDiscardTemplateBinding
import com.example.stack.databinding.DialogTemplateBinding
import com.example.stack.databinding.FragmentTemplateBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TemplateFragment : Fragment() {
    lateinit var binding: FragmentTemplateBinding
    val viewModel: TemplateViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTemplateBinding.inflate(inflater, container, false)

        val templateOnClick: (Template) -> Unit = {
            Log.i("template", "$it")
            showDialog(it)
        }

        val templateOnLongClick: (Int, Template) -> Unit = { templatePosition, template ->
            showDiscardTemplateDialog(templatePosition, template)
        }

        val adapter = TemplateListAdapter(templateOnClick, templateOnLongClick)

        binding.templateRecyclerView.adapter = adapter

        viewModel.searchTemplateByUserId()

        viewModel.templateList.observe(viewLifecycleOwner) {
            Log.i("template", "$it")
            adapter.submitList(it)
        }

        return binding.root
    }

    private fun showDialog(template: Template) {
        val dialog = Dialog(this.requireContext())
        val dialogBinding: DialogTemplateBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_template,
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

        viewModel.searchAllTemplateExerciseByTemplateId(template.templateId)

        dialogBinding.templateTitleText2.text = template.templateName

        val dialogAdapter = TemplateDetailAdapter()

        dialogBinding.templateDetailRecyclerView.adapter = dialogAdapter

        viewModel.templateExerciseList.observe(viewLifecycleOwner) {
            dialogAdapter.submitList(it)
            Log.i("template", "$it")
        }

        dialogBinding.startWorkoutText.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToWorkoutFragment(viewModel.templateExerciseList.value?.toTypedArray()))
            dialog.dismiss()
        }

        dialogBinding.dialogConstraint.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.cancelButtonDialog.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showDiscardTemplateDialog(templatePosition: Int, template: Template) {

        val dialog = Dialog(this.requireContext())

        val dialogBinding: DialogDiscardTemplateBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_discard_template,
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

        dialogBinding.root.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.templateTitle = template.templateName
        dialogBinding.continueText.setOnClickListener {
            viewModel.deleteTemplate(template, templatePosition)
            dialog.dismiss()
        }
    }
}