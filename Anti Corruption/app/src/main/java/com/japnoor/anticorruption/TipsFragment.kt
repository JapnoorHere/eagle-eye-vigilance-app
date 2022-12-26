package com.japnoor.anticorruption

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.japnoor.anticorruption.databinding.FragmentTipsBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class NotificationsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentTipsBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentTipsBinding.inflate(layoutInflater,container,false)

        binding.img1.setOnClickListener {
            var intent=Intent(Intent.ACTION_VIEW)
            var url="https://www.transparency.org/en/news/how-to-stop-corruption-5-key-ingredients"
            intent.data= Uri.parse(url)
            startActivity(intent)

        }
        binding.img2.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW)
            var url =
                "https://www.skillcast.com/blog/reduce-bribery-corruption-risks"
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        binding.img3.setOnClickListener {
            var intent=Intent(Intent.ACTION_VIEW)
            var url="https://www.unglobalcompact.org/take-action/action/anti-corruption-in-business"
            intent.data= Uri.parse(url)
            startActivity(intent)
        }

        return binding.root
    }


}