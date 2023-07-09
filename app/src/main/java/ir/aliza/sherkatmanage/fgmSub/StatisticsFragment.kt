package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.databinding.FragmentStatisticsBinding
class StatisticsFragment(val employee: Employee, val efficiencyEmployeeDao: EfficiencyDao) :
    Fragment() {

    lateinit var binding: FragmentStatisticsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val efficiencyEmployee = efficiencyEmployeeDao.getEfficiencyEmployee(employee.idEmployee!!)

        binding.txtPresence.text = efficiencyEmployee?.efficiencyTotalPresence.toString() + " ساعت"
        binding.progressCircularPresence.progress = efficiencyEmployee?.efficiencyTotalPresence!!.toFloat()

        binding.txtTack.text = efficiencyEmployee.efficiencyTotalDuties.toString() + "%"
        binding.progressCircularTack.progress = efficiencyEmployee.efficiencyTotalDuties!!.toFloat()
    }
}