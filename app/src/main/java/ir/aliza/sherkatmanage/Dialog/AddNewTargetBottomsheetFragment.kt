package ir.aliza.sherkatmanage.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ir.aliza.sherkatmanage.DataBase.Targets
import ir.aliza.sherkatmanage.DataBase.TargetsDao
import ir.aliza.sherkatmanage.databinding.BottomsheetfragmentAddNewTargetBinding

class AddNewTargetBottomsheetFragment(
    val targetsDao: TargetsDao,
) : BottomSheetDialogFragment() {

    lateinit var binding: BottomsheetfragmentAddNewTargetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomsheetfragmentAddNewTargetBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.sheetBtnDone.setOnClickListener {

            addNewTarget()

        }
    }
    private fun addNewTarget() {
        if (
            binding.edtNameTerget.length() > 0 &&
            binding.edtDayTarget.length() > 0 &&
            binding.edtDescription.length() > 0
        ) {
            val txtNameTerget = binding.edtNameTerget.text.toString()
            val txtDayTarget = binding.edtDayTarget.text.toString()
            val txtDescriptionTarget = binding.edtDescription.text.toString()

            val newTarget = Targets(
                nameTarget = txtNameTerget,
                dateTarget = txtDayTarget.toInt(),
                descriptionTarget = txtDescriptionTarget,
            )
            targetsDao.insert(newTarget)
            dismiss()
        } else {
            Toast.makeText(context, "لطفا همه مقادیر را وارد کنید", Toast.LENGTH_SHORT).show()
        }
    }


}