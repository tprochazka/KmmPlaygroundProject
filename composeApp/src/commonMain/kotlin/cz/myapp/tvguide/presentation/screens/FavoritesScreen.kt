package cz.myapp.tvguide.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.myapp.tvguide.presentation.components.ChannelListItem
import cz.myapp.tvguide.presentation.components.EmptyState
import cz.myapp.tvguide.presentation.components.LoadingIndicator
import cz.myapp.tvguide.presentation.components.TVGuideAppBar
import cz.myapp.tvguide.presentation.screens.favorites.FavoritesScreenModel
import cz.myapp.tvguide.presentation.screens.settings.SettingsScreen

/**
 * Favorites screen - Manage favorite channels and custom lists (US2)
 */
object FavoritesScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { FavoritesScreenModel() }
        val state by screenModel.state.collectAsState()
        
        val snackbarHostState = remember { SnackbarHostState() }
        
        // Show error snackbar
        LaunchedEffect(state.error) {
            state.error?.let { error ->
                snackbarHostState.showSnackbar(error)
                screenModel.clearError()
            }
        }
        
        Scaffold(
            topBar = {
                TVGuideAppBar(
                    title = "Oblíbené Stanice",
                    onSettingsClick = {
                        navigator.push(SettingsScreen())
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { screenModel.setShowCreateListDialog(true) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Vytvořit seznam")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search bar
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { screenModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                // Category filters
                CategoryFilters(
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = { screenModel.selectCategory(it) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                HorizontalDivider()
                
                // Channel list
                when {
                    state.isLoading -> {
                        LoadingIndicator(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    state.filteredChannels.isEmpty() -> {
                        EmptyState(
                            message = if (state.searchQuery.isBlank() && state.selectedCategory == null) {
                                "Žádné kanály k dispozici"
                            } else {
                                "Nenalezeny žádné kanály"
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    else -> {
                        ChannelList(
                            channels = state.filteredChannels,
                            favoriteIds = state.favoriteChannelIds,
                            onToggleFavorite = { screenModel.toggleFavorite(it) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Hledat kanály...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Vymazat")
                }
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
private fun CategoryFilters(
    selectedCategory: cz.myapp.tvguide.domain.model.ChannelCategory?,
    onCategorySelected: (cz.myapp.tvguide.domain.model.ChannelCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Vše" to null,
        "Národní" to cz.myapp.tvguide.domain.model.ChannelCategory.NATIONAL,
        "Soukromé" to cz.myapp.tvguide.domain.model.ChannelCategory.PRIVATE,
        "Regionální" to cz.myapp.tvguide.domain.model.ChannelCategory.REGIONAL,
        "Filmové" to cz.myapp.tvguide.domain.model.ChannelCategory.THEMATIC_MOVIES,
        "Sportovní" to cz.myapp.tvguide.domain.model.ChannelCategory.THEMATIC_SPORT,
        "Zpravodajské" to cz.myapp.tvguide.domain.model.ChannelCategory.THEMATIC_NEWS,
        "Dětské" to cz.myapp.tvguide.domain.model.ChannelCategory.THEMATIC_KIDS,
        "Hudební" to cz.myapp.tvguide.domain.model.ChannelCategory.THEMATIC_MUSIC,
        "Dokumentární" to cz.myapp.tvguide.domain.model.ChannelCategory.THEMATIC_DOCUMENTARY
    )
    
    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        items(categories.size) { index ->
            val (label, category) = categories[index]
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(label) },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
private fun ChannelList(
    channels: List<cz.myapp.tvguide.domain.model.Channel>,
    favoriteIds: List<String>,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = channels,
            key = { it.id }
        ) { channel ->
            ChannelListItem(
                channel = channel,
                isSelected = channel.id in favoriteIds,
                onToggle = { onToggleFavorite(channel.id) }
            )
        }
    }
}

