package com.japnoor.anticorruption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import com.denzcoskun.imageslider.constants.ActionTypes
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemChangeListener
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.interfaces.TouchListener
import com.denzcoskun.imageslider.models.SlideModel
import com.japnoor.anticorruption.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHomeBinding
    lateinit var homeScreen : HomeScreen

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
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
         homeScreen=activity as HomeScreen

//        val imageList = ArrayList<SlideModel>()
//
////        imageList.add(SlideModel(R.drawable.a11))
//        imageList.add(SlideModel(R.drawable.a11,"Tap for more info"))
////        imageList.add(SlideModel(R.drawable.a13))
//        binding.imageSlider.setImageList(imageList)
//
//
//        binding.buttonTrans.setOnClickListener{
//            var intent=Intent(Intent.ACTION_VIEW)
//            var url="https://punjab.gov.in/"
//            intent.data= Uri.parse(url)
//            startActivity(intent)
//        }

                val imageList = ArrayList<SlideModel>() // Create image list


        imageList.add(SlideModel(R.drawable.nocorr))
        imageList.add(SlideModel(R.drawable.briberymoney))
        imageList.add(SlideModel(R.drawable.how_to_stop_corruption))
        imageList.add(SlideModel(R.drawable.crosscorruption))
        imageList.add(SlideModel(R.drawable.stepsscoruu))
        binding.imageSlider.setImageList(imageList,ScaleTypes.FIT)

        binding.imageSlider.setItemChangeListener(object : ItemChangeListener {
            override fun onItemChanged(position: Int) {
                if(position==0){
                    binding.buttonTrans1.visibility=View.VISIBLE
                    binding.buttonTrans2.visibility=View.GONE
                    binding.buttonTrans3.visibility=View.GONE
                    binding.buttonTrans4.visibility=View.GONE
                    binding.buttonTrans5.visibility=View.GONE
                    binding.buttonTrans1.setOnClickListener {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.unglobalcompact.org/take-action/action/anti-corruption-in-business"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
                else if(position==1){
                    binding.buttonTrans1.visibility=View.GONE
                    binding.buttonTrans2.visibility=View.VISIBLE
                    binding.buttonTrans3.visibility=View.GONE
                    binding.buttonTrans4.visibility=View.GONE
                    binding.buttonTrans5.visibility=View.GONE
                    binding.buttonTrans2.setOnClickListener {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.skillcast.com/blog/reduce-bribery-corruption-risks"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
                else if(position==2){
                    binding.buttonTrans1.visibility=View.GONE
                    binding.buttonTrans2.visibility=View.GONE
                    binding.buttonTrans3.visibility=View.VISIBLE
                    binding.buttonTrans4.visibility=View.GONE
                    binding.buttonTrans5.visibility=View.GONE
                    binding.buttonTrans3.setOnClickListener {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.transparency.org/en/news/how-to-stop-corruption-5-key-ingredients"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
                else if(position==3){
                    binding.buttonTrans1.visibility=View.GONE
                    binding.buttonTrans2.visibility=View.GONE
                    binding.buttonTrans3.visibility=View.GONE
                    binding.buttonTrans5.visibility=View.GONE
                    binding.buttonTrans4.visibility=View.VISIBLE
                    binding.buttonTrans4.setOnClickListener {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://www.ibac.vic.gov.au/preventing-corruption/you-can-help-prevent-corruption"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
                else if(position==4){
                    binding.buttonTrans1.visibility=View.GONE
                    binding.buttonTrans2.visibility=View.GONE
                    binding.buttonTrans3.visibility=View.GONE
                    binding.buttonTrans4.visibility=View.GONE
                    binding.buttonTrans5.visibility=View.VISIBLE
                    binding.buttonTrans5.setOnClickListener {
                        var intent = Intent(Intent.ACTION_VIEW)
                        var url =
                            "https://indiaforensic.com/certifications/top-ten-things-to-prevent-the-corruption-in-india/"
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
            }
        })

        binding.cardComplaints.setOnClickListener {
            homeScreen.navController.navigate(R.id.action_homeFragment_to_clickedComplaintFagment)
        }

        binding.cardDemand.setOnClickListener {
            homeScreen.navController.navigate(R.id.action_homeFragment_to_userTotalDemandFragment)


        }
            return binding.root
    }


}