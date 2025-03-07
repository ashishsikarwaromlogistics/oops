package com.example.omoperation.activities

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.omoperation.CustomProgress
import com.example.omoperation.NetworkState
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityLoginBinding
import com.example.omoperation.viewmodel.LoginViewMod

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginviewmod: LoginViewMod
   lateinit var cp:CustomProgress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =DataBindingUtil.setContentView(this, R.layout.activity_login)
        cp= CustomProgress(this)
        loginviewmod=ViewModelProvider(this).get(LoginViewMod::class.java)
        binding.loginmod=loginviewmod
        binding.lifecycleOwner=this
        loginviewmod.livedata.observe(/* owner = */ this, Observer {
            when(it){
              is NetworkState.Loading -> {
                  cp.show()
              }
                is NetworkState.Success<*> ->{
                    cp.dismiss()
                startActivity(Intent(this,DashboardAct::class.java))
                    finish()
                }
                is NetworkState.Error ->{
                    cp.dismiss()
                  Utils.showDialog(this,it.title,it.message,R.drawable.ic_error_outline_red_24dp)
                }

            }


        })
        binding.showPasswordCheck.setOnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                binding.showPasswordCheck.setText(R.string.hide_password)
                binding.password.setInputType(InputType.TYPE_CLASS_TEXT)
                binding.password.transformationMethod = HideReturnsTransformationMethod
                    .getInstance()
            } else {
                binding.showPasswordCheck.setText(R.string.show_password)
                binding.password.setInputType(
                    InputType.TYPE_CLASS_TEXT
                            or InputType.TYPE_TEXT_VARIATION_PASSWORD
                )
                binding.password.transformationMethod = PasswordTransformationMethod
                    .getInstance()
            }
        }

    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}