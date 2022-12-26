package com.japnoor.anticorruption

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.japnoor.anticorruption.databinding.ActivitySecurityQuestionBinding

class SecurityQuestion : AppCompatActivity() {
    lateinit var binding: ActivitySecurityQuestionBinding
    lateinit var arrayAdapter: ArrayAdapter<String>
     lateinit var signUpEntityy : SignUpEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecurityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signUpEntityy= SignUpEntity()
        intent?.let {
            signUpEntityy = (it.getSerializableExtra("userInfo")?:SignUpEntity()) as SignUpEntity
            System.out.println("signUpEntity name ${signUpEntityy?.id}")
        }
        binding.btnSave.setOnClickListener {
            if (binding.etAnswer.text.isNullOrEmpty()) {
                binding.etAnswer.error = "Set up a Security Question"
                binding.etAnswer.requestFocus()
            }
            else {
                println(binding.Question.text.toString())
                println(binding.etAnswer.text.toString())
                addQuestion(binding.Question.text.toString(),binding.etAnswer.text.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        var questions = resources.getStringArray(R.array.Question)
        arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, questions)
        binding.Question.setAdapter(arrayAdapter)
    }

    fun addQuestion(
        question: String,
        answer: String,
    ) {
        class Add: AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg p0: Void?): Void? {
                var securityQuestionEntity = SecurityQuestionEntity()
                securityQuestionEntity.question=question
                securityQuestionEntity.answer=answer
                securityQuestionEntity.uId = signUpEntityy.id
                Datbase.getDatabase(this@SecurityQuestion).dao().addQuestion(securityQuestionEntity)
                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                var intent = Intent(this@SecurityQuestion, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this@SecurityQuestion, "Sign Up successful", Toast.LENGTH_LONG)
                    .show()
            }

        }
        Add().execute()
    }
}