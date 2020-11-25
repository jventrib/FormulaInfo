package com.jventrib.formulainfo.race.ui.detail

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dropbox.android.external.store4.ExperimentalStoreApi
import com.jventrib.formulainfo.Application
import com.jventrib.formulainfo.MainViewModel
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.common.ui.beforeTransition
import com.jventrib.formulainfo.common.ui.customDateTimeFormatter
import com.jventrib.formulainfo.databinding.FragmentRaceDetailBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@ExperimentalCoroutinesApi
@FlowPreview
@ExperimentalStoreApi
class RaceDetailFragment : Fragment() {

    private val args: RaceDetailFragmentArgs by navArgs()

    private var _binding: FragmentRaceDetailBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedElementEnterTransition = MaterialContainerTransform().apply {
//            drawingViewId = R.id.nav_host_fragment
//            scrimColor = Color.TRANSPARENT
//            duration = 500
//        }

        sharedElementEnterTransition = android.transition.TransitionInflater.from(context)
            .inflateTransition(android.R.transition.move)
            .apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRaceDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val application = requireActivity().application as Application
        val appContainer = application.appContainer
        val viewModel: MainViewModel by activityViewModels {
            appContainer.getViewModelFactory { MainViewModel(it) }
        }

        viewModel.setRace(args.race)
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.my_toolbar)
        val toolbarTitle: TextView = toolbar.children.first { it is TextView } as TextView

        val raceDetailHeader = binding.raceDetailHeader
        viewModel.race.observe(viewLifecycleOwner) { (race, circuit) ->
            toolbarTitle.text = race.raceName

            handleDateDisplay(race.sessions.fp1, raceDetailHeader.textFp1Date, raceDetailHeader.textLabelFp1Date)
            handleDateDisplay(race.sessions.fp2, raceDetailHeader.textFp2Date, raceDetailHeader.textLabelFp2Date)
            handleDateDisplay(race.sessions.fp3, raceDetailHeader.textFp3Date, raceDetailHeader.textLabelFp3Date)
            handleDateDisplay(race.sessions.qualifying, raceDetailHeader.textQualDate, raceDetailHeader.textLabelQualDate)
            raceDetailHeader.textRaceDate.textAndFormat(race.sessions.race)

            circuit.location.flag?.let {
                raceDetailHeader.imageFlag.load(it)
            }
            circuit.imageUrl?.let {
                binding.imageCircuitImage.load(it)
            }

            binding.textCircuitName.text = circuit.name
        }

        val raceResultList: RecyclerView = binding.listResult

        val context = requireContext()
        val adapter = RaceResultListAdapter(context) { race, binding ->
//TODO navigate to driver fragment
//            val directions = RaceListFragmentDirections.actionRaceFragmentToRaceResultFragment(race)
//            val extras = FragmentNavigatorExtras(
//                binding.root to "race_card_detail",
//                binding.imageFlag to "race_image_flag",
//                binding.textRaceDate to "text_race_date",
//            )
//            raceList.findNavController().navigate(directions, extras)
        }
        raceResultList.adapter = adapter

        viewModel.raceResults.observe(viewLifecycleOwner) { storeResponse ->
            storeResponse.throwIfError()
            storeResponse.dataOrNull()?.let {
                adapter.setRaceResult(it)
            } ?: let { adapter.setRaceResult(listOf()) }
        }


        ViewCompat.setTransitionName(raceDetailHeader.layoutDetail, "race_card_detail")
        ViewCompat.setTransitionName(raceDetailHeader.imageFlag, "race_image_flag")
        ViewCompat.setTransitionName(raceDetailHeader.textRaceDate, "text_race_date")

        //Reset the adapter before transition to avoid glitch with previous race result
        beforeTransition(view) {
            adapter.setRaceResult(listOf())
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun handleDateDisplay(
        date: Instant?,
        field: TextView,
        fieldLabel: TextView
    ) {
        if (date != null) {
            field.textAndFormat(date)
        } else {
            field.visibility = View.GONE
            fieldLabel.visibility = View.GONE
        }
    }

    private fun TextView.textAndFormat(datetime: Instant?) {
        datetime?.let {
            text = ZonedDateTime.ofInstant(datetime, ZoneId.systemDefault()).format(
                customDateTimeFormatter
            )
            typeface =
                if (datetime.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
    }
}