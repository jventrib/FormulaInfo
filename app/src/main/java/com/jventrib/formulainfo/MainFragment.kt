package com.jventrib.formulainfo

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jventrib.formulainfo.databinding.FragmentMainBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class MainFragment : Fragment() {
    private lateinit var navController: NavController

    private val seasonList = (1950..2020).toList().reversed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).setSupportActionBar(binding.myToolbar)

//        val findNavController = view.findNavController()
//        navController = findNavController

        val navHostFragment = childFragmentManager.fragments[0] as NavHostFragment
        navController = navHostFragment.navController
        binding.myToolbar.setupWithNavController(navController)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)

        val item: MenuItem = menu.findItem(R.id.spinner)
        val spinner = item.actionView as Spinner
        spinner.adapter = ArrayAdapter(
            requireContext(), R.layout.spinner_season, seasonList
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val application = requireActivity().application as Application
        val viewModel: MainViewModel by activityViewModels {
            application.appContainer.getViewModelFactory(::MainViewModel)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setSeason(seasonList[position])
                navController.navigateUp()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        //About fragment handling
        val menuItem = menu.findItem(R.id.menu_action_about)
        menuItem.setOnMenuItemClickListener {
            navController.navigate(NavGraphDirections.actionGlobalAboutFragment())
            true
        }
//
//        }
    }


}