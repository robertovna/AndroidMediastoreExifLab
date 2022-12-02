package com.example.androidmediastoreexif

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androidmediastoreexif.databinding.FragmentEditorBinding
import java.io.IOException

class EditorFragment : Fragment() {
    private var _binding: FragmentEditorBinding? = null
    private val binding get() = _binding!!

    private val navigationArgs: ViewImageFragmentArgs by navArgs()
    private var _imgUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _imgUri = Uri.parse(navigationArgs.photoUri)

        binding.saveAction.setOnClickListener {
            saveTags()
            val action = EditorFragmentDirections.actionEditorFragmentToViewImageFragment(
                _imgUri.toString()
            )
            findNavController().navigate(action)
        }
        bindTags()
    }

    private fun saveTags() {
        if (_imgUri != null) {
            requireContext().contentResolver.openFileDescriptor(_imgUri!!, "rw")?.use { fd ->
                val exif = ExifInterface(fd.fileDescriptor)
                if (!binding.itemDate.text.isNullOrBlank() && binding.itemDate.text.toString() != "null") {
                    exif.setAttribute(ExifInterface.TAG_DATETIME, binding.itemDate.text.toString())
                }
                if (!binding.itemLatitude.text.isNullOrBlank() && binding.itemLatitude.text.toString() != "null") {
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, binding.itemLatitude.text.toString())
                }
                if (!binding.itemLongitude.text.isNullOrBlank() && binding.itemLongitude.text.toString() != "null") {
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, binding.itemLongitude.text.toString())
                }
                if (!binding.itemDevice.text.isNullOrBlank() && binding.itemDevice.text.toString() != "null") {
                    exif.setAttribute(ExifInterface.TAG_MAKE, binding.itemDevice.text.toString())
                }
                if (!binding.itemModel.text.isNullOrBlank() && binding.itemModel.text.toString() != "null") {
                    exif.setAttribute(ExifInterface.TAG_MODEL, binding.itemModel.text.toString())
                }
                exif.saveAttributes()
            }
        }
    }

    private fun bindTags() {
        if (_imgUri != null) {
            try {
                requireContext().contentResolver.openInputStream(_imgUri!!)?.use {
                    val exif = ExifInterface(it)
                    binding.apply {
                        itemDate.setText(exif.getAttribute(ExifInterface.TAG_DATETIME).toString())
                        itemLatitude.setText(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE).toString())
                        itemLongitude.setText(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE).toString())
                        itemDevice.setText(exif.getAttribute(ExifInterface.TAG_MAKE).toString())
                        itemModel.setText(exif.getAttribute(ExifInterface.TAG_MODEL).toString())
                    }
                }
            } catch (e: IOException) { }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}