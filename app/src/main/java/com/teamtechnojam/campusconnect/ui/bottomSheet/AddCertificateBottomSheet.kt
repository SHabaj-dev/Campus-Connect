package com.teamtechnojam.campusconnect.ui.bottomSheet

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teamtechnojam.campusconnect.databinding.BottomSheedAddCertificationsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddCertificateBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheedAddCertificationsBinding? = null
    private val binding get() = _binding
    private val calendar: Calendar
        get() = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheedAddCertificationsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.etIssueDate?.setOnClickListener {
            showDatePickerDialog(binding!!.etIssueDate)
        }

        binding?.etExpirationDate?.setOnClickListener {
            showDatePickerDialog(binding!!.etExpirationDate)
        }

    }

    private fun showDatePickerDialog(dateView: EditText) {
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            OnDateSetListener { view, selectedYear, selectedMonth, selectedDayOfMonth ->
                calendar.set(selectedYear, selectedMonth, selectedDayOfMonth)

                // Format the selected date and set it in the EditText
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateView.setText(sdf.format(calendar.getTime()))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}