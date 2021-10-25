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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.MainActivity
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ItemsAdapter
import fi.lauriari.helsinkiapp.adapters.SearchAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.databinding.FragmentSearchBinding
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import android.app.Activity

import android.view.inputmethod.InputMethodManager





class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val apiViewModel: HelsinkiApiViewModel by viewModels()
    private val searchAdapter: ItemsAdapter by lazy { ItemsAdapter("searchFragment") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val view = binding.root
        initializeViewModelRepositoryBinding(binding)
        initNavigation()
        initSearchViewQueryTextListener()


        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.adapter = searchAdapter


        return view
    }

    private fun initSearchViewQueryTextListener() {
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query ?: return false

                val imm =
                    activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view = activity!!.currentFocus
                if (view == null) {
                    view = View(activity)
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)

                val adapterList = mutableListOf<SingleHelsinkiItem>()
                lifecycleScope.launch(Dispatchers.IO) {
                    val getActivities = async {
                        apiViewModel.getActivities(query, "en")
                    }
                    getActivities.await().let {
                        if (it.isSuccessful && it.body()!!.data.isNotEmpty()) {
                            val createList = async {
                                it.body()?.data?.forEach { info ->
                                    adapterList.add(
                                        SingleHelsinkiItem(
                                            id = info.id,
                                            name = info.name.en,
                                            infoUrl = info.info_url,
                                            latitude = info.location.lat,
                                            longitude = info.location.lon,
                                            streetAddress = info.location.address.street_address,
                                            locality = info.location.address.locality,
                                            description = info.description.body,
                                            images = info.description.images,
                                            tags = info.tags,
                                            whereWhenDuration = info.where_when_duration
                                        )
                                    )
                                }
                            }
                            createList.await()
                            activity?.runOnUiThread {
                                searchAdapter.setData(adapterList)
                                binding.recyclerview.visibility = View.VISIBLE
                            }
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    requireContext(),
                                    "Nothing was found!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.recyclerview.visibility = View.GONE
                            }
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query ?: return false

                if (query == "") {
                    binding.recyclerview.visibility = View.GONE
                }

                return true
            }
        })

    }

    private fun initNavigation() {
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            .setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.near_me -> {
                        findNavController().navigate(R.id.action_searchFragment_to_browseFragment)
                    }
                    R.id.search -> {
                        Toast.makeText(requireContext(), "You are here", Toast.LENGTH_SHORT)
                            .show()
                    }
                    R.id.favorites -> {
                        findNavController().navigate(R.id.action_searchFragment_to_favoritesFragment)
                    }

                }
                true

            }
    }

    private fun initializeViewModelRepositoryBinding(binding: FragmentSearchBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = apiViewModel
    }

    override fun onDestroyView() {
        Log.d("destroy", "onDestroyView called")
        super.onDestroyView()
    }
}