package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.databinding.FragmentEmployeeStatisticsBinding
import ir.aliza.sherkatmanage.databinding.FragmentPersonBinding

class PersonFragment : Fragment() {


    lateinit var binding: FragmentPersonBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}