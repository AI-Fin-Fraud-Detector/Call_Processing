package tw.futuremedialab.mycall.domain.entity

data class SimInfo(
    val accountId: String,
    val simSlotIndex: Int,
    val displayName: String?
)