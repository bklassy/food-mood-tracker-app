package com.moodfood.app.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.withLock
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

private const val TAG = "Repository"

/**
 * All persistence for the app, keyed by an ISO date string (LocalDate.toString())
 * for the selected day - Journal defaults to today but can navigate back to
 * past days, reading/writing whichever date is selected. Every call opens
 * its own connection, matching the pattern in Turso's own example app
 * rather than holding one open long-term.
 *
 * Every operation goes through [dbRead]/[dbWrite], which serialize all DB
 * access through [AppDatabase.dbMutex] and catch failures instead of
 * propagating them: typing quickly fires a save per keystroke from
 * independent coroutines, and without serializing them two writes can hit
 * the local SQLite file at the same time and throw "database is locked" -
 * a real crash this app hit before this existed. Catching failures means a
 * rare write hiccup degrades quietly (that one change might not persist)
 * instead of taking the whole app down.
 */
object Repository {
    private val db get() = AppDatabase.db
    private val dbMutex get() = AppDatabase.dbMutex

    private suspend fun <T> dbRead(default: T, block: () -> T): T = dbMutex.withLock {
        withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                Log.e(TAG, "Read failed", e)
                default
            }
        }
    }

    private suspend fun dbWrite(block: () -> Unit) {
        dbMutex.withLock {
            withContext(Dispatchers.IO) {
                try {
                    block()
                } catch (e: Exception) {
                    Log.e(TAG, "Write failed", e)
                }
            }
        }
    }

    suspend fun loadJournalEntry(date: String): JournalEntryData = dbRead(defaultJournalEntry) {
        db.connect().use { conn ->
            conn.query("SELECT journal_text, gratitude_text FROM journal_entries WHERE date = ?", date).use { rows ->
                rows.map { row -> JournalEntryData(row[0] as String, row[1] as String) }
            }.firstOrNull() ?: defaultJournalEntry
        }
    }

    suspend fun saveJournalEntry(date: String, journalText: String, gratitudeText: String) = dbWrite {
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

    suspend fun loadDaySlot(date: String, slot: String): DaySlotData = dbRead(defaultDaySlot) {
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
    ) = dbWrite {
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

    suspend fun loadAlcohol(date: String): AlcoholData = dbRead(defaultAlcohol) {
        db.connect().use { conn ->
            conn.query("SELECT drink_count, note FROM alcohol_log WHERE date = ?", date).use { rows ->
                rows.map { row -> AlcoholData((row[0] as Long).toInt(), row[1] as String) }
            }.firstOrNull() ?: defaultAlcohol
        }
    }

    suspend fun saveAlcohol(date: String, drinkCount: Int, note: String) = dbWrite {
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

    suspend fun loadCoffeeEntries(date: String): List<CoffeeEntryData> = dbRead(emptyList()) {
        db.connect().use { conn ->
            conn.query("SELECT id, time, shot_count, note FROM coffee_log WHERE date = ?", date).use { rows ->
                rows.map { row -> CoffeeEntryData(row[0] as String, row[1] as String, (row[2] as Long).toInt(), row[3] as String) }
            }
        }
    }

    suspend fun addCoffeeEntry(date: String, time: String, shotCount: Int, note: String): String {
        val id = UUID.randomUUID().toString()
        dbWrite {
            db.connect().use { conn ->
                conn.execute(
                    "INSERT INTO coffee_log (id, date, time, shot_count, note) VALUES (?, ?, ?, ?, ?)",
                    id, date, time, shotCount, note,
                )
            }
        }
        return id
    }

    suspend fun updateCoffeeEntry(id: String, time: String, shotCount: Int, note: String) = dbWrite {
        db.connect().use { conn ->
            conn.execute(
                "UPDATE coffee_log SET time = ?, shot_count = ?, note = ? WHERE id = ?",
                time, shotCount, note, id,
            )
        }
    }

    suspend fun deleteCoffeeEntry(id: String) = dbWrite {
        db.connect().use { conn -> conn.execute("DELETE FROM coffee_log WHERE id = ?", id) }
    }

    suspend fun loadBowelMovements(date: String): List<BowelMovementData> = dbRead(emptyList()) {
        db.connect().use { conn ->
            conn.query("SELECT id, time, bristol_type, note FROM bowel_movements WHERE date = ?", date).use { rows ->
                rows.map { row -> BowelMovementData(row[0] as String, row[1] as String, (row[2] as Long).toInt(), row[3] as String) }
            }
        }
    }

    suspend fun addBowelMovement(date: String, time: String, bristolType: Int, note: String): String {
        val id = UUID.randomUUID().toString()
        dbWrite {
            db.connect().use { conn ->
                conn.execute(
                    "INSERT INTO bowel_movements (id, date, time, bristol_type, note) VALUES (?, ?, ?, ?, ?)",
                    id, date, time, bristolType, note,
                )
            }
        }
        return id
    }

    suspend fun updateBowelMovement(id: String, time: String, bristolType: Int, note: String) = dbWrite {
        db.connect().use { conn ->
            conn.execute(
                "UPDATE bowel_movements SET time = ?, bristol_type = ?, note = ? WHERE id = ?",
                time, bristolType, note, id,
            )
        }
    }

    suspend fun deleteBowelMovement(id: String) = dbWrite {
        db.connect().use { conn -> conn.execute("DELETE FROM bowel_movements WHERE id = ?", id) }
    }

    suspend fun loadMovement(date: String): MovementData = dbRead(defaultMovement) {
        db.connect().use { conn ->
            conn.query("SELECT movement_type, feeling, note FROM movement_log WHERE date = ?", date).use { rows ->
                rows.map { row -> MovementData(row[0] as String, row[1] as String?, row[2] as String) }
            }.firstOrNull() ?: defaultMovement
        }
    }

    suspend fun saveMovement(date: String, movementType: String, feeling: String?, note: String) = dbWrite {
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

    suspend fun loadSocial(date: String): SocialData = dbRead(defaultSocial) {
        db.connect().use { conn ->
            conn.query("SELECT note, feeling FROM social_log WHERE date = ?", date).use { rows ->
                rows.map { row -> SocialData(row[0] as String, row[1] as String?) }
            }.firstOrNull() ?: defaultSocial
        }
    }

    suspend fun saveSocial(date: String, note: String, feeling: String?) = dbWrite {
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

    suspend fun loadSymptoms(date: String): List<SymptomEntryData> = dbRead(emptyList()) {
        db.connect().use { conn ->
            conn.query("SELECT id, name, note FROM symptoms_log WHERE date = ?", date).use { rows ->
                rows.map { row -> SymptomEntryData(row[0] as String, row[1] as String, row[2] as String) }
            }
        }
    }

    suspend fun addSymptom(date: String, name: String, note: String): String {
        val id = UUID.randomUUID().toString()
        dbWrite {
            db.connect().use { conn ->
                conn.execute("INSERT INTO symptoms_log (id, date, name, note) VALUES (?, ?, ?, ?)", id, date, name, note)
            }
        }
        return id
    }

    suspend fun deleteSymptom(id: String) = dbWrite {
        db.connect().use { conn -> conn.execute("DELETE FROM symptoms_log WHERE id = ?", id) }
    }

    suspend fun loadCycleSettings(): CycleSettingsData = dbRead(defaultCycleSettings) {
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

    suspend fun saveCycleSettings(avgCycleLength: Int, avgPeriodLength: Int, lastPeriodStartEpochDay: Long?) = dbWrite {
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

    suspend fun loadFavoriteFoods(): String = dbRead("") {
        db.connect().use { conn ->
            conn.query("SELECT content FROM favorite_foods WHERE id = 0").use { rows ->
                rows.map { row -> row[0] as String }
            }.firstOrNull() ?: ""
        }
    }

    suspend fun saveFavoriteFoods(content: String) = dbWrite {
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

    suspend fun loadMentalHealthTools(): String = dbRead("") {
        db.connect().use { conn ->
            conn.query("SELECT content FROM mental_health_tools WHERE id = 0").use { rows ->
                rows.map { row -> row[0] as String }
            }.firstOrNull() ?: ""
        }
    }

    suspend fun saveMentalHealthTools(content: String) = dbWrite {
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

    suspend fun loadFavoriteExercises(): String = dbRead("") {
        db.connect().use { conn ->
            conn.query("SELECT content FROM favorite_exercises WHERE id = 0").use { rows ->
                rows.map { row -> row[0] as String }
            }.firstOrNull() ?: ""
        }
    }

    suspend fun saveFavoriteExercises(content: String) = dbWrite {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO favorite_exercises (id, content) VALUES (0, ?)
                ON CONFLICT(id) DO UPDATE SET content = excluded.content
                """,
                content,
            )
        }
    }

    suspend fun loadAppFeedback(): String = dbRead("") {
        db.connect().use { conn ->
            conn.query("SELECT content FROM app_feedback WHERE id = 0").use { rows ->
                rows.map { row -> row[0] as String }
            }.firstOrNull() ?: ""
        }
    }

    suspend fun saveAppFeedback(content: String) = dbWrite {
        db.connect().use { conn ->
            conn.execute(
                """
                INSERT INTO app_feedback (id, content) VALUES (0, ?)
                ON CONFLICT(id) DO UPDATE SET content = excluded.content
                """,
                content,
            )
        }
    }

    // Below: one "load everything, every date" function per logged table,
    // each returning its header row followed by every data row, for CSV
    // export. Only the day-scoped tracking tables are included - the
    // free-text lists (favorite foods, mental health tools, etc.) and cycle
    // settings aren't the kind of thing you'd chart in a spreadsheet.

    suspend fun loadAllJournalEntries(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                "SELECT date, journal_text, gratitude_text FROM journal_entries ORDER BY date",
            ).use { result ->
                result.map { row -> listOf(row[0] as String, row[1] as String, row[2] as String) }
            }
            listOf(listOf("date", "journal_text", "gratitude_text")) + rows
        }
    }

    suspend fun loadAllDaySlots(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                """
                SELECT date, slot, hunger, fullness, energy, nervous_system, food_note
                FROM day_slots ORDER BY date, slot
                """,
            ).use { result ->
                result.map { row ->
                    listOf(
                        row[0] as String,
                        row[1] as String,
                        (row[2] as Long?)?.toString() ?: "",
                        (row[3] as Long?)?.toString() ?: "",
                        (row[4] as Long?)?.toString() ?: "",
                        (row[5] as Long?)?.toString() ?: "",
                        row[6] as String,
                    )
                }
            }
            listOf(listOf("date", "slot", "hunger", "fullness", "energy", "nervous_system", "food_note")) + rows
        }
    }

    suspend fun loadAllAlcohol(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                "SELECT date, drink_count, note FROM alcohol_log ORDER BY date",
            ).use { result ->
                result.map { row -> listOf(row[0] as String, (row[1] as Long).toString(), row[2] as String) }
            }
            listOf(listOf("date", "drink_count", "note")) + rows
        }
    }

    suspend fun loadAllCoffeeEntries(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                "SELECT date, time, shot_count, note FROM coffee_log ORDER BY date, time",
            ).use { result ->
                result.map { row ->
                    listOf(row[0] as String, row[1] as String, (row[2] as Long).toString(), row[3] as String)
                }
            }
            listOf(listOf("date", "time", "shot_count", "note")) + rows
        }
    }

    suspend fun loadAllBowelMovements(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                "SELECT date, time, bristol_type, note FROM bowel_movements ORDER BY date, time",
            ).use { result ->
                result.map { row ->
                    listOf(row[0] as String, row[1] as String, (row[2] as Long).toString(), row[3] as String)
                }
            }
            listOf(listOf("date", "time", "bristol_type", "note")) + rows
        }
    }

    suspend fun loadAllMovement(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                "SELECT date, movement_type, feeling, note FROM movement_log ORDER BY date",
            ).use { result ->
                result.map { row ->
                    listOf(row[0] as String, row[1] as String, row[2] as String? ?: "", row[3] as String)
                }
            }
            listOf(listOf("date", "movement_type", "feeling", "note")) + rows
        }
    }

    suspend fun loadAllSocial(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                "SELECT date, note, feeling FROM social_log ORDER BY date",
            ).use { result ->
                result.map { row -> listOf(row[0] as String, row[1] as String, row[2] as String? ?: "") }
            }
            listOf(listOf("date", "note", "feeling")) + rows
        }
    }

    suspend fun loadAllSymptoms(): List<List<String>> = dbRead(emptyList()) {
        db.connect().use { conn ->
            val rows = conn.query(
                "SELECT date, name, note FROM symptoms_log ORDER BY date",
            ).use { result ->
                result.map { row -> listOf(row[0] as String, row[1] as String, row[2] as String) }
            }
            listOf(listOf("date", "name", "note")) + rows
        }
    }
}
