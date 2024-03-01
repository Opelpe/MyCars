package com.pepe.mycars.app.ui.view.dialog

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.HistoryItemViewState
import com.pepe.mycars.app.utils.state.view.MainViewState
import com.pepe.mycars.app.viewmodel.HistoryOperations
import com.pepe.mycars.app.viewmodel.RefillDialogViewModel
import com.pepe.mycars.databinding.DialogRefillBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class RefillDialog : DialogFragment() {

    private lateinit var binding: DialogRefillBinding

    private val refillDialogViewModel: RefillDialogViewModel by activityViewModels()

    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DialogRefillBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog ?: return
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
        observeState()
        setDateEditText()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveRefillButton.setOnClickListener { saveNewRefill() }
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.refillDateContainer.setOnClickListener { showDatePicker() }
        binding.refillDateInput.setOnClickListener { showDatePicker() }
    }

    private fun saveNewRefill() {
        val date = binding.refillDateInput.text.toString()
        val currMileage = binding.currentMileageInput.text.toString()
        val refillAmount = binding.refillAmountInput.text.toString()
        val fuelPrice = binding.priceOfFuelInput.text.toString()
        val notes = binding.refillNotesInput.text.toString()

        refillDialogViewModel.addRefill(currMileage, refillAmount, fuelPrice, date, notes)
    }

    private fun observeState() {

        refillDialogViewModel.getHistoryItemViewState().observe(viewLifecycleOwner) { state->
            handleHistoryItemViewState(state)
        }
        refillDialogViewModel.getMainViewState().observe(viewLifecycleOwner) { state ->
            handleMainViewState(state)
        }
    }

    private fun handleHistoryItemViewState(
        historyItemViewState: HistoryItemViewState
    ) {
        when (historyItemViewState) {
            HistoryItemViewState.Loading -> setProgressVisibility(true)
            is HistoryItemViewState.Error -> {
                if (historyItemViewState.errorMsg.isNotBlank()) {
                    requireActivity().displayToast(historyItemViewState.errorMsg)
                }
                setProgressVisibility(false)
            }

            is HistoryItemViewState.Success -> {
                if (historyItemViewState.successMsg.isNotBlank()) {
                    requireActivity().displayToast(historyItemViewState.successMsg)
                }

                if (historyItemViewState.historyOperations == HistoryOperations.ADDED) {
                    dialog?.dismiss()
                    refillDialogViewModel.refreshRefillList()
                }
                setProgressVisibility(false)
            }
        }

    }

    private fun handleMainViewState(mainViewState: MainViewState) {
        when (mainViewState) {
            MainViewState.Loading -> setProgressVisibility(true)
            is MainViewState.Error -> {
                if (mainViewState.errorMsg.isNotBlank()) {
                    requireActivity().displayToast(mainViewState.errorMsg)
                }
                setProgressVisibility(false)
            }

            is MainViewState.Success -> {
                if (mainViewState.successMsg.isNotBlank()) {
                    requireActivity().displayToast(mainViewState.successMsg)
                }
                setProgressVisibility(false)
            }
        }
    }

    private fun setProgressVisibility(loading: Boolean) {
        if (loading) {
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            binding.progressView.visibility = View.VISIBLE
        } else {
            binding.progressView.visibility = View.GONE
            dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    private fun setDateEditText() {
        binding.refillDateInput.setText(getCurrentDate())

    }

    private fun setDateEditText(selectedDate: String) {
        binding.refillDateInput.setText(selectedDate)
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(), { dp: DatePicker?, i: Int, i1: Int, i2: Int ->
                val month = i1 + 1
                val date = formatDate(i, month, i2)
                setDateEditText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun getCurrentDate(): String {
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val month: Int = calendar.get(Calendar.MONTH) + 1
        val year: Int = calendar.get(Calendar.YEAR)

        return formatDate(year, month, day)
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

}