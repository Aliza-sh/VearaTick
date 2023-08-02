package ir.aliza.sherkatmanage.fgmMain

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ghanshyam.graphlibs.GraphData
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.databinding.FragmentCompanyBinding
import ir.aliza.sherkatmanage.databinding.ItemProjectBinding


class CompanyFragment : Fragment() {

    lateinit var binding: FragmentCompanyBinding
    lateinit var bindingItemProject: ItemProjectBinding
    lateinit var projectDao: ProjectDao
    lateinit var efficiencyDao: EfficiencyDao
    lateinit var subTaskProjectDao: SubTaskProjectDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyBinding.inflate(layoutInflater, container, false)
        bindingItemProject = ItemProjectBinding.inflate(layoutInflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        efficiencyDao = AppDatabase.getDataBase(view.context).efficiencyDao
        projectDao = AppDatabase.getDataBase(view.context).projectDao

        subTaskProjectDao = AppDatabase.getDataBase(view.context).subTaskEmployeeProjectDao

        binding.progressEfficiencyPro.setPercent(efficiencyProject())
        binding.txtEfficiencyPro.text = efficiencyProject().toString() + "%"

        binding.progressEfficiencyEmpTask.setPercent(efficiencyEmployeeTack())
        binding.txtEfficiencyEmpTask.text = efficiencyEmployeeTack().toString() + "%"

        binding.progressEfficiencyEmpPresence.setPercent(efficiencyEmployeePresence())
        binding.txtEfficiencyEmpPresence.text = efficiencyEmployeePresence().toString() + "%"

        val graph = binding.graph
        graph.setMinValue(0f)
        graph.setMaxValue(100f)
        graph.setDevideSize(0.5f)
        graph.setBackgroundShapeWidthInDp(10)
        graph.setShapeForegroundColor(resources.getColor(R.color.black))
        graph.setShapeBackgroundColor(resources.getColor(R.color.holo_red_light))
        graph.setForegroundShapeWidthInPx(50)
        val resources = resources
        val data: MutableCollection<GraphData> = ArrayList()
        data.add(GraphData(20f, resources.getColor(R.color.holo_green_light)))
        data.add(GraphData(15f, resources.getColor(R.color.holo_orange_light)))
        data.add(GraphData(55f, resources.getColor(R.color.holo_blue_bright)))
        data.add(GraphData(10f, resources.getColor(R.color.holo_blue_dark)))
        graph.setData(data)

    }


    private fun efficiencyProject(): Int {
        val numberProject = projectDao.getAllProject().size
        val sumProgressProject = projectDao.getColumnprogressProject()

        var sumAllProgressProject =
            sumProgressProject.sum()

        if (sumAllProgressProject != 0)
            sumAllProgressProject /= numberProject

        return sumAllProgressProject
    }

    private fun efficiencyEmployeeTack(): Int {

        val numberEmployee = efficiencyDao.getAllEfficiency().size
        val sumEfficiencyWeekDuties = efficiencyDao.getColumnEfficiencyWeekDuties()
        val sumEfficiencyMonthDuties = efficiencyDao.getColumnEfficiencyMonthDuties()
        val sumEfficiencyTotalDuties = efficiencyDao.getColumnEfficiencyTotalDuties()

        var sumEefficiencyEmployeeTack =
            sumEfficiencyWeekDuties.sum() + sumEfficiencyMonthDuties.sum() + sumEfficiencyTotalDuties.sum()

        if (sumEefficiencyEmployeeTack != 0)
            sumEefficiencyEmployeeTack /= numberEmployee

        return sumEefficiencyEmployeeTack
    }

    private fun efficiencyEmployeePresence(): Int {
        val numberEmployee = efficiencyDao.getAllEfficiency().size
        val sumEfficiencyWeekPresence = efficiencyDao.getColumnEfficiencyWeekPresence()
        val sumEfficiencyTotalPresence = efficiencyDao.getColumnEfficiencyTotalPresence()

        var sumEefficiencyEmployeePresence =
            sumEfficiencyWeekPresence.sum() + sumEfficiencyTotalPresence.sum()

        if (sumEefficiencyEmployeePresence != 0)
            sumEefficiencyEmployeePresence /= numberEmployee

        return sumEefficiencyEmployeePresence
    }

}