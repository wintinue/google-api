package org.example.store

import org.example.model.PendingState
import org.example.model.StoredToken

interface TokenStore {
    fun savePendingState(state: String, scopes: List<String>)
    fun consumePendingState(state: String): PendingState?
    fun saveToken(token: StoredToken): StoredToken
    fun readToken(): StoredToken?
}
