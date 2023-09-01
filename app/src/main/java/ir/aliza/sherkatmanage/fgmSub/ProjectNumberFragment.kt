package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.databinding.FragmentNumberProjectBinding

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

}