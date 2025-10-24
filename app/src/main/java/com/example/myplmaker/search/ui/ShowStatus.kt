package com.example.myplmaker.search.ui


import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.myplmaker.R
import com.example.myplmaker.search.ui.model.Konst
import com.google.android.material.button.MaterialButton

open class ShowStatus {

    lateinit var titleError: LinearLayout
    lateinit var imageError: ImageView
    lateinit var textError: TextView
    lateinit var textErrorInternet: TextView
    lateinit var updateButton: MaterialButton
    lateinit var historyBlock: LinearLayout
    lateinit var historyText: TextView
    lateinit var historyButton: MaterialButton
    lateinit var reciclerViewHistoryTrack: RecyclerView

    fun showStatus(konst: Konst) {
        when (konst) {
            Konst.NO_TRACK -> {
                titleError.isVisible = true
                textErrorInternet.isVisible = false
                updateButton.isVisible = false
                imageError.setImageResource(R.drawable.no_track)
                textError.setText(R.string.no_track)
            }

            Konst.NO_INTERNET -> {
                titleError.isVisible = true
                textErrorInternet.isVisible = true
                updateButton.isVisible = true
                imageError.setImageResource(R.drawable.light_mode)
                textError.setText(R.string.no_internet)
                textErrorInternet.setText(R.string.no_loading)
                updateButton.setText(R.string.update)
            }

            Konst.HISTORY -> {
                titleError.isVisible = false
                historyBlock.isVisible = true
                historyText.isVisible = true
                historyButton.isVisible = true
                reciclerViewHistoryTrack.isVisible = true
            }

            Konst.ZAG -> {
                titleError.isVisible = false
                textErrorInternet.isVisible = false
                updateButton.isVisible = false
                historyText.isVisible = false
                historyButton.isVisible = false
                historyBlock.isVisible = false
                reciclerViewHistoryTrack.isVisible = false
            }
        }
    }
}