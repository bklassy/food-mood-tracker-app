package com.moodfood.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

data class JournalEntryData(val journalText: String, val gratitudeText: String)
data class DaySlotData(val hunger: Int?, val fullness: Int?, val energy: Int?, val nervousSystem: Int?, val foodNote: String)
data class AlcoholData(val drinkCount: Int, val note: String)
data class CoffeeEntryData(val id: String, val time: String, val shotCount: Int, val note: String)
data class BowelMovementData(val id: String, val time: String, val bristolType: Int, val note: String)
data class MovementData(val movementType: String, val feeling: String?, val note: String)
data class SocialData(val note: String, val feeling: String?)
data class SymptomEntryData(val id: String, val name: String, val note: String)
data class CycleSettingsData(val avgCycleLength: Int, val avgPeriodLength: Int, val lastPeriodStartEpochDay: Long?)

private val defaultJournalEntry = JournalEntryData("", "")
private val defaultDaySlot = DaySlotData(hunger = null, fullness = null, energy = null, nervousSystem = null, foodNote = "")
private val defaultAlcohol = AlcoholData(0, "")
private val defaultMovement = MovementData("", null, "")
private val defaultSocial = SocialData("", null)
private val defaultCycleSettings = CycleSettingsData(28, 5, null)

/**
 * All persistence for the app, keyed by an ISO date string (LocalDate.toString())
 * for "today" - there's no history/day-navigation UI yet, so every screen just
 * reads and writes today's row. Every call opens its own connection, matching
 * the pattern in Turso's own example app rather than holding one open long-term.
 */
object Repository {
    private val db get() = AppDatabase.db

