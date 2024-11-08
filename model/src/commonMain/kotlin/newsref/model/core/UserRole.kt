package newsref.model.core

enum class UserRole {
	ADMIN,
	USER,
	BOT;

	companion object {
		private val map = UserRole.entries.associateBy { it.ordinal }

		fun Int.toUserRole() = map[this] ?: throw IllegalArgumentException("UserRole has no ordinal value: $this")
	}
}

fun RoleSet.toClaimValue() = this.joinToString(",")
fun String.toUserRoleSet() = this.split(",")

typealias RoleSet = Set<UserRole>