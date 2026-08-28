package com.moodfood.app.data

import android.content.Context
import tech.turso.libsql.Database
import tech.turso.libsql.Libsql

/**
 * Local-only libSQL database for now. Turso's Android SDK is in technical
 * preview and this app only needs offline storage today - cloud sync
 * (pointing this at a Turso Cloud URL + auth token as an embedded replica)
 * is a later phase, not wired up yet.
 *
 * Initialized once from MainActivity.onCreate via [init]; every screen reads
 * [db] through this singleton rather than prop-drilling a Database instance
 * through the whole composable tree.
 */
object AppDatabase {
    lateinit var db: Database
        private set

    fun init(context: Context) {
        if (::db.isInitialized) return
        db = Libsql.open(context.filesDir.path + "/moodfood.db")
        db.connect().use { conn ->
            for (statement in SCHEMA_STATEMENTS) {
                conn.execute(statement)
            }
        }
    }
}

private val SCHEMA_STATEMENTS = listOf(
    """
    CREATE TABLE IF NOT EXISTS journal_entries (
        date TEXT PRIMARY KEY,
        journal_text TEXT NOT NULL DEFAULT '',
        gratitude_text TEXT NOT NULL DEFAULT ''
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS day_slots (
        date TEXT NOT NULL,
        slot TEXT NOT NULL,
        hunger INTEGER NOT NULL DEFAULT 3,
        fullness INTEGER NOT NULL DEFAULT 2,
        energy INTEGER NOT NULL DEFAULT 3,
        nervous_system INTEGER NOT NULL DEFAULT 2,
        food_note TEXT NOT NULL DEFAULT '',
        PRIMARY KEY (date, slot)
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS alcohol_log (
        date TEXT PRIMARY KEY,
        drink_count INTEGER NOT NULL DEFAULT 0,
        note TEXT NOT NULL DEFAULT ''
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS coffee_log (
        id TEXT PRIMARY KEY,
        date TEXT NOT NULL,
        time TEXT NOT NULL,
        shot_count INTEGER NOT NULL,
        note TEXT NOT NULL DEFAULT ''
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS bowel_movements (
        id TEXT PRIMARY KEY,
        date TEXT NOT NULL,
        time TEXT NOT NULL,
        bristol_type INTEGER NOT NULL,
        note TEXT NOT NULL DEFAULT ''
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS movement_log (
        date TEXT PRIMARY KEY,
        movement_type TEXT NOT NULL DEFAULT '',
        feeling TEXT,
        note TEXT NOT NULL DEFAULT ''
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS social_log (
        date TEXT PRIMARY KEY,
        note TEXT NOT NULL DEFAULT '',
        feeling TEXT
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS symptoms_log (
        id TEXT PRIMARY KEY,
        date TEXT NOT NULL,
        name TEXT NOT NULL,
        note TEXT NOT NULL DEFAULT ''
    )
    """,
    """
    CREATE TABLE IF NOT EXISTS cycle_settings (
        id INTEGER PRIMARY KEY CHECK (id = 0),
        avg_cycle_length INTEGER NOT NULL DEFAULT 28,
        avg_period_length INTEGER NOT NULL DEFAULT 5,
        last_period_start_epoch_day INTEGER
    )
    """,
)
