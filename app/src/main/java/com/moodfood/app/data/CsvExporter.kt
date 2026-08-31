package com.moodfood.app.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

/**
 * Exports every logged table to its own CSV file under cacheDir/exports,
 * then hands back a share-sheet Intent pointing at all of them via
 * FileProvider - so "export" just means "send these files somewhere" (Drive,
 * email, a file manager, Sheets' own CSV import), no Google API/OAuth setup
 * needed.
 */
object CsvExporter {
    suspend fun exportAll(context: Context): Intent {
        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        exportsDir.listFiles()?.forEach { it.delete() }

        val tables = listOf(
            "journal_entries" to Repository.loadAllJournalEntries(),
            "day_slots" to Repository.loadAllDaySlots(),
            "alcohol" to Repository.loadAllAlcohol(),
            "caffeine" to Repository.loadAllCoffeeEntries(),
            "bowel_movements" to Repository.loadAllBowelMovements(),
            "movement" to Repository.loadAllMovement(),
            "social" to Repository.loadAllSocial(),
            "symptoms" to Repository.loadAllSymptoms(),
        )

        val uris = tables.map { (name, rows) ->
            val file = File(exportsDir, "$name.csv")
            file.writeText(rows.joinToString("\n") { row -> row.joinToString(",", transform = ::escapeCsvCell) })
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        }

        return Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "text/csv"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun escapeCsvCell(value: String): String =
        if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
}
