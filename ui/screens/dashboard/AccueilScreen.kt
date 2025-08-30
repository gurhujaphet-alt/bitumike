@@ .. @@
import org.babetech.borastock.data.models.StockStatistics
import org.babetech.borastock.ui.screens.screennavigation.AccueilUiState
import org.babetech.borastock.ui.screens.screennavigation.AccueilViewModel
+import org.babetech.borastock.ui.screens.dashboard.viewmodel.GraphicViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
@@ .. @@
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AccueilScreen(viewModel: AccueilViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
+    val graphicViewModel: GraphicViewModel = koinViewModel()
+    val chartData by graphicViewModel.chartData.collectAsStateWithLifecycle()
    val navigator = rememberSupportingPaneScaffoldNavigator()
@@ .. @@
                        is AccueilUiState.Success -> {
                            MainDashboardPane(
                                statistics = state.statistics,
                                recentMovements = state.recentMovements,
                                criticalStockItems = state.criticalStockItems,
+                                chartData = chartData,
                                showChartButton = !showSupporting,
@@ .. @@
@Composable
fun ThreePaneScaffoldScope.MainDashboardPane(
    statistics: StockStatistics,
    recentMovements: List<RecentMovement>,
    criticalStockItems: List<StockItem>,
+    chartData: ChartData?,
    showChartButton: Boolean,
@@ .. @@
            item { DashboardMetricsGrid(statistics) }
+            chartData?.let { data ->
+                item { 
+                    MiniStockChart(
+                        chartData = data,
+                        modifier = Modifier.fillMaxWidth()
+                    )
+                }
+            }
            item { RecentMovementsList(recentMovements) }