    suspend fun loadJournalEntry(date: String): JournalEntryData = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT journal_text, gratitude_text FROM journal_entries WHERE date = ?", date).use { rows ->
                rows.map { row -> JournalEntryData(row[0] as String, row[1] as String) }
            }.firstOrNull() ?: defaultJournalEntry
        }
    }

    suspend fun saveJournalEntry(date: String, journalText: String, gratitudeText: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO journal_entries (date, journal_text, gratitude_text) VALUES (?, ?, ?)
                ON CONFLICT(date) DO UPDATE SET journal_text = excluded.journal_text, gratitude_text = excluded.gratitude_text
                """,
                date, journalText, gratitudeText,
            )
        }
    }

    suspend fun loadDaySlot(date: String, slot: String): DaySlotData = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query(
                "SELECT hunger, fullness, energy, nervous_system, food_note FROM day_slots WHERE date = ? AND slot = ?",
                date, slot,
            ).use { rows ->
                rows.map { row ->
                    DaySlotData(
                        hunger = (row[0] as Long?)?.toInt(),
                        fullness = (row[1] as Long?)?.toInt(),
                        energy = (row[2] as Long?)?.toInt(),
                        nervousSystem = (row[3] as Long?)?.toInt(),
                        foodNote = row[4] as String,
                    )
                }
            }.firstOrNull() ?: defaultDaySlot
        }
    }

    suspend fun saveDaySlot(
        date: String,
        slot: String,
        hunger: Int?,
        fullness: Int?,
        energy: Int?,
        nervousSystem: Int?,
        foodNote: String,
    ) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO day_slots (date, slot, hunger, fullness, energy, nervous_system, food_note)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(date, slot) DO UPDATE SET
                    hunger = excluded.hunger, fullness = excluded.fullness, energy = excluded.energy,
                    nervous_system = excluded.nervous_system, food_note = excluded.food_note
                """,
                date, slot, hunger, fullness, energy, nervousSystem, foodNote,
            )
        }
    }

    suspend fun loadAlcohol(date: String): AlcoholData = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT drink_count, note FROM alcohol_log WHERE date = ?", date).use { rows ->
                rows.map { row -> AlcoholData((row[0] as Long).toInt(), row[1] as String) }
            }.firstOrNull() ?: defaultAlcohol
        }
    }

    suspend fun saveAlcohol(date: String, drinkCount: Int, note: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO alcohol_log (date, drink_count, note) VALUES (?, ?, ?)
                ON CONFLICT(date) DO UPDATE SET drink_count = excluded.drink_count, note = excluded.note
                """,
                date, drinkCount, note,
            )
        }
    }

    suspend fun loadCoffeeEntries(date: String): List<CoffeeEntryData> = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT id, time, shot_count, note FROM coffee_log WHERE date = ?", date).use { rows ->
                rows.map { row -> CoffeeEntryData(row[0] as String, row[1] as String, (row[2] as Long).toInt(), row[3] as String) }
            }
        }
    }

    suspend fun addCoffeeEntry(date: String, time: String, shotCount: Int, note: String): String = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        db.connect().use { conn ->
            conn.execute(
                "INSERT INTO coffee_log (id, date, time, shot_count, note) VALUES (?, ?, ?, ?, ?)",
                id, date, time, shotCount, note,
            )
        }
        id
    }

    suspend fun deleteCoffeeEntry(id: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn -> conn.execute("DELETE FROM coffee_log WHERE id = ?", id) }
    }

    suspend fun loadBowelMovements(date: String): List<BowelMovementData> = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT id, time, bristol_type, note FROM bowel_movements WHERE date = ?", date).use { rows ->
                rows.map { row -> BowelMovementData(row[0] as String, row[1] as String, (row[2] as Long).toInt(), row[3] as String) }
            }
        }
    }

    suspend fun addBowelMovement(date: String, time: String, bristolType: Int, note: String): String = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        db.connect().use { conn ->
            conn.execute(
                "INSERT INTO bowel_movements (id, date, time, bristol_type, note) VALUES (?, ?, ?, ?, ?)",
                id, date, time, bristolType, note,
            )
        }
        id
    }

    suspend fun deleteBowelMovement(id: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn -> conn.execute("DELETE FROM bowel_movements WHERE id = ?", id) }
    }

    suspend fun loadMovement(date: String): MovementData = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT movement_type, feeling, note FROM movement_log WHERE date = ?", date).use { rows ->
                rows.map { row -> MovementData(row[0] as String, row[1] as String?, row[2] as String) }
            }.firstOrNull() ?: defaultMovement
        }
    }

    suspend fun saveMovement(date: String, movementType: String, feeling: String?, note: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO movement_log (date, movement_type, feeling, note) VALUES (?, ?, ?, ?)
                ON CONFLICT(date) DO UPDATE SET movement_type = excluded.movement_type, feeling = excluded.feeling, note = excluded.note
                """,
                date, movementType, feeling, note,
            )
        }
    }

    suspend fun loadSocial(date: String): SocialData = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT note, feeling FROM social_log WHERE date = ?", date).use { rows ->
                rows.map { row -> SocialData(row[0] as String, row[1] as String?) }
            }.firstOrNull() ?: defaultSocial
        }
    }

    suspend fun saveSocial(date: String, note: String, feeling: String?) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO social_log (date, note, feeling) VALUES (?, ?, ?)
                ON CONFLICT(date) DO UPDATE SET note = excluded.note, feeling = excluded.feeling
                """,
                date, note, feeling,
            )
        }
    }

    suspend fun loadSymptoms(date: String): List<SymptomEntryData> = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT id, name, note FROM symptoms_log WHERE date = ?", date).use { rows ->
                rows.map { row -> SymptomEntryData(row[0] as String, row[1] as String, row[2] as String) }
            }
        }
    }

    suspend fun addSymptom(date: String, name: String, note: String): String = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        db.connect().use { conn ->
            conn.execute("INSERT INTO symptoms_log (id, date, name, note) VALUES (?, ?, ?, ?)", id, date, name, note)
        }
        id
    }

    suspend fun deleteSymptom(id: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn -> conn.execute("DELETE FROM symptoms_log WHERE id = ?", id) }
    }

    suspend fun loadCycleSettings(): CycleSettingsData = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query(
                "SELECT avg_cycle_length, avg_period_length, last_period_start_epoch_day FROM cycle_settings WHERE id = 0",
            ).use { rows ->
                rows.map { row ->
                    CycleSettingsData(
                        avgCycleLength = (row[0] as Long).toInt(),
                        avgPeriodLength = (row[1] as Long).toInt(),
                        lastPeriodStartEpochDay = row[2] as Long?,
                    )
                }
            }.firstOrNull() ?: defaultCycleSettings
        }
    }

    suspend fun saveCycleSettings(avgCycleLength: Int, avgPeriodLength: Int, lastPeriodStartEpochDay: Long?) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO cycle_settings (id, avg_cycle_length, avg_period_length, last_period_start_epoch_day) VALUES (0, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET
                    avg_cycle_length = excluded.avg_cycle_length,
                    avg_period_length = excluded.avg_period_length,
                    last_period_start_epoch_day = excluded.last_period_start_epoch_day
                """,
                avgCycleLength, avgPeriodLength, lastPeriodStartEpochDay,
            )
        }
    }

    suspend fun loadFavoriteFoods(): String = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT content FROM favorite_foods WHERE id = 0").use { rows ->
                rows.map { row -> row[0] as String }
            }.firstOrNull() ?: ""
        }
    }

    suspend fun saveFavoriteFoods(content: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO favorite_foods (id, content) VALUES (0, ?)
                ON CONFLICT(id) DO UPDATE SET content = excluded.content
                """,
                content,
            )
        }
    }

    suspend fun loadMentalHealthTools(): String = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.query("SELECT content FROM mental_health_tools WHERE id = 0").use { rows ->
                rows.map { row -> row[0] as String }
            }.firstOrNull() ?: ""
        }
    }

    suspend fun saveMentalHealthTools(content: String) = withContext(Dispatchers.IO) {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO mental_health_tools (id, content) VALUES (0, ?)
                ON CONFLICT(id) DO UPDATE SET content = excluded.content
                """,
                content,
            )
        }
    }
}
