package com.vela.data.network

fun trimTrailingSlash(url: String, trailingSlash: Boolean = false): String {
    var normalized = url
    while (normalized.endsWith("/")) {
        normalized = normalized.dropLast(1)
    }
    return if (trailingSlash) "$normalized/" else normalized
}

fun canonicalServerUrl(url: String): String {
    return trimTrailingSlash(url.trim())
}

fun canonicalServerUrlKey(url: String): String {
    return canonicalServerUrl(url).lowercase()
}

fun sameServerUrl(left: String?, right: String?): Boolean {
    if (left.isNullOrBlank() || right.isNullOrBlank()) return false
    return canonicalServerUrl(left).equals(canonicalServerUrl(right), ignoreCase = true)
}

/**
 * 已保存条目只在「同一 userId + 同一登录地址」时复用。
 * 不按用户名、实例 ID 或其它线路自动合并；多地址只能由用户手动添加线路。
 */
fun matchesSavedServerIdentity(
    existingUserId: String,
    existingServerUrl: String,
    incomingUserId: String,
    incomingServerUrl: String
): Boolean {
    if (existingUserId.isBlank() || incomingUserId.isBlank()) return false
    return existingUserId == incomingUserId &&
        sameServerUrl(existingServerUrl, incomingServerUrl)
}

fun buildBaseUrlCandidates(serverUrl: String): List<String> {
    val normalized = trimTrailingSlash(serverUrl.trim())
    if (normalized.endsWith("/emby", ignoreCase = true)) {
        return listOf(normalized)
    }

    return listOf(normalized, "$normalized/emby")
}