package com.puntogris.blint.ui.custom_views.line_indicator


data class RallyLineIndicatorData(
    val portions: List<RallyLineIndicatorPortion>,
    val maxValue: Float? = null
)

data class RallyLineIndicatorPortion(
  val name: String,
  val value: Float,
  val colorInt: Int
)

fun List<RallyLineIndicatorPortion>.toPoints(maxValue: Float): List<RallyLineIndicatorRenderData> {
  val renderDataList = mutableListOf<RallyLineIndicatorRenderData>()
  forEachIndexed { index, it ->
    val percent = it.value / maxValue
    renderDataList.add(RallyLineIndicatorRenderData(it.name, percent, it.colorInt))
  }

  return renderDataList
}

data class RallyLineIndicatorRenderData(
  val name: String,
  val percentage: Float,
  val color: Int
)