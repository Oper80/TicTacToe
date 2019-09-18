package dev.maxn.tictactoe

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class GameOver : DialogFragment(){

    private var activity: MainActivity? = null
    private var message = ""

    companion object {
        fun newInstance(activity: MainActivity, mes: String): GameOver {
            val dialog = GameOver()
            dialog.activity = activity
            dialog.message = mes
            return dialog
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {

        val alertDialog = AlertDialog.Builder(context!!)
            .setTitle(R.string.game_over_title)
            .setPositiveButton(R.string.new_game, null)
            .setNegativeButton(R.string.cancel, null)
            .setMessage(message)
            .create()
        alertDialog.setCanceledOnTouchOutside(true)
        alertDialog.setCancelable(true)

        alertDialog.setOnShowListener { onDialogShow(alertDialog) }
        alertDialog.setOnCancelListener{dismiss()}
        return alertDialog
    }

    private fun onDialogShow(dialog: AlertDialog) {
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setOnClickListener { onDoneClicked() }
        negativeButton.setOnClickListener{dismiss()}
    }

    private fun onDoneClicked() {
        activity?.startNewGame()
        dismiss()
    }
}
