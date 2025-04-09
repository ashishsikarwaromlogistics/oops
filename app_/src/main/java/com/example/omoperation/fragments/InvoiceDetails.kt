package com.example.omoperation.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.compose.ui.unit.Constraints
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.NetworkState
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.EmployeeAdapter
import com.example.omoperation.databinding.FragmentInvoiceDetailsBinding
import com.example.omoperation.model.cncreation.CnCreationMod
import com.example.omoperation.room.tables.Employees
import com.example.omoperation.viewmodel.InvoicesViewMod
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class InvoiceDetails : Fragment() {

    private var _binding: FragmentInvoiceDetailsBinding?=null
    private val binding get() = _binding!!
      val invoicemod:InvoicesViewMod by activityViewModels()
    var loadtype=""
    lateinit var cp:CustomProgress
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoiceDetailsBinding.inflate(inflater, container, false)
        return binding.root

          }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cp= CustomProgress(requireActivity())
        binding.invoicemod=invoicemod
        binding.lifecycleOwner=this
      val mod:CnCreationMod=  Gson().fromJson(OmOperation.getPreferences(Constants.CNCREATION,""),CnCreationMod::class.java)
        loadtype=mod.load_type
        setspinnner()
        setclicklistener()
        binding.date.setOnClickListener { opencalender(binding.date) }
        binding.ewaybilldate.setOnClickListener { opencalender(binding.ewaybilldate) }
        invoicemod.livedata.observe(viewLifecycleOwner, Observer {
            when(it){
                is NetworkState.Error ->{
                    cp.dismiss()
                    Utils.showDialog(activity,it.title,it.message,R.drawable.ic_error_outline_red_24dp)
                }
                is  NetworkState.Success<*> -> {
                    cp.dismiss()
                    Utils.showDialog(activity,"Success",it.data.toString(),R.drawable.ic_success)
                   blankallfields()
                }
                is NetworkState.Loading -> {
                    cp.show()
                }
            }

        })

    }

    private fun blankallfields() {
        binding.invoiceNo.setText("")
        binding.date.setText("")
        binding.partNo.setText("")
        binding.noOfPkg.setText("")
        binding.qty.setText("")
        binding.netValue.setText("")
        binding.grossValue.setText("")
        binding.actualWt.setText("")
        binding.length.setText("")
        binding.width.setText("")
        binding.height.setText("")
        binding.cftWeight.setText("")
        binding.chWt.setText("")
        binding.ewaybilldate.setText("")
        binding.EWayBillNo.setText("")
        binding.itemDesc.setText("")
    }

    fun setclicklistener(){
        binding.actualWt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                calculateChargeableWeight()
            }
        })
        binding.cftWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                calculateChargeableWeight()
            }
        })

        val measoradapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.measurement, android.R.layout.simple_spinner_item
        )
        measoradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.cftUnit.setAdapter(measoradapter)


        binding.cftUnit.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent!!.getItemAtPosition(position).toString()
                invoicemod.updatecftunit(selectedItem)
                Toast.makeText(requireContext(),selectedItem,Toast.LENGTH_SHORT).show()

                calculateCFT()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        textChangeListener(binding.noOfPkg)
        textChangeListener(binding.width)
        textChangeListener(binding.height)
        textChangeListener(binding.length)
        textChangeListener(binding.cftCharge)
    }
    private fun textChangeListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                calculateCFT()
            }
        })
    }
    private fun setspinnner() {
        val tadapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.pck_type, android.R.layout.simple_spinner_item
        )
        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.pckType.setAdapter(tadapter)
        binding.pckType.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                invoicemod.updatepckType(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no item is selected if needed
            }}






    }


    fun opencalender(edt: EditText){
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date and set it to the TextView
                //val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)


                 edt.setText(formattedDate)
            },
            year, month, day
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }
    private fun calculateCFT() {
        val l: String = binding.length.getText().toString().trim { it <= ' ' }
        val w: String = binding.width.getText().toString().trim { it <= ' ' }
        val h: String = binding.height.getText().toString().trim { it <= ' ' }
        val unit: String = binding.cftUnit.getSelectedItem().toString().trim { it <= ' ' }
        val CFT_r: String = binding.cftCharge.getText().toString()
        val pkg: String = binding.noOfPkg.getText().toString()
        if (!l.isEmpty() && !w.isEmpty() && !h.isEmpty() && !unit.isEmpty() && !CFT_r.isEmpty() && !pkg.isEmpty()) {
            val length = l.toDouble()
            val wid = w.toDouble()
            val height = h.toDouble()
            val CFTRate = CFT_r.toDouble()
            val packages = pkg.toInt()
            var res = 0.0
            if (loadtype.equals(getString(R.string.surface))) {
                res = if (unit.equals(
                        resources.getStringArray(R.array.measurement)[0],
                        ignoreCase = true
                    )
                ) {
                    String.format("%.3f", length * wid * height / 1728 * CFTRate * packages)
                        .toDouble()
                } else if (unit.equals(
                        resources.getStringArray(R.array.measurement)[1],
                        ignoreCase = true
                    )
                ) {
                    String.format("%.3f", length * wid * height / 27000000 * CFTRate * packages)
                        .toDouble()
                } else if (unit.equals(
                        resources.getStringArray(R.array.measurement)[2],
                        ignoreCase = true
                    )
                ) {
                    String.format("%.3f", length * wid * height / 27000 * CFTRate * packages)
                        .toDouble()
                } else {
                    String.format("%.3f", length * wid * height * CFTRate * packages).toDouble()
                }
            } else if (loadtype
                    .equals(getString(R.string.train))
            ) {
                if (unit.equals(
                        resources.getStringArray(R.array.measurement)[2],
                        ignoreCase = true
                    )
                ) {
                    res = String.format(
                        "%.3f",
                        length * wid * height * 5 / 28000 * CFTRate * packages
                    ).toDouble()
                }
            } else {
                if (unit.equals(
                        resources.getStringArray(R.array.measurement)[0],
                        ignoreCase = true
                    )
                ) {
                    res = String.format("%.3f", length * wid * height / 366 * CFTRate * packages)
                        .toDouble()
                } else if (unit.equals(
                        resources.getStringArray(R.array.measurement)[2],
                        ignoreCase = true
                    )
                ) {
                    res = String.format("%.3f", length * wid * height / 6000 * CFTRate * packages)
                        .toDouble()
                }
            }
            binding.cftWeight.setText(res.toString())
        }
    }
    private fun calculateChargeableWeight() {
        val wt_act: String = binding.actualWt.getText().toString().trim { it <= ' ' }
        val wt_cft: String = binding.cftWeight.getText().toString()
        if (!wt_act.isEmpty() && !wt_cft.isEmpty()) {
            val act_wt = wt_act.toDouble()
            val cft_wt = wt_cft.toDouble()
            if (act_wt > cft_wt) {
                binding.chWt.setText(act_wt.toString())
            } else {
                binding.chWt.setText(cft_wt.toString())
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}