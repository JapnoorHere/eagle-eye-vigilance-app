package com.japnoor.anticorruption

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.japnoor.anticorruption.databinding.FragmentImportantLinksBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImportantLinks.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImportantLinks : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var homeScreen: HomeScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeScreen = activity as HomeScreen
        var binding = FragmentImportantLinksBinding.inflate(layoutInflater, container, false)
        binding.punjabGovernment.setOnClickListener {
            var intent = Intent(homeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://punjab.gov.in/")
            homeScreen.startActivity(intent)
        }
        binding.vigilanceBureau.setOnClickListener {
            var intent = Intent(homeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://vigilancebureau.punjab.gov.in/")
            homeScreen.startActivity(intent)
        }
        binding.punjabpolice.setOnClickListener {
            var intent = Intent(homeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://www.punjabpolice.gov.in/")
            homeScreen.startActivity(intent)
        }
        binding.punjabArmedPolice.setOnClickListener {
            var intent = Intent(homeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://armedpolice.punjab.gov.in//")
            homeScreen.startActivity(intent)
        }
        binding.punjabPoliceAcademy.setOnClickListener {
            var intent = Intent(homeScreen, LinkActivity::class.java)
            intent.putExtra("url", "https://www.highcourtchd.gov.in/index.php/")
            homeScreen.startActivity(intent)
        }
return binding.root
    }



}