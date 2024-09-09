package streetlight.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String = "",
    val password: String = "",
    val email: String? = null,
    val name: String? = null
) {
    val validUsernameLength: Boolean
        get() = username.length in 3..20
    val validUsernameChars: Boolean
        get() = username.all() { it.isLetterOrDigit() || it == '_' }
    val validUsername: Boolean
        get() = validUsernameLength && validUsernameChars
    val validEmail: Boolean
        get() = email?.let { email ->
            email.contains("@") && email.all { it.isLetterOrDigit() || it == '@' || it == '.' }
        } ?: true // if email is null, it is valid
    val validPasswordLength: Boolean
        get() = password.length >= 8

    val strongPasswordLength: Boolean
        get() = password.length >= 12
    val bestPasswordLength: Boolean
        get() = password.length >= 16
    val passwordHasLetter: Boolean
        get() = password.any { it.isLetter() }

    val passwordHasDigit: Boolean
        get() = password.any { it.isDigit() }
    val passwordHasSpecial: Boolean
        get() = password.any { !it.isLetterOrDigit() }
    val passwordHasUpper: Boolean
        get() = password.any { it.isUpperCase() }
    val passwordHasLower: Boolean
        get() = password.any { it.isLowerCase() }
    val validPassword: Boolean
        get() = passwordScore >= 6

    val validSignUp: Boolean
        get() = validPassword && validUsername && validEmail

    val passwordScore: Int
        get() {
            var score = 0
            if (passwordHasLetter) score++
            if (passwordHasDigit) score++
            if (passwordHasSpecial) score++
            if (passwordHasUpper) score++
            if (passwordHasLower) score++
            if (validPasswordLength) score++
            if (strongPasswordLength) score++
            if (bestPasswordLength) score++
            return score
        }

    val passwordStrength: PasswordStrength
        get() {
            return when (passwordScore) {
                0 -> PasswordStrength.NONE
                in 1..3 -> PasswordStrength.WEAKEST
                in 4..5 -> PasswordStrength.WEAK
                6 -> PasswordStrength.MEDIUM
                7 -> PasswordStrength.STRONG
                8 -> PasswordStrength.DIAMOND
                else -> PasswordStrength.NONE
            }
        }
}

enum class PasswordStrength(val label: String) {
    NONE("None"), WEAKEST("Weakest"), WEAK("Weak"), MEDIUM("Medium"), STRONG("Strong"), DIAMOND("Diamond")
}