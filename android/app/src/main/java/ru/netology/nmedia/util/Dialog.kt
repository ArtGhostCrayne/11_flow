package ru.netology.nmedia.util

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import ru.netology.nmedia.R

object Dialog {

    fun needLogin(context: Context, fragment: Fragment) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Требуется авторизация")
        dialog.setMessage("Хотите авторизироватся?")
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        dialog.setPositiveButton("Да") { dialogInterface, which ->
            findNavController(fragment).navigate(R.id.loginFragment)
        }
        dialog.setNegativeButton("Нет") { dialogInterface, which ->
            Toast.makeText(context, "Действие невозможно, без авторизации", Toast.LENGTH_LONG)
                .show()
        }
        val alertDialog: AlertDialog = dialog.create()
        alertDialog.show()
    }


}
