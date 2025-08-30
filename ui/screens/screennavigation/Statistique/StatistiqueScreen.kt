@@ .. @@
import org.babetech.borastock.data.models.ChartPeriod
import org.babetech.borastock.data.models.StatisticCard
import org.babetech.borastock.data.models.TopProduct
+import org.babetech.borastock.ui.screens.dashboard.MiniStockChart
+import org.babetech.borastock.ui.screens.dashboard.viewmodel.GraphicViewModel
import org.jetbrains.compose.resources.painterResource
@@ .. @@
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun StatistiqueScreen() {
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()
+    val graphicViewModel: GraphicViewModel = koinViewModel()
+    val chartData by graphicViewModel.chartData.collectAsStateWithLifecycle()

    var selectedPeriod by remember { mutableStateOf("7j") }
@@ .. @@
                    // Cartes de statistiques
                    item {
                        StatisticsCardsGrid(statisticCards)
                    }

+                    // Mini chart preview
+                    chartData?.let { data ->
+                        item {
+                            Card(
+                                modifier = Modifier.fillMaxWidth(),
+                                shape = RoundedCornerShape(16.dp)
+                            ) {
+                                Column(
+                                    modifier = Modifier.padding(16.dp),
+                                    verticalArrangement = Arrangement.spacedBy(12.dp)
+                                ) {
+                                    Text(
+                                        "Aperçu des Tendances",
+                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
+                                    )
+                                    MiniStockChart(
+                                        chartData = data,
+                                        modifier = Modifier.fillMaxWidth()
+                                    )
+                                }
+                            }
+                        }
+                    }

                    // Sélecteur de période
@@ .. @@
                    // Bouton pour afficher les graphiques détaillés
                    item {
                        Button(
                            onClick = {
                                scope.launch {
                                    navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painterResource(Res.drawable.analytics),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
-                                    "Voir les graphiques détaillés",
+                                    "Analyses Avancées",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }
@@ .. @@
        },
        supportingPane = {
            AnimatedPane {
-                DetailedAnalyticsPane(
-                    selectedChart = selectedChart,
-                    onChartSelected = { selectedChart = it },
-                    selectedPeriod = selectedPeriod,
-                    onBackClick = {
-                        scope.launch {
-                            navigator.navigateBack()
-                        }
-                    },
-                    showBackButton = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded
-                )
+                GraphiquesDetailsScreen(
+                    onBackClick = {
+                        scope.launch {
+                            navigator.navigateBack()
+                        }
+                    }
+                )
            }
        }
@@ .. @@
-@OptIn(ExperimentalMaterial3Api::class)
-@Composable
-private fun DetailedAnalyticsPane(
-    selectedChart: String,
-    onChartSelected: (String) -> Unit,
-    selectedPeriod: String,
-    onBackClick: () -> Unit,
-    showBackButton: Boolean
-) {
-    val scrollState = rememberScrollState()
-    val chartTypes = listOf("Ventes", "Revenus", "Commandes", "Clients")
-
-    Scaffold(
-        topBar = {
-            TopAppBar(
-                title = { Text("Analytics détaillées") },
-                navigationIcon = {
-                    if (showBackButton) {
-                        IconButton(onClick = onBackClick) {
-                            Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
-                        }
-                    }
-                }
-            )
-        }
-    ) { paddingValues ->
-        Column(
-            modifier = Modifier
-                .fillMaxSize()
-                .padding(paddingValues)
-                .padding(16.dp)
-                .verticalScroll(scrollState),
-            verticalArrangement = Arrangement.spacedBy(16.dp)
-        ) {
-            // Sélecteur de type de graphique
-            Card(
-                modifier = Modifier.fillMaxWidth(),
-                shape = RoundedCornerShape(12.dp)
-            ) {
-                Column(
-                    modifier = Modifier.padding(16.dp),
-                    verticalArrangement = Arrangement.spacedBy(12.dp)
-                ) {
-                    Text(
-                        text = "Type d'analyse",
-                        style = MaterialTheme.typography.titleMedium.copy(
-                            fontWeight = FontWeight.Bold
-                        )
-                    )
-
-                    LazyRow(
-                        horizontalArrangement = Arrangement.spacedBy(8.dp)
-                    ) {
-                        items(chartTypes) { type ->
-                            FilterChip(
-                                onClick = { onChartSelected(type) },
-                                label = { Text(type) },
-                                selected = selectedChart == type,
-                                colors = FilterChipDefaults.filterChipColors(
-                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
-                                    selectedLabelColor = MaterialTheme.colorScheme.primary
-                                )
-                            )
-                        }
-                    }
-                }
-            }
-
-            // Zone pour les graphiques (placeholder)
-            Card(
-                modifier = Modifier
-                    .fillMaxWidth()
-                    .height(300.dp),
-                shape = RoundedCornerShape(16.dp)
-            ) {
-                Box(
-                    modifier = Modifier.fillMaxSize(),
-                    contentAlignment = Alignment.Center
-                ) {
-                    Column(
-                        horizontalAlignment = Alignment.CenterHorizontally,
-                        verticalArrangement = Arrangement.spacedBy(8.dp)
-                    ) {
-                        Icon(
-                            painter = painterResource(Res.drawable.analytics),
-                            contentDescription = null,
-                            modifier = Modifier.size(48.dp),
-                            tint = MaterialTheme.colorScheme.primary
-                        )
-                        Text(
-                            text = "Graphique $selectedChart",
-                            style = MaterialTheme.typography.titleMedium,
-                            textAlign = TextAlign.Center
-                        )
-                        Text(
-                            text = "Période: $selectedPeriod",
-                            style = MaterialTheme.typography.bodyMedium,
-                            color = MaterialTheme.colorScheme.onSurfaceVariant,
-                            textAlign = TextAlign.Center
-                        )
-                    }
-                }
-            }
-
-            // Métriques détaillées
-            Card(
-                modifier = Modifier.fillMaxWidth(),
-                shape = RoundedCornerShape(16.dp)
-            ) {
-                Column(
-                    modifier = Modifier.padding(16.dp),
-                    verticalArrangement = Arrangement.spacedBy(12.dp)
-                ) {
-                    Text(
-                        text = "Métriques détaillées",
-                        style = MaterialTheme.typography.titleMedium.copy(
-                            fontWeight = FontWeight.Bold
-                        )
-                    )
-
-                    repeat(4) { index ->
-                        Row(
-                            modifier = Modifier.fillMaxWidth(),
-                            horizontalArrangement = Arrangement.SpaceBetween
-                        ) {
-                            Text(
-                                text = "Métrique ${index + 1}",
-                                style = MaterialTheme.typography.bodyMedium
-                            )
-                            Text(
-                                text = "${(index + 1) * 1234}",
-                                style = MaterialTheme.typography.bodyMedium.copy(
-                                    fontWeight = FontWeight.Bold
-                                ),
-                                color = MaterialTheme.colorScheme.primary
-                            )
-                        }
-                        if (index < 3) {
-                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
-                        }
-                    }
-                }
-            }
-        }
-    }
-}