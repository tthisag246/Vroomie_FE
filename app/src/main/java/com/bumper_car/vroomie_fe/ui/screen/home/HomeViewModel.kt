//package com.bumper_car.vroomie_fe.ui.screen.home
//
//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val getUserUseCase: GetUserUseCase
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(HomeUiState())
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            val user = getUserUseCase()
//            _uiState.value = HomeUiState(user.nickname)
//        }
//    }
//}