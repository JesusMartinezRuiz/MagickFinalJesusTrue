package com.example.magikfinaljesus.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.magickfinaljesus.EiActivity
import com.example.magickfinaljesus.R
import com.example.magickfinaljesus.databinding.FragmentSlideshowBinding

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentSlideshowBinding? = null
    lateinit var menu: Menu
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    val ma by lazy{
        activity as EiActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onStart() {
        super.onStart()
        ma.FAB_manager(0)

        binding.rvPedidos.apply {
            adapter= ma.adaptadorPedidos
            layoutManager= LinearLayoutManager(ma)
            setHasFixedSize(true)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}