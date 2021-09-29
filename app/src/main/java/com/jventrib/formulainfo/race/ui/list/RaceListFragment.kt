package com.jventrib.formulainfo.race.ui.list

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dropbox.android.external.store4.StoreResponse
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialElevationScale
import com.jventrib.formulainfo.Application
import com.jventrib.formulainfo.MainViewModel
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.about.AboutFragment
import com.jventrib.formulainfo.common.ui.autoCleared
import com.jventrib.formulainfo.common.utils.getLong
import com.jventrib.formulainfo.databinding.FragmentRaceListBinding
import com.jventrib.formulainfo.databinding.ItemRaceBinding
import com.jventrib.formulainfo.race.model.db.RaceFull
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

/**
 * A fragment representing a list of Items.
 */
class RaceListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var binding by autoCleared<FragmentRaceListBinding>()


    private val navController by lazy { findNavController() }

    private val viewModel: MainViewModel by activityViewModels {
        (requireActivity().application as Application).appContainer.getViewModelFactory(::MainViewModel)
    }

    private fun getHold() = Hold().apply {
        duration = requireContext().getLong(R.integer.shared_element_transition_duration)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRaceListBinding.inflate(inflater, container, false)
        postponeEnterTransition()
        val adapter = RaceListListAdapter(onRaceClicked())
        binding.list.adapter = adapter
        observeRaces(adapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarRaceList)
        binding.toolbarRaceList.setupWithNavController(navController)

        setupSwipeRefresh()
    }


    private fun setupSwipeRefresh() {
        binding.swipeRaceList.setDistanceToTriggerSync(800)
        binding.swipeRaceList.setOnRefreshListener {
            logcat { "Refresh Races" }
            viewModel.viewModelScope.launch {
                viewModel.refreshRaces()
                binding.swipeRaceList.isRefreshing = false
            }
        }
    }

    private fun onRaceClicked() = { race: RaceFull, itemRaceBinding: ItemRaceBinding ->
//        exitTransition = getHold()
//        reenterTransition = getHold()

        exitTransition = MaterialElevationScale(false).apply {
            duration = requireContext().getLong(R.integer.shared_element_transition_duration)
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = requireContext().getLong(R.integer.shared_element_transition_duration)
        }
        val directions = RaceListFragmentDirections.actionRaceFragmentToRaceResultFragment(race)
        val extras = FragmentNavigatorExtras(
            itemRaceBinding.root to "race_card_detail",
        )
        findNavController().navigate(directions, extras)
    }

    private fun observeRaces(raceListListAdapter: RaceListListAdapter) {
        viewModel.races.observe(viewLifecycleOwner) { response ->
            when (response) {
                is StoreResponse.Data -> {
                    raceListListAdapter.races = response.value

                    //FIXME postponed transition
                    (view?.parent as ViewGroup).doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
                is StoreResponse.Error.Exception -> {
                    logcat(LogPriority.ERROR) { "Error: ${response.errorMessageOrNull()}" }
                    Toast.makeText(
                        this.context,
                        response.errorMessageOrNull(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                is StoreResponse.Loading ->
                    logcat { "Resource.Status.LOADING" }
                is StoreResponse.NoNewData -> TODO()
                is StoreResponse.Error.Message -> TODO()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)

        val item: MenuItem = menu.findItem(R.id.spinner)
        val spinner = item.actionView as Spinner
        spinner.adapter = ArrayAdapter(
            requireContext(), R.layout.spinner_season, viewModel.seasonList
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinner.onItemSelectedListener = this
//        viewModel.seasonPosition.value?.let { spinner.setSelection(it) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_action_about) {
            val aboutDestination = getNavDestination<AboutFragment>()
            if (navController.currentDestination != aboutDestination) {
                exitTransition = null
                reenterTransition = null
                navController.navigate(
                    RaceListFragmentDirections.actionFragmentRaceListToAboutFragment()
                )
            }

            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        viewModel.seasonPosition.value = position
        //        viewModel.setSeason(seasonList[position])
//        @Suppress("ControlFlowWithEmptyBody")
//        while (navController.navigateUp()) {
//        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private inline fun <reified T> getNavDestination() =
        navController.graph.first { (it as FragmentNavigator.Destination).className == T::class.java.name }

}