package com.jventrib.formulainfo.race.ui.detail

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import coil.transform.CircleCropTransformation
import com.commit451.coiltransformations.facedetection.CenterOnFaceTransformation
import com.dropbox.android.external.store4.ExperimentalStoreApi
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.transition.MaterialContainerTransform
import com.jventrib.formulainfo.Application
import com.jventrib.formulainfo.MainViewModel
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.common.ui.autoCleared
import com.jventrib.formulainfo.common.ui.customDateTimeFormatter
import com.jventrib.formulainfo.common.ui.loadImage
import com.jventrib.formulainfo.common.utils.getLong
import com.jventrib.formulainfo.databinding.FragmentRaceDetailBinding
import com.jventrib.formulainfo.databinding.LayoutRaceDetailHeaderBinding
import com.jventrib.formulainfo.race.model.db.Circuit
import com.jventrib.formulainfo.race.model.db.Race
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

    private var binding by autoCleared<FragmentRaceDetailBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            scrimColor = Color.TRANSPARENT
            duration = requireContext().getLong(R.integer.shared_element_transition_duration)
            val containerColor = TypedValue().let {
                requireContext().theme.resolveAttribute(R.attr.colorSurface, it, true)
                it.data
            }
            setAllContainerColors(containerColor)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRaceDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val raceDetailHeader = binding.raceDetailHeader
        initToolBar()
        val viewModel = getViewModel()

        viewModel.setRace(args.race)
        viewModel.race.observe(viewLifecycleOwner) { (race, circuit) ->
            (requireActivity() as AppCompatActivity).supportActionBar?.title = race.raceName
            displayHeader(race, raceDetailHeader, circuit)
        }

        val raceResultList: RecyclerView = binding.listResult
        val adapter = RaceResultListAdapter()
        raceResultList.adapter = adapter

        viewModel.raceResults.observe(viewLifecycleOwner) { storeResponse ->
            storeResponse.throwIfError()
            storeResponse.dataOrNull()?.let {
                adapter.setRaceResult(it)
            } ?: let { adapter.setRaceResult(listOf()) }
        }


        //TODO navigate to driver fragment
//            val directions = RaceListFragmentDirections.actionRaceFragmentToRaceResultFragment(race)
//            val extras = FragmentNavigatorExtras(
//                binding.root to "race_card_detail",
//                binding.imageFlag to "race_image_flag",
//                binding.textRaceDate to "text_race_date",
//            )
//            raceList.findNavController().navigate(directions, extras)


        return view
    }

    private fun getViewModel(): MainViewModel {
        val appContainer = (requireActivity().application as Application).appContainer
        val viewModel: MainViewModel by activityViewModels {
            appContainer.getViewModelFactory { MainViewModel(it) }
        }
        return viewModel
    }

    private fun displayHeader(
        race: Race,
        raceDetailHeader: LayoutRaceDetailHeaderBinding,
        circuit: Circuit
    ) {
        handleDateDisplay(
            race.sessions.fp1,
            raceDetailHeader.textFp1Date,
            raceDetailHeader.textLabelFp1Date
        )
        handleDateDisplay(
            race.sessions.fp2,
            raceDetailHeader.textFp2Date,
            raceDetailHeader.textLabelFp2Date
        )
        handleDateDisplay(
            race.sessions.fp3,
            raceDetailHeader.textFp3Date,
            raceDetailHeader.textLabelFp3Date
        )
        handleDateDisplay(
            race.sessions.qualifying,
            raceDetailHeader.textQualDate,
            raceDetailHeader.textLabelQualDate
        )
        raceDetailHeader.textRaceDate.textAndFormat(race.sessions.race)

        circuit.location.flag?.let {
            raceDetailHeader.imageFlag.loadImage(it)
        }
        circuit.imageUrl?.let {
            binding.imageCircuitImage.loadImage(it)
        }

        //Adjust FrameLayout height
        val framePixels: Int = resources.getDimensionPixelSize(R.dimen.header_detail_frame_height)
        val sessionHeight = if (hasSessionsInfo(race)) 100f else 60f
        val sessionPixels: Int =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                sessionHeight,
                resources.displayMetrics
            ).toInt()
        binding.framelayoutHeader.layoutParams = CollapsingToolbarLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            framePixels + sessionPixels
        )

    }

    private fun hasSessionsInfo(race: Race) =
        race.sessions.qualifying != null
                && race.sessions.fp1 != null
                && race.sessions.fp2 != null
                && race.sessions.fp3 != null

    private fun initToolBar() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarRaceDetail)
        val supportActionBar = (activity as AppCompatActivity).supportActionBar
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().navigateUp()
        return true
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

