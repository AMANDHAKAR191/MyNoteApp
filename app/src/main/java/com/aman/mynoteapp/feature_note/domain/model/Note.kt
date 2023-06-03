package com.aman.mynoteapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aman.mynoteapp.ui.theme.BabyBlue
import com.aman.mynoteapp.ui.theme.LightGreen
import com.aman.mynoteapp.ui.theme.RedOrange
import com.aman.mynoteapp.ui.theme.RedPink
import com.aman.mynoteapp.ui.theme.Violet

@Entity()
data class Note(
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
    @PrimaryKey val id: Int? = null
) {
    companion object {
        val noteColors = listOf(RedOrange, LightGreen, Violet, BabyBlue, RedPink)
    }
}

class InvalidNoteException(message: String) : Exception(message)