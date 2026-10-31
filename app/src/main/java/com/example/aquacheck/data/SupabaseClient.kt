package com.example.aquacheck.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

object SupabaseClientConfig {
    // Reemplaza estas dos constantes con los datos de tu proyecto en el panel de Supabase:
    // Project Settings -> API -> Project URL / anon public key
    var SUPABASE_URL = "https://gbockxonxtccgxlcgnke.supabase.co"
    var SUPABASE_ANON_KEY = "sb_publishable_NqntRjVppIqyxqYTq6iPvQ_nG1dhr33"

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    val postgrest get() = client.postgrest
    val storage get() = client.storage
}
