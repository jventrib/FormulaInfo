package com.jventrib.f1infos.race.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.dropbox.android.external.store4.StoreResponse
import com.jventrib.f1infos.R

/**
 * A fragment representing a list of Items.
 */
class RaceFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: RecyclerView =
            inflater.inflate(R.layout.fragment_race_list, container, false) as RecyclerView

        val context = requireContext()
        val adapter = RaceListAdapter(context) {
            val action = RaceFragmentDirections.actionRaceFragmentToRaceResultFragment(it)
            view.findNavController().navigate(action)
        }
        view.adapter = adapter
        view.layoutManager = LinearLayoutManager(context)

        var raceViewModel = ViewModelProvider(this).get(RaceViewModel::class.java)
        raceViewModel.allRaces.observe(requireActivity(), { response ->
            when (response) {
                is StoreResponse.Data -> {
                    Log.d(javaClass.name, "Resource.Status.SUCCESS: ${response.value}")
//                    progress_bar.visibility = View.GONE
                    adapter.setRaces(response.value)
                }
                is StoreResponse.Error.Exception -> {
                    Log.e(javaClass.name, "Error: ${response.errorMessageOrNull()}", response.error)
                    Toast.makeText(context, response.errorMessageOrNull(), Toast.LENGTH_SHORT).show()
                }
                is StoreResponse.Loading ->
                    Log.d(javaClass.name, "Resource.Status.LOADING")
//                    progress_bar.visibility = View.VISIBLE
            }
        })
        return view
    }
}