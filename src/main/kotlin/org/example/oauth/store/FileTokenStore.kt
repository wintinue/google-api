package org.example.oauth.store

import java.nio.file.Path
import java.time.Instant
import kotlinx.serialization.encodeToString
import org.example.config.appJson
import org.example.oauth.model.PendingState
import org.example.oauth.model.StoredToken

class FileTokenStore(
    private val stateFile: Path = Path.of("data/oauth-state.json"),
    private val tokenFile: Path = Path.of("data/google-oauth-token.json")
) : TokenStore {
    override fun savePendingState(state: String, scopes: List<String>, credentialKey: String) {
        writeJson(stateFile, PendingState(state, scopes, credentialKey, Instant.now().epochSecond))
    }

    override fun consumePendingState(state: String): PendingState? {
        val pendingState = readJson<PendingState>(stateFile) ?: return null
        if (pendingState.state != state) {
            return null
        }
        stateFile.toFile().delete()
        return pendingState
    }

    override fun saveToken(token: StoredToken): StoredToken {
        writeJson(tokenFile, token)
        return token
    }

    override fun readToken(): StoredToken? = readJson(tokenFile)

    private inline fun <reified T> readJson(path: Path): T? {
        val file = path.toFile()
        if (!file.exists()) {
            return null
        }
        return appJson.decodeFromString<T>(file.readText())
    }

    private fun writeJson(path: Path, value: Any) {
        val file = path.toFile()
        file.parentFile?.mkdirs()
        val content = when (value) {
            is PendingState -> appJson.encodeToString(PendingState.serializer(), value)
            is StoredToken -> appJson.encodeToString(StoredToken.serializer(), value)
            else -> error("Unsupported value type: ${value::class.qualifiedName}")
        }
        file.writeText(content)
    }
}
