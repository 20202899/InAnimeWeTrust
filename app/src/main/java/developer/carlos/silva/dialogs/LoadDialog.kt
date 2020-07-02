package developer.carlos.silva.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import developer.carlos.silva.R

class LoadDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_load_layout, container, false)
    }

    companion object {

        fun show(fragmentManager: FragmentManager) {
            val transaction = fragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction
                .replace(android.R.id.content, newInstance())
                .addToBackStack(null)
                .commit()

        }

        fun hide (fragmentManager: FragmentManager) {
            fragmentManager.popBackStack()
        }

        fun newInstance() = LoadDialog()
    }
}