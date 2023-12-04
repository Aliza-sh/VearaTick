package com.vearad.vearatick.fgmSub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.vearad.vearatick.DataBase.ProjectDao
import com.vearad.vearatick.MainActivity
import com.vearad.vearatick.R
import com.vearad.vearatick.databinding.FragmentNumberProjectBinding

class ProjectNumberFragment(val projectDao: ProjectDao) : Fragment() {

    lateinit var binding:FragmentNumberProjectBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNumberProjectBinding.inflate(layoutInflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        binding.btnBck.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        val numAndroid = projectDao.getNumberProject("اندروید",true).size
        binding.txtNumProAndroid.text = " $numAndroid"
        val numFrontEnd = projectDao.getNumberProject("فرانت اند",true).size
        binding.txtNumProFrontEnd.text = " $numFrontEnd"
        val numBackEnd = projectDao.getNumberProject("بک اند",true).size
        binding.txtNumProBackEnd.text = " $numBackEnd"
        val numRobotic = projectDao.getNumberProject("رباتیک",true).size
        binding.txtNumProRobotic.text = " $numRobotic"
        val numDsign = projectDao.getNumberProject("طراحی",true).size
        binding.txtNumProDsign.text = " $numDsign"
        val numSeo = projectDao.getNumberProject("سئو",true).size
        binding.txtNumProSeo.text = " $numSeo"

    }
    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
                }
            })
    }

}