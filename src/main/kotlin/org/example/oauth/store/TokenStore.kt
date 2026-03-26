package org.example.oauth.store

import org.example.oauth.model.PendingState
import org.example.oauth.model.StoredToken

interface TokenStore {
    fun savePendingState(state: String, scopes: List<String>, credentialKey: String)
    fun consumePendingState(state: String): PendingState?
    fun saveToken(token: StoredToken): StoredToken
    fun readToken(): StoredToken?
}
