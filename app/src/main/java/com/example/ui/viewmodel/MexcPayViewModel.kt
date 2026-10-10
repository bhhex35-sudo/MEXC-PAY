package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminOrderEntity
import com.example.data.model.BonusSettingEntity
import com.example.data.model.OrderType
import com.example.data.model.PaymentSubmissionEntity
import com.example.data.model.PaymentVerificationState
import com.example.data.model.UserProfileAccount
import com.example.data.model.WalletTransactionEntity
import com.example.data.repository.MexcPayRepository
import com.example.data.repository.UtrSubmissionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WalletSummaryState(
    val sampleVerifiedAvailableInr: Double = 0.0,
    val sampleVerifiedBonusEarnedInr: Double = 0.0,
    val unverifiedPendingClaimsInr: Double = 0.0,
    val unverifiedClaimsCount: Int = 0
)

sealed class UtrFormStatus {
    data object Idle : UtrFormStatus()
    data object Submitting : UtrFormStatus()
    data class Error(val message: String) : UtrFormStatus()
    data class RecordedPending(val submission: PaymentSubmissionEntity) : UtrFormStatus()
}

class MexcPayViewModel(
    private val repository: MexcPayRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow(
        UserProfileAccount(
            userId = "MXP-USR-774109",
            fullName = "Arjun Mehta (Sample Account)",
            email = "arjun.mehta@sandbox.mexcpay.in",
            phoneNumber = "+91 98201 44810",
            kycTierLabel = "Tier-2 Verified OTC Participant (Sample)",
            linkedSettlementUpi = "arjun.mehta.sample@okicici",
            accountEnvironment = "SANDBOX_SAMPLE_MODE",
            isLoggedIn = false
        )
    )
    val userProfile: StateFlow<UserProfileAccount> = _userProfile.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    private val _biometricAppLockEnabled = MutableStateFlow(true)
    val biometricAppLockEnabled: StateFlow<Boolean> = _biometricAppLockEnabled.asStateFlow()

    private val _isSessionBiometricLocked = MutableStateFlow(false)
    val isSessionBiometricLocked: StateFlow<Boolean> = _isSessionBiometricLocked.asStateFlow()

    private val _selectedOrderTypeFilter = MutableStateFlow<OrderType?>(null) // null = ALL
    val selectedOrderTypeFilter: StateFlow<OrderType?> = _selectedOrderTypeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _utrFormStatus = MutableStateFlow<UtrFormStatus>(UtrFormStatus.Idle)
    val utrFormStatus: StateFlow<UtrFormStatus> = _utrFormStatus.asStateFlow()

    private val _scannedUtrPrefill = MutableStateFlow<String?>(null)
    val scannedUtrPrefill: StateFlow<String?> = _scannedUtrPrefill.asStateFlow()

    private val _scannedUpiPrefill = MutableStateFlow<String?>(null)
    val scannedUpiPrefill: StateFlow<String?> = _scannedUpiPrefill.asStateFlow()

    val allOrders: StateFlow<List<AdminOrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredOrders: StateFlow<List<AdminOrderEntity>> = combine(
        repository.allOrders,
        _selectedOrderTypeFilter,
        _searchQuery
    ) { orders, typeFilter, query ->
        orders.filter { order ->
            val matchesType = typeFilter == null || order.parsedOrderType == typeFilter
            val matchesQuery = query.isBlank() ||
                order.orderId.contains(query, ignoreCase = true) ||
                order.adminDeskName.contains(query, ignoreCase = true) ||
                order.adminBatchCode.contains(query, ignoreCase = true)
            matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSubmissions: StateFlow<List<PaymentSubmissionEntity>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<WalletTransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bonusSettings: StateFlow<List<BonusSettingEntity>> = repository.bonusSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Wallet balance calculation strictly separates pre-seeded SAMPLE verified records
     * from unverified UTR claims. Submitting a UTR NEVER increments `sampleVerifiedAvailableInr`.
     */
    val walletSummary: StateFlow<WalletSummaryState> = combine(
        repository.allTransactions,
        repository.allSubmissions
    ) { transactions, submissions ->
        val verifiedTxs = transactions.filter { it.affectsVerifiedBalance }
        val availableBalance = verifiedTxs.sumOf { it.amountInr }
        val bonusEarned = verifiedTxs.filter { it.bonusAmountInr > 0 }.sumOf { it.bonusAmountInr }

        val pendingSubmissions = submissions.filter {
            it.parsedVerificationState == PaymentVerificationState.PENDING_BACKEND_VERIFICATION ||
                it.parsedVerificationState == PaymentVerificationState.UNDER_ADMIN_AUDIT
        }
        val pendingInr = pendingSubmissions.sumOf { it.claimedInrAmount }

        WalletSummaryState(
            sampleVerifiedAvailableInr = availableBalance.coerceAtLeast(0.0),
            sampleVerifiedBonusEarnedInr = bonusEarned,
            unverifiedPendingClaimsInr = pendingInr,
            unverifiedClaimsCount = pendingSubmissions.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WalletSummaryState())

    init {
        viewModelScope.launch {
            repository.ensureSampleDataSeeded()
        }
    }

    fun clearAuthError() {
        _authErrorMessage.value = null
    }

    fun setAuthError(message: String) {
        _authErrorMessage.value = message
    }

    fun setBiometricAppLockEnabled(enabled: Boolean) {
        _biometricAppLockEnabled.value = enabled
        if (!enabled) {
            _isSessionBiometricLocked.value = false
        }
    }

    fun lockSessionNow() {
        if (_userProfile.value.isLoggedIn) {
            _isSessionBiometricLocked.value = true
        }
    }

    fun unlockBiometricSession() {
        _isSessionBiometricLocked.value = false
    }

    fun loginWithCredentials(email: String, password: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || !trimmedEmail.contains("@")) {
            _authErrorMessage.value = "Please enter a valid email address (e.g., trader@domain.com)."
            return
        }
        if (password.length < 6) {
            _authErrorMessage.value = "Password must be at least 6 characters long."
            return
        }
        _authErrorMessage.value = null
        _userProfile.value = _userProfile.value.copy(
            email = trimmedEmail,
            fullName = trimmedEmail.substringBefore("@")
                .replace(".", " ")
                .replaceFirstChar { it.uppercase() } + " (Sample Desk User)",
            isLoggedIn = true
        )
        onSuccess()
    }

    fun loginWithSandboxDemoAccount(onSuccess: () -> Unit) {
        _authErrorMessage.value = null
        _userProfile.value = UserProfileAccount(
            userId = "MXP-USR-774109",
            fullName = "Arjun Mehta (Sample Account)",
            email = "arjun.mehta@sandbox.mexcpay.in",
            phoneNumber = "+91 98201 44810",
            kycTierLabel = "Tier-2 Verified OTC Participant (Sample)",
            linkedSettlementUpi = "arjun.mehta.sample@okicici",
            accountEnvironment = "SANDBOX_SAMPLE_MODE",
            isLoggedIn = true
        )
        onSuccess()
    }

    fun signUpAccount(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        settlementUpi: String,
        onSuccess: () -> Unit
    ) {
        if (fullName.trim().length < 2) {
            _authErrorMessage.value = "Please enter your full legal name."
            return
        }
        if (!email.trim().contains("@")) {
            _authErrorMessage.value = "Please enter a valid email address."
            return
        }
        if (phone.trim().length < 10) {
            _authErrorMessage.value = "Please enter a valid 10-digit mobile number."
            return
        }
        if (password.length < 6) {
            _authErrorMessage.value = "Password must be at least 6 characters."
            return
        }
        _authErrorMessage.value = null
        _userProfile.value = UserProfileAccount(
            userId = "MXP-USR-${(100000..999999).random()}",
            fullName = "${fullName.trim()} (Sample Account)",
            email = email.trim(),
            phoneNumber = phone.trim(),
            kycTierLabel = "Tier-1 Sandbox Registered (Sample)",
            linkedSettlementUpi = settlementUpi.trim().ifEmpty { "sample.user@okaxis" },
            accountEnvironment = "SANDBOX_SAMPLE_MODE",
            isLoggedIn = true
        )
        onSuccess()
    }

    fun logout(onLoggedOut: () -> Unit) {
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
        onLoggedOut()
    }

    fun setOrderTypeFilter(orderType: OrderType?) {
        _selectedOrderTypeFilter.value = orderType
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun resetUtrFormStatus() {
        _utrFormStatus.value = UtrFormStatus.Idle
    }

    fun applyScannedQrPrefill(scannedUtr: String?, scannedUpi: String?) {
        _scannedUtrPrefill.value = scannedUtr
        _scannedUpiPrefill.value = scannedUpi
    }

    fun clearScannedQrPrefill() {
        _scannedUtrPrefill.value = null
        _scannedUpiPrefill.value = null
    }

    fun submitUtrClaim(
        orderId: String,
        utrNumber: String,
        payerUpi: String,
        remarks: String
    ) {
        viewModelScope.launch {
            _utrFormStatus.value = UtrFormStatus.Submitting
            when (val result = repository.submitUtrForOrder(orderId, utrNumber, payerUpi, remarks)) {
                is UtrSubmissionResult.RecordedPendingVerification -> {
                    _utrFormStatus.value = UtrFormStatus.RecordedPending(result.submission)
                }
                is UtrSubmissionResult.ValidationError -> {
                    _utrFormStatus.value = UtrFormStatus.Error(result.message)
                }
            }
        }
    }

    fun resetSampleData() {
        viewModelScope.launch {
            repository.resetSampleSandboxData()
            _utrFormStatus.value = UtrFormStatus.Idle
        }
    }
}

class MexcPayViewModelFactory(
    private val repository: MexcPayRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MexcPayViewModel::class.java)) {
            return MexcPayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
