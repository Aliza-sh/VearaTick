package com.vearad.vearatick.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vearad.vearatick.databinding.FragmentCompanyEmployeeResumeBinding

class CompanyEmployeeResumeFragment : Fragment() {

    lateinit var binding: FragmentCompanyEmployeeResumeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentCompanyEmployeeResumeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}