package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.MainActivity
import fi.lauriari.helsinkiapp.R


class FavoritesFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        initNavigation()

        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    private fun initNavigation() {
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.near_me -> {
                        findNavController().navigate(R.id.action_favoritesFragment_to_browseFragment)
                    }
                    R.id.search -> {
                        findNavController().navigate(R.id.action_favoritesFragment_to_searchFragment)
                    }
                    R.id.favorites -> {
                        Toast.makeText(requireContext(), "You are here", Toast.LENGTH_SHORT).show()
                    }

                }
                true

            }
    }
}