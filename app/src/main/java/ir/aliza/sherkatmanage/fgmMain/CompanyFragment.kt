package ir.aliza.sherkatmanage.fgmMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import ir.aliza.sherkatmanage.DataBase.AppDatabase
import ir.aliza.sherkatmanage.DataBase.Employee
import ir.aliza.sherkatmanage.DataBase.Project
import ir.aliza.sherkatmanage.DataBase.ProjectDao
import ir.aliza.sherkatmanage.MainActivity
import ir.aliza.sherkatmanage.R
import ir.aliza.sherkatmanage.adapter.AvatarNearAdapter
import ir.aliza.sherkatmanage.adapter.PagerAdapter
import ir.aliza.sherkatmanage.adapter.ProjectNearAdapter
import ir.aliza.sherkatmanage.adapter.ZoomOutPageTransformer
import ir.aliza.sherkatmanage.databinding.FragmentCompanyBinding
import ir.aliza.sherkatmanage.databinding.ItemProjectBinding
import ir.aliza.sherkatmanage.fgmSub.ProjectInformationFragment

class CompanyFragment : Fragment(), ProjectNearAdapter.ProjectNearEvents {

    lateinit var binding: FragmentCompanyBinding
    lateinit var binding1: ItemProjectBinding
    lateinit var projectNearAdapter: ProjectNearAdapter
    lateinit var projectDao: ProjectDao
    lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewPager2: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCompanyBinding.inflate(layoutInflater, container, false)
        binding1 = ItemProjectBinding.inflate(layoutInflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectDao = AppDatabase.getDataBase(view.context).projectDao
        val projectNearData = projectDao.getAllProject()
        projectNearAdapter = ProjectNearAdapter(ArrayList(projectNearData), this)
        binding.recyclerView.adapter = projectNearAdapter

        val data = arrayListOf<Employee>()
        data.add(Employee(1, "ali", "hasani", 20, "man", "aa", 0, 9111112134, "bbb"))
        val adpter = AvatarNearAdapter(data)
//        employeeDao = AppDatabase.getDataBase(view.context).employeeDao
//        val avatarData = employeeDao.getAllEmployee()
//        val avatarAdapter = AvatarNearAdapter(ArrayList(avatarData))
        binding1.recyclerView.adapter = adpter

        viewPager2 = binding.viewPager
        pager()

    }

    private fun pager() {

        pagerAdapter = PagerAdapter(viewPager2, object : PagerAdapter.SliderEvent {
            override fun onSliderClicked(movieId: Int) {

            }
        })
        viewPager2.adapter = pagerAdapter
        viewPager2.offscreenPageLimit = 3
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        viewPager2.setPageTransformer(ZoomOutPageTransformer())


    }

    override fun onProjectClicked(project: Project) {
        val transaction = (activity as MainActivity).supportFragmentManager.beginTransaction()
        transaction.add(R.id.frame_layout_main, ProjectInformationFragment())
            .addToBackStack(null)
            .commit()    }

    override fun onProjectLongClicked(project: Project, position: Int) {
        TODO("Not yet implemented")
    }

}