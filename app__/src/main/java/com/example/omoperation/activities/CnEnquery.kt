package com.example.omoperation.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityCnEnqueryBinding
import com.example.omoperation.factory.CnEnqueryFact
import com.example.omoperation.model.cn_enquery.CnEnquiry
import com.example.omoperation.model.cn_enquery.Myquery
import com.example.omoperation.model.dispactch.CnEnquiryDetail
import com.example.omoperation.model.dispactch.DispatchResp
import com.example.omoperation.model.freight.CnEnquiryFright
import com.example.omoperation.model.freight.FreightResp
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.repositories.CnEnqueryRepository
import com.example.omoperation.viewmodel.CnEnqueryViewmod

class CnEnquery : AppCompatActivity() {
    lateinit var binding: ActivityCnEnqueryBinding
    lateinit var cnEnqueryViewmod: CnEnqueryViewmod
    private var CN_GPS_API: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       binding=DataBindingUtil. setContentView(this,R.layout.activity_cn_enquery)
        cnEnqueryViewmod=ViewModelProvider(this,CnEnqueryFact(this, CnEnqueryRepository())).get(CnEnqueryViewmod::class.java)
        binding.cnenquery=cnEnqueryViewmod
        binding.lifecycleOwner=this

        cnEnqueryViewmod.cndata.observe(this) { cnEnqueryResp -> getCNDetails(cnEnqueryResp) }
        cnEnqueryViewmod.dispatchdetails.observe(this) { dispatchdetails ->
            getDispatchDetail(
                dispatchdetails
            )
        }

