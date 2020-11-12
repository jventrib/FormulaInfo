package com.jventrib.formulainfo.about

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
        binding.textIcon.text = Html.fromHtml(
            "<div>Icon made by <a href=\"https://www.flaticon.com/authors/freepik\" " +
                    "title=\"Freepik\">Freepik</a> from <a href=\"https://www.flaticon.com/\" " +
                    "title=\"Flaticon\">www.flaticon.com</a></div>",
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.textIcon.movementMethod = LinkMovementMethod.getInstance()
        return binding.root
    }
}