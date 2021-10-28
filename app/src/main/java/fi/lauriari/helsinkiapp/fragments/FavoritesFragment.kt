package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.MainActivity
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ItemsAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.databinding.FragmentFavoritesBinding
import fi.lauriari.helsinkiapp.viewmodels.FavoriteViewModel


class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val favoritesViewModel: FavoriteViewModel by viewModels()
    private val itemsAdapter: ItemsAdapter by lazy { ItemsAdapter("favoritesFragment") }
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorites, container, false)
        val view = binding.root
        initComponents()
        initNavigation()
        setButtonOnClickListeners()
        setObservers()
        return view
    }

    private fun setButtonOnClickListeners() {
        binding.activitesBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.viewmodel?.getFavoriteActivities()
        }

        binding.placesBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.viewmodel?.getFavoritePlaces()
        }

        binding.eventsBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.viewmodel?.getFavoriteEvents()
        }
    }

    private fun setObservers() {
        binding.viewmodel?.favoriteActivities?.observe(viewLifecycleOwner, { favoriteActivities ->
            handleLiveDataChange(favoriteActivities, "activities")
        })

        binding.viewmodel?.favoritePlaces?.observe(viewLifecycleOwner, { favoritePlaces ->
            handleLiveDataChange(favoritePlaces, "places")
        })

        binding.viewmodel?.favoriteEvents?.observe(viewLifecycleOwner, { favoriteEvents ->
            handleLiveDataChange(favoriteEvents, "events")
        })

    }

    private fun handleLiveDataChange(favorites: MutableList<SingleHelsinkiItem>, type: String) {
        if (favorites.isNotEmpty()) {
            itemsAdapter.setData(favorites)
        } else {
            Toast.makeText(requireContext(), "No $type set as favorite!", Toast.LENGTH_SHORT)
                .show()
        }
        binding.progressBar.visibility = View.GONE
    }

    private fun initComponents() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = favoritesViewModel
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = itemsAdapter
        val dividerItemDecoration = DividerItemDecoration(
            context,
            layoutManager.orientation
        )
        binding.recyclerview.addItemDecoration(dividerItemDecoration)
    }

    private fun initNavigation() {
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
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