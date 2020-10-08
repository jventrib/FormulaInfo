package com.jventrib.f1infos.race.ui

import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.liveData
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.R
import com.jventrib.f1infos.common.ui.customDateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RaceResultFragment : Fragment() {

    val args: RaceResultFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_race_result, container, false)

        val viewModel = ViewModelProvider(this).get(RaceResultViewModel::class.java)
        viewModel.setRace(args.race)
        viewModel.race.observe(requireActivity()) { race ->

            view.findViewById<TextView>(R.id.nameTextViewResult).text = race.raceName
            race.datetime?.let {
                val dateTV = view.findViewById<TextView>(R.id.dateTextViewResult)
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
                    .into(view.findViewById(R.id.imageViewResult))
            }
            race.circuit.circuitImageUrl?.let {
                Glide
                    .with(requireContext())
                    .load(it)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(view.findViewById(R.id.circuitImageViewResult))
            }
        }
        return view
    }

}