package fi.lauriari.helsinkiapp.fragments

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import fi.lauriari.helsinkiapp.MainActivity
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.databinding.FragmentSingleItemBinding
import fi.lauriari.helsinkiapp.entities.Favorite
import fi.lauriari.helsinkiapp.viewmodels.FavoriteViewModel
import fi.lauriari.helsinkiapp.viewmodels.SingleItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SingleItemFragment : Fragment() {

    private val args by navArgs<SingleItemFragmentArgs>()
    private val singleItemViewModel: SingleItemViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private lateinit var binding: FragmentSingleItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_item, container, false)
        initializeBinding(binding)
        initNavigation()

        setImages()

        setBasicInformation()

        setSpecificInformation()

        setOnclickListeners()

        return binding.root
    }

    private fun initNavigation() {
        (requireActivity() as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
    }

    private fun setOnclickListeners() {
        binding.openMapFab.setOnClickListener {
            if (args.helsinkiItem.latitude!! < 90 && args.helsinkiItem.latitude!! > -90 &&
                args.helsinkiItem.longitude!! < 180 && args.helsinkiItem.longitude!! > -180
            ) {
                val action = SingleItemFragmentDirections.actionSingleItemFragmentToMapFragment(
                    (args.helsinkiItem.latitude as Double).toFloat(),
                    (args.helsinkiItem.longitude as Double).toFloat(),
                    args.helsinkiItem.name as String
                )
                findNavController().navigate(action)
            } else {
                Toast.makeText(
                    requireContext(),
                    "No location available\nCheck website for further details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val list = favoriteViewModel.getFavoriteList()
        list.filter {
            it.itemApiId == args.helsinkiItem.id
        }.let {
            if(it.isEmpty()) {
                createInsertListener()
            } else {
                createDeleteListener(it)
            }
        }
    }

    private fun createInsertListener() {
        binding.addFavoritesFab.let { fab ->
            fab.isClickable = true
            fab.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_favorite_24
                )
            )
            fab.setOnClickListener {
                fab.isClickable = false
                lifecycleScope.launch(Dispatchers.Main) {
                    val insertFavorite = async {
                        favoriteViewModel.insertFavorite(
                            Favorite(
                                id = 0,
                                itemType = args.helsinkiItem.itemType,
                                itemApiId = args.helsinkiItem.id.toString(),
                            )
                        )
                    }
                    insertFavorite.await()
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "${args.helsinkiItem.name} added to favorites!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setOnclickListeners()
                }
            }
        }
    }

    private fun createDeleteListener(isFavorited: List<Favorite>) {
        binding.addFavoritesFab.let { fab ->
            fab.isClickable = true
            fab.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_remove_circle_24
                )
            )
            fab.setOnClickListener {
                fab.isClickable = false
                lifecycleScope.launch(Dispatchers.Main) {

                    val deleteFavorite = async {
                        favoriteViewModel.deleteFavorite(isFavorited[0].id)
                    }
                    deleteFavorite.join()
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "${args.helsinkiItem.name} removed from favorites!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    setOnclickListeners()
                }
            }
        }
    }

    private fun setImages() {
        if (args.helsinkiItem.images?.isNotEmpty() == true) {
            val imageList = ArrayList<SlideModel>()
            args.helsinkiItem.images!!.forEach {
                imageList.add(SlideModel(it.url))
            }
            binding.slider.setImageList(imageList, ScaleTypes.FIT)
        } else {
            val imageList = ArrayList<SlideModel>()
            imageList.add(SlideModel(R.drawable.image_not_available))
            binding.slider.setImageList(imageList)
        }
    }

    private fun setBasicInformation() {
        binding.nameTv.text = args.helsinkiItem.name
        binding.nameTv.paintFlags = binding.nameTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.descriptionTv.text = args.helsinkiItem.description
        "${args.helsinkiItem.locality}\n".also { binding.localityTv.text = it }
        binding.infoUrlTv.text = args.helsinkiItem.infoUrl
        binding.streetaddressTv.text = args.helsinkiItem.streetAddress
        binding.streetaddressTv.paintFlags =
            binding.streetaddressTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun setSpecificInformation() {
        when {
            args.helsinkiItem.whereWhenDuration != null -> {
                handleActivityItemSpecificInfo()
            }
            args.helsinkiItem.openingHours != null -> {
                handlePlaceItemSpecificInfo()
            }
            args.helsinkiItem.eventDates != null -> {
                handleEventItemSpecificInfo()
            }
        }
    }

    private fun handleActivityItemSpecificInfo() {
        binding.specificInfoTv.append("Where and when:\n")
        "${args.helsinkiItem.whereWhenDuration!!.where_and_when}\nDuration: ${args.helsinkiItem.whereWhenDuration!!.duration}".also {
            binding.specificInfoTv.append(it)
        }
    }

    private fun handlePlaceItemSpecificInfo() {
        binding.specificInfoTv.append("Opening hours:\n")
        args.helsinkiItem.openingHours?.hours?.forEach {
            when (it.weekday_id) {
                1 -> {
                    binding.specificInfoTv.append(
                        "Monday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                2 -> {
                    binding.specificInfoTv.append(
                        "Tuesday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                3 -> {
                    binding.specificInfoTv.append(
                        "Wednesday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                4 -> {
                    binding.specificInfoTv.append(
                        "Thursday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                5 -> {
                    binding.specificInfoTv.append(
                        "Friday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                6 -> {
                    binding.specificInfoTv.append(
                        "Saturday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                7 -> {
                    binding.specificInfoTv.append(
                        "Sunday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
            }
        }
    }

    private fun handleEventItemSpecificInfo() {
        binding.specificInfoTv.append("Event times:\n")
        val startTime = args.helsinkiItem.eventDates?.starting_day
        val endTime = args.helsinkiItem.eventDates?.ending_day
        val additionalDescription = args.helsinkiItem.eventDates?.additional_description
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        try {
            var dateStart: Date? = null
            var dateEnd: Date? = null
            if (startTime != null) {
                dateStart = dateFormat.parse(startTime)
            }
            if (endTime != null) {
                dateEnd = dateFormat.parse(endTime)
            }
            "Start date: ${dateStart ?: "N/A"}\nEnd date: ${dateEnd ?: "N/A"}\n ${additionalDescription ?: ""}".also {
                binding.specificInfoTv.append(it)
            }
        } catch (e: ParseException) {
        }

    }

    private fun initializeBinding(binding: FragmentSingleItemBinding) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.singleItemViewmodel = singleItemViewModel
    }

}