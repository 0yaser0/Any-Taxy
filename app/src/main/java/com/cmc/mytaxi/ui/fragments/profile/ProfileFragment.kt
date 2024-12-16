package com.cmc.mytaxi.ui.fragments.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.cmc.mytaxi.App
import com.cmc.mytaxi.R
import com.cmc.mytaxi.data.local.models.Driver
import com.cmc.mytaxi.data.repository.DriverRepository
import com.cmc.mytaxi.data.viewmodel.ProfileViewModel
import com.cmc.mytaxi.data.viewmodel.ProfileViewModelFactory
import com.cmc.mytaxi.databinding.ProfileFragmentLayoutBinding
import com.cmc.mytaxi.ui.activity.HomePage
import com.cmc.mytaxi.utils.AnyTaxyFragments
import com.cmc.mytaxi.utils.SetupUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class ProfileFragment : AnyTaxyFragments() {

    private var _binding: ProfileFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var driverViewModel: ProfileViewModel
    private var ageCalculated: Int = 0
    private val pickImageRequest = 1
    private val cameraRequest = 2
    private var driverProfileImage: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment_layout, container, false)
    }

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

        imageUpload()

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

    private fun buildProfile() {
        binding.btnBuildProfile.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val permiType = binding.etPermiType.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty() || permiType.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (driverProfileImage == null) {
                Toast.makeText(requireContext(), "Please upload a profile image!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loadingDialog = ProgressDialog(requireContext()).apply {
                setMessage("Creating profile, please wait...")
                setCancelable(false)
                show()
            }

            lifecycleScope.launch {
                try {
                    val driver = Driver(
                        driverId = 1,
                        firstName = firstName,
                        lastName = lastName,
                        age = ageCalculated,
                        permiType = permiType,
                        isCreated = true,
                        imageUri = driverProfileImage.toString()
                    )
                    withContext(Dispatchers.IO) {
                        driverViewModel.addDriver(driver)
                    }

                    loadingDialog.dismiss()
                    val intent = Intent(requireContext(), HomePage::class.java)
                    startActivity(intent)
                } catch (e: Exception) {
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), "Error creating profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun imageUpload() {
        binding.camera.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            AlertDialog.Builder(requireContext())
                .setTitle("Select Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> dispatchTakePictureIntent()
                        1 -> dispatchChoosePictureIntent()
                        2 -> {}
                    }
                }
                .show()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, cameraRequest)
        }
    }

    private fun dispatchChoosePictureIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickImageRequest)
    }

    private fun handleImageUpload(imageUri: Uri) {
        try {
            val requestOptions = RequestOptions()
                .transforms(CircleCrop())
                .placeholder(R.drawable.ic_camera)
                .error(R.drawable.ic_error)

            Glide.with(this)
                .load(imageUri)
                .apply(requestOptions)
                .into(binding.camera)

            driverProfileImage = imageUri
        } catch (e: Exception) {
            Log.e("ProfileFragment", "Error loading image: ${e.message}")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                pickImageRequest -> {
                    data.data?.let { uri ->
                        handleImageUpload(uri)
                    }
                }

                cameraRequest -> {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    val uri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            requireContext().contentResolver,
                            imageBitmap,
                            null,
                            null
                        )
                    )
                    handleImageUpload(uri)
                }
            }
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


