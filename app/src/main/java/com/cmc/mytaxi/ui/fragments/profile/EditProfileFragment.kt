package com.cmc.mytaxi.ui.fragments.profile

import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.cmc.mytaxi.databinding.FragmentEditProfileBinding
import com.cmc.mytaxi.utils.SetupUI
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.util.Calendar


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var driverViewModel: ProfileViewModel
    private lateinit var PersonalInfos: String
    private var isEditing = false
    private var ageCalculated: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideSystemUIFragments()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditProfileBinding.bind(view)

        val driverRepository = DriverRepository(App.database.driverDao())
        val factory = ProfileViewModelFactory(driverRepository)
        driverViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        binding.editDriverDetails.setOnClickListener {
            if (isEditing) {
                saveDriverDetails()
            } else {
                enableEditing()
                Toast.makeText(
                    requireContext(), "Edit Driver mode enabled", Toast.LENGTH_SHORT
                ).show()
            }
            isEditing = !isEditing
            Toast.makeText(
                requireContext(), "Profile Updated", Toast.LENGTH_SHORT
            ).show()
        }

        driverViewModel.getDriverById(1).observe(viewLifecycleOwner) { driver ->
            driver?.let {
                displayDriverDetails(it)
            }
        }

    }

    private fun enableEditing() {
        binding.fname.visibility = View.GONE
        binding.fNameEdit.visibility = View.VISIBLE

        binding.lname.visibility = View.GONE
        binding.lNameEdit.visibility = View.VISIBLE

        binding.age.visibility = View.GONE
        binding.ageEdit.visibility = View.VISIBLE

        binding.permiType.visibility = View.GONE
        binding.permiTypeEdit.visibility = View.VISIBLE

        binding.fNameEdit.setText(binding.fname.text)
        binding.lNameEdit.setText(binding.lname.text)
        binding.ageEdit.setText(binding.age.text)
        binding.permiTypeEdit.setText(binding.permiType.text)

        handleErrorsCalendar()

    }

    private fun saveDriverDetails() {
        val fname = binding.fNameEdit.text.toString().trim()
        val lname = binding.lNameEdit.text.toString().trim()
        val age = binding.ageEdit.text.toString().trim()
        val permiType = binding.permiTypeEdit.text.toString().trim()

        val lettersOnlyPattern = "^[A-Za-z]+$"
        val lettersRequiredPattern = ".*[A-Za-z].*"
        var userState = 0

        if (fname.isEmpty()) {
            binding.fNameEdit.error = "First name cannot be empty"
        }
        if (!fname.matches(lettersOnlyPattern.toRegex())) {
            binding.fNameEdit.error = "First name must contain only letters"
        }
        if (lname.isEmpty()) {
            binding.lNameEdit.error = "Last name cannot be empty"
        }
        if (!lname.matches(lettersOnlyPattern.toRegex())) {
            binding.lNameEdit.error = "Last name must contain only letters"
        }
        if (permiType.isEmpty()) {
            binding.permiTypeEdit.error = "Permi type cannot be empty"
        }
        if (!permiType.matches(lettersRequiredPattern.toRegex())) {
            binding.permiTypeEdit.error = "Permi type must contain at least one letter"
        }
        if (fname.matches(lettersOnlyPattern.toRegex()) && lname.matches(lettersOnlyPattern.toRegex()) && permiType.matches(lettersRequiredPattern.toRegex())
        ) {
            userState = 1
        } else {
            Toast.makeText(
                requireContext(), "Please fill all the fields correctly", Toast.LENGTH_SHORT
            ).show()
        }

        if (userState == 1) {
            val driver = Driver(
                firstName = binding.fNameEdit.text.toString(),
                lastName = binding.lNameEdit.text.toString(),
                age = ageCalculated,
                permiType = binding.permiTypeEdit.text.toString(),
                isCreated = true
            )
            driverViewModel.updateDriver(driver)
        }


    }



    fun QRCodegenerator(infos: String) {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(infos, BarcodeFormat.QR_CODE, 400, 400)
            binding.qrcode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun displayDriverDetails(driver: Driver) {
        binding.fname.text = driver.firstName
        binding.lname.text = driver.lastName
        binding.age.text = driver.age.toString()
        binding.permiType.text = driver.permiType

        val imageUri = driver.imageUri
        if (!imageUri.isNullOrEmpty()) {
            val uri = Uri.parse(imageUri)
            Glide.with(this@EditProfileFragment)
                .load(uri)
                .placeholder(R.drawable.ic_camera)
                .error(R.drawable.ic_error)
                .into(binding.driverProfil)
        } else {
            binding.driverProfil.setImageResource(R.drawable.first_name)
        }

        PersonalInfos = """
            Nom: ${driver.firstName}
            Prenom: ${driver.lastName}
            Age: ${driver.age}
            Type De Permie: ${driver.permiType}
        """.trimIndent()

        QRCodegenerator(PersonalInfos)
    }

    private fun hideSystemUIFragments() {
        activity?.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun handleErrorsCalendar() {
        val calendarEditText = binding.ageEdit

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
        val calendarEditText = binding.ageEdit
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