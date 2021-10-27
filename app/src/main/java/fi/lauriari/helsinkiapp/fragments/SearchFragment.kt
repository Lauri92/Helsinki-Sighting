package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.MainActivity
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ItemsAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.databinding.FragmentSearchBinding
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.forEachIndexed

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val apiViewModel: HelsinkiApiViewModel by viewModels()
    private val itemsAdapter: ItemsAdapter by lazy { ItemsAdapter("searchFragment") }
    private lateinit var layoutManager: LinearLayoutManager
    private var spinnerValue = "Activities"
    private var firstLoad: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        val view = binding.root
        initializeComponents(binding)
        initNavigation()
        initSearchViewQueryTextListener()
        setButtons()
        setSpinner()
        binding.progressBar.visibility = View.GONE
        return view
    }

    private fun initSearchViewQueryTextListener() {
        binding.searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query ?: return false
                getItems(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query ?: return false
                if (query == "") {
                    binding.recyclerview.visibility = View.GONE
                    layoutManager.scrollToPositionWithOffset(0, 0)
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

    private fun initializeComponents(binding: FragmentSearchBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = apiViewModel
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = itemsAdapter

        if (firstLoad) {
            binding.recyclerview.visibility = View.GONE
            binding.buttonsContainer.visibility = View.VISIBLE
            firstLoad = false
        } else {
            binding.recyclerview.invalidate()
            binding.recyclerview.visibility = View.VISIBLE
            binding.buttonsContainer.invalidate()
            binding.buttonsContainer.visibility = View.GONE
        }
    }

    private fun getItems(text: String) {

        hideKeyboard()
        binding.progressBar.visibility = View.VISIBLE
        when (spinnerValue) {
            "Activities" -> {
                handleActivityRequest(text)
            }
            "Places" -> {
                handlePlacesRequest(text)
            }
            "Events" -> {
                handleEventsRequest(text)
            }
        }
    }

    private fun hideKeyboard() {
        val imm =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setButtons() {
        val activitiesList = listOf(
            "Sauna",
            "Boating",
            "Ecological",
            "National park",
            "Food",
            "Archipelago",
            "History",
            "Music",
            "Trips",
            "Sea"
        )

        val placesList = listOf(
            "Rock",
            "Billiards",
            "Auditorium",
            "Luxury",
            "StreetStyle",
            "NordicSwan",
            "Hotel",
            "EcoShop",
            "FreeCatering",
            "Vegetarian",
        )

        val eventsList = listOf(
            "fairy tales",
            "babies",
            "lectures",
            "celebrations",
            "pupils",
            "ice dancing",
            "young adults",
            "rainbows",
            "history",
            "well-being",
        )
        when (spinnerValue) {
            "Activities" -> {
                binding.buttonsContainer.forEachIndexed { index, view ->
                    setSingleButtonTextAndListener(view as Button, activitiesList[index])
                }
            }
            "Places" -> {
                binding.buttonsContainer.forEachIndexed { index, view ->
                    setSingleButtonTextAndListener(view as Button, placesList[index])
                }
            }
            "Events" -> {
                binding.buttonsContainer.forEachIndexed { index, view ->
                    setSingleButtonTextAndListener(view as Button, eventsList[index])
                }
            }
        }
    }

    private fun handleActivityRequest(text: String) {
        val adapterList = mutableListOf<SingleHelsinkiItem>()
        lifecycleScope.launch(Dispatchers.IO) {
            val getItems = async {
                apiViewModel.getActivities(text, "en")
            }
            getItems.await().let {
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
                                    whereWhenDuration = info.where_when_duration,
                                    itemType = info.source_type.name
                                )
                            )
                        }
                    }
                    createList.await()
                    activity?.runOnUiThread {
                        itemsAdapter.setData(adapterList)
                        binding.progressBar.visibility = View.GONE
                        binding.buttonsContainer.visibility = View.GONE
                        binding.recyclerview.visibility = View.VISIBLE
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Nothing was found!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerview.visibility = View.GONE
                        binding.buttonsContainer.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun handlePlacesRequest(text: String) {
        val adapterList = mutableListOf<SingleHelsinkiItem>()
        lifecycleScope.launch(Dispatchers.IO) {
            val getItems = async {
                apiViewModel.getPlaces(text, "en")
            }
            getItems.await().let {
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
                                    openingHours = info.opening_hours,
                                    itemType = info.source_type.name
                                )
                            )
                        }
                    }
                    createList.await()
                    activity?.runOnUiThread {
                        itemsAdapter.setData(adapterList)
                        binding.progressBar.visibility = View.GONE
                        binding.buttonsContainer.visibility = View.GONE
                        binding.recyclerview.visibility = View.VISIBLE
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Nothing was found!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerview.visibility = View.GONE
                        binding.buttonsContainer.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun handleEventsRequest(text: String) {
        val adapterList = mutableListOf<SingleHelsinkiItem>()
        lifecycleScope.launch(Dispatchers.IO) {
            val getItems = async {
                apiViewModel.getEvents(text, "en")
            }
            getItems.await().let {
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
                                    eventDates = info.event_dates,
                                    itemType = info.source_type.name
                                )
                            )
                        }
                    }
                    createList.await()
                    activity?.runOnUiThread {
                        itemsAdapter.setData(adapterList)
                        binding.progressBar.visibility = View.GONE
                        binding.buttonsContainer.visibility = View.GONE
                        binding.recyclerview.visibility = View.VISIBLE
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Nothing was found!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerview.visibility = View.GONE
                        binding.buttonsContainer.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setSingleButtonTextAndListener(button: Button, textValue: String) {
        val textLowerCase = textValue.lowercase()
        val textCapitalize = textValue.replaceFirstChar { it.uppercase() }
        button.text = textLowerCase
        when (spinnerValue) {
            "Activities" -> {
                button.setOnClickListener {
                    binding.searchview.setQuery(textLowerCase, false)
                    getItems(textLowerCase)
                }
            }
            "Places" -> {
                button.setOnClickListener {
                    binding.searchview.setQuery(textCapitalize, false)
                    getItems(textCapitalize)
                }
            }
            "Events" -> {
                button.setOnClickListener {
                    binding.searchview.setQuery(textLowerCase, false)
                    getItems(textLowerCase)
                }
            }
        }
    }

    private fun setSpinner() {
        val spinnerArray = listOf("Activities", "Places", "Events")

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            spinnerArray
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.itemtypeSpinner.adapter = adapter
            binding.itemtypeSpinner.onItemSelectedListener = ItemsSpinner()
        }
    }

    inner class ItemsSpinner : Fragment(), AdapterView.OnItemSelectedListener {

        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            if (view != null) {
                when (parent.getItemAtPosition(pos).toString()) {
                    "Activities" -> {
                        Log.d("response", "Activities spinner")
                        spinnerValue = "Activities"
                        setButtons()
                    }
                    "Places" -> {
                        Log.d("response", "Places spinner")
                        spinnerValue = "Places"
                        setButtons()
                    }
                    "Events" -> {
                        Log.d("response", "Events spinner")
                        spinnerValue = "Events"
                        setButtons()
                    }
                }
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
            TODO("Not yet implemented")
        }

        override fun onDestroyView() {
            Log.d("destroy", "onDestroyView called")
            super.onDestroyView()
        }
    }
}