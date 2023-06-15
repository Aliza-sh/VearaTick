package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.Task
import ir.aliza.sherkatmanage.databinding.FragmentTaskInformationBinding

class TaskInformationFragment(val task: Task) : Fragment() {

    lateinit var binding: FragmentTaskInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskInformationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtNamePro.text = task.nameTask
        binding.txtDescription.text = task.descriptionTask


    }
}