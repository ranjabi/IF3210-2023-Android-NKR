package com.example.majika.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.budiyev.android.codescanner.*
import com.example.majika.R
import com.example.majika.databinding.FragmentPaymentBinding
import com.example.majika.viewmodel.PaymentViewModel

private const val CAMERA_REQUEST_CODE = 101

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner
    private val viewModel: PaymentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPaymentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.paymentViewModel = viewModel

        setupPermission()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()

        // observe payment status
        val nameObserver = Observer<String> { status ->
            val qrTextView: TextView = binding.qrTextView
            qrTextView.text = status
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.status.observe(viewLifecycleOwner, nameObserver)

        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                viewModel.getStatus(it.text)
                Toast.makeText(activity, it.text, Toast.LENGTH_SHORT).show()
                Toast.makeText(activity, viewModel.status.value.toString(), Toast.LENGTH_SHORT).show()
                Log.d("PaymentFragment", "status: ${viewModel.status} or ${viewModel.status.value}")

            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermission() {
        val permission = ContextCompat.checkSelfPermission(requireContext(),  android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "need camera permission to continue", Toast.LENGTH_SHORT)
                }
            }
        }
    }
}