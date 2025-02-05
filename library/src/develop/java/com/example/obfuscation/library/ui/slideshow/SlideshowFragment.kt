package com.example.obfuscation.library.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.obfuscation.library.R
import com.example.obfuscation.library.custom.CustomFragment
import com.example.obfuscation.library.custom.createViewModel
import com.example.obfuscation.library.databinding.FragmentSlideshowBinding
import com.example.obfuscation.library.decrypt

class SlideshowFragment : CustomFragment() {

    private var _binding: FragmentSlideshowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val text = inflater.context.decrypt(R.string.fragment_slideshow_title)
        val slideshowViewModel = this.createViewModel {
            SlideshowViewModel(text)
        }
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        slideshowViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}