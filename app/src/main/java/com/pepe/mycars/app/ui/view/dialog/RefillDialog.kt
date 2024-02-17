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
import com.pepe.mycars.app.viewmodel.HistoryOperations
import com.pepe.mycars.app.viewmodel.HistoryViewViewModel
import com.pepe.mycars.app.viewmodel.MainViewViewModel
import com.pepe.mycars.databinding.DialogRefillBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RefillDialog : DialogFragment() {

    private lateinit var binding: DialogRefillBinding

    private val historyViewViewModel: HistoryViewViewModel by activityViewModels()
    private val mainViewViewModel: MainViewViewModel by activityViewModels()

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
        historyViewViewModel.startDataSync()
        observeItemState()
        setDateEditText()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveRefillButton.setOnClickListener { saveNewRefill() }
        binding.cancelButton.setOnClickListener {
            dismiss()
            historyViewViewModel.getListOfRefills()
            mainViewViewModel.getListOfRefills()
        }
        binding.refillDateContainer.setOnClickListener { showDatePicker() }
        binding.refillDateInput.setOnClickListener { showDatePicker() }
    }

    private fun saveNewRefill() {
        val date = binding.refillDateInput.getText().toString()
        val currMileage = binding.currentMileageInput.getText().toString()
        val refillAmount = binding.refillAmountInput.getText().toString()
        val fuelPrice = binding.priceOfFuelInput.getText().toString()
        val notes = binding.refillNotesInput.getText().toString()

        historyViewViewModel.addRefill(currMileage, refillAmount, fuelPrice, date, notes)
        observeItemState()
    }

    private fun observeItemState() {
        historyViewViewModel.historyItemViewState.observe(this) {
            when (it) {
                HistoryItemViewState.Loading -> setProgressVisibility(true)
                is HistoryItemViewState.Error -> {
                    if (it.errorMsg.isNotBlank()) {
                        requireActivity().displayToast(it.errorMsg)
                    }
                    setProgressVisibility(false)
                }

                is HistoryItemViewState.Success -> {
                    if (it.successMsg.isNotBlank()) {
                        requireActivity().displayToast(it.successMsg)
                    }

                    if (it.historyOperations == HistoryOperations.ADDED) {
                        dialog!!.dismiss()
                        historyViewViewModel.getListOfRefills()
                        mainViewViewModel.getListOfRefills()
                    }

                    setProgressVisibility(false)
                }
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
        val stringBuilder = StringBuilder()
        if (day != 0 && month != 0 && year != 0) {
            if (day < 10 && month < 10) {
                stringBuilder.append(0).append(day).append("/").append(0).append(month).append("/")
                    .append(year)
                return stringBuilder.toString()
            } else {
                if (month < 10) {
                    stringBuilder.append(day).append("/").append(0).append(month).append("/")
                        .append(year)
                    return stringBuilder.toString()
                }
                if (day < 10) {
                    stringBuilder.append(0).append(day).append("/").append(month).append("/")
                        .append(year)
                    return stringBuilder.toString()
                }
            }
        }
        stringBuilder.append(0).append(0).append("/").append(0).append(0).append("/").append(year)
        return stringBuilder.toString()
    }

}