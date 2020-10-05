package com.jventrib.f1infos.race.ui

import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.R
import com.jventrib.f1infos.common.ui.customDateTimeFormatter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class RaceResultFragment : Fragment() {

    companion object {
        fun newInstance() = RaceResultFragment()
    }

    private lateinit var viewModel: RaceResultViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_race_result, container, false)


        viewModel.race.observe(requireActivity()) {current ->

            view.findViewById<TextView>(R.id.nameTextView).text = current.raceName
            current.datetime?.let {
                val dateTV = view.findViewById<TextView>(R.id.dateTextView)
                dateTV.text =
                    ZonedDateTime.ofInstant(it, ZoneId.systemDefault()).format(
                        customDateTimeFormatter
                    )
                dateTV.typeface =
                    if (it.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            }
            current.circuit.location.flag?.let {
                val s = "https://www.countryflags.io/$it/flat/64.png"
                Glide
                    .with(requireContext())
                    .load(s)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(view.findViewById(R.id.imageView))
            }
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RaceResultViewModel::class.java)
    }

}