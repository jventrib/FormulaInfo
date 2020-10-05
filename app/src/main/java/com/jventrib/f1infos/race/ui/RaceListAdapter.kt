package com.jventrib.f1infos.race.ui

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jventrib.f1infos.R
import com.jventrib.f1infos.common.ui.customDateTimeFormatter
import com.jventrib.f1infos.race.model.Race
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


class RaceListAdapter internal constructor(
    val context: Context
) : RecyclerView.Adapter<RaceListAdapter.RaceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var races = emptyList<Race>()

    inner class RaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val raceNameItemView: TextView = itemView.findViewById(R.id.nameTextView)
        val raceDateItemView: TextView = itemView.findViewById(R.id.dateTextView)
        val flagItemView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(item: Race, listener: AdapterView.OnItemClickListener) {
            itemView.setOnClickListener { listener.onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val itemView = inflater.inflate(R.layout.fragment_race, parent, false)
        itemView.setOnClickListener(RaceOnClickListener())
        return RaceViewHolder(itemView)
    }

    private inner class RaceOnClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            val itemPosition: Int = v.getChildLayoutPosition(view)
            val item: String = mList.get(itemPosition)
            Toast.makeText(mContext, item, Toast.LENGTH_LONG).show()
        }

    }

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val current = races.toList()[position]
        holder.raceNameItemView.text = current.raceName
        current.datetime?.let {
            holder.raceDateItemView.text =
                ZonedDateTime.ofInstant(it, ZoneId.systemDefault()).format(
                    customDateTimeFormatter
                )
            holder.raceDateItemView.typeface =
                if (it.isAfter(Instant.now())) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
        current.circuit.location.flag?.let {
            val s = "https://www.countryflags.io/$it/flat/64.png"
            Glide
                .with(context)
                .load(s)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(holder.flagItemView)
        }
        itemV
    }


    internal fun setRaces(races: List<Race>) {
        this.races = races
        notifyDataSetChanged()
    }

    override fun getItemCount() = races.size
}