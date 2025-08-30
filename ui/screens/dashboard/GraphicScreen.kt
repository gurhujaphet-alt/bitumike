package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.donutChart.DonutChart
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle
import org.babetech.borastock.data.models.ChartData
import org.babetech.borastock.ui.screens.dashboard.viewmodel.GraphicViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

data class ChartType(
    val key: String,
    val title: String,
    val icon: Painter,
    val description: String
)

@Composable
fun GraphicSwitcherScreen(
    viewModel: GraphicViewModel = koinViewModel()
) {
    val chartData by viewModel.chartData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var selectedChart by remember { mutableStateOf("Line") }

    val chartTypes = listOf(
        ChartType("Line", "Mouvements", painterResource(Res.drawable.analytics), "Évolution des stocks"),
        ChartType("Bar", "Catégories", painterResource(Res.drawable.barchart), "Distribution par catégorie"),
        ChartType("Pie", "Fournisseurs", painterResource(Res.drawable.piechart), "Performance fournisseurs"),
        ChartType("Donut", "Statuts", painterResource(Res.drawable.donutlarge), "États des stocks"),
        ChartType("Radar", "Performance", painterResource(Res.drawable.analytics), "Vue d'ensemble")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chartTypes) { chartType ->
                ElevatedCard(
                    onClick = { selectedChart = chartType.key },
                    modifier = Modifier.width(140.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selectedChart == chartType.key) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = chartType.icon,
                            contentDescription = chartType.title,
                            tint = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            chartType.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            chartType.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                chartData?.let { data ->
                    when (selectedChart) {
                        "Line" -> StockMovementChart(data)
                        "Bar" -> CategoryDistributionChart(data)
                        "Pie" -> SupplierPerformanceChart(data)
                        "Donut" -> StockStatusChart(data)
                        "Radar" -> PerformanceRadarChart(data)
                    }
                }
            }
        }
    }
}

@Composable
fun StockMovementChart(chartData: ChartData) {
    val lineParameters = listOf(
        LineParameters(
            label = "Entrées",
            data = chartData.stockMovements.map { it.entries },
            lineColor = Color(0xFF22c55e),
            lineType = LineType.CURVED_LINE,
            lineShadow = true
        ),
        LineParameters(
            label = "Sorties",
            data = chartData.stockMovements.map { it.exits },
            lineColor = Color(0xFFef4444),
            lineType = LineType.CURVED_LINE,
            lineShadow = true
        ),
        LineParameters(
            label = "Net",
            data = chartData.stockMovements.map { it.netMovement },
            lineColor = Color(0xFF3b82f6),
            lineType = LineType.DEFAULT_LINE,
            lineShadow = false
        )
    )

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Mouvements de Stock (7 derniers jours)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LineChart(
                modifier = Modifier.fillMaxSize(),
                linesParameters = lineParameters,
                isGrid = true,
                gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                xAxisData = chartData.stockMovements.map { it.date.takeLast(5) },
                animateChart = true,
                showGridWithSpacer = true,
                yAxisStyle = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                xAxisStyle = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.W400
                ),
                yAxisRange = 10,
                oneLineChart = false,
                gridOrientation = GridOrientation.VERTICAL
            )
        }
    }
}

@Composable
fun CategoryDistributionChart(chartData: ChartData) {
    val barParameters = chartData.categoryDistribution.mapIndexed { index, category ->
        val colors = listOf(
            Color(0xFF3b82f6),
            Color(0xFF22c55e),
            Color(0xFFf59e0b),
            Color(0xFFef4444),
            Color(0xFF8b5cf6)
        )
        
        BarParameters(
            dataName = category.category,
            data = listOf(category.value),
            barColor = colors[index % colors.size]
        )
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Distribution par Catégorie",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (barParameters.isNotEmpty()) {
                BarChart(
                    chartParameters = barParameters,
                    gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    xAxisData = chartData.categoryDistribution.map { it.category },
                    isShowGrid = true,
                    animateChart = true,
                    showGridWithSpacer = true,
                    yAxisStyle = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    xAxisStyle = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.W400
                    ),
                    yAxisRange = 10,
                    barWidth = 20.dp
                )
            }
        }
    }
}

