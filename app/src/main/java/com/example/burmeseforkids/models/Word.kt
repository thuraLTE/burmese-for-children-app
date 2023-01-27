package com.example.burmeseforkids.models

import android.graphics.drawable.Drawable

data class Word(
    val burmeseTrans: String,
    val englishTrans: String,
    val imgDrawable: Drawable?,
    val audioPlayback: Int
) {
    override fun toString(): String {
        return "Word(burmeseTrans='$burmeseTrans', englishTrans='$englishTrans', imgDrawable=$imgDrawable, audioPlayback=$audioPlayback)"
    }
}
