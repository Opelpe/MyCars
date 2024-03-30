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
import com.pepe.mycars.app.data.model.HistoryItemModel
import com.pepe.mycars.app.utils.displayToast
import com.pepe.mycars.app.utils.state.view.RefillItemViewState
import com.pepe.mycars.app.viewmodel.RefillDialogViewModel
import com.pepe.mycars.app.viewmodel.RefillOperations
import com.pepe.mycars.databinding.DialogRefillBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class RefillDialog : DialogFragment() {

    private lateinit var binding: DialogRefillBinding

    private val refillDialogViewModel: RefillDialogViewModel by activityViewModels()

    private val calendar: Calendar = Calendar.getInstance()

    private var dialogMode = DialogMode.ADD

    private var editItemID = ""


    companion object {
        fun newInstance(dialogMode: DialogMode, itemId: String) = RefillDialog().apply {
            arguments = Bundle().apply {
                putInt("dialogMode", dialogMode.value)
                putString("itemId", itemId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogRefillBinding.inflate(layoutInflater, container, false)
        dialogMode = DialogMode.fromInt(arguments?.getInt("dialogMode") ?: 1)
        editItemID = arguments?.getString("itemId") ?: ""
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog ?: return
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
        observeState()
        bindEditText()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveRefillButton.setOnClickListener {
            when (dialogMode) {
                DialogMode.ADD -> saveNewRefill()
                DialogMode.DETAILS -> enableEditMode()
                DialogMode.EDIT -> if (editItemID.isNotEmpty()) updateRefillItem()
            }
        }
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.refillDateContainer.setOnClickListener { showDatePicker() }
        binding.refillDateInput.setOnClickListener { showDatePicker() }
    }

    private fun bindEditText() {
        if (dialogMode == DialogMode.ADD && editItemID.isEmpty()) {
            setAddModeView()
        } else {
            setDetailsModeView(editItemID)
        }
    }

    private fun enableEditMode() {
        dialogMode = DialogMode.EDIT
        binding.refillDialogTitle.text = "EDIT REFILL"
        binding.saveRefillText.text = "SAVE"
        setEditTextsEnabled(true)
    }

    private fun setAddModeView() {
        setDateEditText()
        binding.refillDialogTitle.text = "ADD REFILL"
        binding.currentMileageInput.setText("")
        binding.refillAmountInput.setText("")
        binding.priceOfFuelInput.setText("")
        binding.refillNotesInput.setText("")
        binding.saveRefillText.text = "SAVE"
        setIsTouchable(false)
    }

    private fun setDetailsModeView(editItemID: String) {
        refillDialogViewModel.getItemById(editItemID)
    }

    private fun setRefillDetails(item: HistoryItemModel) {
        binding.refillDialogTitle.text = "REFILL DETAILS"
        binding.refillDateInput.setText(item.refillDate)
        binding.currentMileageInput.setText(item.currMileage.toString())
        binding.refillAmountInput.setText(item.fuelAmount.toString())
        binding.priceOfFuelInput.setText(item.fuelPrice.toString())
        binding.refillNotesInput.setText(item.notes)
        binding.fullRefillCheckBox.isChecked = item.fullTank
        binding.saveRefillText.text = "EDIT"
        setEditTextsEnabled(false)
    }

    private fun saveNewRefill() {
        val date = binding.refillDateInput.text.toString()
        val currMileage = binding.currentMileageInput.text.toString()
        val refillAmount = binding.refillAmountInput.text.toString()
        val fuelPrice = binding.priceOfFuelInput.text.toString()
        val notes = binding.refillNotesInput.text.toString()
        val fullTank = binding.fullRefillCheckBox.isChecked

        refillDialogViewModel.addRefill(currMileage, refillAmount, fuelPrice, date, notes, fullTank)
    }

    private fun updateRefillItem() {
        val date = binding.refillDateInput.text.toString()
        val currMileage = binding.currentMileageInput.text.toString()
        val refillAmount = binding.refillAmountInput.text.toString()
        val fuelPrice = binding.priceOfFuelInput.text.toString()
        val notes = binding.refillNotesInput.text.toString()
        val fullTank = binding.fullRefillCheckBox.isChecked
        refillDialogViewModel.updateHistoryItem(editItemID, currMileage, refillAmount, fuelPrice, date, notes, fullTank)
    }

    private fun observeState() {
        refillDialogViewModel.dialogStartEnd()
        refillDialogViewModel.refillItemViewState.observe(viewLifecycleOwner) { viewState ->
            when (viewState) {
                RefillItemViewState.Loading -> setProgressVisibility(true)
                is RefillItemViewState.Error -> {
                    setProgressVisibility(false)
                    if (viewState.errorMsg.isNotEmpty()) {
                        requireActivity().displayToast(viewState.errorMsg)
                    }
                }

                is RefillItemViewState.Success -> {

                    if (viewState.item != null) {
                        if (dialogMode == DialogMode.DETAILS) {
                            setRefillDetails(viewState.item)
                        }
                        if (dialogMode == DialogMode.EDIT) {
                            setRefillDetails(viewState.item)
                            enableEditMode()
                        }
                    }

                    if (viewState.operations == RefillOperations.ADDED || viewState.operations == RefillOperations.UPDATED) {
                        refillDialogViewModel.dialogStartEnd()
                        dismiss()
                    }

                    if (viewState.successMsg.isNotEmpty()) {
                        requireActivity().displayToast(viewState.successMsg)
                    }

                    setProgressVisibility(false)
                }
            }
        }

    }

    private fun setEditTextsEnabled(isInEditMode: Boolean) {
        binding.refillDateInput.isEnabled = isInEditMode
        binding.currentMileageInput.isEnabled = isInEditMode
        binding.refillAmountInput.isEnabled = isInEditMode
        binding.priceOfFuelInput.isEnabled = isInEditMode
        binding.refillNotesInput.isEnabled = isInEditMode
        binding.fullRefillCheckBox.isEnabled = isInEditMode
    }

    private fun setProgressVisibility(loading: Boolean) {
        if (loading) {
            setIsTouchable(false)
            binding.progressView.visibility = View.VISIBLE
        } else {
            binding.progressView.visibility = View.GONE
            setIsTouchable(true)
        }
    }

    private fun setIsTouchable(isTouchable: Boolean) {
        if (isTouchable) {
            dialog!!.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        } else {
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
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

enum class DialogMode(val value: Int) {
    ADD(1),
    DETAILS(2),
    EDIT(3);

    companion object {
        fun fromInt(value: Int) = DialogMode.values().first { it.value == value }
    }
}