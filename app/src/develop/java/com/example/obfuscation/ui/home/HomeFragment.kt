package com.example.obfuscation.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.obfuscation.R
import com.example.obfuscation.custom.CustomFragment
import com.example.obfuscation.custom.createViewModelFactory
import com.example.obfuscation.databinding.FragmentHomeBinding
import com.example.obfuscation.decrypt

class HomeFragment : CustomFragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val text = inflater.context.decrypt(R.string.fragment_home_title)
        val homeViewModel: HomeViewModel by viewModels {
            createViewModelFactory {
                HomeViewModel(text)
            }
        }
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}