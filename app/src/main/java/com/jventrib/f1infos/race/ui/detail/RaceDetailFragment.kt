package com.jventrib.f1infos.race.ui.detail

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.Application
import com.jventrib.f1infos.common.ui.customDateTimeFormatter
import com.jventrib.f1infos.databinding.FragmentRaceDetailBinding
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

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
    ): View? {
        _binding = FragmentRaceDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val f1IApplication = requireActivity().application as Application
        val appContainer = f1IApplication.appContainer
        val viewModel: RaceDetailViewModel by viewModels(
            factoryProducer = appContainer.getRaceListViewModelFactory { RaceDetailViewModel(it) }
        )

        viewModel.setRace(args.race)
        viewModel.race.observe(viewLifecycleOwner) { race ->

            binding.textRaceName.text = race.raceName

            binding.textFp1Date.textAndFormat(race.sessions.fp1)
            binding.textFp2Date.textAndFormat(race.sessions.fp2)
            binding.textFp3Date.textAndFormat(race.sessions.fp3)
            binding.textQualDate.textAndFormat(race.sessions.qualifying)
            binding.textRaceDate.textAndFormat(race.sessions.race)

            race.circuit.location.flag?.let {
                Glide.with(this)
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.imageFlag)
            }
            race.circuit.circuitImageUrl?.let {
                Glide.with(this)
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.imageCircuitImage)
            }
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

        viewModel.raceResult.observe(viewLifecycleOwner) { storeResponse ->
            storeResponse.throwIfError()
            storeResponse.dataOrNull()?.let { adapter.setRaceResult(it) }
        }

        ViewCompat.setTransitionName(binding.root, "race_card_detail")
        ViewCompat.setTransitionName(binding.imageFlag, "race_image_flag")
        ViewCompat.setTransitionName(binding.textRaceDate, "text_race_date")
        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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