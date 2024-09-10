package com.example.omoperation.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.CustomProgress
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityBillSubmissionBinding
import com.example.omoperation.model.CommonMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class BillSubmissionAct : AppCompatActivity() {
    lateinit var binding: ActivityBillSubmissionBinding
    private var minDays: Long = 0
    lateinit  var cp:CustomProgress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bill_submission)
        val  title : TextView = findViewById(R.id.title)
        title.setText("Bill Submition")
       cp= CustomProgress(this)
        binding.searchBtn.setOnClickListener {
           if(Utils.haveInternet(this))
            searchbill()
        }
    }

    private fun searchbill() {
        cp.show()
        val mod = CommonMod()
        mod.sub_no = binding.subnoText.getText().toString()
      lifecycleScope.launch {
      val response=ApiClient.getClient().create(ServiceInterface::class.java).submission_enq(Utils.getheaders(),mod)
          cp.dismiss()
          if(response.code()==200){
          val CNerror: String = response.body()!!.error
          if (CNerror == "true") {
              binding.table.setVisibility(View.GONE)
              binding.floatingBtnImage.hide()
              binding.uploadBtn.setVisibility(View.GONE)
              binding.selectImgText.setText("")
              Utils.showDialog(this@BillSubmissionAct,"error","Invalid submission number",R.drawable.ic_error_outline_red_24dp)

          } else {
              val BILL_SUBMISSION_NO: String? =
                  response.body()!!.sub_enquiry.BILL_SUBMISSION_NO
              val BILL_SUBMISSION_DATE: String =
                  response.body()!!.sub_enquiry.BILL_SUBMISSION_DATE!!
              val BRANCH: String = response.body()!!.sub_enquiry.BRANCH!!
              val ENTER_BY: String = response.body()!!.sub_enquiry.ENTER_BY!!
              binding.colSubnoValue.setText(BILL_SUBMISSION_NO ?: "-")
              binding.colSubdateValue.setText(BILL_SUBMISSION_DATE ?: "-")
              binding.colBranchValue.setText(BRANCH ?: "-")
              binding.colEmpcodeValue.setText(ENTER_BY ?: "-")
              val format = SimpleDateFormat("dd-MM-yyyy", Locale.US)
              try {
                  minDays = format.parse(binding.colSubdateValue.getText().toString()).time
                  Toast.makeText(
                      this@BillSubmissionAct,
                      minDays.toString() + "\n" + format.parse(
                          binding.colSubdateValue.getText().toString()
                      ) + "\n" + binding.colSubdateValue.getText().toString(),
                      Toast.LENGTH_SHORT
                  ).show()
              } catch (e: ParseException) {
                  Toast.makeText(this@BillSubmissionAct, e.message, Toast.LENGTH_SHORT).show()
              }
              binding.table.setVisibility(View.VISIBLE)
              binding.floatingBtnImage.show()
              binding.uploadBtn.setVisibility(View.VISIBLE)
              binding. selectImgText.setText("")
          }
      }
          else
          Utils.showDialog(
              this@BillSubmissionAct,
              "error code " + response.code(),
              response.message(),
              R.drawable.ic_error_outline_red_24dp)
      }

       }
}