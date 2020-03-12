/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.android.navigation.databinding.FragmentGameBinding
import kotlinx.android.synthetic.main.fragment_name.*

class GameFragment : Fragment() {
    //getting the data
    private val info: Information = Information("sampleName")

    //declaring binding as a global variable
    private lateinit var binding: FragmentGameBinding

    //global variable clicks
    private var clicks = 0

    //where the buttons will be stored
    private lateinit var clickableViews: List<TextView>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentGameBinding>(
                inflater, R.layout.fragment_game, container, false)

        // Bind this fragment class to the layout
        binding.game = this
        binding.info = info

        info.name = arguments?.getString("userName") ?: binding.invalidateAll().toString()

        //setting the listeners for the elements in this fragment
        setListeners()

        return binding.root
    }

    //function for when the player wins
    private fun playerWin(view: TextView) {
        val bundle = Bundle()
        bundle.putString("clicks", clicks.toString())
        view.findNavController().navigate(R.id.action_gameFragment_to_gameWonFragment, bundle)
    }

    //function for when the player wins
    private fun playerLose(view: TextView) {
        val bundle = Bundle()
        bundle.putString("clicks", clicks.toString())
        view.findNavController().navigate(R.id.action_gameFragment_to_gameOverFragment, bundle)
    }

    //function for clicking a box
    private fun click(view: TextView) {
        binding.apply {
            //updating the clicks
            clicks += 1
            clickCounter.text = resources.getString(R.string.click_text) + clicks.toString()

            //minimum clicks to win is 4, so only check when clicks are 4
            if (clicks >= 4) {
                var count = 0
                for (item in clickableViews) {
                    if (item.text == resources.getString(R.string.off_text)) {
                        count += 1
                    }
                }

                if (count == 16 && clicks <= 5) {
                    Log.d("Clicks: ", clicks.toString())
                    Log.d("WIN: ", clicks.toString())
                    playerWin(view)
                } else if (clicks > 5) {
                    Log.d("LOSE: ", clicks.toString())
                    playerLose(view)
                }
            }
        }
    }

    private fun switchChecker(view: TextView) {
        //if the view is currently on, turn off
        if (view.text.toString() == resources.getString(R.string.on_text)) {
            view.setBackgroundResource(R.color.offColor)
            view.text = resources.getString(R.string.off_text)
            view.setTextColor(resources.getColor(R.color.offColor))
        } else { //else, turn on
            view.setBackgroundResource(R.color.onColor)
            view.text = resources.getString(R.string.on_text)
            view.setTextColor(resources.getColor(R.color.onColor))
        }
    }

    //function for setting the listeners
    private fun setListeners() {
        binding.apply {
            clickableViews =
                    listOf(
                            box1, box2, box3, box4, box5,
                            box6, box7, box8, box9, box10,
                            box11, box12, box13, box14, box15,
                            box16, retryButton
                    )

            for (i in 0 until clickableViews.count()) {
                if (clickableViews[i] != retryButton) {
                    clickableViews[i].setOnClickListener {
                        switchChecker(clickableViews[i])

                        //if i is not 0,1,2,3, it has a tile above it
                        if (i !in intArrayOf(0, 1, 2, 3)) {
                            switchChecker(clickableViews[i - 4])
                        }

                        //if i is not 0,4,8,12, it has a tile to the left of it
                        if (i !in intArrayOf(0, 4, 8, 12)) {
                            switchChecker(clickableViews[i - 1])
                        }

                        //if i is not 3,7,11,15, it has a tile to the right of it
                        if (i !in intArrayOf(3, 7, 11, 15)) {
                            switchChecker(clickableViews[i + 1])
                        }

                        //if i is not 12,13,14,15, it has a tile below it
                        if (i !in intArrayOf(12, 13, 14, 15)) {
                            switchChecker(clickableViews[i + 4])
                        }

                        click(clickableViews[i])
                    }
                } else {
                    clickableViews[i].setOnClickListener {
                        retry()
                    }
                }
            }
        }
    }

    //function for retrying, catches both retry during game and when player wins
    private fun retry() {
        binding.apply {

            for (item in clickableViews) {
                if (item != retryButton) {
                    //changing background color back to yellow
                    item.setBackgroundResource(R.color.onColor)

                    //changing text color of the checker (only the programmer will see this)
                    item.setTextColor(resources.getColor(R.color.onColor))

                    //changing the text (only the programmer will see this)
                    item.text = resources.getString(R.string.on_text)
                }
            }

            //updating number of clicks
            clickCounter.text = resources.getString(R.string.clicks_text)
            clicks = 0
        }
    }


}
