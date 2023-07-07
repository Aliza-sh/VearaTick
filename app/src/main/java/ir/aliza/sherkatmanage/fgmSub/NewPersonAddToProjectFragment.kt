package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.adapter.AddNewPersonToProjectAdapter
import ir.aliza.sherkatmanage.databinding.FragmentNewPersonAddToProjectBinding

class NewPersonAddToProjectFragment(val project: Project) : Fragment() {

    lateinit var binding: FragmentNewPersonAddToProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPersonAddToProjectBinding.inflate(layoutInflater, null, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val employeeDao = AppDatabase.getDataBase(view.context).employeeDao
        val employeeData = employeeDao.getAllEmployee()
        val addNewPersonToProjectAdapter = AddNewPersonToProjectAdapter(ArrayList(employeeData),project)
        binding.recyclerView.adapter = addNewPersonToProjectAdapter


    }


}