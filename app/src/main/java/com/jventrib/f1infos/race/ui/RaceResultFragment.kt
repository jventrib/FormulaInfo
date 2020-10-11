package com.jventrib.f1infos.race.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.common.ui.customDateTimeFormatter
import com.jventrib.f1infos.databinding.FragmentRaceResultBinding
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RaceResultFragment : Fragment() {

    val args: RaceResultFragmentArgs by navArgs()

    var _binding: FragmentRaceResultBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRaceResultBinding.inflate(inflater, container, false)
        val view = binding.root

        val viewModel = ViewModelProvider(this).get(RaceResultViewModel::class.java)
        viewModel.setRace(args.race)
        viewModel.race.observe(requireActivity()) { race ->

            binding.nameTextViewResult.text = race.raceName
            race.datetime?.let {
                val dateTV = binding.dateTextViewResult
                dateTV.text =
                    ZonedDateTime.ofInstant(it, ZoneId.systemDefault()).format(
                        customDateTimeFormatter
                    )
                dateTV.typeface =
                    if (it.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            }
            race.circuit.location.flag?.let {
                val s = "https://www.countryflags.io/$it/flat/64.png"
                Glide
                    .with(requireContext())
                    .load(s)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.imageViewResult)
            }
            race.circuit.circuitImageUrl?.let {
                Glide
                    .with(requireContext())
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.circuitImageViewResult)
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}