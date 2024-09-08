package streetlight.model.dto

import disallowInUsername
import kotlinx.serialization.Serializable

@Serializable
data class SignUpInfo(
    val username: String = "",
    val password: String = "",
    val email: String? = null,
    val name: String? = null
) {
    val validUsernameLength = username.length > 3
    val validUsernameChars = username.all() { !disallowInUsername.contains(it) }
    val validUsername = validUsernameLength && validUsernameChars
    val validEmail = email?.let {
        email -> email.contains("@") && email.all { it.isLetterOrDigit() || it == '@' || it == '.'}
    } ?: true // if email is null, it is valid
    val validPasswordLength = password.length >= 8

    val strongPasswordLength = password.length >= 12
    val bestPasswordLength = password.length >= 16
    val passwordHasLetter = password.any { it.isLetter() }

    val passwordHasDigit = password.any { it.isDigit() }
    val passwordHasSpecial = password.any { !it.isLetterOrDigit() }
    val passwordHasUpper = password.any { it.isUpperCase() }
    val passwordHasLower = password.any { it.isLowerCase() }
    val validPassword = passwordScore >= 6

    val validSignUp = validPassword && validUsername && validEmail

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