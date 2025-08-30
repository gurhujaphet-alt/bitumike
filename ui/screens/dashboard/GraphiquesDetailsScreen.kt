package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import org.babetech.borastock.data.models.ChartData
import org.babetech.borastock.ui.screens.dashboard.viewmodel.GraphicViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphiquesDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: GraphicViewModel = koinViewModel()
) {
    val chartData by viewModel.chartData.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var selectedIndicator by remember { mutableStateOf("Mouvements") }

    val indicators = listOf(
        "Mouvements" to "Évolution des entrées/sorties",
        "Catégories" to "Distribution par catégorie",
        "Fournisseurs" to "Performance des fournisseurs",
        "Rentabilité" to "Analyse de rentabilité",
        "Tendances" to "Tendances des stocks"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Analyses Détaillées",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicator selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Indicateurs",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(indicators) { (key, description) ->
                                FilterChip(
                                    onClick = { selectedIndicator = key },
                                    label = { Text(key) },
                                    selected = selectedIndicator == key,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Main chart area
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp)
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
                            when (selectedIndicator) {
                                "Mouvements" -> StockMovementChart(data)
                                "Catégories" -> CategoryDistributionChart(data)
                                "Fournisseurs" -> SupplierPerformanceChart(data)
                                "Rentabilité" -> ProfitabilityChart(data)
                                "Tendances" -> StockTrendsChart(data)
                            }
                        }
                    }
                }
            }

            // Key metrics summary
            item {
                chartData?.let { data ->
                    KeyMetricsSummary(data, selectedIndicator)
                }
            }

            // Insights and recommendations
            item {
                chartData?.let { data ->
                    InsightsCard(data, selectedIndicator)
                }
            }
        }
    }
}

@Composable
fun ProfitabilityChart(chartData: ChartData) {
    val profitData = chartData.stockMovements.map { movement ->
        movement.entries - movement.exits
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Analyse de Rentabilité",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.euro),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Analyse de Rentabilité",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Profit moyen: €${profitData.average().toInt()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StockTrendsChart(chartData: ChartData) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Tendances des Stocks",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.trendingup),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Tendances des Stocks",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Évolution sur 7 jours",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun KeyMetricsSummary(chartData: ChartData, selectedIndicator: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Métriques Clés",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            
            when (selectedIndicator) {
                "Mouvements" -> {
                    val totalEntries = chartData.stockMovements.sumOf { it.entries }
                    val totalExits = chartData.stockMovements.sumOf { it.exits }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricItem("Entrées", "€${totalEntries.toInt()}", Color(0xFF22c55e))
                        MetricItem("Sorties", "€${totalExits.toInt()}", Color(0xFFef4444))
                        MetricItem("Net", "€${(totalEntries - totalExits).toInt()}", Color(0xFF3b82f6))
                    }
                }
                "Catégories" -> {
                    val topCategory = chartData.categoryDistribution.maxByOrNull { it.value }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MetricItem("Catégories", "${chartData.categoryDistribution.size}", Color(0xFF3b82f6))
                        MetricItem("Top Catégorie", topCategory?.category ?: "N/A", Color(0xFF22c55e))
                        MetricItem("Valeur Max", "€${topCategory?.value?.toInt() ?: 0}", Color(0xFFf59e0b))
                    }
                }
                else -> {
                    Text(
                        "Métriques pour $selectedIndicator",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun InsightsCard(chartData: ChartData, selectedIndicator: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.analytics),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Insights & Recommandations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            when (selectedIndicator) {
                "Mouvements" -> {
                    val netMovement = chartData.stockMovements.sumOf { it.netMovement }
                    Text(
                        if (netMovement > 0) 
                            "✅ Tendance positive: Plus d'entrées que de sorties" 
                        else 
                            "⚠️ Attention: Plus de sorties que d'entrées",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                "Catégories" -> {
                    val lowStockCategories = chartData.categoryDistribution.filter { it.count < 5 }
                    if (lowStockCategories.isNotEmpty()) {
                        Text(
                            "⚠️ Catégories avec peu de produits: ${lowStockCategories.joinToString { it.category }}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            "✅ Bonne diversification des catégories",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                else -> {
                    Text(
                        "Analysez les données pour optimiser votre gestion de stock",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}