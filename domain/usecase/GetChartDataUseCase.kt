package org.babetech.borastock.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import org.babetech.borastock.data.models.*
import org.babetech.borastock.data.repository.StockRepository

class GetChartDataUseCase(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): ChartData {
        val stockItems = repository.getAllStockItems().first()
        val stockEntries = repository.getAllStockEntries().first()
        val stockExits = repository.getAllStockExits().first()
        val suppliers = repository.getAllSuppliers().first()

        return ChartData(
            stockMovements = generateStockMovements(stockEntries, stockExits),
            categoryDistribution = generateCategoryDistribution(stockItems),
            supplierPerformance = generateSupplierPerformance(suppliers, stockEntries),
            stockLevelTrends = generateStockLevelTrends(stockItems)
        )
    }

    fun getChartDataFlow(): Flow<ChartData> {
        return combine(
            repository.getAllStockItems(),
            repository.getAllStockEntries(),
            repository.getAllStockExits(),
            repository.getAllSuppliers()
        ) { stockItems, stockEntries, stockExits, suppliers ->
            ChartData(
                stockMovements = generateStockMovements(stockEntries, stockExits),
                categoryDistribution = generateCategoryDistribution(stockItems),
                supplierPerformance = generateSupplierPerformance(suppliers, stockEntries),
                stockLevelTrends = generateStockLevelTrends(stockItems)
            )
        }
    }

    private fun generateStockMovements(
        entries: List<StockEntry>,
        exits: List<StockExit>
    ): List<StockMovementChart> {
        val last7Days = generateLast7Days()
        
        return last7Days.map { date ->
            val dayEntries = entries.filter { it.entryDate.startsWith(date) }
            val dayExits = exits.filter { it.exitDate.startsWith(date) }
            
            val entriesValue = dayEntries.sumOf { it.totalValue }
            val exitsValue = dayExits.sumOf { it.totalValue }
            
            StockMovementChart(
                date = date,
                entries = entriesValue,
                exits = exitsValue,
                netMovement = entriesValue - exitsValue
            )
        }
    }

    private fun generateCategoryDistribution(stockItems: List<StockItem>): List<CategoryDistribution> {
        val categoryGroups = stockItems.groupBy { it.category }
        val totalValue = stockItems.sumOf { it.totalValue }
        
        return categoryGroups.map { (category, items) ->
            val categoryValue = items.sumOf { it.totalValue }
            CategoryDistribution(
                category = category,
                count = items.size,
                value = categoryValue,
                percentage = if (totalValue > 0) (categoryValue / totalValue * 100).toFloat() else 0f
            )
        }
    }

    private fun generateSupplierPerformance(
        suppliers: List<Supplier>,
        entries: List<StockEntry>
    ): List<SupplierPerformance> {
        return suppliers.map { supplier ->
            val supplierEntries = entries.filter { it.supplierId == supplier.id }
            SupplierPerformance(
                supplierName = supplier.name,
                totalOrders = supplierEntries.size,
                totalValue = supplierEntries.sumOf { it.totalValue },
                reliability = supplier.reliability.label,
                rating = supplier.rating
            )
        }.sortedByDescending { it.totalValue }
    }

    private fun generateStockLevelTrends(stockItems: List<StockItem>): List<StockLevelTrend> {
        val last7Days = generateLast7Days()
        
        return last7Days.map { date ->
            StockLevelTrend(
                date = date,
                totalItems = stockItems.size,
                inStock = stockItems.count { it.stockStatus == StockStatus.IN_STOCK },
                lowStock = stockItems.count { it.stockStatus == StockStatus.LOW_STOCK },
                outOfStock = stockItems.count { it.stockStatus == StockStatus.OUT_OF_STOCK }
            )
        }
    }

    private fun generateLast7Days(): List<String> {
        // Simple date generation for the last 7 days
        // In a real app, you'd use proper date handling
        return listOf("2024-01-01", "2024-01-02", "2024-01-03", "2024-01-04", "2024-01-05", "2024-01-06", "2024-01-07")
    }
}