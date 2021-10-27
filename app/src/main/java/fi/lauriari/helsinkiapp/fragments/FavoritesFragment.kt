package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import fi.lauriari.helsinkiapp.databinding.FragmentFavoritesBinding
import fi.lauriari.helsinkiapp.datamodels.SingleHelsinkiActivity
import fi.lauriari.helsinkiapp.datamodels.SingleHelsinkiEvent
import fi.lauriari.helsinkiapp.datamodels.SingleHelsinkiPlace
import fi.lauriari.helsinkiapp.entities.Favorite
import fi.lauriari.helsinkiapp.viewmodels.FavoriteViewModel
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response


class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val apiViewModel: HelsinkiApiViewModel by viewModels()
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
        return view
    }

    private fun setButtonOnClickListeners() {
        binding.activitesBtn.setOnClickListener {
            getFavorites("Activities")
        }

        binding.placesBtn.setOnClickListener {
            getFavorites("Places")
        }

        binding.eventsBtn.setOnClickListener {
            getFavorites("Events")
        }
    }

    private fun getFavorites(itemType: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            var favorites: List<Favorite>? = null
            when (itemType) {
                "Activities" -> {
                    val getActivities = async {
                        favorites =
                            favoritesViewModel.getFavoriteList().toMutableList().filter {
                                it.itemType == "MyHelsinki"
                            }
                    }
                    getActivities.await()
                    if (favorites?.isNotEmpty() == true) {
                        setActivitiesListAndAdapter(favorites)
                    } else {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "No activities set as favorite!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                "Places" -> {
                    val getPlaces = async {
                        favorites =
                            favoritesViewModel.getFavoriteList().toMutableList().filter {
                                it.itemType == "Matko"
                            }
                    }
                    getPlaces.await()
                    if (favorites?.isNotEmpty() == true) {
                        setPlacesListAndAdapter(favorites)
                    } else {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "No places set as favorite!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                "Events" -> {
                    val getEvents = async {
                        favorites =
                            favoritesViewModel.getFavoriteList().toMutableList().filter {
                                it.itemType == "LinkedEvents"
                            }
                    }
                    getEvents.await()
                    if (favorites?.isNotEmpty() == true) {
                        setEventsListAndAdapter(favorites)
                    } else {
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "No events set as favorite!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private suspend fun setActivitiesListAndAdapter(favorites: List<Favorite>?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val listForAdapter = mutableListOf<SingleHelsinkiItem>()
            val populateList = async {
                favorites?.forEach {
                    var itemResponse: Response<SingleHelsinkiActivity>? = null
                    val getSingleItem = async {
                        itemResponse = apiViewModel.getActivityById(it.itemApiId, "en")
                    }
                    getSingleItem.await()
                    val addSingleItem = async {
                        if (itemResponse?.isSuccessful == true) {
                            listForAdapter.add(
                                SingleHelsinkiItem(
                                    id = itemResponse?.body()?.id,
                                    name = itemResponse?.body()?.name?.en,
                                    infoUrl = itemResponse?.body()?.info_url,
                                    latitude = itemResponse?.body()?.location?.lat,
                                    longitude = itemResponse?.body()?.location?.lon,
                                    streetAddress = itemResponse?.body()?.location?.address?.street_address,
                                    locality = itemResponse?.body()?.location?.address?.locality,
                                    description = itemResponse?.body()?.description?.body,
                                    images = itemResponse?.body()?.description?.images,
                                    tags = itemResponse?.body()?.tags,
                                    whereWhenDuration = itemResponse?.body()?.where_when_duration,
                                    itemType = itemResponse?.body()?.source_type!!.name
                                )
                            )
                        }
                    }
                    addSingleItem.await()
                }
            }
            populateList.await()
            activity?.runOnUiThread {
                itemsAdapter.setData(listForAdapter)
            }
        }
    }

    private suspend fun setPlacesListAndAdapter(favorites: List<Favorite>?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val listForAdapter = mutableListOf<SingleHelsinkiItem>()
            val populateList = async {
                favorites?.forEach {
                    var itemResponse: Response<SingleHelsinkiPlace>? = null
                    val getSingleItem = async {
                        itemResponse = apiViewModel.getPlaceById(it.itemApiId, "en")
                    }
                    getSingleItem.await()
                    val addSingleItem = async {
                        if (itemResponse?.isSuccessful == true) {
                            listForAdapter.add(
                                SingleHelsinkiItem(
                                    id = itemResponse?.body()?.id,
                                    name = itemResponse?.body()?.name?.en,
                                    infoUrl = itemResponse?.body()?.info_url,
                                    latitude = itemResponse?.body()?.location?.lat,
                                    longitude = itemResponse?.body()?.location?.lon,
                                    streetAddress = itemResponse?.body()?.location?.address?.street_address,
                                    locality = itemResponse?.body()?.location?.address?.locality,
                                    description = itemResponse?.body()?.description?.body,
                                    images = itemResponse?.body()?.description?.images,
                                    tags = itemResponse?.body()?.tags,
                                    openingHours = itemResponse?.body()?.opening_hours,
                                    itemType = itemResponse?.body()?.source_type!!.name
                                )
                            )
                        }
                    }
                    addSingleItem.await()
                }
            }
            populateList.await()
            activity?.runOnUiThread {
                itemsAdapter.setData(listForAdapter)
            }
        }
    }

    private suspend fun setEventsListAndAdapter(favorites: List<Favorite>?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val listForAdapter = mutableListOf<SingleHelsinkiItem>()
            val populateList = async {
                favorites?.forEach {
                    var itemResponse: Response<SingleHelsinkiEvent>? = null
                    val getSingleItem = async {
                        itemResponse = apiViewModel.getEventById(it.itemApiId, "en")
                    }
                    getSingleItem.await()
                    val addSingleItem = async {
                        if (itemResponse?.isSuccessful == true) {
                            listForAdapter.add(
                                SingleHelsinkiItem(
                                    id = itemResponse?.body()?.id,
                                    name = itemResponse?.body()?.name?.en,
                                    infoUrl = itemResponse?.body()?.info_url,
                                    latitude = itemResponse?.body()?.location?.lat,
                                    longitude = itemResponse?.body()?.location?.lon,
                                    streetAddress = itemResponse?.body()?.location?.address?.street_address,
                                    locality = itemResponse?.body()?.location?.address?.locality,
                                    description = itemResponse?.body()?.description?.body,
                                    images = itemResponse?.body()?.description?.images,
                                    tags = itemResponse?.body()?.tags,
                                    eventDates = itemResponse?.body()?.event_dates,
                                    itemType = itemResponse?.body()?.source_type!!.name
                                )
                            )
                        }
                    }
                    addSingleItem.await()
                }
            }
            populateList.await()
            activity?.runOnUiThread {
                itemsAdapter.setData(listForAdapter)
            }
        }
    }


    private fun initComponents() {
        binding.lifecycleOwner = viewLifecycleOwner
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = itemsAdapter
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