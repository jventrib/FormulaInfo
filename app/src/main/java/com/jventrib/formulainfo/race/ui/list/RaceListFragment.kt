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
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.dropbox.android.external.store4.ExperimentalStoreApi
import com.dropbox.android.external.store4.StoreResponse
import com.google.android.material.transition.Hold
import com.jventrib.formulainfo.Application
import com.jventrib.formulainfo.MainViewModel
import com.jventrib.formulainfo.NavGraphDirections
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.about.AboutFragment
import com.jventrib.formulainfo.common.ui.autoCleared
import com.jventrib.formulainfo.common.ui.postponeTransition
import com.jventrib.formulainfo.common.utils.getLong
import com.jventrib.formulainfo.databinding.FragmentRaceListBinding
import com.jventrib.formulainfo.databinding.ItemRaceBinding
import com.jventrib.formulainfo.race.model.db.RaceFull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * A fragment representing a list of Items.
 */
@ExperimentalStoreApi
@ExperimentalCoroutinesApi
@FlowPreview
class RaceListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var binding by autoCleared<FragmentRaceListBinding>()

    private val seasonList = (1950..2020).toList().reversed()

    private val navController by lazy { findNavController() }

    private val viewModel: MainViewModel by activityViewModels {
        (requireActivity().application as Application).appContainer.getViewModelFactory(::MainViewModel)
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


        exitTransition = Hold().apply {
            duration = requireContext().getLong(R.integer.shared_element_transition_duration)
        }
        reenterTransition = Hold().apply {
            duration = requireContext().getLong(R.integer.shared_element_transition_duration)
        }


        setupSwipeRefresh()
//        postponeEnterTransition(2L, TimeUnit.SECONDS)
//        postponeTransition(view) {}
    }

    private fun setupSwipeRefresh() {
        binding.swipeRaceList.setDistanceToTriggerSync(800)
        binding.swipeRaceList.setOnRefreshListener {
            Timber.d("Refresh Races")
            viewModel.viewModelScope.launch {
                viewModel.refreshRaces()
                binding.swipeRaceList.isRefreshing = false
            }
        }
    }

    private fun onRaceClicked() = { race: RaceFull, itemRaceBinding: ItemRaceBinding ->
        val directions = RaceListFragmentDirections.actionRaceFragmentToRaceResultFragment(race)
        val extras = FragmentNavigatorExtras(
            itemRaceBinding.root to "race_card_detail",
//            itemRaceBinding.imageFlag to "race_image_flag",
//            itemRaceBinding.textRaceDate to "text_race_date",
        )
        findNavController().navigate(directions, extras)
    }

    private fun observeRaces(raceListListAdapter: RaceListListAdapter) {
        viewModel.races.observe(viewLifecycleOwner) { response ->
            when (response) {
                is StoreResponse.Data -> {
                    Timber.d("Resource.Status.SUCCESS: ${response.value}")
                    //                    progress_bar.visibility = View.GONE
                    raceListListAdapter.races = response.value

                    //FIXME postponed transition
                    (view?.parent as ViewGroup).doOnPreDraw {
                        startPostponedEnterTransition()
                    }
                }
                is StoreResponse.Error.Exception -> {
                    Timber.e(response.error, "Error: ${response.errorMessageOrNull()}")
                    Toast.makeText(
                        this.context,
                        response.errorMessageOrNull(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                is StoreResponse.Loading ->
                    Timber.d("Resource.Status.LOADING")
                //                    progress_bar.visibility = View.VISIBLE
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
            requireContext(), R.layout.spinner_season, seasonList
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        spinner.onItemSelectedListener = this
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_action_about) {
            val aboutDestination = getNavDestination<AboutFragment>()
            if (navController.currentDestination != aboutDestination) {
                navController.navigate(
                    NavGraphDirections.actionGlobalAboutFragment()
                )
            }

            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setSeason(seasonList[position])
        @Suppress("ControlFlowWithEmptyBody")
        while (navController.navigateUp()) {
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private inline fun <reified T> getNavDestination() =
        navController.graph.first { (it as FragmentNavigator.Destination).className == T::class.java.name }

}