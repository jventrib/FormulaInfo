package com.jventrib.f1infos.race.ui.detail

import android.graphics.Typeface
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.common.ui.customDateTimeFormatter
import com.jventrib.f1infos.databinding.FragmentRaceDetailBinding
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RaceDetailFragment : Fragment() {

    val args: RaceDetailFragmentArgs by navArgs()

    private var _binding: FragmentRaceDetailBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
            .apply {
                duration = 500
//                interpolator = AccelerateDecelerateInterpolator()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRaceDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewModel = ViewModelProvider(this).get(RaceDetailViewModel::class.java)
        viewModel.setRace(args.race)
        viewModel.race.observe(requireActivity()) { race ->

            binding.textRaceName.text = race.raceName

            binding.textFp1Date.textAndFormat(race.sessions.fp1)
            binding.textFp2Date.textAndFormat(race.sessions.fp2)
            binding.textFp3Date.textAndFormat(race.sessions.fp3)
            binding.textQualDate.textAndFormat(race.sessions.qualifying)
            binding.textRaceDate.textAndFormat(race.sessions.race)

            race.circuit.location.flag?.let {
                val s = "https://www.countryflags.io/$it/flat/64.png"
                Glide.with(requireContext())
                    .load(s)
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(binding.cardRaceDetail, "race_card${args.race.round}")
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