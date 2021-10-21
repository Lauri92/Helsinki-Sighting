package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.models.SlideModel
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.databinding.FragmentSingleItemBinding
import fi.lauriari.helsinkiapp.viewmodels.SingleItemViewModel

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

        setImage()

        binding!!.nameTv.text = args.helsinkiItem.name
        binding!!.descriptionTv.text = args.helsinkiItem.description
        binding!!.localityTv.text = args.helsinkiItem.locality
        binding!!.infoUrlTv.text = args.helsinkiItem.infoUrl




        return binding!!.root
    }

    private fun setImage() {
        if (args.helsinkiItem.images!!.isNotEmpty()) {
            val imageList = ArrayList<SlideModel>()
            args.helsinkiItem.images!!.forEach {
                imageList.add(SlideModel(it.url))
            }
            binding!!.slider.setImageList(imageList)
        } else {
            val imageList = ArrayList<SlideModel>()
            imageList.add(SlideModel(R.drawable.image_not_available))
            binding!!.slider.setImageList(imageList)
        }
    }

    private fun initializeBinding(binding: FragmentSingleItemBinding) {
        binding.lifecycleOwner = this
        binding.singleItemViewmodel = singleItemViewModel
    }

}