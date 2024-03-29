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
import com.pepe.mycars.app.utils.state.view.AddItemViewState
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

    private var itemEditMode = false

    private var editItemID = ""


    companion object {
        fun newInstance(editMode: Boolean, itemId: String): RefillDialog {
            val dialog = RefillDialog()
            val args = Bundle()
            args.putBoolean("editMode", editMode)
            args.putString("itemId", itemId)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogRefillBinding.inflate(layoutInflater, container, false)
        itemEditMode = arguments?.getBoolean("editMode") ?: false
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
            if (itemEditMode && editItemID.isNotEmpty()) {
                updateRefillItem()
            } else {
                saveNewRefill()
            }
        }
        binding.cancelButton.setOnClickListener { dismiss() }
        binding.refillDateContainer.setOnClickListener { showDatePicker() }
        binding.refillDateInput.setOnClickListener { showDatePicker() }
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

    private fun bindEditText() {
        if (itemEditMode && editItemID.isNotEmpty()) {
            setEditModeView(editItemID)
        } else {
            setAddModeView()
        }
    }

    private fun setAddModeView() {
        setDateEditText()
        binding.currentMileageInput.setText("")
        binding.refillAmountInput.setText("")
        binding.priceOfFuelInput.setText("")
        binding.refillNotesInput.setText("")
        binding.saveRefillText.text = "SAVE"
        setIsTouchable(false)
    }

    private fun setEditModeView(editItemID: String) {
//        val itemModel = refillDialogViewModel.getItemDataById(editItemID)
//        binding.refillDateInput.setText(itemModel.refillDate)
//        binding.currentMileageInput.setText(itemModel.currMileage.toString())
//        binding.refillAmountInput.setText(itemModel.fuelAmount.toString())
//        binding.priceOfFuelInput.setText(itemModel.fuelPrice.toString())
//        binding.refillNotesInput.setText(itemModel.notes)
//        binding.saveButtonTitle.setText("EDIT")
//        setIsTouchable(false)
//        binding.fullRefillCheckBox.isChecked = itemModel.
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
        refillDialogViewModel.dialogStart(itemEditMode)
        refillDialogViewModel.addingItemViewState.observe(viewLifecycleOwner){viewState ->
            when(viewState){
                AddItemViewState.Loading -> setProgressVisibility(true)
                is AddItemViewState.Error -> {
                    setProgressVisibility(false)
                    if (viewState.errorMsg.isNotEmpty()){
                        requireActivity().displayToast(viewState.errorMsg)
                    }
                }
                is AddItemViewState.Success -> {
                    if (viewState.successMsg.isNotEmpty()){
                        requireActivity().displayToast(viewState.successMsg)
                    }
                    refillDialogViewModel.dialogEnd()
                    dismiss()
                }
            }
        }

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