## 점심 주문 애플리케이션 만들기 예제

<br>

### 1. AppBar 구성
```
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
```
- 초기 화면을 제외한 다른 화면들에서 뒤로 돌아가기 위해 `canNavigateBack`과 `navigateUp()`을 배치함

<br>

### 2. NavController 선언

```
val navController:NavHostController = rememberNavController()
val backStartEntry by navController.currentBackStackEntryAsState()
```
- `rememberNavController()`로 NavHostController를 가져옴
- `backStartEntry`는 쌓이는 화면들에 대한 정보를 저장함

<br>

### 3. NavHost 구현
```
NavHost(
    navController = navController,
    startDestination = LunchTrayScreen.StartOrder.name,
    modifier = Modifier.padding(innerPadding)
)
```
- `NavHost`는 지정된 경로와 컴포저블을 표시하는 컴포저블

<br>

### 4. NavHost 내 composable 구현
```
composable(route = LunchTrayScreen.StartOrder.name){
    StartOrderScreen(
        onStartOrderButtonClicked = {
            navController.navigate(LunchTrayScreen.EntreeMenu.name) },
        modifier = Modifier)
}
```
- `route`를 통해 경로를 지정
- 내부에 호출할 액티비티 지정 (이 예제에선 StartOrderScreen())

<br>

### 5. 시작화면으로 돌아가기 구현
```
fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController:NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack( LunchTrayScreen.StartOrder.name, inclusive = false)

}
```
- `popBackStack` 메서드에 돌아갈 경로와 `inclusive = false`를 매개변수로 넘겨서 지정 경로의 액티비티로 돌아갈 수 있음
