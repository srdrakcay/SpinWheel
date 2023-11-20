package com.serdar.spinwheel

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.serdar.spinwheel.databinding.ActivityMainBinding
import com.serdar.whell.wheel.WheelItem
import com.serdar.whell.R
import com.serdar.whell.wheel.SpinWheelRoundSelectedListener
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data= ArrayList<WheelItem>()
        val luckyItem1 = WheelItem(
            "1000",
            R.drawable.ic_whell_star,
            Color.parseColor("#EEE8EB"),
            )
        data.add(luckyItem1)

        val luckyItem2 =  WheelItem(
            "2500",
            R.drawable.ic_whell_star,
            Color.parseColor("#F356A7"),
        )
        data.add(luckyItem2)

        val luckyItem3 = WheelItem(
            "5000",
            R.drawable.ic_whell_star,
            Color.parseColor("#EEE8EB"),
        )
        data.add(luckyItem3)

        val luckyItem4 = WheelItem(
            "RESPIN",
            R.drawable.ic_whell_star,
            Color.parseColor("#F356A7"),
        )
        data.add(luckyItem4)

        val luckyItem5  = WheelItem(
            "7500",
            R.drawable.ic_whell_star,
            Color.parseColor("#EEE8EB"),
        )
        data.add(luckyItem5)

        val luckyItem6  = WheelItem(
            "10000",
            R.drawable.ic_jackpot,
            Color.parseColor("#F356A7"),
        )
        data.add(luckyItem6)
        binding.customSpinWheel.setSpinData(data)
        binding.customSpinWheel.setSpinRound(getRandomRound())

        binding.customSpinWheel.setOnClickListener {
            val index = getRandomIndex()
            binding.customSpinWheel.startSpinWheel(index)

        }
    }
    private fun wheelListener(){
        binding.customSpinWheel.setSpinRoundItemSelectedListener(object :
            SpinWheelRoundSelectedListener {
            override fun selectedRoundItemSelected(index: Int) {
                when (index) {
                    1 ->{
                        binding.customSpinWheel.setSpinText("Kazandınız 1")
                    }
                    2 ->{
                        binding.customSpinWheel.setSpinText("Kazandınız 2")

                    }
                    3 ->{
                        binding.customSpinWheel.setSpinText("Kazandınız 3")

                    }
                    4 ->{
                        binding.customSpinWheel.setSpinText("Kazandınız 4")

                    }
                    5 ->{
                        binding.customSpinWheel.setSpinText("Kazandınız 5")

                    }
                    6 ->{
                        binding.customSpinWheel.setSpinText("Kazandınız 6")

                    }

                }
            }
        })
    }
    private fun getRandomIndex(): Int {
        val ind = intArrayOf(1, 6,)
        val rand = Random().nextInt(ind.size)
        return ind[rand]
    }

    private fun getRandomRound(): Int {
        val rand = Random()
        return rand.nextInt(10) + 15
    }
}