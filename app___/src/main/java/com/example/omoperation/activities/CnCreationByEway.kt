package com.example.omoperation.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityCnCreationByEwayBinding
import com.example.omoperation.model.calculate_charge.CalculateMod
import com.example.omoperation.model.cn_create_eway.CnCreateEwayMod
import com.example.omoperation.model.eway.Detail
import com.example.omoperation.model.eway.EwayMod
import com.example.omoperation.model.eway.EwayResp
import com.example.omoperation.model.eway_dropdown.Dropdowns
import com.example.omoperation.model.eway_dropdown.Eway_DropDownMod
import com.example.omoperation.model.verifyCNE.VerifyCneMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.google.zxing.integration.android.IntentIntegrator
import com.omlogistics.deepak.omlogistics.model.calculate_charge.CalculateResp
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create

class CnCreationByEway : AppCompatActivity() {
    lateinit var binding: ActivityCnCreationByEwayBinding

    val SPEECH_REQUEST_CODE: Int = 0
    var f_mode: String = ""
    var d_inst: String = ""
    var b_mode: String = ""
    var pkg_type: String = ""
    var cne_code: String? = null
    var cnr_code: String? = null


    var cft_unit: Int = 1
    var pd: ProgressDialog? = null
    var details: Detail? = null

    var billing_party: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cn_creation_by_eway)
        init()
        getAllDropDown()
    }

    var data_availble: Boolean = false


    lateinit var items: Array<String?>
    private fun init() {
        pd = ProgressDialog(this)


        // eway_text.setText("341711534986");
        binding.llEwayReport.visibility = View.GONE

    }
    private fun getDetailsofcn() {
        if (binding.ewayText.text.toString().length < 12) {
            Toast.makeText(this, "Kindly insert proper value", Toast.LENGTH_SHORT).show()
        } else if (Utils.haveInternet(this)) {
            pd!!.show()


            val mod = EwayMod()
            mod.status = "searchEwb"
            mod.ewayBillNo = binding.ewayText.text.toString()
            lifecycleScope.launch {
                val response = ApiClient.getClientsanchar().create(ServiceInterface::class.java)
                    .searchEwb(Utils.getheaders(), mod)
                if (response.code() == 200) {
                    pd!!.dismiss()
                    if (response.body()?.error.equals("false")) {
                        data_availble = true
                        binding.btnSubmit.visibility = View.VISIBLE
                        details = response.body()!!.detail
                        binding.llEwayReport.visibility = View.VISIBLE
                        val detail = response.body()!!.detail
                        binding.tvCnr.text = Html.fromHtml(
                            """<b>CNR</b><br/>
                            ${detail.cnr_gstn}<br/>
                            ${detail.cnr_addr}<br/>
                            ${detail.cnr_Pin}
                            """.trimIndent(), Html.FROM_HTML_MODE_LEGACY
                        )

                        binding.tvCne.text = Html.fromHtml(
                            ((("<b>CNE</b><br/>" +
                                    detail.cne_gstn).toString() + "<br/>" +
                                    detail.cne_addr).toString() + "<br/>" +
                                    detail.cne_Pin)
                        )
                        binding.tvEwayDetails.text = Html.fromHtml(
                            ((((("<b>Invoice Number : </b>" + detail.inv_no
                                    ).toString() + "<br/><b>Invoice Date : </b>" +
                                    detail.inv_date
                                    ).toString() + "<br/><b>Invoice Gross Value : </b>" +
                                    detail.inv_grs_value
                                    ).toString() + "<br/><b>Invoice Net Value : </b>" +
                                    detail.inv_net_value
                                    //  +"<br/><b>Invoice Qty : </b>" + response.body().getDetail().getInv_qty()
                                    ).toString() + "<br/><b>Product Description : </b>" +
                                    detail.productDesc)
                        )
                        binding.edtQty.setText(detail.inv_qty.toString())

                        binding.cneeList.setOnClickListener(View.OnClickListener {
                            if (detail.cneList == null) {
                                cne_code = ""
                                showRequestAccessDialog(
                                    1,
                                    detail.cne_gstn.toString(),
                                    detail.cne_Pin.toString()+ ""
                                )
                                return@OnClickListener
                            } else {
                                items =
                                    arrayOfNulls(detail.cneList.size)
                                for (i in items!!.indices) {
                                    items!![i] = (((detail.cneList.get(i).ccode.toString()
                                            + " " + detail.cneList.get(i).cname.toString()
                                            ).toString() + " (" + detail.cneList.get(i).caddress.toString() + " )\n<-------------------------------------------->"))
                                    val content = SpannableString(items!![i])
                                    content.setSpan(UnderlineSpan(), 0, items!![i]!!.length, 0)
                                }


                                val builder = AlertDialog.Builder(this@CnCreationByEway)
                                // builder.set
                                builder.setItems(items) { dialog: DialogInterface, item: Int ->
                                    cne_code =detail.cneList.get(item).ccode.toString()
                                    binding.cneeList.setText(items!!.get(item))
                                    dialog.dismiss()
                                }
                                builder.show()
                            }
                        })

                        binding.cnrList.setOnClickListener(View.OnClickListener {
                            if (detail.cnrList == null) {
                                cnr_code = ""
                                showRequestAccessDialog(
                                    2,
                                    detail.cnr_gstn.toString(),
                                    detail.cnr_Pin .toString()
                                )
                                return@OnClickListener
                            } else {
                                items =
                                    arrayOfNulls<String>(detail.cnrList.size)
                                for (i in items!!.indices) {
                                    items!![i] = (((detail.cnrList.get(i)
                                        .ccode.toString()
                                            + " " + detail.cnrList.get(i)
                                        .cname.toString()
                                            ).toString() + " (" + detail.cnrList.get(i)
                                        .caddress.toString() + " )\n<-------------------------------------------->"))
                                    val content = SpannableString(items!![i])
                                    content.setSpan(UnderlineSpan(), 0, items!![i]!!.length, 0)
                                }


                                val builder = AlertDialog.Builder(this@CnCreationByEway)
                                // builder.set
                                builder.setItems(items) { dialog: DialogInterface, item: Int ->
                                    cnr_code = detail.cnrList.get(item).ccode.toString()
                                    binding.cnrList.setText(items!!.get(item))
                                    dialog.dismiss()
                                }
                                builder.show()
                            }
                        })
                    } else {
                        Utils.showDialog(
                            this@CnCreationByEway,
                            "Fail",
                            response.body()?.message ?: "message not found ",
                            R.drawable.ic_error_outline_red_24dp
                        )
                        data_availble = false
                        binding.tvCnr.text = ""
                        binding.tvCne.text = ""
                        binding.tvEwayDetails.text = ""
                    }
                } else {
                    pd!!.dismiss()
                    data_availble = false
                    Toast.makeText(this@CnCreationByEway, response.message(), Toast.LENGTH_SHORT)
                        .show()

                }
            }


        }
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun clickListener() {
        binding.searchByVoice.setOnClickListener { view ->
            val intent =
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            startActivityForResult(
                intent,
                SPEECH_REQUEST_CODE
            )
        }
        binding.searchByCode.setOnClickListener { view ->
            val integrator = IntentIntegrator(this@CnCreationByEway)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            integrator.setPrompt("Scan Barcode")
            integrator.setCameraId(0)
            integrator.setBeepEnabled(true)
            integrator.setBarcodeImageEnabled(true)
            integrator.initiateScan()
        }
        binding.searchBtn.setOnClickListener { view ->
            getDetailsofcn()
        }

        binding.edtCftUnit.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val items = resources.getStringArray(R.array.cft_unit)
                val builder = AlertDialog.Builder(this@CnCreationByEway)
                builder.setItems(items) { dialog, item ->
                    cft_unit = item
                    binding.edtCftUnit.setText(items[item])
                    dialog.dismiss()

                    // Check if all the required EditText fields are filled
                    if (!binding.edtL.text.isNullOrEmpty() &&
                        !binding.edtW.text.isNullOrEmpty() &&
                        !binding.edtH.text.isNullOrEmpty() &&
                        !binding.edtCftRate.text.isNullOrEmpty() &&
                        !binding.edtNPkg.text.isNullOrEmpty() &&
                        !binding.edtActWeight.text.isNullOrEmpty()
                    ) {
                        // Set up mod for API request
                        val mod = CalculateMod().apply {
                            status = "cftWt"
                            BOOKING_MODE = b_mode
                            NO_OF_PKG = binding.edtNPkg.text.toString()
                            ACTUAL_WEIGHT = binding.edtActWeight.text.toString()
                            CFT_UNIT = cft_unit.toString()
                            LEN = binding.edtL.text.toString()
                            WIDTH = binding.edtW.text.toString()
                            HEIGHT = binding.edtH.text.toString()
                            CFT_RATE = binding.edtCftRate.text.toString()
                        }

                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.getClientsanchar()
                                    .create(ServiceInterface::class.java)
                                    .cftWt(Utils.getheaders(), mod)

                                if (response.code() == 200) {
                                    pd?.dismiss()
                                    if (response.body()?.error == "false") {
                                        binding.edtChWeight.setText(response.body()?.cftwt.toString())
                                    } else {
                                        // Handle any API errors
                                    }
                                } else {
                                    pd?.dismiss()
                                    Toast.makeText(
                                        this@CnCreationByEway,
                                        response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                pd?.dismiss()
                                Toast.makeText(
                                    this@CnCreationByEway,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                builder.show()
            }
            true
        }






        binding.edtChWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    if (binding.edtActWeight.text.toString()
                            .toInt() > binding.edtChWeight.text.toString().toInt()
                    ) {
                        binding.edtChWeight.error =
                            "Chargeable weight should be more than Actual weight"
                    } else {
                        binding.edtActWeight.error = null
                        binding.edtChWeight.error = null
                    }
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.edtActWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    if (binding.edtActWeight.text.toString()
                            .toInt() > binding.edtChWeight.text.toString().toInt()
                    ) {
                        binding.edtActWeight.error =
                            "Chargeble weight should be more than Actual weight"
                    } else {
                        binding.edtActWeight.error = null
                        binding.edtChWeight.error = null
                    }
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.btnSubmit.setOnClickListener {
            if (data_availble) CreateCN()
            else {
                Utils.showDialog(
                    this@CnCreationByEway,
                    "Error",
                    "Please scan QR code first",
                    R.drawable.ic_error_outline_red_24dp
                )
            }
        }


        binding.edtFMode.setText(dropdowns.freightMode.get(1).NAME)
        binding.edtDInst.setText(dropdowns.del_inst.get(0).NAME)
        binding.edtBMode.setText(dropdowns.bookingMode.get(0).NAME)
        binding.edtPkgType.setText(dropdowns.pkgType.get(0).NAME)
        f_mode = dropdowns.freightMode.get(1).VAL
        d_inst = dropdowns.del_inst.get(0).VAL
        b_mode = dropdowns.bookingMode.get(0).VAL
        pkg_type = dropdowns.pkgType.get(0).VAL
        binding.edtBMode.setOnTouchListener { v, event ->
            items = arrayOfNulls(dropdowns.bookingMode.size)
            for (i in 0 until dropdowns.bookingMode.size) {
                items!![i] = dropdowns.bookingMode.get(i).NAME
            }

            val builder = AlertDialog.Builder(this@CnCreationByEway)
            builder.setItems(items) { dialog: DialogInterface, item: Int ->
                b_mode = dropdowns.bookingMode.get(item).VAL
                binding.edtBMode.setText(items!!.get(item))
                dialog.dismiss()
            }
            if (event.action == MotionEvent.ACTION_UP) builder.show()
            false
        }
        binding.edtDInst.setOnTouchListener { v, event ->
            items = arrayOfNulls(dropdowns.del_inst.size)
            for (i in 0 until dropdowns.del_inst.size) {
                items!![i] = dropdowns.del_inst.get(i).NAME
            }

            val builder =
                AlertDialog.Builder(this@CnCreationByEway)
            builder.setItems(items) { dialog: DialogInterface, item: Int ->
                d_inst = dropdowns.del_inst.get(item).VAL
                binding.edtDInst.setText(items!!.get(item))
                dialog.dismiss()
            }
            if (event.action == MotionEvent.ACTION_UP) builder.show()
            false
        }
        binding.edtFMode.setOnTouchListener { v, event ->
            items = arrayOfNulls(dropdowns.freightMode.size)
            for (i in 0 until dropdowns.freightMode.size) {
                items!![i] = dropdowns.freightMode.get(i).NAME
            }

            val builder =
                AlertDialog.Builder(this@CnCreationByEway)
            builder.setItems(items) { dialog: DialogInterface, item: Int ->
                if (item == 0) {
                    SaveBillingPartyDialog()
                } else {
                    billing_party = ""
                }
                f_mode = dropdowns.freightMode.get(item).VAL
                binding.edtFMode.setText(items!!.get(item))
                dialog.dismiss()
            }
            if (event.action == MotionEvent.ACTION_UP) builder.show()
            false
        }
        binding.edtPkgType.setOnTouchListener { v, event ->
            items = arrayOfNulls(dropdowns.pkgType.size)
            for (i in 0 until dropdowns.pkgType.size) {
                items!![i] = dropdowns.pkgType.get(i).NAME
            }

            val builder =
                AlertDialog.Builder(this@CnCreationByEway)
            builder.setItems(items) { dialog: DialogInterface, item: Int ->
                pkg_type = dropdowns.pkgType.get(item).VAL
                binding.edtPkgType.setText(items!!.get(item))
                dialog.dismiss()
            }
            if (event.action == MotionEvent.ACTION_UP) builder.show()
            false
        }
    }


    private fun CreateCN() {
        if (CNCreationValidate()) {
            val mod: CnCreateEwayMod = CnCreateEwayMod()
            mod.status=("createCn")
            mod.loginBr=OmOperation.getPreferences(Constants.BCODE,"")
            mod.enterBy=OmOperation.getPreferences(Constants.EMP_CODE,"")
            // mod.setCnrCode(details.getCnr_code());
            mod.cnrCode=cnr_code
            mod.cnrGstn=details?.cnr_gstn?:""  //(if (details.cnr_gstn.toString() == null) "" else details.cnr_gstn.toString())
            mod.cneePincode=details?.cne_Pin.toString()?:""
            mod.cneeAddress=details?.cne_addr
            mod.palceOfSupply=details?.cnr_gstn!!.substring(0, 2)
            mod.bookingMode=b_mode
            mod.billingGstNo=details?.cnr_gstn
            mod.invoiceNo=details?.inv_no
            mod.invoiceDate=details?.inv_date
            mod.noOfPkg=binding.edtNPkg.text.toString()
            mod.grossValue=details?.inv_grs_value.toString() + ""
            mod.netValues=details?.inv_net_value.toString()+ ""
            mod.qty=binding.edtQty.text.toString() + ""
            mod.actualWeight=binding.edtActWeight.text.toString()
            mod.chrgWeight=binding.edtChWeight.text.toString()
            mod.cftUnit=cft_unit.toString() + ""
            mod.itemDescription=details?.productDesc.toString()
            mod.ewbNo=details?.ewb_no?:""
            mod.ewbDate=details?.ewb_date?:""
            mod.billing_party=billing_party



            mod.setCNE_CODE(cne_code)

            mod.setDEL_INST(d_inst)
            mod.setPO_NO(binding.edtPoNo.text.toString())
            mod.setFREIGHT_MODE(f_mode)
            mod.setPKG_TYPE(pkg_type)
            mod.setPART_NO(binding.edtPart.text.toString())
            mod.setLEN(binding.edtL.text.toString())
            mod.setWIDTH(binding.edtW.text.toString())
            mod.setHEIGHT(binding.edtH.text.toString())
            mod.setCFT_RATE(binding.edtCftRate.text.toString())
            mod.setMANNUAL_NO(binding.edtManualNo.text.toString())
            mod.setRemarks(binding.edtRemarks.text.toString())
           lifecycleScope.launch {
               val response=ApiClient.getClientsanchar().create(ServiceInterface::class.java).createCn(Utils.getheaders(),mod)
               if(response.code()==200){
                   pd!!.dismiss()
                   if(response.body()?.error.equals("false")){
                       data_availble = false
                       binding.btnSubmit.visibility = View.GONE
                       binding.llEwayReport.visibility = View.GONE
                       val alertDialogBuilder = AlertDialog.Builder(this@CnCreationByEway)
                       alertDialogBuilder.setIcon(R.drawable.ic_success)

                       alertDialogBuilder.setMessage(response.body()?.message?:"")
                       alertDialogBuilder.setPositiveButton(
                           "Print"
                       ) { arg0, arg1 -> //  sendEmail(response.body().getCn_no()+"");
                           sendmail(response.body()?.cn_no.toString() )
                       }

                       alertDialogBuilder.setNegativeButton(
                           "Share"
                       ) { dialog, which ->
                           val sharingIntent =
                               Intent(Intent.ACTION_SEND)
                           sharingIntent.setType("text/plain")
                           sharingIntent.putExtra(
                               Intent.EXTRA_TEXT,
                               response.body()?.link?:"Link not found"
                           )
                           sharingIntent.putExtra(
                               Intent.EXTRA_SUBJECT,
                               "CN No :" + response.body()?.cn_no
                           )
                           startActivity(
                               Intent.createChooser(
                                   sharingIntent,
                                   "Share using"
                               )
                           )
                           blankAllValues()
                       }
                       alertDialogBuilder.setNeutralButton(
                           "Exit"
                       ) { dialog, which -> finish() }
                       val alertDialog = alertDialogBuilder.create()
                       alertDialog.show()

                   }
                   else{   Utils.showDialog(
                       this@CnCreationByEway,
                       "Fail",
                       response.body()?.message,
                       R.drawable.ic_error_outline_red_24dp
                   )
                       binding.tvCnr.text = ""
                       binding.tvCne.text = ""
                       binding.tvEwayDetails.text = "" }
               }
               else{
                   pd!!.dismiss()
                   Utils.showDialog(this@CnCreationByEway,"error code${response.code()}",response.message(),R.drawable.ic_error_outline_red_24dp)
               }


           }

          lifecycleScope.launch {
              if(Utils.haveInternet(this@CnCreationByEway)){
              val response= ApiClient.getClientsanchar().create(ServiceInterface::class.java).createCn(Utils.getheaders(),mod)
                  if(response.code()==200){
                      pd!!.dismiss()
                      if(response.body()?.error.equals("false")){
                          data_availble = false
                          binding.btnSubmit.visibility = View.GONE
                          binding.llEwayReport.visibility = View.GONE
                          val alertDialogBuilder = AlertDialog.Builder(this@CnCreationByEway)
                          alertDialogBuilder.setIcon(R.drawable.ic_success)

                          alertDialogBuilder.setMessage(response.body()?.message)
                          alertDialogBuilder.setPositiveButton(
                              "Print"
                          ) { arg0, arg1 -> //  sendEmail(response.body().getCn_no()+"");
                              sendmail(response.body()?.cn_no.toString() + "")
                          }

                          alertDialogBuilder.setNegativeButton(
                              "Share"
                          ) { dialog, which ->
                              val sharingIntent =
                                  Intent(Intent.ACTION_SEND)
                              sharingIntent.setType("text/plain")
                              sharingIntent.putExtra(
                                  Intent.EXTRA_TEXT,
                                  response.body()?.link.toString()
                              )
                              sharingIntent.putExtra(
                                  Intent.EXTRA_SUBJECT,
                                  "CN No :" + response.body()?.cn_no
                              )
                              startActivity(
                                  Intent.createChooser(
                                      sharingIntent,
                                      "Share using"
                                  )
                              )
                              blankAllValues()
                          }
                          alertDialogBuilder.setNeutralButton(
                              "Exit"
                          ) { dialog, which -> finish() }
                          val alertDialog = alertDialogBuilder.create()
                          alertDialog.show()

                      }
                      else {
                          Utils.showDialog(
                              this@CnCreationByEway,
                              "Fail",
                              response.body()?.message,
                              R.drawable.ic_error_outline_red_24dp
                          )
                          binding.tvCnr.text = ""
                          binding.tvCne.text = ""
                          binding.tvEwayDetails.text = ""
                      }
                  }
                  else{
                      pd!!.dismiss()
                      Utils.showDialog(this@CnCreationByEway,"error code ${response.code()}",response.message(),R.drawable.ic_error_outline_red_24dp)
                  }
              }
          }

        }
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                binding.ewayText.setText(ValidateBarcode(result.contents))
                getDetailsofcn()
                //eway_text.setSelection(result.getContents().length());
                Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
                //                getCNDetails();
            }
        }

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            val results: List<String>? = data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            )
            val spokenText =
                results!![0].replace("\\s".toRegex(), "").replace("[^0-9]".toRegex(), "")
                    .trim { it <= ' ' }
            binding.ewayText.setText(spokenText)
            binding.ewayText.setSelection(spokenText.length)
            //    getCNDetails();
        }
    }

    private fun sendmail(cnNO: String) {
        val dialog = Dialog(this@CnCreationByEway)
        dialog.setContentView(R.layout.custommail)
        val messageTextView = dialog.findViewById<TextView>(R.id.messageTextView)
        val emailEditText = dialog.findViewById<EditText>(R.id.emailEditText)
        val okButton = dialog.findViewById<Button>(R.id.okButton)
        messageTextView.text = "CN has been created successfully. your CN number is $cnNO"
        okButton.setOnClickListener {
            if (emailEditText.text.toString().equals("", ignoreCase = true)) {
                Utils.showDialog(
                    this@CnCreationByEway,
                    "error",
                    "Please enter email address",
                    R.drawable.ic_error_outline_red_24dp
                )
            } else {
                PRINT(cnNO, emailEditText.text.toString())

                //  sendEmail(cnNO, emailEditText.getText().toString());
            }
        }

        dialog.show()
    }

    private fun PRINT(cn: String, emailid: String) {
    }

    private fun ValidateBarcode(input: String): String {
        if (input.length == 12) {
            return input
        } else {
            try {
                val result = input.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                return result[0]
            } catch (e: Exception) {
            }
        }
        return ""
    }

    private fun CNCreationValidate(): Boolean {
        if (details == null) {
            Toast.makeText(this, "Kindly Scan E-Way Bill", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.edtNPkg.text.toString().equals("")) {
            Toast.makeText(this, "Kindly Enter No of PKG", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.edtCftUnit.text.toString().equals("")) {
            Toast.makeText(this, "Kindly Enter CFT Unit", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.edtActWeight.text.toString().equals("")) {
            Toast.makeText(this, "Kindly Enter Actual Weight", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.edtChWeight.text.toString().equals("")) {
            Toast.makeText(this, "Kindly Enter Chargeble Weight", Toast.LENGTH_SHORT).show()
            return false
        } else if (binding.edtActWeight.text.toString()
                .toInt() > binding.edtChWeight.text.toString().toInt()
        ) {
            binding.edtChWeight.error = "Chargeble weight should be more than Actual weight"
            return false
        } else if (cne_code == null) {
            Utils.showDialog(
                this,
                "error",
                "Please select Consignee",
                R.drawable.ic_error_outline_red_24dp
            )
            return false
        }


        return true
    }

    //EP002706775
  lateinit  var dropdowns: Dropdowns 
    private fun getAllDropDown() {
        if (Utils.isNetworkConnected(this)) {
            pd!!.show()

            val mod = Eway_DropDownMod()
            mod.status=("dropdowns")
            lifecycleScope.launch {
                if(Utils.haveInternet(this@CnCreationByEway)){
                  val response=  ApiClient.getClientsanchar().create(ServiceInterface::class.java).dropdowns(Utils.getheaders(),mod)
                    if(response.code()==200){
                        pd!!.dismiss()
                        if(response.body()?.error.equals("false")){
                            dropdowns = response.body()!!.dropdowns
                            clickListener()
                        }
                    }
                    else{
                        pd!!.dismiss()
                        Utils.showDialog(this@CnCreationByEway,"error code ${response.code()}",response.message(),R.drawable.ic_error_outline_red_24dp)
                    }

                }
            }

        } else {
            finish()
        }
    }

    private fun SaveBillingPartyDialog() {
        val employee_dialog = AlertDialog.Builder(this@CnCreationByEway).create()
        val layoutInflater =
            applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.dialog_insert_cne, null)
        val tv_head = view.findViewById<TextView>(R.id.tv_head)
        val tv_msg = view.findViewById<TextView>(R.id.tv_msg)
        val edt_cne = view.findViewById<EditText>(R.id.edt_cne)
        val edt_gst = view.findViewById<EditText>(R.id.edt_gst)
        val edt_pin = view.findViewById<EditText>(R.id.edt_pin)
        val btn_yes = view.findViewById<Button>(R.id.yes_btn)
        tv_head.text = "Enter Billing Code"
        tv_msg.text = "Enter Billing Party Code"
        /* edt_cne.setText("101870");
        edt_gst.setText("23AIIPAPA0865B1ZZ");
        edt_pin.setText("470002");*/
        edt_cne.hint = "Enter Billing party Code"
        edt_gst.visibility = View.GONE
        edt_pin.visibility = View.GONE

        val btn_no = view.findViewById<Button>(R.id.no_btn)
        btn_yes.setOnClickListener { view1: View? ->
            billing_party = edt_cne.getText().toString()
            if (billing_party.equals("", ignoreCase = true)) {
                Toast.makeText(
                    this,
                    "Kindly enter Billing party Code",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (Utils.isNetworkConnected(this)) {
                pd!!.show()
                val mod: VerifyCneMod = VerifyCneMod()
                mod.status=("checkBilling")
                mod.billing_party=(billing_party)
               lifecycleScope.launch {
                   val response= ApiClient.getClientsanchar().create(ServiceInterface::class.java).checkBilling(Utils.getheaders(),mod)
                   if(response.code()==200){
                       if(response.body()?.error.equals("false")){
                           employee_dialog.dismiss()
                           Utils.showDialog(
                               this@CnCreationByEway,
                               "Success",
                               response.body()?.message,
                               R.drawable.ic_success
                           )
                           Toast.makeText(
                               this@CnCreationByEway,
                               response.body()?.message,
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                       else {
                           billing_party = ""
                           Utils.showDialog(
                               this@CnCreationByEway,
                               "Fail",
                               response.body()?.message,
                               R.drawable.ic_error_outline_red_24dp
                           )
                           Toast.makeText(
                               this@CnCreationByEway,
                               response.body()?.message,
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                   }
                   else{
                       Utils.showDialog(this@CnCreationByEway,"error code ${response.code()}",response.message(),R.drawable.ic_error_outline_red_24dp)
                   }
               }

                 
            }
        }
        btn_no.setOnClickListener { view12: View? -> employee_dialog.dismiss() }
        employee_dialog.setView(view)
        employee_dialog.show()
    }

    private fun showRequestAccessDialog(a: Int, gst: String, pin: String) {
        val employee_dialog = AlertDialog.Builder(this@CnCreationByEway).create()
        val layoutInflater =
            applicationContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater.inflate(R.layout.dialog_insert_cne, null)
        val edt_cne = view.findViewById<EditText>(R.id.edt_cne)
        val edt_gst = view.findViewById<EditText>(R.id.edt_gst)
        val edt_pin = view.findViewById<EditText>(R.id.edt_pin)
        val btn_yes = view.findViewById<Button>(R.id.yes_btn)


        /* edt_cne.setText("101870");
        edt_gst.setText("23AIIPAPA0865B1ZZ");
        edt_pin.setText("470002");*/
        edt_gst.setText(gst)
        edt_pin.setText(pin)
        edt_gst.visibility = View.GONE
        edt_pin.visibility = View.GONE
        val btn_no = view.findViewById<Button>(R.id.no_btn)
        btn_yes.setOnClickListener { view1: View? ->
            if ((edt_cne.getText().toString().equals("", ignoreCase = true) ||
                        edt_gst.getText().toString().equals("", ignoreCase = true) ||
                        edt_pin.getText().toString().equals("", ignoreCase = true))

            ) {
                Toast.makeText(this, "Kindly Fill all details", Toast.LENGTH_SHORT).show()
            } else {
                if (Utils.isNetworkConnected(this)) {
                    pd!!.show()
                     val mod = VerifyCneMod()
                    mod.status=("checkCNE")
                    mod.cne=(edt_cne.getText().toString())
                    mod.cneGst=(gst.uppercase())
                    mod.cnePin=(pin.uppercase())
                    lifecycleScope.launch {
                       val response= ApiClient.getClientsanchar().create(ServiceInterface::class.java).checkCNE(Utils.getheaders(),mod)
                    if(response.code()==200){
                        pd!!.dismiss()
                        if(response.body()?.error.equals("false")){
                            employee_dialog.dismiss()
                            if (a == 1) {
                                cne_code = edt_cne.getText().toString()
                                binding.cneeList.setText(edt_cne.getText().toString())
                            } else if (a == 2) {
                                cnr_code = edt_cne.getText().toString()
                                binding.cnrList.setText(edt_cne.getText().toString())
                            }
                            Toast.makeText(
                                this@CnCreationByEway,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else
                            Toast.makeText(
                                this@CnCreationByEway,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                    }
                        else{
                        pd!!.dismiss()
                            Utils.showDialog(this@CnCreationByEway,"error code ${response.code()}",response.message(),R.drawable.ic_error_outline_red_24dp)
                        }
                    }


                }
            }
        }
        btn_no.setOnClickListener { view12: View? -> employee_dialog.dismiss() }
        employee_dialog.setView(view)
        employee_dialog.show()
    }

    private fun blankAllValues() {
        binding.ewayText.setText("")
        binding.edtBMode.setText("")
        binding.edtNPkg.setText("")
        binding.edtCftUnit.setText("")
        binding.edtActWeight.setText("")
        binding.edtChWeight.setText("")

        binding.edtDInst.setText("")
        binding.edtFMode.setText("")
        binding.edtPart.setText("")
        binding.edtL.setText("")
        binding.edtW.setText("")
        binding.edtH.setText("")
        binding.edtPkgType.setText("")
        binding.edtCftRate.setText("")

        binding.edtManualNo.setText("")
        binding.edtPoNo.setText("")
        binding.edtQty.setText("")
        binding.edtRemarks.setText("")
        binding.cneeList.text = "Select Consignee"
        binding.cnrList.text = "Select Consigner"
        f_mode = ""
        d_inst = ""
        b_mode = ""
        pkg_type = ""
        cne_code = ""
        cnr_code = ""
        getAllDropDown()
    }


}