        cnEnqueryViewmod.freightdetails.observe(this) { freightresp ->
            getFrightDetails(
                freightresp
            )
        }

        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.setDuration(500) //You can manage the blinking time with this parameter

        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.setRepeatCount(Animation.INFINITE)
        binding.cnCommuniocation.startAnimation(anim)
        binding.moreBtn.setOnClickListener { view ->
            if (binding.moreBtn.getText().toString().equals("More Details")) {
                binding.ddrrow.visibility = View.VISIBLE
                binding.adrrow.visibility = View.VISIBLE
                binding.billnorow.visibility = View.VISIBLE
                binding.subdaterow.visibility = View.VISIBLE
                binding.subnorow.visibility = View.VISIBLE
                binding.moreBtn.text = "Hide Details"
            } else {
                binding.ddrrow.visibility = View.GONE
                binding.adrrow.visibility = View.GONE
                binding.billnorow.visibility = View.GONE
                binding.subdaterow.visibility = View.GONE
                binding.subnorow.visibility = View.GONE
                binding.moreBtn.text = getString(R.string.more_details)
            }
        }
        binding.downloadPod.setOnClickListener { view ->
            val intent =
                Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(
                Uri.parse(//https://omsanchar.omlogistics.co.in/
                    (ServiceInterface.omsanchar + "oracle/show_pod.php?cnno=" + binding.cnText.getText()
                        .toString()).toString() + ""
                ), "text/html"
            )
            startActivity(intent)
        }

    }
    private fun getFrightDetails(response: FreightResp) {
        try {
            val CNerror: String = response.error
            if (CNerror == "true") {
                binding.table4.visibility = View.GONE
                binding.frightDetailsBtn.visibility = View.VISIBLE
            } else {
                val jsonArray: List<CnEnquiryFright> = response.cn_enquiry_fright
                val jsonObject: CnEnquiryFright = jsonArray[0]
                val RATE = if (jsonObject.RATE == null) "-" else jsonObject.RATE
                val FRIGHT_CHARGE =
                    if (jsonObject.FRIGHT_CHARGE == null) "-" else jsonObject.FRIGHT_CHARGE
                val CN_TOTAL =
                    if (jsonObject.CN_TOTAL == null) "-" else jsonObject.CN_TOTAL
                val CN_TOTAL_TOTAL =
                    if (jsonObject.CN_TOTAL_TOTAL == null) "-" else jsonObject.CN_TOTAL_TOTAL
                binding.colRateValue.text = RATE
                binding.colFrightchargeValue.text = FRIGHT_CHARGE
                binding.colCntotalValue.text = CN_TOTAL
                binding.colCngrossValue.text = CN_TOTAL_TOTAL
                binding.table4.visibility = View.VISIBLE
                binding.table3.visibility = View.GONE
                binding.dispatchDetailsBtn.visibility = View.VISIBLE
                binding.frightDetailsBtn.visibility = View.GONE
            }
        } catch (t: Exception) {
            Utils.showCopyDialog(
                this@CnEnquery,
                "Exception",
                t.message,
                R.drawable.ic_error_outline_red_24dp
            )
        }
    }

    fun getCNDetails(response: Myquery) {
        if (response.error.equals("false")) {
            binding.table.visibility = View.VISIBLE
            binding.table2.visibility = View.VISIBLE
            binding.table3.visibility = View.GONE
            binding.table4.visibility = View.GONE
            binding.dispatchDetailsBtn.visibility = View.VISIBLE
            binding.frightDetailsBtn.visibility = View.VISIBLE
            val data: List<CnEnquiry> =
                response.cn_enquiry//CNResponse.getJSONArray("cn_enquiry");
            val object1: CnEnquiry = data[0]
            val CNNO = if (object1.CNNO == null) "-" else object1.CNNO
            val CNDATE = if (object1.CNDATE == null) "-" else object1.CNDATE
            val CNE = if (object1.CNE == null) "-" else object1.CNE
            val CNR = if (object1.CNR == null) "-" else object1.CNR
            val BFROM = if (object1.BFROM == null) "-" else object1.BFROM
            val BTO = if (object1.BTO == null) "-" else object1.BTO
            val BOOKING_MODE =
                if (object1.BOOKING_MODE == null) "-" else object1.BOOKING_MODE
            val FREIGHT_MODE =
                if (object1.FREIGHT_MODE == null) "-" else object1.FREIGHT_MODE
            val CN_DELIVERY_DATE =
                if (object1.CN_DELIVERY_DATE == null) "" else object1.CN_DELIVERY_DATE
                    .replace(".000000", "")
            val STATUS = if (object1.STATUS == null) "-" else object1.STATUS + "\n" +
                    "" + CN_DELIVERY_DATE
            val LORRY_NO = if (object1.LORRY_NO == null) "-" else object1.LORRY_NO
            val POD_STATUS = if (object1.POD_STATUS == null) "-" else object1.POD_STATUS
            val CN_DDR_NO = if (object1.CN_DDR_NO == null) "-" else object1.CN_DDR_NO
            val CN_ADR_NO = if (object1.CN_ADR_NO == null) "-" else object1.CN_ADR_NO
            val BILL_NO = if (object1.BILL_NO == null) "-" else object1.BILL_NO
            val SUBMISSION_NO =
                if (object1.SUBMISSION_NO == null) "-" else object1.SUBMISSION_NO
            val SUBMITTED_DATE =
                if (object1.SUBMITTED_DATE == null) "-" else object1.SUBMITTED_DATE
            val ETA = if (object1.ETA == null) "-" else object1.ETA
            val CN_GPS_API1 = if (object1.CN_GPS_API == null) "-" else object1.CN_GPS_API
            val status = if (object1.STATUS == null) "-" else object1.STATUS
            binding.colCnoValue.text = CNNO
            binding.colCndateValue.text = CNDATE
            binding.colConnameValue.text = CNE
            binding.colStatusValue.text = CNR
            binding.lorryNo.text = LORRY_NO
            binding.colFromValue.text = BFROM
            binding.colToValue.text = BTO //col_mode_value
            binding.colModeValue.text = BOOKING_MODE
            binding.ddrValue.text = CN_DDR_NO
            binding.adrValue.text = CN_ADR_NO
            binding.billnoValue.text = BILL_NO
            binding.subDateValue.text = SUBMITTED_DATE
            binding.subnoValue.text = SUBMISSION_NO
            binding.frightValue.text = FREIGHT_MODE
            binding.colundeliver.text =
                if (object1.UNDELIVERED_REASON == null) "-" else object1.UNDELIVERED_REASON
            binding.eta.text = ETA
            // binding. eta.setText(object1.getSTATUS());
            if (status.startsWith("In-Transit") && CN_GPS_API1 != null && BOOKING_MODE != "AIR" && BOOKING_MODE != "TRAIN") {
                //col_expdate.setText(STATUS);//col_exp_value
                binding.colExpValue.text = Html.fromHtml("<u>$STATUS</u>")
                binding.colExpValue.setTextColor(Color.BLUE)
                binding.colExpValue.setOnClickListener { view ->
                  /*  val intent: Intent = Intent(
                        this@CnEnquery,
                        CNTrackMap::class.java
                    )
                    intent.putExtra("url", CN_GPS_API1)
                    startActivity(intent)*/
                }
            } else if (status.startsWith("In-Transit") && BOOKING_MODE == "AIR" || BOOKING_MODE == "TRAIN") {
                binding.colExpValue.setOnClickListener(null)
                binding.colExpValue.text = "In-Transit"
                binding.colExpValue.setTextColor(Color.BLACK)
                binding.dispatchDetailsBtn.visibility = View.GONE
            } else {
                binding.colExpValue.setOnClickListener(null)
                binding.colExpValue.text = STATUS
                binding.colExpValue.setTextColor(Color.BLACK)
                binding.dispatchDetailsBtn.visibility = View.VISIBLE
            }
            if ( /*!CN_GPS_API1.equals("null")*/false) {
                CN_GPS_API = CN_GPS_API1
                binding.trackPodLayout.visibility = View.VISIBLE
            } else {
                binding.trackPodLayout.visibility = View.GONE
            }
            if (POD_STATUS == "Y" || POD_STATUS == "B" || POD_STATUS == "YM") {
                binding.downloadPodLayout.visibility = View.VISIBLE
            } else {
                binding.downloadPodLayout.visibility = View.GONE
            }
            val heading: View =
                LayoutInflater.from(this@CnEnquery).inflate(R.layout.table_row_cn, null, false)
            val heading1 = heading.findViewById<TextView>(R.id.packages)
            heading1.setTypeface(null, Typeface.BOLD)
            heading1.setTextColor(Color.WHITE)
            heading1.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading2 = heading.findViewById<TextView>(R.id.invoiceNO)
            heading2.setTypeface(null, Typeface.BOLD)
            heading2.setTextColor(Color.WHITE)
            heading2.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading3 = heading.findViewById<TextView>(R.id.invoiceDate)
            heading3.setTypeface(null, Typeface.BOLD)
            heading3.setTextColor(Color.WHITE)
            heading3.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading4 = heading.findViewById<TextView>(R.id.part_number)
            heading4.setTypeface(null, Typeface.BOLD)
            heading4.setTextColor(Color.WHITE)
            heading4.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading5 = heading.findViewById<TextView>(R.id.type)
            heading5.setTypeface(null, Typeface.BOLD)
            heading5.setTextColor(Color.WHITE)
            heading5.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading6 = heading.findViewById<TextView>(R.id.quantity)
            heading6.setTypeface(null, Typeface.BOLD)
            heading6.setTextColor(Color.WHITE)
            heading6.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading7 = heading.findViewById<TextView>(R.id.weight)
            heading7.setTypeface(null, Typeface.BOLD)
            heading7.setTextColor(Color.WHITE)
            heading7.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading8 = heading.findViewById<TextView>(R.id.desc)
            heading8.setTypeface(null, Typeface.BOLD)
            heading8.setTextColor(Color.WHITE)
            heading8.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            val heading9 = heading.findViewById<TextView>(R.id.dems)
            heading9.setTypeface(null, Typeface.BOLD)
            heading9.setTextColor(Color.WHITE)
            heading9.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
            heading1.text = "NO OF PKG"
            heading2.text = "INVOICE NUMBER"
            heading3.text = "INVOICE DATE"
            heading4.text = "PART NUMBER"
            heading5.text = "PACKAGING TYPE"
            heading6.text = "QUANTITY"
            heading7.text = "WEIGHT"
            heading8.text = "ITEM DESCRIPTION"
            heading9.text = "DIMENSION"
            binding.dynamicTable.addView(heading)
            for (i in data.indices) {
                val object2: CnEnquiry = data[i]
                val view: View =
                    LayoutInflater.from(this@CnEnquery).inflate(R.layout.table_row_cn, null, false)
                val textView1 = view.findViewById<TextView>(R.id.packages)
                val textView2 = view.findViewById<TextView>(R.id.type)
                val textView3 = view.findViewById<TextView>(R.id.desc)
                val textView4 = view.findViewById<TextView>(R.id.invoiceNO)
                val textView5 = view.findViewById<TextView>(R.id.invoiceDate)
                val textView6 = view.findViewById<TextView>(R.id.part_number)
                val textView7 = view.findViewById<TextView>(R.id.quantity)
                val textView8 = view.findViewById<TextView>(R.id.dems)
                val textView9 = view.findViewById<TextView>(R.id.weight)
                textView1.text = if (object2. NO_OF_PKG  == null) "-" else object2.NO_OF_PKG
                textView2.text =
                    if (object2.PACKING_TYPE == null) "-" else object2.PACKING_TYPE
                textView3.text = if (object2.ITEM_DESC== null) "-" else object2.ITEM_DESC
                textView4.text = if (object2.INVNO == null) "-" else object2.INVNO
                textView5.text = if (object2.INV_DATE == null) "-" else object2.INV_DATE
                textView6.text =
                    if (object2.PART_NUMBER == null) "-" else object2.PART_NUMBER
                textView7.text = if (object2.QUANTITY== null) "-" else object2.QUANTITY
                textView8.text =
                    if (object2.CN_DIMENSION == null) "-" else object2.CN_DIMENSION
                textView9.text = if (object2.CN_WEIGHT == null) "-" else object2.CN_WEIGHT
                binding.dynamicTable.addView(view)
            }
            binding.table.visibility = View.VISIBLE
            binding.table2.visibility = View.VISIBLE
        } else {
            binding.table.visibility = View.GONE
            binding.table2.visibility = View.GONE
            binding.table3.visibility = View.GONE
            binding.table4.visibility = View.GONE
            binding.dispatchDetailsBtn.visibility = View.GONE
            binding.frightDetailsBtn.visibility = View.GONE
            Utils.showDialog(
                this@CnEnquery,
                "error true",
                "Invalid CN Number",
                R.drawable.ic_error_outline_red_24dp
            )
        }
    }

    fun getDispatchDetail(response: DispatchResp) {
        try {
            val CNerror: String = response.error
            if (CNerror == "true") {
                Toast.makeText(this@CnEnquery, "Invalid CN Number", Toast.LENGTH_SHORT).show()
            } else {
                binding.dynamicTableDispatch.removeAllViews()
                binding.dispatchDetailsBtn.visibility = View.GONE
                binding.table3.visibility = View.VISIBLE
                binding.table4.visibility = View.GONE
                binding.frightDetailsBtn.visibility = View.VISIBLE
                val data: List<CnEnquiryDetail> = response.cn_enquiry_detail
                val heading: View = LayoutInflater.from(this@CnEnquery)
                    .inflate(R.layout.table_row_dispatch, null, false)
                val heading1 = heading.findViewById<TextView>(R.id.BOOKING_DATE)
                heading1.setTypeface(null, Typeface.BOLD)
                heading1.setTextColor(Color.WHITE)
                heading1.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                val heading2 = heading.findViewById<TextView>(R.id.BOOKED_FROM)
                heading2.setTypeface(null, Typeface.BOLD)
                heading2.setTextColor(Color.WHITE)
                heading2.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                val heading3 = heading.findViewById<TextView>(R.id.TO_BRANCH)
                heading3.setTypeface(null, Typeface.BOLD)
                heading3.setTextColor(Color.WHITE)
                heading3.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                val heading4 = heading.findViewById<TextView>(R.id.GATE_ENTRY)
                heading4.setTypeface(null, Typeface.BOLD)
                heading4.setTextColor(Color.WHITE)
                heading4.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                val heading5 = heading.findViewById<TextView>(R.id.TRIP_TIME)
                heading5.setTypeface(null, Typeface.BOLD)
                heading5.setTextColor(Color.WHITE)
                heading5.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
                heading1.text = "DISPATCH DATE"
                heading3.text = "To"
                heading2.text = "FROM"
                heading4.text = "ARRIVAL DATE"
                heading5.text = "TRIP TIME"
                binding.dynamicTableDispatch.addView(heading)
                for (i in data.indices) {
                    val object2: CnEnquiryDetail = data[i]
                    val view: View = LayoutInflater.from(this@CnEnquery)
                        .inflate(R.layout.table_row_dispatch, null, false)
                    val textView1 = view.findViewById<TextView>(R.id.BOOKING_DATE)
                    val textView2 = view.findViewById<TextView>(R.id.BOOKED_FROM)
                    val textView3 = view.findViewById<TextView>(R.id.TO_BRANCH)
                    val textView4 = view.findViewById<TextView>(R.id.GATE_ENTRY)
                    val TRIP_TIME = view.findViewById<TextView>(R.id.TRIP_TIME)
                    textView1.text =
                        if (object2.BOOKING_DATE == null) "-" else object2.BOOKING_DATE
                    textView2.text =
                        if (object2.BOOKED_FROM == null) "-" else object2.BOOKED_FROM
                    textView3.text =
                        if (object2.TO_BRANCH == null) "-" else object2.TO_BRANCH
                    textView4.text =
                        if (object2.GATE_ENTRY== null) "-" else object2.GATE_ENTRY
                            .replace(".000000", "")
                    TRIP_TIME.text =
                        if (object2.TRIP_TIME == null) "-" else object2.TRIP_TIME.toString()
                            .replace(".000000", "")
                    binding.dynamicTableDispatch.addView(view)
                }
            }
        } catch (e: Exception) {
            Utils.showDialog(
                this@CnEnquery,
                "Exception",
                e.message,
                R.drawable.ic_error_outline_red_24dp
            )
        }
    }
}