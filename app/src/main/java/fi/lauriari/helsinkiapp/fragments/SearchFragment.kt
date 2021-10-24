package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.MainActivity
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.databinding.FragmentSearchBinding


class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val view = binding.root
        binding.lifecycleOwner = viewLifecycleOwner
        initNavigation()
        initSearchViewQueryTextListener()




        return view
    }

    private fun initSearchViewQueryTextListener() {
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("submit", "Textsubmit: $query")
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("submit", "Textchange: $query")
                return true
            }
        })

    }

    private fun initNavigation() {
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.near_me -> {
                        findNavController().navigate(R.id.action_searchFragment_to_browseFragment)
                    }
                    R.id.search -> {
                        Toast.makeText(requireContext(), "You are here", Toast.LENGTH_SHORT).show()
                    }
                    R.id.favorites -> {
                        findNavController().navigate(R.id.action_searchFragment_to_favoritesFragment)
                    }

                }
                true

            }
    }
}