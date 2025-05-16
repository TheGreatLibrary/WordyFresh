package com.sinya.projects.wordle.data.remote.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SupabaseClientHolder {

    private const val SUPABASE_URL = "https://vlikgqmxzodtpkubwlrf.supabase.co"
    private const val SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZsaWtncW14em9kdHBrdWJ3bHJmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDU3NDE2NDcsImV4cCI6MjA2MTMxNzY0N30.ctGnSuGEYEio8kuH3Jarh-8cn9-ZWzg0PVSNXxY5aXU"

    lateinit var client: SupabaseClient
        private set

    fun init() {
        client = createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_API_KEY,
        ) {
            install(Auth)
            install(Storage)
            install(Postgrest)
        }
    }
}