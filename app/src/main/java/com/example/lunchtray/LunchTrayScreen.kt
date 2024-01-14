/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum
enum class LunchTrayScreen() {
    StartOrder,
    EntreeMenu,
    SideMenu,
    AccompanimentMenu,
    Checkout
}

// TODO: AppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayAppBar(
    currentScreen:String,
    canNavigateBack:Boolean,
    navigateUp:() -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(text = currentScreen ) },
        modifier = modifier,
        navigationIcon = {
            if(canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                }
            }
        }

    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp() {
    // TODO: Create Controller and initialization
    val navController:NavHostController = rememberNavController()
    val backStartEntry by navController.currentBackStackEntryAsState()

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    Scaffold(
        topBar = {
            LunchTrayAppBar(
                currentScreen = backStartEntry?.destination?.route?: LunchTrayScreen.StartOrder.name,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
        NavHost(
            navController = navController,
            startDestination = LunchTrayScreen.StartOrder.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = LunchTrayScreen.StartOrder.name){
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                     navController.navigate(LunchTrayScreen.EntreeMenu.name) },
                    modifier = Modifier)
            }

            composable(route = LunchTrayScreen.EntreeMenu.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) },
                    onNextButtonClicked = {
                        if(uiState.entree != null) {
                            viewModel.updateEntree(uiState.entree!!)
                        }
                        navController.navigate(LunchTrayScreen.SideMenu.name) },
                    onSelectionChanged = {
                        viewModel.updateEntree(it)
                    }
                )
            }

            composable(route = LunchTrayScreen.SideMenu.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) },
                    onNextButtonClicked = {
                        if(uiState.sideDish != null) {
                            viewModel.updateSideDish(uiState.sideDish!!)
                        }
                        navController.navigate(LunchTrayScreen.AccompanimentMenu.name) },
                    onSelectionChanged = {
                        viewModel.updateSideDish(it)
                    }
                )
            }

            composable(route = LunchTrayScreen.AccompanimentMenu.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) },
                    onNextButtonClicked = {
                        if(uiState.accompaniment != null) {
                            viewModel.updateAccompaniment(uiState.accompaniment!!)
                        }
                        navController.navigate(LunchTrayScreen.Checkout.name) },
                    onSelectionChanged = {
                        viewModel.updateAccompaniment(it)
                    }
                )
            }

            composable(route = LunchTrayScreen.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = {
                        viewModel.resetOrder()
                        navController.navigate(LunchTrayScreen.StartOrder.name) },
                    onCancelButtonClicked = { cancelOrderAndNavigateToStart(viewModel, navController) })
            }
        }
    }
}

fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController:NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack( LunchTrayScreen.StartOrder.name, inclusive = false)

}