@Composable
fun SupplierPerformanceChart(chartData: ChartData) {
    val pieChartData = chartData.supplierPerformance.take(5).mapIndexed { index, supplier ->
        val colors = listOf(
            Color(0xFF3b82f6),
            Color(0xFF22c55e),
            Color(0xFFf59e0b),
            Color(0xFFef4444),
            Color(0xFF8b5cf6)
        )
        
        PieChartData(
            partName = supplier.supplierName,
            data = supplier.totalValue,
            color = colors[index % colors.size]
        )
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Performance des Fournisseurs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (pieChartData.isNotEmpty()) {
                PieChart(
                    modifier = Modifier.fillMaxSize(),
                    pieChartData = pieChartData,
                    ratioLineColor = MaterialTheme.colorScheme.outline,
                    textRatioStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
fun StockStatusChart(chartData: ChartData) {
    val stockLevels = chartData.stockLevelTrends.lastOrNull()
    
    val pieChartData = stockLevels?.let {
        listOf(
            PieChartData(
                partName = "En Stock",
                data = it.inStock.toDouble(),
                color = Color(0xFF22c55e)
            ),
            PieChartData(
                partName = "Stock Faible",
                data = it.lowStock.toDouble(),
                color = Color(0xFFf59e0b)
            ),
            PieChartData(
                partName = "Rupture",
                data = it.outOfStock.toDouble(),
                color = Color(0xFFef4444)
            )
        )
    } ?: emptyList()

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "État des Stocks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (pieChartData.isNotEmpty()) {
                DonutChart(
                    modifier = Modifier.fillMaxSize(),
                    pieChartData = pieChartData,
                    centerTitle = "Stocks",
                    centerTitleStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    ),
                    outerCircularColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    innerCircularColor = MaterialTheme.colorScheme.surface,
                    ratioLineColor = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun PerformanceRadarChart(chartData: ChartData) {
    val categories = chartData.categoryDistribution.take(6)
    val radarLabels = categories.map { it.category }
    val values = categories.map { it.percentage.toDouble() }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Vue d'Ensemble Performance",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (radarLabels.isNotEmpty() && values.isNotEmpty()) {
                RadarChart(
                    modifier = Modifier.fillMaxSize(),
                    radarLabels = radarLabels,
                    labelsStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    ),
                    netLinesStyle = NetLinesStyle(
                        netLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        netLinesStrokeWidth = 2f,
                        netLinesStrokeCap = StrokeCap.Round
                    ),
                    scalarSteps = 5,
                    scalarValue = 100.0,
                    scalarValuesStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    ),
                    polygons = listOf(
                        Polygon(
                            values = values,
                            unit = "%",
                            style = PolygonStyle(
                                fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                fillColorAlpha = 0.5f,
                                borderColor = MaterialTheme.colorScheme.primary,
                                borderColorAlpha = 0.8f,
                                borderStrokeWidth = 2f,
                                borderStrokeCap = StrokeCap.Round
                            )
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun MiniStockChart(
    chartData: ChartData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Tendance Stock",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            if (chartData.stockMovements.isNotEmpty()) {
                val lineParameters = listOf(
                    LineParameters(
                        label = "Net",
                        data = chartData.stockMovements.map { it.netMovement },
                        lineColor = MaterialTheme.colorScheme.primary,
                        lineType = LineType.CURVED_LINE,
                        lineShadow = false
                    )
                )
                
                LineChart(
                    modifier = Modifier.fillMaxSize(),
                    linesParameters = lineParameters,
                    isGrid = false,
                    xAxisData = chartData.stockMovements.map { it.date.takeLast(2) },
                    animateChart = true,
                    showGridWithSpacer = false,
                    yAxisStyle = TextStyle(fontSize = 8.sp, color = Color.Transparent),
                    xAxisStyle = TextStyle(fontSize = 8.sp, color = Color.Transparent),
                    yAxisRange = 5,
                    oneLineChart = true,
                    gridOrientation = GridOrientation.HORIZONTAL
                )
            }
        }
    }
}