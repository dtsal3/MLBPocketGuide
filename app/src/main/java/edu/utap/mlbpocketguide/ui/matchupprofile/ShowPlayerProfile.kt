package edu.utap.mlbpocketguide.ui.matchupprofile

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import edu.utap.mlbpocketguide.R
import edu.utap.mlbpocketguide.databinding.FragPlayerProfileBinding
import edu.utap.mlbpocketguide.ui.search.SearchPlayers
import kotlin.math.floor


class ShowPlayerProfile: Fragment(){

    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart
    private val comparisonViewModel: ComparisonViewModel by activityViewModels()
    private var _binding: FragPlayerProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): ShowPlayerProfile {
            return ShowPlayerProfile()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Player Spotlight"
        _binding = FragPlayerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Select a Favorite
        binding.searchFavorites.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SearchPlayers.newInstance("searchFavoriteProfiles"), "search")
                .commitNow()
        }
        // Select any Player
        binding.searchAll.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragContainer, SearchPlayers.newInstance("searchAnyProfiles"), "search")
                .commitNow()
        }

        // Show the player name in the profile page
        if (comparisonViewModel.checkPlayerProfile()) {
            val fullName = comparisonViewModel.getPlayerForProfile().firstName + " " + comparisonViewModel.getPlayerForProfile().lastName
            binding.playerProfileName.text = fullName
        }

        // Fetch that players stats
        binding.fetchProfileData.setOnClickListener {
            if (comparisonViewModel.checkPlayerProfile()) {
                comparisonViewModel.fetchPlayerProfileStats()
            }
        }

        // Set up our pie chart before loading data
        pieChart = binding.pieChartView
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.setDrawCenterText(true)
        pieChart.isRotationEnabled = true
        pieChart.legend.isEnabled = false
        pieChart.holeRadius = 0f
        pieChart.transparentCircleRadius = 0f
        val l = pieChart.legend
        pieChart.legend.isWordWrapEnabled = true
        pieChart.legend.isEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // position
        l.formSize = 14F
        l.formToTextSpace = 0f
        l.form = Legend.LegendForm.LINE
        l.textSize = 10f
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.isWordWrapEnabled = true


        val pieColors: ArrayList<Int> = ArrayList()
        pieColors.add(ContextCompat.getColor(this.requireContext(), R.color.blueGray))
        pieColors.add(ContextCompat.getColor(this.requireContext(), R.color.mediumBlue))
        pieColors.add(ContextCompat.getColor(this.requireContext(), R.color.teal_700))
        pieColors.add(ContextCompat.getColor(this.requireContext(), R.color.teal_200))
        pieColors.add(ContextCompat.getColor(this.requireContext(), R.color.lightBlue))

        // describe what is found in the piechart if tapping the info icon
        binding.hittingOutcomesInfo.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Got It") { dialog, _ ->
                    dialog.cancel()
                }
                .setTitle("Hitting Outcome Explanation")
                .setMessage("""
                |The Pie Chart describes the different batting outcomes for the player 
                |
                |If the player observed is a hitter, this chart shows the distribution of hits, homeruns, walks, strikeouts and other outs that hitter performed in their career. The sum is the total plate appearances for that hitter.
                |
                |If the player observed is a pitcher, this chart shows the distribution of hits, homeruns, walks, strikeouts and other outs that pitcher gave up in their career. The sum is the total number of batters faced for that pitcher.
                """.trimMargin())
            val alert = dialogBuilder.create()
            alert.show()
        }

        // set up our line chart before loading data
        lineChart = binding.lineGraphView
        lineChart.setBackgroundColor(ContextCompat.getColor(this.requireContext(), R.color.white))
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(false)
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.labelRotationAngle = -30f
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.axisRight.setDrawLabels(false)
        lineChart.axisRight.setDrawGridLines(false)
        lineChart.axisRight.axisLineColor = ContextCompat.getColor(this.requireContext(), R.color.white)
        lineChart.legend.isEnabled = false

        // describe what is found in the linechart if tapping the info icon
        binding.hittingAveragesInfo.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Got It") { dialog, _ ->
                    dialog.cancel()
                }
                .setTitle("Performance Averages Explanation")
                .setMessage("""
                |The Line Chart shows the key performance indicator year-over-year for the player. 
                |
                |If the player observed is a hitter, we are observing their batting average, which is the percentage of at-bats that result in a hit. A strong performance is about 0.300 
                |
                |If the player observed is a pitcher, we are observing their earned run average, which is the number of runs given up per 9 innings pitched. A strong performance is about 3.20
                """.trimMargin())
            val alert = dialogBuilder.create()
            alert.show()
        }

        // Listen for and display the data
        comparisonViewModel.observeLivingPlayerStats().observe(viewLifecycleOwner) { it ->
            // Update Characteristics
            binding.profileAge.text = it.profileCharacteristics["playerAge"]
            binding.profilePos.text = it.profileCharacteristics["playerPosition"]!!.split("/")[0] // I don't like how it looks with multiple positions, so we show the primary
            binding.profileThrow.text = it.profileCharacteristics["playerThrow"]
            binding.profileHit.text = it.profileCharacteristics["playerHit"]

            // Update the pie chart
            val pieEntries: ArrayList<PieEntry> = ArrayList()
            pieEntries.add(PieEntry(it.profileStatsCounting["HR"]!!.toFloat(), "HRs"))
            pieEntries.add(PieEntry(it.profileStatsCounting["RemHITS"]!!.toFloat(), "Hits"))
            pieEntries.add(PieEntry(it.profileStatsCounting["BB"]!!.toFloat(), "BBs"))
            pieEntries.add(PieEntry(it.profileStatsCounting["SO"]!!.toFloat(), "Ks"))
            pieEntries.add(PieEntry(it.profileStatsCounting["OUTS"]!!.toFloat(), "Outs"))
            val dataSet = PieDataSet(pieEntries,"")
            dataSet.sliceSpace = 3f
            dataSet.colors = pieColors
            val data = PieData(dataSet)
            data.setValueTextSize(14f)
            data.setValueTypeface(Typeface.DEFAULT)


            pieChart.data = data
            pieChart.highlightValues(null)
            pieChart.invalidate()

            // Update the line chart
            val stat: String
            when (it.profileCharacteristics["playerPosition"]) {
                "P" -> {
                    stat = "ERA"
                }
                else -> {
                    stat = "AVG"
                    lineChart.axisLeft.axisMaximum = 0.45f
                }
            }
            val chartData = it.profileStatsAvg[stat]
            val chartValues = arrayListOf<Entry>()
            val chartLabels = arrayListOf<String>()
            var maxEra = 0f
            chartData!!.forEach {
                if (it.second.toFloat() > maxEra) {maxEra = it.second.toFloat()}
                chartValues.add(Entry(it.first.toFloat(), it.second.toFloat()))
                Log.d("TracingLabels", "We are adding this: %s to our labels".format(it.first.toString()))
                chartLabels.add(it.first.toString())
            }
            when(stat) {
                "ERA" -> lineChart.axisLeft.axisMaximum = floor(maxEra.toDouble() + 1).toFloat()
            }
            val lineDataSet = LineDataSet(chartValues,"")
            lineDataSet.setDrawValues(false)
            val lineData = LineData(lineDataSet)
            binding.statToShow.text = stat
            lineChart.xAxis.granularity = 1.0f
            lineChart.xAxis.isGranularityEnabled = true

            val xAxisFormatter = object: ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    Log.d("TracingLabels", "We are getting this: %s in our getAxisLabels".format(value.toInt().toString()))
                    return value.toInt().toString()
                }
            }
            with(lineChart.xAxis) {
                valueFormatter = xAxisFormatter
            }
            lineChart.data = lineData
            lineChart.invalidate()

        }

    }

}