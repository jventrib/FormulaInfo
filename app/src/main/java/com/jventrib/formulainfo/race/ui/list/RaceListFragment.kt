package com.jventrib.formulainfo.race.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.dropbox.android.external.store4.ExperimentalStoreApi
import com.dropbox.android.external.store4.StoreResponse
import com.google.android.material.transition.platform.Hold
import com.jventrib.formulainfo.Application
import com.jventrib.formulainfo.MainViewModel
import com.jventrib.formulainfo.common.ui.beforeTransition
import com.jventrib.formulainfo.databinding.FragmentRaceListBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * A fragment representing a list of Items.
 */
@ExperimentalStoreApi
@ExperimentalCoroutinesApi
@FlowPreview
class RaceListFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels {
        (requireActivity().application as Application).appContainer.getViewModelFactory(::MainViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRaceListBinding.inflate(inflater, container, false)
        val view = binding.root
        val list = binding.list

        val context = requireContext()

        val adapter = RaceListAdapter(context) { race, itemRaceBinding ->
            exitTransition = Hold().apply { duration = 300 }
            reenterTransition = Hold().apply { duration = 300 }

            val directions = RaceListFragmentDirections.actionRaceFragmentToRaceResultFragment(race)
            val extras = FragmentNavigatorExtras(
                itemRaceBinding.root to "race_card_detail",
                itemRaceBinding.imageFlag to "race_image_flag",
                itemRaceBinding.textRaceDate to "text_race_date",
            )
            view.findNavController().navigate(directions, extras)
        }
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(context)


        viewModel.seasonRaces.observe(viewLifecycleOwner) { response ->
            when (response) {
                is StoreResponse.Data -> {
                    Timber.d("Resource.Status.SUCCESS: ${response.value}")
//                    progress_bar.visibility = View.GONE
                    adapter.setRaces(response.value)
                }
                is StoreResponse.Error.Exception -> {
                    Timber.e(response.error, "Error: ${response.errorMessageOrNull()}")
                    Toast.makeText(context, response.errorMessageOrNull(), Toast.LENGTH_SHORT)
                        .show()
                }
                is StoreResponse.Loading ->
                    Timber.d("Resource.Status.LOADING")
//                    progress_bar.visibility = View.VISIBLE
                is StoreResponse.NoNewData -> TODO()
                is StoreResponse.Error.Message -> TODO()
            }
        }

        //Pull to refresh handling
        view.setDistanceToTriggerSync(800)
        view.setOnRefreshListener {
            Timber.d("Refresh Races")
            viewModel.viewModelScope.launch {
//                viewModel.refreshRaces()
                view.isRefreshing = false
            }
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beforeTransition(view) {}
    }
}