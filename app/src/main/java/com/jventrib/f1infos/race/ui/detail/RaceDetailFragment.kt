package com.jventrib.f1infos.race.ui.detail

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    var _binding: FragmentRaceDetailBinding? = null

    private val binding get() = _binding!!

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

            binding.textFp1Date.format(race.sessions.fp1)
            binding.textFp2Date.format(race.sessions.fp2)
            binding.textFp3Date.format(race.sessions.fp3)
            binding.textQualDate.format(race.sessions.qualifying)
            binding.textRaceDate.format(race.sessions.race)

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

    private fun TextView.format(datetime: Instant?) {
        datetime?.let {
            text = ZonedDateTime.ofInstant(datetime, ZoneId.systemDefault()).format(
                customDateTimeFormatter
            )
            typeface =
                if (datetime.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}