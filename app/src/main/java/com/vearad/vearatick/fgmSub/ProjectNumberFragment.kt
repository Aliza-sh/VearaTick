package com.vearad.vearatick.fgmSub

import android.annotation.SuppressLint
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
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()

        binding.btnBck.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }

        val numAndroid = projectDao.getNumberProject("اندروید").size
        binding.txtNumProAndroid.text = " $numAndroid"
        val numSite = projectDao.getNumberProject("سایت").size
        binding.txtNumProSite.text = " $numSite"
        val numFrontEnd = projectDao.getNumberProject("فرانت اند").size
        binding.txtNumProFrontEnd.text = " $numFrontEnd"
        val numBackEnd = projectDao.getNumberProject("بک اند").size
        binding.txtNumProBackEnd.text = " $numBackEnd"
        val numRobotic = projectDao.getNumberProject("رباتیک").size
        binding.txtNumProRobotic.text = " $numRobotic"
        val numDsign = projectDao.getNumberProject("طراحی").size
        binding.txtNumProDsign.text = " $numDsign"
        val numSeo = projectDao.getNumberProject("سئو").size
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