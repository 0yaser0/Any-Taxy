package com.cmc.mytaxi.ui.fragments.profile

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.DatePickerDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cmc.mytaxi.App
import com.cmc.mytaxi.R
import com.cmc.mytaxi.data.local.models.Driver
import com.cmc.mytaxi.data.repository.DriverRepository
import com.cmc.mytaxi.data.viewmodel.ProfileViewModel
import com.cmc.mytaxi.data.viewmodel.ProfileViewModelFactory
import com.cmc.mytaxi.databinding.ProfileFragmentLayoutBinding
import com.cmc.mytaxi.ui.activity.HomePage
import com.cmc.mytaxi.utils.SetupUI
import java.util.Calendar

class ProfileFragment : Fragment(R.layout.profile_fragment_layout) {

    private var _binding: ProfileFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var driverViewModel: ProfileViewModel
    private var ageCalculated: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ProfileFragmentLayoutBinding.bind(view)

        val driverRepository = DriverRepository(App.database.driverDao())
        val factory = ProfileViewModelFactory(driverRepository)
        hideSystemUIFragments()

        driverViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        Glide.with(this)
            .load(R.drawable.mini_red_taxy)
            .into(binding.miniRedTaxy)

        handleErrorsCalendar()

        buildProfile()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideSystemUIFragments() {
        activity?.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun buildProfile(){
        binding.btnBuildProfile.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val permiType = binding.etPermiType.text.toString()

            val driver = Driver(
                driverId = 1,
                firstName = firstName,
                lastName = lastName,
                age = ageCalculated,
                permiType = permiType,
                isCreated = true
            )
            driverViewModel.addDriver(driver)

            val intent = Intent(requireContext(), HomePage::class.java)
            startActivity(intent)

        }
    }

    private fun handleErrorsCalendar() {
        val calendarEditText = binding.etCalendar

        calendarEditText.setOnClickListener {
            showDatePickerDialog()
            SetupUI.setupUI(binding.root)
        }

        calendarEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {

                val input = calendarEditText.text.toString()
                val regex = Regex("^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-[0-9]{4}\$")
                val dayMonthYear = input.split("-")

                if (dayMonthYear.size == 3) {
                    val day = dayMonthYear[0].toIntOrNull() ?: 0
                    val month = dayMonthYear[1].toIntOrNull() ?: 0
                    val year = dayMonthYear[2].toIntOrNull() ?: 0

                    if (!input.matches(regex)) {
                        calendarEditText.error =
                            "Invalid date format. Please use DD-MM-YYYY."
                    } else if (day !in 1..31) {
                        calendarEditText.error =
                            "Invalid date. Day must be between 1 and 31."
                    } else if (month !in 1..12) {
                        calendarEditText.error =
                            "Invalid date. Month must be between 1 and 12."
                    } else if (year < 1900 || year > 2100) {
                        calendarEditText.error =
                            "Invalid year. Year must be between 1900 and 2100."
                    } else {
                        if (isFutureDate(day, month, year)) {
                            calendarEditText.error =
                                "Invalid date. You cannot select a future date."
                        } else {
                            calendarEditText.error = null
                        }
                    }
                } else {
                    calendarEditText.error = "Invalid date format. Please use DD-MM-YYYY."
                }
            }
        }
    }

    private fun isFutureDate(day: Int, month: Int, year: Int): Boolean {
        val today = Calendar.getInstance()
        val inputDate = Calendar.getInstance().apply {
            set(year, month - 1, day)
        }
        return inputDate.after(today)
    }

    private fun calculateAge(day: Int, month: Int, year: Int): Int {
        val today = Calendar.getInstance()
        val birthDate = Calendar.getInstance().apply {
            set(year, month - 1, day)
        }

        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }


    private fun showDatePickerDialog() {
        val calendarEditText = binding.etCalendar
        val datePickerDialog = android.app.DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _, year, month, day ->
                val selectedDate = "$day-${month + 1}-$year"
                calendarEditText.setText(selectedDate)

                ageCalculated = calculateAge(day, month + 1, year)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        datePickerDialog.window?.setGravity(Gravity.CENTER)

        datePickerDialog.setOnShowListener {
            val positiveButton = datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            val negativeButton = datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)

            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkGreen))
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.deathRed))
        }

        datePickerDialog.show()
    }


}


