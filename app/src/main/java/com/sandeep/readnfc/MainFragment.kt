package com.sandeep.readnfc

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sandeep.readnfc.Constants.PAN_ID_KEY
import com.sandeep.readnfc.Constants.TAG_KEY
import com.sandeep.readnfc.databinding.FragmentBinder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "MainFragment"

class MainFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    private var binder: FragmentBinder? = null
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this)[MainViewModel::class.java] }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binder = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binder?.viewModel = viewModel
        binder?.lifecycleOwner = this@MainFragment
        return binder?.root ?: super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val receivedPanId = arguments?.getString(PAN_ID_KEY).string()
        Coroutines.main(this@MainFragment) { scope ->
            scope.launch(block = {
                binder?.viewModel?.observeToast()?.collectLatest(action = { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                })
            })
            scope.launch(block = {
                binder?.viewModel?.observeTag()?.collectLatest(action = { tag ->
                    binder?.textViewExplanation?.text = tag
                    binder?.button5?.setOnClickListener {
                        val intent = Intent(view.context, DisplayDetailsActivity::class.java)
                        intent.putExtra(TAG_KEY, tag)
                        intent.putExtra(PAN_ID_KEY, receivedPanId)
                        startActivity(intent)
                    }
                })
            })
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView == binder?.toggleButton) viewModel.onCheckNFC(isChecked)
    }

    companion object {
        fun newInstance(panId: String): Fragment {
            val fragment = MainFragment()
            val bundle = Bundle()
            bundle.putString(PAN_ID_KEY, panId)
            fragment.arguments = bundle
            return fragment
        }
    }
}
