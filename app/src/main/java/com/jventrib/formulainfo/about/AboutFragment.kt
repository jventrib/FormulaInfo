package com.jventrib.formulainfo.about

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jventrib.formulainfo.R
import com.jventrib.formulainfo.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAboutBinding.inflate(inflater, container, false)
        val s = "<div>Icon made by <a href=\"https://www.flaticon.com/authors/freepik\" " +
                "title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" " +
                "title=\"Flaticon\">www.flaticon.com</a></div>"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
            // we are using this flag to give a consistent behaviour
            binding.textIcon.text = Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
        } else {
            binding.textIcon.text = Html.fromHtml(s);
        }
        binding.textIcon.movementMethod = LinkMovementMethod.getInstance()
        return binding.root
    }
}