package com.jventrib.formulainfo.race.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dropbox.android.external.store4.StoreResponse
import com.google.android.material.transition.platform.Hold
import com.jventrib.formulainfo.Application
import com.jventrib.formulainfo.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

/**
 * A fragment representing a list of Items.
 */
@ExperimentalCoroutinesApi
@FlowPreview
class RaceListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val raceList: RecyclerView =
            inflater.inflate(R.layout.fragment_race_list, container, false) as RecyclerView

        val context = requireContext()
        val application = requireActivity().application as Application
        val appContainer = application.appContainer

        val adapter = RaceListAdapter(context) { race, binding ->
            exitTransition = Hold().apply { duration = 300 }
            reenterTransition = Hold().apply { duration = 300 }

            val directions = RaceListFragmentDirections.actionRaceFragmentToRaceResultFragment(race)
            val extras = FragmentNavigatorExtras(
                binding.root to "race_card_detail",
                binding.imageFlag to "race_image_flag",
                binding.textRaceDate to "text_race_date",
            )
            raceList.findNavController().navigate(directions, extras)
        }
        raceList.adapter = adapter
        raceList.layoutManager = LinearLayoutManager(context)

        val viewModel: RaceListViewModel by viewModels(
            factoryProducer = appContainer.getRaceListViewModelFactory(::RaceListViewModel)
        )

        viewModel.allRaces.observe(viewLifecycleOwner) { response ->
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
                is StoreResponse.NoNewData -> TODO()
                is StoreResponse.Error.Message -> TODO()
            }
        }
        return raceList
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }
}