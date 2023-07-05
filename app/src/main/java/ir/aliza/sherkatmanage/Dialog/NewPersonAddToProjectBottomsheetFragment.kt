package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.adapter.AddNewPersonToProjectAdapter
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentNewPersonAddToProjectBinding

class NewPersonAddToProjectBottomsheetFragment : Fragment() {

    lateinit var binding: BottomsheetfragmentNewPersonAddToProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentNewPersonAddToProjectBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        val employeeData = employeeDao.getAllEmployee()
        val addNewPersonToProjectAdapter = AddNewPersonToProjectAdapter(ArrayList(employeeData))
        binding.recyclerView.adapter = addNewPersonToProjectAdapter


    }


}