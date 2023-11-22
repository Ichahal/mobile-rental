package com.hy.group3_project.ViewActivities.Account

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hy.group3_project.Enums.EditPasswordStatus
import com.hy.group3_project.Models.User
import com.hy.group3_project.databinding.ActivityEditPasswordBinding


class EditPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPasswordBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.sharedPreferences = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
        this.prefEditor = sharedPreferences.edit()

        val currentIntent = this@EditPasswordActivity.intent
        if (currentIntent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                user = currentIntent.getSerializableExtra("extra_user", User::class.java)!!
            } else {
                user = currentIntent.getSerializableExtra("extra_user") as User
            }
        }

        binding.btnSave.setOnClickListener {
            editPassword()
        }
    }
    private fun getUserList(): MutableList<User> {
        var userList = mutableListOf<User>()
        val gson = Gson()
        val userListFromSP = sharedPreferences.getString("KEY_USERLIST", null)
        if (userListFromSP != null) {
            val typeToken = object : TypeToken<MutableList<User>>() {}.type
            userList = gson.fromJson<MutableList<User>>(userListFromSP, typeToken)
        }
        return userList
    }
    private fun editPassword() {
        val etCurrPassword = binding.etCurrentPassword
        val etNewPassword = binding.etPassword
        val etConfirmPassword = binding.etConfirmPassword

        //check empty values
        val editTexts = listOf(etCurrPassword, etNewPassword, etConfirmPassword)
        var hasEmptyValues = false
        for (editText in editTexts) {
            if (editText.text.toString().isNullOrEmpty()) {
                editText.error = "Empty value"
                hasEmptyValues = true
            }
        }
        if (hasEmptyValues) {
            return
        }

            val gson = Gson()
            // find user list
        val userList = getUserList()
        //find user in user list
        user = userList.find { it.email == user.email }!!

        val changePasswordStatus =
            user.changePassword(
                    etCurrPassword.text.toString(),
                    etNewPassword.text.toString(),
                    etConfirmPassword.text.toString()
                )
            if (changePasswordStatus == EditPasswordStatus.Success) {
                val userJson = gson.toJson(user)
                prefEditor.putString("KEY_USER", userJson)

                val userListJson = gson.toJson(userList)
                prefEditor.putString("KEY_USERLIST", userListJson)

                prefEditor.apply()
                Toast.makeText(
                    this@EditPasswordActivity,
                    "$changePasswordStatus",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this@EditPasswordActivity,
                    "Error:$changePasswordStatus",
                    Toast.LENGTH_LONG
                ).show()
                cleanEditTextView(editTexts)
            }
    }

    private fun cleanEditTextView(editTexts: List<EditText>) {
        for (editText in editTexts) {
            editText.text.clear()
        }
    }
}

