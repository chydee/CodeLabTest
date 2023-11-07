package com.desmond.codelab.view.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.desmond.codelab.R
import com.desmond.codelab.core.SettingsContext
import com.desmond.codelab.databinding.FragmentProfileBinding
import com.desmond.codelab.view.vm.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class FragmentProfile : Fragment() {

    @Inject
    lateinit var settingsContext: SettingsContext
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by activityViewModels<MainViewModel>()

    private var takePictureLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let { handleImage(it) }
        }
    }
    private var pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            if (selectedImageUri != null) {
                handleImage(selectedImageUri)
            }
        }
    }
    private var imageUri: Uri? = null
    private var currentPhotoPath: String? = null

    private val cameraPermission = Manifest.permission.CAMERA
    private val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private var cameraPermissionGranted = false
    private var storagePermissionGranted = false

    private val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraPermissionGranted = ContextCompat.checkSelfPermission(requireContext(), cameraPermission) == PackageManager.PERMISSION_GRANTED
        storagePermissionGranted = ContextCompat.checkSelfPermission(requireContext(), storagePermission) == PackageManager.PERMISSION_GRANTED

        showUserData()

        binding.btnTakePicture.setOnClickListener {
            showProfilePicsOptions()
        }

        binding.btnLogout.setOnClickListener {
            clearProfilePicture()
            val isCleared = settingsContext.clearAllStoredData()
            if (isCleared) {
                findNavController().navigate(R.id.action_fragmentProfile_to_fragmentLogin)
            } else {
                Toast.makeText(requireContext(), "Error: Unable to clear user data!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUserData() {
        Glide.with(this).load(settingsContext.avatar).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).apply(requestOptions).into(binding.userPicture)

        binding.apply {
            userFullName.text = "${settingsContext.firstName} ${settingsContext.lastName}"
            userEmail.text = settingsContext.userEmail
            userGender.text = settingsContext.gender
        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(requireActivity(), "com.desmond.codelab.fileprovider", imageFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            takePictureLauncher.launch(intent)
        }
    }


    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun handleImage(uri: Uri) {
        // Implement this function to save the image and set it as the profile picture
        // Example: you can use Glide or Picasso to display the image in an ImageView
        Glide.with(this).load(uri).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).apply(requestOptions).into(binding.userPicture)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun clearProfilePicture() {
        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (currentPhotoPath != null) {
            val profilePictureFile = File(currentPhotoPath!!)

            if (profilePictureFile.exists()) {
                val deleted = profilePictureFile.delete()
                if (deleted) {
                    // The profile picture has been successfully deleted
                    // You can update your UI to show the default profile picture
                    Toast.makeText(requireContext(), "Image File deleted!", Toast.LENGTH_SHORT).show()
                } else {
                    // Failed to delete the profile picture, handle the error
                    Toast.makeText(requireContext(), "Error: Unable to delete Image file!", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Profile picture does not exist
                // Handle the case where there was no profile picture to delete
            }
        }
    }

    private fun showProfilePicsOptions() {
        MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.alert_title)).setMessage(resources.getString(R.string.supporting_text)).setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }.setNegativeButton(resources.getString(R.string.gallery)) { dialog, which ->
            pickImage()
        }.setPositiveButton(resources.getString(R.string.take_photo)) { dialog, which ->
            dispatchTakePictureIntent()
        }.show()
    }

}