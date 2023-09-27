package com.example.stack.plan


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRailDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.stack.NavigationDirections
import com.example.stack.databinding.FragmentPlanTinderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlanTinderFragment : Fragment() {
    lateinit var binding: FragmentPlanTinderBinding
//    private val viewModel by viewModels<TinderViewModel> { getVmFactory() }
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val swipeResults = mutableListOf<Int>()
        val mapOfResults:MutableMap<String, Int> = mutableMapOf("A" to 0, "B" to 0, "C" to 0)

        binding = FragmentPlanTinderBinding.inflate(inflater, container, false)
//        return binding.root

        return ComposeView(requireContext()).apply {
            setContent {
                TinderCloneTheme {
                    var isEmpty by remember { mutableStateOf(false) }
                    var showOverlay by remember { mutableStateOf(true) }

                    Scaffold {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (!isEmpty) {
                                    CardStack(
                                        swipeResults = mapOfResults,
                                        items = accounts,
                                        styles = styles,
                                        onEmptyStack = {
                                            isEmpty = true
                                        }
                                    )
                                } else {
                                    Text(text = "No more cards", fontWeight = FontWeight.Bold)
                                    findNavController().navigate(NavigationDirections.navigateToPlanFragment())
                                    //post api to submit the outcomes
                                    //get the outcome from api and then
                                    Log.i("tinder", "$mapOfResults")
                                    var maxMap = Pair("A",0)
                                    for(map in mapOfResults){
                                        if(map.value > maxMap.second){
                                            maxMap = Pair(map.key, map.value)
                                            if (mapOfResults["B"] == mapOfResults["C"] && mapOfResults["B"] == 1 && mapOfResults["A"] == 0) {
                                                maxMap = Pair("A", 0)
                                            }
                                        }
                                    }
//                                    UserManager.marketingStyle = maxMap.first
                                    Log.i("tinder", "$maxMap")
//                                    findNavController().navigate(NavigationDirections.navigateToTinderSuccessFragment())

                                }
                            }
                            if (showOverlay) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.7f))
                                        .zIndex(1f)
                                        .clickable {
                                            showOverlay = false
                                        }// Place this on top
                                ) {
                                    // Add your content for the overlay here
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight() // Height wraps the content
                                            .padding(20.dp)
                                            .align(Alignment.Center)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "",
                                            tint = peachPink,
                                            modifier = Modifier.size(50.dp)
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            Icons.Default.FavoriteBorder,
                                            contentDescription = "",
                                            tint = green10,
                                            modifier = Modifier.size(50.dp)
                                        )
                                    }
                                    val align= BiasAlignment(0f, -0.3f)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight() // Height wraps the content
                                            .padding(10.dp)
                                            .align(align)
                                    ) {
                                        Icon(
                                            Icons.Default.ArrowBack,
                                            contentDescription = "",
                                            tint = peachPink,
                                            modifier = Modifier.size(70.dp)
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = "",
                                            tint = green10,
                                            modifier = Modifier.size(70.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private val accounts = mutableListOf(
        Item(
            com.example.stack.R.drawable.lean,
            "stay lean",
            ""
        ),
        Item(
            com.example.stack.R.drawable.skinny,
            "get skinny",
            ""
        ),
        Item(
            com.example.stack.R.drawable.bulking,
            "bulk",
            ""
        ),
        Item(
            com.example.stack.R.drawable.cutting,
            "cut",
            ""
        ),
        Item(
            com.example.stack.R.drawable.strength,
            "gain strength",
            ""
        )
    )
    private val styles = mutableListOf("A", "B", "C", "A", "C")
}