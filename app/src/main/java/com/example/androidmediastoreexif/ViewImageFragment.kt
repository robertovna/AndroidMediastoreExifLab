package com.example.androidmediastoreexif

import android.app.Activity
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidmediastoreexif.databinding.FragmentViewImageBinding
import java.io.IOException

class ViewImageFragment : Fragment() {
    companion object {
        const val FILE_CHOOSED = 1313
    }
    private var _binding: FragmentViewImageBinding? = null
    private val binding get() = _binding!!
    private var _imageUri: Uri? = null

    private val navigationArgs: EditorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!navigationArgs.photoUri.isNullOrBlank() &&  navigationArgs.photoUri != "null") {
            val uri = Uri.parse(navigationArgs.photoUri)
            _imageUri = uri
            drawImage(uri)
        }
        binding.chooseImage.setOnClickListener {
            val imageGalleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            imageGalleryIntent.type = "image/*"
            startActivityForResult(imageGalleryIntent, FILE_CHOOSED)
        }
        binding.editImage.setOnClickListener {
            val action = ViewImageFragmentDirections.actionViewImageFragmentToEditorFragment(
                _imageUri.toString()
            )
            findNavController().navigate(action)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSED && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                _imageUri = uri
                drawImage(uri)
            }
        }
    }

    private fun drawImage(photoUri: Uri) {
        var creationDate = ""
        var latitude = ""
        var longitude = ""
        var device = ""
        var model = ""
        binding.imageFromGallery.setImageURI(photoUri)
        try {
            requireContext().contentResolver.openInputStream(photoUri)?.use {
                val exif = ExifInterface(it)
                creationDate = exif.getAttribute(ExifInterface.TAG_DATETIME).toString()
                latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE).toString()
                longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE).toString()
                device = exif.getAttribute(ExifInterface.TAG_MAKE).toString()
                model = exif.getAttribute(ExifInterface.TAG_MODEL).toString()
            }
        } catch (e: IOException) { }

        var text = "Date: $creationDate\nLatitude: $latitude\nLongtitude: $longitude\n"
        text += "Device: $device\nModel: $model\n"
        binding.tags.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}