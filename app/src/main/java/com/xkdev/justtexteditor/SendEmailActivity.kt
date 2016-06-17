package com.xkdev.justtexteditor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.email_layout.*


 class SendEmailActivity : Activity(){




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.email_layout)


        btnSend.setOnClickListener( {
           val intent: Intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, Array(10){etAddress.text.toString()})
            intent.putExtra(Intent.EXTRA_SUBJECT, etSubject.text.toString())
            intent.putExtra(Intent.EXTRA_TEXT, etMessage.text.toString())

            this.startActivity(Intent.createChooser(intent, "Отправка письма...."))
        })
    }
}