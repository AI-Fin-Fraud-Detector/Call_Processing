package tw.futuremedialab.mycall.domain.entity

data class UserProfile(
    val uuid: String,
    val email: String,
    val phoneNumber: String,
    val name: String
)
