package com.example.androidmediastoreexif

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.androidmediastoreexif.databinding.FragmentViewImageBinding

class ViewImageFragment : Fragment() {
    companion object {
        const val FILE_CHOOSED = 1313
    }
    private var _binding: FragmentViewImageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chooseImage.setOnClickListener {
            val imageGalleryIntent = Intent(Intent.ACTION_PICK)
            imageGalleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageGalleryIntent, FILE_CHOOSED)
        }
        binding.editImage.setOnClickListener {
            val action = ViewImageFragmentDirections.actionViewImageFragmentToEditorFragment()
            findNavController().navigate(action)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_CHOOSED && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                binding.imageFromGallery.setImageURI(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}