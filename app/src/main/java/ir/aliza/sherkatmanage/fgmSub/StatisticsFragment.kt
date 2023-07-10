package ir.aliza.sherkatmanage.fgmSub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kizitonwose.calendarview.utils.persian.PersianCalendar
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
        val day = PersianCalendar()

        if (day.persianWeekDayName.toString() == "\u062c\u0645\u0639\u0647") {
            efficiencyEmployee?.totalMonthWatch = efficiencyEmployee?.totalWeekWatch
            efficiencyEmployee?.totalWeekWatch = 0
        }
        efficiencyEmployee?.totalMonthWatch = efficiencyEmployee?.totalWeekWatch

        if (day.persianDay == 30) {
            efficiencyEmployee?.totalWatch = efficiencyEmployee?.totalMonthWatch
            efficiencyEmployee?.totalMonthWatch = 0
        }
        efficiencyEmployee?.totalWatch = efficiencyEmployee?.totalMonthWatch

        binding.txtWatchWeek.text = efficiencyEmployee?.totalWeekWatch.toString() + " ساعت"
        binding.txtWatchMonth.text = efficiencyEmployee?.totalMonthWatch.toString() + " ساعت"
        binding.txtWatchTotal.text = efficiencyEmployee?.totalWatch.toString() + " ساعت"

        binding.txtPresence.text = efficiencyEmployee?.efficiencyTotalPresence.toString() + "%"
        binding.progressCircularPresence.progress =
            efficiencyEmployee?.efficiencyTotalPresence!!.toFloat()

        binding.txtTack.text = efficiencyEmployee.efficiencyTotalDuties.toString() + "%"
        binding.progressCircularTack.progress = efficiencyEmployee.efficiencyTotalDuties!!.toFloat()
    }
}