package org.babetech.borastock.data.models

import androidx.compose.ui.graphics.Color

/**
 * Data models for chart functionality
 */
data class StockMovementChart(
    val date: String,
    val entries: Double,
    val exits: Double,
    val netMovement: Double
)

data class CategoryDistribution(
    val category: String,
    val count: Int,
    val value: Double,
    val percentage: Float
)

data class SupplierPerformance(
    val supplierName: String,
    val totalOrders: Int,
    val totalValue: Double,
    val reliability: String,
    val rating: Float
)

data class StockLevelTrend(
    val date: String,
    val totalItems: Int,
    val inStock: Int,
    val lowStock: Int,
    val outOfStock: Int
)

data class ChartData(
    val stockMovements: List<StockMovementChart>,
    val categoryDistribution: List<CategoryDistribution>,
    val supplierPerformance: List<SupplierPerformance>,
    val stockLevelTrends: List<StockLevelTrend>
)