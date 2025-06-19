package com.example.omoperation.activities

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.LoadingPlanTallyAdapter
import com.example.omoperation.databinding.ActivityLoadingPlanBinding
import com.example.omoperation.databinding.ActivityLoadingPlanTallyBinding
import com.example.omoperation.model.findlorry.LorryMod
import com.example.omoperation.model.findlorry.LorryTypeResp
import com.example.omoperation.model.generatetally.Cnlist
import com.example.omoperation.model.generatetally.GenerateTallYMod
import com.example.omoperation.model.tally.Detail
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale

class LoadingPlanTally : AppCompatActivity(),LoadingPlanTallyAdapter.LoadingPlanInterface {
  lateinit  var binding : ActivityLoadingPlanTallyBinding
    var items = arrayOf("Branch", "Hub/Region", "All", "Route")
    var items2 = arrayOf("")
    var tally_by = "M"
    var formatter = "##,###,###.##"
    lateinit var df: DecimalFormat
    var grcount_num: Int = 0
    var total_weight: Double = 0.0
    var sacn_weight: Double = 0.0

    lateinit var adapter: LoadingPlanTallyAdapter
    lateinit var send_lorryType: String
    var check_lorryType: Int = 0
    val detail: ArrayList<Detail> by lazy { ArrayList() }
    lateinit var Lodinginterfaces: LoadingPlanTallyAdapter.LoadingPlanInterface
    var remarks = ""
      val cp :CustomProgress by lazy { CustomProgress(this)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil. setContentView(this,R.layout.activity_loading_plan_tally)
        binding.destination.setText(OmOperation.getPreferences(Constants.BCODE,""))
        Lodinginterfaces = this
        df = DecimalFormat(formatter)
        speech = TextToSpeech(applicationContext) { status: Int ->
            if (status != TextToSpeech.ERROR) {
                speech!!.setLanguage(Locale.UK)
            }
        }
        binding.recyCn.setHasFixedSize(true)
        binding.recyCn.layoutManager = LinearLayoutManager(this)
        setoptions()
        findalllorry()
        binding.cnText.setOnEditorActionListener(TextView.OnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                SearchCN()
                binding.cnText.setText("")
                binding.cnText.requestFocus()
                closekeyboard()
                return@OnEditorActionListener true
            } else {
                binding.cnText.setText("")
                binding.cnText.requestFocus()
                return@OnEditorActionListener true
            }
        })
        binding.search.setOnClickListener {
            if(binding.edtToBranch.text.toString().isEmpty() || binding.edtToBranch.text.toString().equals("")){
                    Utils.showDialog(this,"error","Please enter to Branch ",R.drawable.ic_error_outline_red_24dp)
                    return@setOnClickListener
            }
            val mod = LorryMod()
            mod.status = "search"
            mod.from_branch = OmOperation.getPreferences(Constants.BCODE,"")
            // mod.from_branch="1314"
            mod.tally_by = tally_by
            mod.to_branch = binding.edtToBranch.text.toString()
            if(Utils.haveInternet(this)){
                cp.show()
                lifecycleScope.launch {
                    val response=  ApiClient.getClientsanchar().create(ServiceInterface::class.java).Tall_Search(Utils.getheaders(),mod)
                   if(response!=null){
                       if(response.code()==200){
                           cp.dismiss()
                           if(response.body()!!.error.equals("false")){
                               binding.cnText.requestFocus()
                               closekeyboard()

                               total_weight = 0.0
                               sacn_weight = 0.0
                               detail.clear()
                               for (i in 0 until response!!.body()!!.detail!!.size) {
                                   total_weight =
                                       (total_weight + response!!.body()!!.detail!!.get(i).WT!!.toDouble())
                                   detail.add(
                                       Detail(
                                           response!!.body()!!.detail!!.get(i).CEE,
                                           response!!.body()!!.detail!!.get(i).CEE_CUSTOMER_NAME,
                                           response!!.body()!!.detail!!.get(i).CNR,
                                           response!!.body()!!.detail!!.get(i).CN_CN_DATE,
                                           response!!.body()!!.detail!!.get(i).CN_CN_NO,
                                           response!!.body()!!.detail!!.get(i).CN_FREIGHT_PAID_MODE,
                                           response!!.body()!!.detail!!.get(i).CN_MAMUAL_CN_DATE,
                                           response!!.body()!!.detail!!.get(i).CUSTOMER_CUSTOMER_NAME,
                                           response!!.body()!!.detail!!.get(i).DEL_POINT,
                                           response!!.body()!!.detail!!.get(i).DESTINATION_BRANCH_NAME,
                                           response!!.body()!!.detail!!.get(i).DEST_CODE,
                                           response!!.body()!!.detail!!.get(i).FRIGHT_MODE,
                                           response!!.body()!!.detail!!.get(i).M_CN_NO,
                                           response!!.body()!!.detail!!.get(i).ORIGIN,
                                           response!!.body()!!.detail!!.get(i).PKG,
                                           response!!.body()!!.detail!!.get(i).QTY,
                                           response!!.body()!!.detail!!.get(i).REASON,
                                           response!!.body()!!.detail!!.get(i).SOURCE_BRANCH_NAME,
                                           response!!.body()!!.detail!!.get(i).STATUS,
                                           response!!.body()!!.detail!!.get(i).TMODE,
                                           response!!.body()!!.detail!!.get(i).TPTR_MODE,
                                           response!!.body()!!.detail!!.get(i).T_S_MODE,
                                           response!!.body()!!.detail!!.get(i).WT,
                                           response!!.body()!!.detail!!.get(i).CHRGWT,
                                           "*",
                                           response!!.body()!!.detail!!.get(i).COLOR_BY,
                                           response!!.body()!!.detail!!.get(i).CN_AGE,
                                           false,
                                       )
                                   )
                               }


                               //  detail= (response.body()!!.detail as ArrayList<Detail>?)!!
                               grcount_num = 0
                               if (detail.size > 0)
                                   binding.barcodeCount.setText("Total GR = " + detail.size.toString())
                               else binding.barcodeCount.setText("Scanned GR =0")
                               binding.GRCount.setText("" + grcount_num)
                               binding.tvTotWeight.setText("Total Weight = " + df.format(total_weight))
                               grcount_num = 0
                               adapter = LoadingPlanTallyAdapter(detail, Lodinginterfaces)
                               binding.recyCn.adapter = adapter

                           }
                           else{
                               Utils.showDialog(this@LoadingPlanTally,"error","Data not found",R.drawable.ic_error_outline_red_24dp)
                           }
                       }
                       else{
                           Utils.showDialog(this@LoadingPlanTally,"error ${response.code()}",response.message(),R.drawable.ic_error_outline_red_24dp)

                           cp.dismiss()
                       }
                   }
                    else {
                       Utils.showDialog(this@LoadingPlanTally,"error","response not found",R.drawable.ic_error_outline_red_24dp)
                       cp.dismiss()

                    }



                }
            }


        }
        binding. save.setOnClickListener {
            var a = 0
            if(detail.size==0){
                Utils.showDialog(this,"error","Please Add some GR",R.drawable.ic_error_outline_red_24dp)
                return@setOnClickListener
            }
            for (i in 0 until detail.size) {
                if (detail.get(i).remarks.equals("*") && detail.get(i).checkvarue != true) {
                    a = a + 1
                }

            }
            Log.d("ashishsikarwar", "" + sacn_weight);
            Log.d("ashishsikarwar", "" + check_lorryType);
            if (a > 0 ) {
                Utils.showDialog(
                    this,
                    "Error",
                    "You have " + a + " missing some GR . Kindly add remark to complete this.",
                    R.drawable.ic_error_outline_red_24dp
                )
            }
            else  if (a > 0 && sacn_weight < check_lorryType) {
                Utils.showDialog(
                    this,
                    "Error",
                    "You have " + a + " missing some GR . Kindly add remark to complete this.",
                    R.drawable.ic_error_outline_red_24dp
                )
            } else {

                val mod = GenerateTallYMod()
                mod.status = "insert"
                mod.from_branch = OmOperation.getPreferences(Constants.BCODE,"")
                // mod.from_branch="1314"
                mod.tally_by = tally_by
                mod.to_branch = binding.edtToBranch.text.toString()
                mod.enter_by = OmOperation.getPreferences(Constants.EMP_CODE,"")
                val array: ArrayList<Cnlist>
                array = ArrayList()
                for (i in 0 until detail.size) {

                    if (detail.get(i).checkvarue == true)
                        array.add(
                            Cnlist(
                                detail.get(i).CN_CN_NO,
                                detail.get(i).PKG,
                                "",
                                detail.get(i).WT
                            )
                        )
                    else
                        array.add(
                            Cnlist(
                                detail.get(i).CN_CN_NO,
                                detail.get(i).PKG,
                                detail.get(i).remarks,
                                detail.get(i).WT
                            )
                        )
                }
                mod.cnlist = array
          if(Utils.haveInternet(this)){
              cp.show()
              lifecycleScope.launch {
                  val response= ApiClient.getClientsanchar().create(ServiceInterface::class.java).GenerateTally(Utils.getheaders(),mod)
                if(response!=null){
                    if(response!!.code()==200){
                        cp.dismiss()
                        if (response.body()!!.error.equals("false", ignoreCase = true)) {
                            Utils.showDialog(
                                this@LoadingPlanTally,
                                "Success",
                                response.body()!!.response,
                                R.drawable.ic_success
                            )
                        } else {
                            Utils.showDialog(
                                this@LoadingPlanTally,
                                "Fail",
                                response.body()!!.error,
                                R.drawable.ic_error_outline_red_24dp
                            )
                        }
                    }
                    else{
                        cp.dismiss()
                        Utils.showDialog(
                            this@LoadingPlanTally,
                            "code : "+ response.code(),
                            response.message(),
                            R.drawable.ic_error_outline_red_24dp
                        )

                    }
                }
                else{
                    cp.dismiss()
                    Utils.showDialog(
                        this@LoadingPlanTally,
                        "error",
                       "response not found",
                        R.drawable.ic_error_outline_red_24dp
                    )

                }
              }
          }



            }


        }
      }
    var foundcn = false
    private fun SearchCN() {

        for (i in 0 until detail.size) {
            if (binding.cnText.text.toString()
                    .equals(detail.get(i).M_CN_NO) && detail.get(i).checkvarue == false
            ) {
                grcount_num = grcount_num + 1
                sacn_weight = sacn_weight + detail.get(i).WT!!.toDouble()
                binding.GRCount.setText("Scanned GR = " + grcount_num)
                binding.tvSacWeight.setText("Scanned Weight = " + sacn_weight)
                foundcn = true
                detail.set(
                    i, Detail(
                        detail.get(i).CEE,
                        detail.get(i).CEE_CUSTOMER_NAME,
                        detail.get(i).CNR,
                        detail.get(i).CN_CN_DATE,
                        detail.get(i).CN_CN_NO,
                        detail.get(i).CN_FREIGHT_PAID_MODE,
                        detail.get(i).CN_MAMUAL_CN_DATE,
                        detail.get(i).CUSTOMER_CUSTOMER_NAME,
                        detail.get(i).DEL_POINT,
                        detail.get(i).DESTINATION_BRANCH_NAME,
                        detail.get(i).DEST_CODE,
                        detail.get(i).FRIGHT_MODE,
                        detail.get(i).M_CN_NO,
                        detail.get(i).ORIGIN,
                        detail.get(i).PKG,
                        detail.get(i).QTY,
                        detail.get(i).REASON,
                        detail.get(i).SOURCE_BRANCH_NAME,
                        detail.get(i).STATUS,
                        detail.get(i).TMODE,
                        detail.get(i).TPTR_MODE,
                        detail.get(i).T_S_MODE,
                        detail.get(i).WT,
                        detail.get(i).CHRGWT,
                        "*",
                        detail!!.get(i).COLOR_BY,
                        detail!!.get(i).CN_AGE,
                        true,
                    )
                )
                detail.add(
                    0, Detail(
                        detail.get(i).CEE,
                        detail.get(i).CEE_CUSTOMER_NAME,
                        detail.get(i).CNR,
                        detail.get(i).CN_CN_DATE,
                        detail.get(i).CN_CN_NO,
                        detail.get(i).CN_FREIGHT_PAID_MODE,
                        detail.get(i).CN_MAMUAL_CN_DATE,
                        detail.get(i).CUSTOMER_CUSTOMER_NAME,
                        detail.get(i).DEL_POINT,
                        detail.get(i).DESTINATION_BRANCH_NAME,
                        detail.get(i).DEST_CODE,
                        detail.get(i).FRIGHT_MODE,
                        detail.get(i).M_CN_NO,
                        detail.get(i).ORIGIN,
                        detail.get(i).PKG,
                        detail.get(i).QTY,
                        detail.get(i).REASON,
                        detail.get(i).SOURCE_BRANCH_NAME,
                        detail.get(i).STATUS,
                        detail.get(i).TMODE,
                        detail.get(i).TPTR_MODE,
                        detail.get(i).T_S_MODE,
                        detail.get(i).WT,
                        detail.get(i).CHRGWT,
                        "*",
                        detail!!.get(i).COLOR_BY,
                        detail!!.get(i).CN_AGE,
                        true,
                    )
                )
                binding.cnText.setText("")
                detail.removeAt(i + 1)


                //Collections.rotate(detail, i);
                adapter.notifyDataSetChanged()
                // cn_text.setText("")
                /*  cn_text.setCursorVisible(true);
                  cn_text.requestFocus()
                  cn_text.setSelection(0);*/
                return
            } else if (binding.cnText.text.toString()
                    .equals(detail.get(i).M_CN_NO) && detail.get(i).checkvarue == true
            ) {
                foundcn = true
                speak("Already Added")
                break
            }

        }

        if (!foundcn) {
            binding.cnText.setText("")
            speak("not in List")
            Utils.showDialog(this, "Error", "Not in List", R.drawable.ic_error_outline_red_24dp)
        }
        foundcn = false

    }
    private var speech: TextToSpeech? = null
    private fun speak(msg: String) {
        speech!!.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
    }
    private fun setoptions() {
        binding.edtTallyBy.setText("Branch")
        binding.edtTallyBy.setOnTouchListener { v, event ->
            //items = resources.getStringArray(R.array.tally_by)
            items = arrayOf("Branch", "Hub/Region", "All", "Route")
            val builder = AlertDialog.Builder(this@LoadingPlanTally)
            builder.setItems(
                items,
                DialogInterface.OnClickListener { dialog: DialogInterface, item: Int ->
                    if (item == 0) {
                        tally_by = "M"
                    } else if (item == 1) {
                        tally_by = "H"
                    } else if (item == 2) {
                        tally_by = "A"
                    } else if (item == 3) {
                        tally_by = "R"
                    }
                    binding.edtTallyBy.setText(items.get(item))
                    dialog.dismiss()
                })
            if (event.action == MotionEvent.ACTION_UP) builder.show()
            false 
        }
    }
    private fun findalllorry() {
        if (Utils.haveInternet(this)) {

            CoroutineScope(Dispatchers.IO).launch {
                val mod = LorryMod().apply {
                    status = "lorry_type"
                }
                try {
                    // Show progress dialog on the main thread
                    val response: Response<LorryTypeResp> =
                        ApiClient.getClientsanchar().create(ServiceInterface::class.java).LorryType(Utils.getheaders(), mod)

                    // Dismiss progress dialog on the main thread

                    // Handle the response
                    withContext(Dispatchers.Main) {

                        if (response.isSuccessful && response.body() != null) {
                            if (response.body()!!.error.equals("false", ignoreCase = true)) {
                                val lorryType = response.body()!!.lorryType
                                val lorryArray =
                                    lorryType.map { "${it.SYSCDS_CODE_DESC}--${it.SYSCDS_TRUCK_CAPACITY_KG}KG" }
                                val items2 = lorryArray.toTypedArray()

                                binding.edtLorryType.setText(lorryType[0].SYSCDS_CODE_DESC)
                                send_lorryType = lorryType[0].SYSCDS_CODE_VALUE
                                check_lorryType = lorryType[0].SYSCDS_TRUCK_CAPACITY_KG.toInt()

                                binding.edtLorryType.setOnTouchListener { _, event ->
                                    if (event.action == MotionEvent.ACTION_UP) {
                                        AlertDialog.Builder(this@LoadingPlanTally).apply {
                                            setItems(items2) { dialog, item ->
                                                lorryType[item].let {
                                                    binding.edtLorryType.setText(it.SYSCDS_CODE_DESC)
                                                    send_lorryType = it.SYSCDS_CODE_VALUE
                                                    check_lorryType = it.SYSCDS_TRUCK_CAPACITY_KG.toInt()
                                                }
                                                dialog.dismiss()
                                            }
                                        }.show()
                                        return@setOnTouchListener true
                                    }
                                    false
                                }
                                binding.searchBtn.setOnClickListener {
                                    SearchCN()
                                    binding.cnText.setText("")
                                    binding.cnText.requestFocus()
                                    closekeyboard()
                                }

                            } else {
                                Utils.showDialog(
                                    this@LoadingPlanTally,
                                    "Fail",
                                    response.body()!!.error,
                                    R.drawable.ic_error_outline_red_24dp
                                )
                            }
                        } else {
                            Utils.showDialog(
                                this@LoadingPlanTally,
                                "Fail",
                                "Error code: ${response.code()}",
                                R.drawable.ic_error_outline_red_24dp
                            )
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                     //   pd?.dismiss()
                        Utils.showDialog(
                            this@LoadingPlanTally,
                            "Error",
                            e.message ?: "Unknown error",
                            R.drawable.ic_error_outline_red_24dp
                        )
                    }
                }
            }
        }
    }
    private fun closekeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun sendRemarks(i: Int, task: Boolean) {
        detail.set(
            i, Detail(
                detail.get(i).CEE,
                detail.get(i).CEE_CUSTOMER_NAME,
                detail.get(i).CNR,
                detail.get(i).CN_CN_DATE,
                detail.get(i).CN_CN_NO,
                detail.get(i).CN_FREIGHT_PAID_MODE,
                detail.get(i).CN_MAMUAL_CN_DATE,
                detail.get(i).CUSTOMER_CUSTOMER_NAME,
                detail.get(i).DEL_POINT,
                detail.get(i).DESTINATION_BRANCH_NAME,
                detail.get(i).DEST_CODE,
                detail.get(i).FRIGHT_MODE,
                detail.get(i).M_CN_NO,
                detail.get(i).ORIGIN,
                detail.get(i).PKG,
                detail.get(i).QTY,
                detail.get(i).REASON,
                detail.get(i).SOURCE_BRANCH_NAME,
                detail.get(i).STATUS,
                detail.get(i).TMODE,
                detail.get(i).TPTR_MODE,
                detail.get(i).T_S_MODE,
                detail.get(i).WT,
                detail.get(i).CHRGWT,
                detail.get(i).remarks,
                detail.get(i).COLOR_BY,
                detail.get(i).CN_AGE,
                task,
            )
        )
        adapter.notifyDataSetChanged()
    }

    override fun sendRemarks(i: Int) {

        items = resources.getStringArray(R.array.loading_remarks)
        val builder = AlertDialog.Builder(this@LoadingPlanTally)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            if (item == 0) {
                remarks = "MNR"
            } else if (item == 1) {
                remarks = "ICP"
            } else if (item == 2) {
                remarks = "LC"
            } else if (item == 3) {
                remarks = "OL"
            } else if (item == 4) {
                remarks = "WOP"
            } else if (item == 5) {
                remarks = "WF"
            } else if (item == 6) {
                remarks = "WCD"
            }
            detail.set(
                i, Detail(
                    detail.get(i).CEE,
                    detail.get(i).CEE_CUSTOMER_NAME,
                    detail.get(i).CNR,
                    detail.get(i).CN_CN_DATE,
                    detail.get(i).CN_CN_NO,
                    detail.get(i).CN_FREIGHT_PAID_MODE,
                    detail.get(i).CN_MAMUAL_CN_DATE,
                    detail.get(i).CUSTOMER_CUSTOMER_NAME,
                    detail.get(i).DEL_POINT,
                    detail.get(i).DESTINATION_BRANCH_NAME,
                    detail.get(i).DEST_CODE,
                    detail.get(i).FRIGHT_MODE,
                    detail.get(i).M_CN_NO,
                    detail.get(i).ORIGIN,
                    detail.get(i).PKG,
                    detail.get(i).QTY,
                    detail.get(i).REASON,
                    detail.get(i).SOURCE_BRANCH_NAME,
                    detail.get(i).STATUS,
                    detail.get(i).TMODE,
                    detail.get(i).TPTR_MODE,
                    detail.get(i).T_S_MODE,
                    detail.get(i).WT,
                    detail.get(i).CHRGWT,
                    remarks,
                    detail!!.get(i).COLOR_BY,
                    detail!!.get(i).CN_AGE,
                    false,
                )
            )
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }

        builder.show()
    }
}