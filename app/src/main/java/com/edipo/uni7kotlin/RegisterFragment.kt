package com.edipo.uni7kotlin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.edipo.uni7kotlin.local.AppDatabase
import com.edipo.uni7kotlin.local.Credentials
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class RegisterFragment : Fragment() {
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_register.setOnClickListener { onRegisterClick() }
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            fragmentManager?.popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onRegisterClick() {
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
                val registerTask = async(CommonPool) {
                    with(AppDatabase.database(context).credentialsDAO){
                        val newUser = findByName(name) == null
                        if (newUser) {
                            insertAll(Credentials(name, password))
                        }
                        newUser
                    }
                }
                if(registerTask.await()){
                    Toast.makeText(context, R.string.success_register, Toast.LENGTH_LONG).show()
                    fragmentManager?.popBackStack()
                } else {
                    Toast.makeText(context, R.string.error_name_used, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}