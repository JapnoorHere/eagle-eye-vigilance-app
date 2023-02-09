package com.japnoor.anticorruption

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.japnoor.anticorruption.databinding.BlockedUserDialogBinding
import com.japnoor.anticorruption.databinding.FragmentHomeBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHomeBinding
    lateinit var homeScreen: HomeScreen
    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var adapter: ImageAdapter
    lateinit var database: FirebaseDatabase
    lateinit var useRef: DatabaseReference
    var count = 0

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
    ): View {
        homeScreen = activity as HomeScreen
        database = FirebaseDatabase.getInstance()
        useRef = database.reference.child("Users")
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)



        val textView = binding.movText
        val objectAnimator = ObjectAnimator.ofFloat(textView, "translationX", 600f, -600f)

        objectAnimator.duration = 8000
        objectAnimator.repeatCount = ObjectAnimator.INFINITE
        objectAnimator.repeatMode = ObjectAnimator.RESTART
        objectAnimator.start()


        binding.cardComplaints.setOnClickListener {
            homeScreen.navController.navigate(R.id.action_homeFragment_to_userComplaints)
        }

        binding.cardDemand.setOnClickListener {
            homeScreen.navController.navigate(R.id.action_homeFragment_to_userTotalDemandFragment)


        }

        init()
        setUpTransformer()
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 2000)
            }
        })
        return binding.root
    }

    override fun onPause() {
        super.onPause()

        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()

        handler.postDelayed(runnable, 2000)
    }

    private val runnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - kotlin.math.abs(position)
            page.scaleY = 0.85f + r * 0.14f
            page.setOnClickListener {
                when (count) {
                    0 -> {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.unglobalcompact.org/take-action/action/anti-corruption-in-business"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                    1 -> {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.skillcast.com/blog/reduce-bribery-corruption-risks"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                    2 -> {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.transparency.org/en/news/how-to-stop-corruption-5-key-ingredients"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                    3 -> {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.ibac.vic.gov.au/preventing-corruption/you-can-help-prevent-corruption"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                    4 -> {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://indiaforensic.com/certifications/top-ten-things-to-prevent-the-corruption-in-india/"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
            }
        }

        viewPager2.setPageTransformer(transformer)
    }

    private fun init() {
        viewPager2 = binding.viewpager
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()

        imageList.add(R.drawable.nocorr)
        imageList.add(R.drawable.briberymoney)
        imageList.add(R.drawable.how_to_stop_corruption)
        imageList.add(R.drawable.crosscorruption)
        imageList.add(R.drawable.stepsscoruu)

        adapter = ImageAdapter(imageList, viewPager2)

        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> count = 0
                    1 -> count = 1
                    2 -> count = 2
                    3 -> count = 3
                    4 -> count = 4

                }
            }
        })


    }


}