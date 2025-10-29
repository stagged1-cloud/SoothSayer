package com.soothsayer.predictor.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soothsayer.predictor.databinding.FragmentAboutBinding

/**
 * About screen showing app info, disclaimer, author, and links
 */
class AboutFragment : Fragment() {
    
    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupLinks()
    }
    
    private fun setupLinks() {
        binding.githubButton.setOnClickListener {
            openUrl("https://github.com/stagged1-cloud/SoothSayer")
        }
        
        binding.websiteButton.setOnClickListener {
            openUrl("https://stagged1-cloud.github.io/SoothSayer/")
        }
    }
    
    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error silently - browser not available
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
