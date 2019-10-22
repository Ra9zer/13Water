package com.example.water13.login

import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.water13.MainActivity
import com.example.water13.R
import com.example.water13.bean.User
import com.example.water13.component.toast
import com.example.water13.databinding.ActivityLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import kotlinx.android.synthetic.main.dialog_jwch.*
import kotlinx.android.synthetic.main.dialog_jwch.view.*


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.model = viewModel
        viewModel.uiUsername.value = User.instance.username
        viewModel.uiPassword.value = User.instance.password
        subscribeUi()
    }

    private fun subscribeUi() {
        binding.btLogin.setOnClickListener {
            viewModel.onLoginClicked()
        }
        binding.btRegister.setOnClickListener {
            viewModel.onRegisterClicked()
        }
        viewModel.showDialog.observe(this, Observer {
            val dialog = MaterialAlertDialogBuilder(this,R.style.AlertDialogTheme)
                .setView(R.layout.dialog_jwch)
                .setTitle("请输入教务处账号密码")
                .setPositiveButton("注册",null).create()
            if(it) {
                dialog.show()
                dialog.getButton(BUTTON_POSITIVE).setOnClickListener {
                    val numberText = dialog.et_student_number.text!!
                    val passwordText = dialog.et_student_password.text!!
                    if(numberText.isEmpty() || passwordText.isEmpty()) {
                        toast("学号和密码不能为空")
                    } else {
                        viewModel.onRegisterDialogConfirm(numberText.toString(),passwordText.toString())
                    }
                }
            } else {
                dialog.dismiss()
            }
        })
        viewModel.message.observe(this, Observer {
            toast(it)
            if (it.isNotEmpty()) {
                viewModel.onMsgShowed()
            }
        })
        viewModel.success.observe(this, Observer { success ->
            if (success) {
                navigateToMain()
            }
        })
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
