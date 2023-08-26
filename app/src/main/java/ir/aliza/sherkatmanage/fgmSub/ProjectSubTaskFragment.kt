package ir.aliza.sherkatmanage.fgmSub

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.DataBase.SubTaskProject
import ir.aliza.sherkatmanage.DataBase.SubTaskProjectDao
import ir.aliza.sherkatmanage.Dialog.ProjectAddNewSubTaskBottomsheetFragment
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.SubTaskProjectAdapter
import ir.aliza.sherkatmanage.databinding.ActivityProAndEmpBinding
import ir.aliza.sherkatmanage.databinding.FragmentProjectSubTaskBinding

class ProjectSubTaskFragment(
    var project: Project,
    val projectDao: ProjectDao,
    val position: Int,
    val bindingActivityProAndEmp: ActivityProAndEmpBinding,
    val subTaskProjectDao: SubTaskProjectDao
) : Fragment(), SubTaskProjectAdapter.SubTaskEvent {

    lateinit var binding: FragmentProjectSubTaskBinding
    lateinit var subTaskProjectAdapter: SubTaskProjectAdapter
    private var isScrollingUp = false
    private var buttonAnimator: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectSubTaskBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressed()
        btnOnScroll()

        project = projectDao.getProject(project.idProject!!)!!

        binding.btnBck.setOnClickListener {
            parentFragmentManager.beginTransaction().detach(this@ProjectSubTaskFragment)
                .replace(R.id.layout_pro_and_emp, ProjectInformationFragment(project,
                    project.watchDeadlineProject!!,project.dateDeadlineProject!!,subTaskProjectDao,projectDao,position,bindingActivityProAndEmp)).commit()
        }

        binding.btnFabTack.setOnClickListener{
            addNewTask()
        }

        val subTaskProjectData = subTaskProjectDao.getSubTaskProject(project.idProject!!)
        subTaskProjectAdapter =
            SubTaskProjectAdapter(
                ArrayList(subTaskProjectData),
                this,
                project,
                projectDao,
                subTaskProjectDao
            )
        binding.recyclerView.adapter = subTaskProjectAdapter

    }

    private fun addNewTask() {
        val bottomsheet = ProjectAddNewSubTaskBottomsheetFragment(
            subTaskProjectDao,
            project,
            subTaskProjectAdapter,
            projectDao,
            position,
            bindingActivityProAndEmp
        )
        bottomsheet.setStyle(R.style.BottomSheetStyle,R.style.BottomSheetDialogTheme)
        bottomsheet.show(parentFragmentManager, null)
    }

    private fun btnOnScroll() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isScrollingUp) {
                        animateButtonUp()
                    } else {
                        animateButtonDown()
                    }
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                isScrollingUp = dy < 0
            }
        })
    }

    private fun animateButtonDown() {
        if (binding.btnFabTack.translationY != binding.btnFabTack.height.toFloat()) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(binding.btnFabTack, "translationY", binding.btnFabTack.height.toFloat())
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    private fun animateButtonUp() {
        if (binding.btnFabTack.translationY != 0f) {
            buttonAnimator?.cancel()
            buttonAnimator = ObjectAnimator.ofFloat(binding.btnFabTack, "translationY", 0f)
            buttonAnimator?.apply {
                duration = 200
                interpolator = LinearInterpolator()
                start()
            }
        }
    }

    private fun onBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parentFragmentManager.beginTransaction().detach(this@ProjectSubTaskFragment)
                        .replace(R.id.frame_layout_sub, ProjectFragment(bindingActivityProAndEmp))
                        .commit()
                }
            })
    }

    override fun onResume() {
        super.onResume()
        updateYourData()
    }

    private fun updateYourData() {
        project = projectDao.getProject(project.idProject!!)!!
    }

    override fun onSubTaskClicked(task: SubTaskProject, position: Int) {
        TODO("Not yet implemented")
    }

    override fun onSubTaskLongClicked(subTask: SubTaskProject, position: Int) {
        TODO("Not yet implemented")
    }
}