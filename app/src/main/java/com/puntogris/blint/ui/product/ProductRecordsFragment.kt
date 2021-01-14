package com.puntogris.blint.ui.product

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentProductRecordsBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.ui.custom_views.DataPoint
import com.puntogris.blint.ui.custom_views.MonthlyPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductRecordsFragment : BaseFragment<FragmentProductRecordsBinding>(R.layout.fragment_product_records) {

    private val viewModel: ProductViewModel by viewModels()

    override fun initializeViews() {
 //       val productRecordsAdapter = ProductRecordsAdapter { onRecordClickListener(it) }
//        binding.recyclerView.adapter = productRecordsAdapter
//        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        arguments?.takeIf { it.containsKey("product_key") }?.apply {
//            getParcelable<Product>("product_key")?.let { productBundle->
//                viewModel.setProductData(productBundle)
//                lifecycleScope.launch {
//                    viewModel.getProductRecords().collect {
//                        productRecordsAdapter.submitData(it)
//                    }
//                }
//            }
//        }
        val lineSet = listOf(
            "label1" to 5f,
            "label2" to 4.5f,
            "label3" to 4.7f,
            "label4" to 3.5f,
            "label5" to 3.6f,
            "label6" to 7.5f,
            "label7" to 7.5f,
            "label8" to 10f,
            "label9" to 5f,
            "label10" to 6.5f,
            "label11" to 3f,
            "label12" to 4f
        )
//        binding.chart.apply{
//            gradientFillColors =
//                intArrayOf(
//                    Color.parseColor("#81FFFFFF"),
//                    Color.TRANSPARENT
//                )
//            animation.duration = 200
//            tooltip =
//                SliderTooltip().also {
//                    it.color = Color.WHITE
//                }
//            onDataPointTouchListener = { index, _, _ ->
//
//            }
//            animate(lineSet)
//
//        }
        binding.rallyLine.setCurveBorderColor(requireActivity().intent.getIntExtra(KEY_COLOR, R.color.rally_dark_green))
        setUpTab()

        binding.rallyLine.addDataPoints(getRandomPoints())

    }

    private fun onRecordClickListener(record: Record){

    }

    private fun setUpTab() {
        binding.tab.addTabs(
            listOf(
                "Jan 2018", "Feb 2018", "Mar 2018", "Apr 2018", "May 2018", "Jun 2018", "July 2018",
                "Aug 2018", "Sep 2018", "Oct 2018", "Nov 2018", "Dec 2018"
            )
        )
        binding.tab.addOnPageChangeListener {
            println("asdasd")
            binding.rallyLine.addDataPoints(getRandomPoints())
        }

    }

    fun getRandomPoints(): MutableList<DataPoint> {
        val list = mutableListOf<DataPoint>()
        val range = (0..10)

        (1..15).forEach { _ ->
            list.add(DataPoint(range.random()*100f))
        }
        return list
    }
    companion object {
        private const val KEY_COLOR = "key-color"
        fun start(
            context: Context,
            shareView: View, @ColorRes color: Int
        ) {
            val intent = Intent(context, this::class.java)
            intent.putExtra(KEY_COLOR, color)

           // val pair = Pair(shareView.findViewById<View>(id.shareView), "DetailView")
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(context as AppCompatActivity)

            val transition = context.window.exitTransition
            transition.excludeTarget(shareView, true)
            context.window.exitTransition = transition

            context.startActivity(intent, options.toBundle())
        }
    }
}