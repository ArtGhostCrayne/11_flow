package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(
            applicationContext,
            android.R.string.yes, Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)



        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        lifecycleScope

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

        checkGoogleApiAvailability()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                // TODO: just hardcode it, implementation must be in homework
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_feedFragment_to_loginFragment)

                //  AppAuth.getInstance().setAuth(5, "x-token")
                true
            }

            R.id.signup -> {
                // TODO: just hardcode it, implementation must be in homework
//                AppAuth.getInstance().setAuth(5, "x-token")
                true
            }

            R.id.signout -> {
                val currentFragment =
                    findNavController(R.id.nav_host_fragment).currentDestination?.id

                if (currentFragment == R.id.newPostFragment) {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Выход")
                    dialog.setMessage("Вы уверены, что хотите выйти")
                    dialog.setIcon(android.R.drawable.ic_dialog_alert)
                    dialog.setPositiveButton("Да") { dialogInterface, which ->
                        AppAuth.getInstance().removeAuth()
                        findNavController(R.id.nav_host_fragment).navigateUp()
                        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_LONG)
                            .show()
                    }
                    dialog.setNegativeButton("Нет") { dialogInterface, which ->

                    }

                    val alertDialog: AlertDialog = dialog.create()
                    alertDialog.show()

                } else {
                    AppAuth.getInstance().removeAuth()
                    Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_LONG)
                        .show()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
