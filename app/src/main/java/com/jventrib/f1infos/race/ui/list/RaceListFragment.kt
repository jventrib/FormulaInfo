package com.jventrib.f1infos.race.ui.list

import android.os.Bundle
import android.transition.Fade
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dropbox.android.external.store4.StoreResponse
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.jventrib.f1infos.R

/**
 * A fragment representing a list of Items.
 */
class RaceListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val raceList: RecyclerView =
            inflater.inflate(R.layout.fragment_race_list, container, false) as RecyclerView

        val context = requireContext()
        val adapter = RaceListAdapter(context) { race, binding ->

            exitTransition = Hold().apply { duration = 500 }
            reenterTransition = Hold().apply { duration = 500 }

            val directions = RaceListFragmentDirections.actionRaceFragmentToRaceResultFragment(race)
            val extras = FragmentNavigatorExtras(
                binding.root to "race_card_detail",
            )
            raceList.findNavController().navigate(directions, extras)
        }
        raceList.adapter = adapter
        raceList.layoutManager = LinearLayoutManager(context)

        val raceViewModel = ViewModelProvider(this).get(RaceListViewModel::class.java)
        raceViewModel.allRaces.observe(requireActivity(), { response ->
            when (response) {
                is StoreResponse.Data -> {
                    Log.d(javaClass.name, "Resource.Status.SUCCESS: ${response.value}")
//                    progress_bar.visibility = View.GONE
                    adapter.setRaces(response.value)
                }
                is StoreResponse.Error.Exception -> {
                    Log.e(javaClass.name, "Error: ${response.errorMessageOrNull()}", response.error)
                    Toast.makeText(context, response.errorMessageOrNull(), Toast.LENGTH_SHORT)
                        .show()
                }
                is StoreResponse.Loading ->
                    Log.d(javaClass.name, "Resource.Status.LOADING")
//                    progress_bar.visibility = View.VISIBLE
            }
        })
        return raceList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}