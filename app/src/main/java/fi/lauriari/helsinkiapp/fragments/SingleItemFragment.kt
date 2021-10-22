package fi.lauriari.helsinkiapp.fragments

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.databinding.FragmentSingleItemBinding
import fi.lauriari.helsinkiapp.viewmodels.SingleItemViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SingleItemFragment : Fragment() {

    private val args by navArgs<SingleItemFragmentArgs>()
    private val singleItemViewModel: SingleItemViewModel by viewModels()
    var binding: FragmentSingleItemBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_item, container, false)
        initializeBinding(binding!!)

        setImages()

        setBasicInformation()

        setSpecificInformation()

        setOnclickListeners()

        return binding!!.root
    }

    private fun setOnclickListeners() {
        binding!!.streetaddressTv.setOnClickListener {
            val action = SingleItemFragmentDirections.actionSingleItemFragmentToMapFragment(
                (args.helsinkiItem.latitude as Double).toFloat(),
                (args.helsinkiItem.longitude as Double).toFloat()
            )
            findNavController().navigate(action)
        }
    }

    private fun setImages() {
        if (args.helsinkiItem.images!!.isNotEmpty()) {
            val imageList = ArrayList<SlideModel>()
            args.helsinkiItem.images!!.forEach {
                imageList.add(SlideModel(it.url))
            }
            binding!!.slider.setImageList(imageList, ScaleTypes.FIT)
        } else {
            val imageList = ArrayList<SlideModel>()
            imageList.add(SlideModel(R.drawable.image_not_available))
            binding!!.slider.setImageList(imageList)
        }
    }

    private fun setBasicInformation() {
        binding!!.nameTv.text = args.helsinkiItem.name
        binding!!.nameTv.paintFlags = binding!!.nameTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding!!.descriptionTv.text = args.helsinkiItem.description
        "${args.helsinkiItem.locality}\n".also { binding!!.localityTv.text = it }
        binding!!.infoUrlTv.text = args.helsinkiItem.infoUrl
        binding!!.streetaddressTv.text = args.helsinkiItem.streetAddress
        binding!!.streetaddressTv.paintFlags =
            binding!!.streetaddressTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
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
        binding!!.specificInfoTv.append("Where and when:\n")
        "${args.helsinkiItem.whereWhenDuration!!.where_and_when}\nDuration: ${args.helsinkiItem.whereWhenDuration!!.duration}".also {
            binding!!.specificInfoTv.append(it)
        }
    }

    private fun handlePlaceItemSpecificInfo() {
        binding!!.specificInfoTv.append("Opening hours:\n")
        args.helsinkiItem.openingHours?.hours?.forEach {
            when (it.weekday_id) {
                1 -> {
                    binding!!.specificInfoTv.append(
                        "Monday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                2 -> {
                    binding!!.specificInfoTv.append(
                        "Tuesday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                3 -> {
                    binding!!.specificInfoTv.append(
                        "Wednesday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                4 -> {
                    binding!!.specificInfoTv.append(
                        "Thursday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                5 -> {
                    binding!!.specificInfoTv.append(
                        "Friday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                6 -> {
                    binding!!.specificInfoTv.append(
                        "Saturday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
                7 -> {
                    binding!!.specificInfoTv.append(
                        "Sunday: Opens: ${it.opens ?: "N/A"} Closes: ${it.closes ?: "N/A"} " +
                                "${if (it.open24h == true) "Open24H" else ""}\n"
                    )
                }
            }
        }
    }

    private fun handleEventItemSpecificInfo() {
        binding!!.specificInfoTv.append("Event times:\n")
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
                binding!!.specificInfoTv.append(it)
            }
        } catch (e: ParseException) {
        }

    }

    private fun initializeBinding(binding: FragmentSingleItemBinding) {
        binding.lifecycleOwner = this
        binding.singleItemViewmodel = singleItemViewModel
    }

}