package com.example.omoperation.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.FragmentMainDetailsBinding
import com.example.omoperation.model.findcustomer.CustomerMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.viewmodel.MainDetailsViewMod
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainDetails : Fragment() {

    private var _binding:FragmentMainDetailsBinding?=null
    private val binding get() = _binding!!
    private val sharedViewModel: MainDetailsViewMod by activityViewModels()
    var fromBranchCode=""
    var toBranchCode=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.maindetails=sharedViewModel
        binding.lifecycleOwner=this
        setspinnner()
        binding.manualDate.setOnClickListener {
            opencalender()
        }
        binding.cnrno.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
            if (!b) {
                customersearch( binding.cnrno.getText().toString().trim { it <= ' ' },null, 1)
            } else {
                binding.cnr.setText("")
            }
        }
        binding.cneeno.onFocusChangeListener = OnFocusChangeListener { view: View?, b: Boolean ->
            if (!b) {
                customersearch( binding.cneeno.getText().toString().trim { it <= ' ' },null, 2)
            } else {
                binding.cnr.setText("")
            }
        }

        binding.billMode.setOnFocusChangeListener(OnFocusChangeListener { view: View?, b: Boolean ->
            if (!b) {
                customersearch( binding.cneeno.getText().toString().trim { it <= ' ' },"billing", 3)
            } else {
                binding.billMode.setText("")
            }
        })

    }


    fun customersearch(cust_code: String, cust_type: Any?, type: Int){
        val mod=CustomerMod(cust_code,cust_type,"customer")
        lifecycleScope.launch {
           val resp= ApiClient.getClient().create(ServiceInterface::class.java).findcustomer(Utils.getheaders(),mod)
            if(resp.code()==200){
                if(resp.body()?.error.equals("false")){
                    val details=resp.body()!!.customer
                    if (type == 1) {
                        binding.cnr.setText(details.CNAME)
                        binding.fromBranch.setText(details.LNAME)
                        fromBranchCode = details.LCODE
                        sharedViewModel.updatefrombranch(fromBranchCode)
                        binding.cnrloc.setText(details.LCODE + "-" + details.LNAME)
                        binding.cnrloc.setVisibility(View.VISIBLE)
                    } else if (type == 2) {
                        binding.cnee.setText(details.CNAME)
                        binding.toBranch.setText(details.LNAME)
                        toBranchCode = details.LCODE
                        sharedViewModel.updatetobranch(toBranchCode)
                        binding.ceeloc.setText(details.LCODE+ "-" + details.LNAME)
                        binding. ceeloc.setVisibility(View.VISIBLE)
                    } else {
                        binding.billMode1.setText(details.CNAME)
                    }
                }
                else{
                    Utils.showDialog(activity,"error true","Data not found",R.drawable.ic_error_outline_red_24dp)

                }

            }
            else{
                Utils.showDialog(activity,"","",R.drawable.ic_error_outline_red_24dp)
            }

        }
    }
    private fun setspinnner() {
        val tadapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.TPMODE, android.R.layout.simple_spinner_item
        )
        tadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.tranMode.setAdapter(tadapter)
        binding.tranMode.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                sharedViewModel .updatetran_mod(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no item is selected if needed
            }}

        val fadapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.freightMode, android.R.layout.simple_spinner_item
        )
        fadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.freightMode.setAdapter(fadapter)
        binding.freightMode.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                sharedViewModel .updatefreight_mod(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no item is selected if needed
            }}

        val ladapter = ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.loadType, android.R.layout.simple_spinner_item
        )
        ladapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.loadType.setAdapter(tadapter)
        binding.loadType.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                sharedViewModel .updateload_type(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where no item is selected if needed
            }}
    }


    fun opencalender(){
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


                binding.manualDate.setText(formattedDate)
            },
            year, month, day
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}