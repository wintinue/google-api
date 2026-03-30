package org.example.config

import kotlinx.serialization.json.Json

val appJson = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}
