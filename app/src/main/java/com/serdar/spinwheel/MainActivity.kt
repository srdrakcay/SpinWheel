package com.serdar.spinwheel

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.serdar.spinwheel.databinding.ActivityMainBinding
import com.serdar.whell.R
import com.serdar.whell.wheel.SpinWheelRoundSelectedListener
import com.serdar.whell.wheel.WheelItem
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val data = ArrayList<WheelItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSpinData()
        binding.customSpinWheel.setSpinData(data)
        binding.customSpinWheel.setSpinRound(getRandomRound())
        binding.customSpinWheel.setSpinCenterImage(R.drawable.ic_center_image)
        binding.customSpinWheel.setOnClickListener {
        binding.customSpinWheel.startSpinWheel(getRandomIndex())
        binding.customSpinWheel.setCursorAnimate()
        }
    }

    private fun setSpinData() {
        data.add(
            WheelItem(
                credit = "100",
                iconRes = R.drawable.ic_money,
                color = Color.parseColor("#fcdf03")
            )
        )
        data.add(
            WheelItem(
                credit = "250",
                iconRes = R.drawable.ic_money,
                color = Color.parseColor("#fc6703")
            )
        )

        data.add(
            WheelItem(
                credit = "500",
                iconRes = R.drawable.ic_money,
                color = Color.parseColor("#fcdf03")
            )
        )

        data.add(
            WheelItem(
                credit = "RESPIN",
                iconRes = R.drawable.ic_money,
                color = Color.parseColor("#fc6703")
            )
        )

        data.add(
            WheelItem(
                credit = "750",
                iconRes = R.drawable.ic_money,
                color = Color.parseColor("#fcdf03")
            )
        )

        data.add(
            WheelItem(
                credit = "1000",
                iconRes = R.drawable.ic_money,
                color = Color.parseColor("#fc6703")
            )
        )
        wheelListener()
    }

    private fun wheelListener() {
        binding.customSpinWheel.setSpinRoundItemSelectedListener(object :
            SpinWheelRoundSelectedListener {
            override fun roundItemSelected(index: Int) {
                when (index) {
                    1 -> {
                        Toast.makeText(this@MainActivity, data[0].credit, Toast.LENGTH_SHORT)
                            .show()
                    }

                    2 -> {
                        Toast.makeText(this@MainActivity, data[1].credit, Toast.LENGTH_SHORT)
                            .show()


                    }

                    3 -> {
                        Toast.makeText(this@MainActivity, data[2].credit, Toast.LENGTH_SHORT)
                            .show()

                    }

                    4 -> {
                        Toast.makeText(this@MainActivity, data[3].credit, Toast.LENGTH_SHORT)
                            .show()

                    }

                    5 -> {
                        Toast.makeText(this@MainActivity, data[4].credit, Toast.LENGTH_SHORT)
                            .show()

                    }

                    6 -> {
                        Toast.makeText(this@MainActivity, data[5].credit, Toast.LENGTH_SHORT)
                            .show()
                    }

                }
            }
        })
    }

    private fun getRandomIndex(): Int {
        val ind = intArrayOf(1, 2, 3, 4, 5, 6)
        val rand = Random().nextInt(ind.size)
        return ind[rand]
    }


    private fun getRandomRound(): Int {
        val rand = Random()
        return rand.nextInt(10) + 15
    }
}