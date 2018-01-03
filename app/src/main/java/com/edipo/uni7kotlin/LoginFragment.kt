package com.edipo.uni7kotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.edipo.uni7kotlin.local.AppDatabase
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_login.setOnClickListener { onLoginClick() }
        button_register.setOnClickListener { onRegisterClick() }
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun onLoginClick() {
        if (edit_name.editText == null || edit_password.editText == null) {
            return
        }
        val name = edit_name.editText?.text?.toString() ?: ""
        if (name.isEmpty()) {
            edit_name.error = getString(R.string.error_name_empty)
            return
        }
        val password = edit_password.editText?.text?.toString() ?: ""
        if (password.isEmpty()) {
            edit_password.error = getString(R.string.error_password_empty)
            return
        }
        context?.let { context ->
            launch(UI){
                val loginTask = async(CommonPool) {
                    val dao = AppDatabase.database(context).credentialsDAO
                    dao.findByNamePassword(name, password) != null
                }
                if(loginTask.await()){
//                    startActivity(WeatherInfoActivity.getIntent(application, username))
                } else {
                    Toast.makeText(context, R.string.error_credentials_invalid, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun onRegisterClick() {
        fragmentManager?.beginTransaction()
                ?.setCustomAnimations(R.anim.set_slide_from_right, R.anim.set_slide_to_left,
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                ?.replace(R.id.container_login, RegisterFragment())
                ?.addToBackStack(null)
                ?.commit()
    }

}
