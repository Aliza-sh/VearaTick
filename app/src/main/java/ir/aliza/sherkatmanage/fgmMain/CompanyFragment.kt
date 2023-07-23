package ir.aliza.sherkatmanage.fgmMain

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetDialog
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.EfficiencyDao
import ir.aliza.sherkatmanage.DataBase.EmployeeDao
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.DataBase.Targets
import ir.aliza.sherkatmanage.DataBase.TargetsDao
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.ProjectNearAdapter
import ir.aliza.sherkatmanage.adapter.TargetsAdapter
import ir.aliza.sherkatmanage.adapter.ZoomOutPageTransformer
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentAddNewTargetBinding
import ir.aliza.sherkatmanage.databinding.FragmentCompanyBinding
import ir.aliza.sherkatmanage.databinding.FragmentDialogDeleteTargetBinding
import ir.aliza.sherkatmanage.databinding.ItemProjectBinding
import ir.aliza.sherkatmanage.fgmSub.ProjectInformationFragment
import me.relex.circleindicator.CircleIndicator3

class CompanyFragment : Fragment(), ProjectNearAdapter.ProjectNearEvents {

    lateinit var binding: FragmentCompanyBinding
    lateinit var bindingItemProject: ItemProjectBinding
    lateinit var bindingBottomsheet: BottomsheetfragmentAddNewTargetBinding
    lateinit var bindingDialog: FragmentDialogDeleteTargetBinding
    lateinit var projectNearAdapter: ProjectNearAdapter
    lateinit var projectDao: ProjectDao
    lateinit var employeeDao: EmployeeDao
    lateinit var efficiencyDao: EfficiencyDao
    lateinit var targetsDao: TargetsDao
    lateinit var subTaskProjectDao: SubTaskProjectDao
    lateinit var targetsAdapter: TargetsAdapter
    private lateinit var viewPager2: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyBinding.inflate(layoutInflater, container, false)
        bindingItemProject = ItemProjectBinding.inflate(layoutInflater, container, false)
        bindingBottomsheet =
            BottomsheetfragmentAddNewTargetBinding.inflate(layoutInflater, container, false)
        bindingDialog = FragmentDialogDeleteTargetBinding.inflate(layoutInflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        targetsDao = AppDatabase.getDataBase(view.context).targetsDao
        efficiencyDao = AppDatabase.getDataBase(view.context).efficiencyDao

        projectDao = AppDatabase.getDataBase(view.context).projectDao
        val projectNearData = projectDao.getAllProject()
        projectNearAdapter = ProjectNearAdapter(
            ArrayList(projectNearData),
            this,
            projectDao
        )
        binding.recyclerView.adapter = projectNearAdapter

        subTaskProjectDao = AppDatabase.getDataBase(view.context).subTaskEmployeeProjectDao

        viewPager2 = binding.targetsPager
        pagerTargets()

        binding.progressEfficiencyPro.progress = efficiencyProject()
        binding.txtEfficiencyPro.text = efficiencyProject().toString() + "%"

        binding.progressEfficiencyEmpTask.progress = efficiencyEmployeeTack()
        binding.txtEfficiencyEmpTask.text = efficiencyEmployeeTack().toString() + "%"

        binding.progressEfficiencyEmpPresence.progress = efficiencyEmployeePresence()
        binding.txtEfficiencyEmpPresence.text = efficiencyEmployeePresence().toString() + "%"
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

    private fun pagerTargets() {

        val targetsData = targetsDao.getAllTargets()
        var isFixedItemClicked = false
        if (targetsData.size != 0)
            isFixedItemClicked = true


        targetsAdapter = TargetsAdapter(
            ArrayList(targetsData),
            viewPager2,
            parentFragmentManager,
            targetsDao,
            isFixedItemClicked,
            object : TargetsAdapter.SliderEvent {

                override fun onSliderClicked(target: Targets, position: Int) {
                    val bottomSheetDialog = BottomSheetDialog(context!!)
                    showBottomsheet(bottomSheetDialog, target, position, targetsAdapter)
                    bottomSheetDialog.setContentView(bindingBottomsheet.root)
                    bottomSheetDialog.show()
                }

                override fun onSliderLongClicked(target: Targets, position: Int) {
                    showDialog(target, position, targetsAdapter)
                }
            })
        viewPager2.adapter = targetsAdapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager2.setPageTransformer(ZoomOutPageTransformer())

        val indicator: CircleIndicator3 = binding.indicator
        indicator.setViewPager(viewPager2)
        targetsAdapter.registerAdapterDataObserver(indicator.adapterDataObserver)

    }

    private fun showBottomsheet(
        bottomSheetDialog: BottomSheetDialog,
        target: Targets,
        position: Int,
        targetsAdapter: TargetsAdapter
    ) {

        bindingBottomsheet.edtNameTerget.setText(target.nameTarget)
        bindingBottomsheet.edtDayTarget.setText(target.dateTarget.toString())
        bindingBottomsheet.edtDescription.setText(target.descriptionTarget)
        bindingBottomsheet.sheetBtnDone.setOnClickListener {

            if (
                bindingBottomsheet.edtNameTerget.length() > 0 &&
                bindingBottomsheet.edtDayTarget.length() > 0 &&
                bindingBottomsheet.edtDescription.length() > 0
            ) {
                val txtNameTerget = bindingBottomsheet.edtNameTerget.text.toString()
                val txtDayTarget = bindingBottomsheet.edtDayTarget.text.toString()
                val txtDescriptionTarget = bindingBottomsheet.edtDescription.text.toString()

                val newTarget = Targets(
                    idTarget = target.idTarget,
                    nameTarget = txtNameTerget,
                    dateTarget = txtDayTarget.toInt(),
                    descriptionTarget = txtDescriptionTarget,
                )
                targetsDao.update(newTarget)
                targetsAdapter.updateTarget(newTarget, position)
                targetsAdapter.notifyDataSetChanged()
                targetsAdapter.notifyItemChanged(position)
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialog(target: Targets, position: Int, targetsAdapter: TargetsAdapter) {
        val dialog = Dialog(binding.root.context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(bindingDialog.root)

        bindingDialog.dialogBtnDeleteSure.setOnClickListener {
            targetsAdapter.removeTarget(target, position)
            targetsAdapter.notifyItemRemoved(position)
            targetsDao.delete(target)
            dialog.dismiss()
        }
        bindingDialog.dialogBtnDeleteCansel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onProjectClicked(project: Project, day: String, nameMonth: String) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.add(
            R.id.frame_layout_main, ProjectInformationFragment(
                project,
                day,
                nameMonth,
                subTaskProjectDao,
                projectDao
            )
        )
            .addToBackStack(null)
            .commit()
    }

    override fun onProjectLongClicked(project: Project, position: Int) {
        TODO("Not yet implemented")
    }

}