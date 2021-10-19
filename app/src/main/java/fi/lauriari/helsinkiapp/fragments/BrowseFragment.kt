package fi.lauriari.helsinkiapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.adapters.ActivitiesAdapter
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiActivity
import fi.lauriari.helsinkiapp.repositories.HelsinkiApiRepository
import fi.lauriari.helsinkiapp.viewmodelfactories.HelsinkiApiViewModelFactory
import fi.lauriari.helsinkiapp.viewmodels.HelsinkiApiViewModel

class BrowseFragment : Fragment() {

    private lateinit var apiViewModel: HelsinkiApiViewModel
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)

        initializeViewModelAndRepository()

        apiViewModel.getActivities("guidance", "en")

        apiViewModel.response.observe(viewLifecycleOwner, { response ->
            if (response.isSuccessful) {
                Log.d("response", "${response.body()!!.data}")
                val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                val adapterList = mutableListOf<SingleHelsinkiActivity>()
                response.body()?.data?.forEach {
                    adapterList.add(
                        SingleHelsinkiActivity(
                            id = it.id,
                            name = it.name.en,
                            infoUrl = it.info_url,
                            latitude = it.location.lat,
                            longitude = it.location.lon,
                            locality = it.location.address.locality,
                            description = it.description.body,
                        )
                    )
                    recyclerView.adapter = ActivitiesAdapter(adapterList, requireContext())
                }

            } else {
                Toast.makeText(requireContext(), "Fail fetching items", Toast.LENGTH_SHORT).show()
            }
        })


        return view
    }

    private fun initializeViewModelAndRepository() {
        val apiRepository = HelsinkiApiRepository()
        val viewModelFactory = HelsinkiApiViewModelFactory(apiRepository)
        apiViewModel =
            ViewModelProvider(this, viewModelFactory).get(HelsinkiApiViewModel::class.java)
    }

}