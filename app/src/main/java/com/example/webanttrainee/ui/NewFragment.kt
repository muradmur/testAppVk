package com.example.webanttrainee.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.webanttrainee.App
import com.example.webanttrainee.App.Companion.ARG_DATA
import com.example.webanttrainee.ItemClickListener
import com.example.webanttrainee.R
import com.example.webanttrainee.databinding.ContentFragmentBinding
import com.example.webanttrainee.model.Data
import com.example.webanttrainee.model.PictureList
import com.example.webanttrainee.remote.PictureApi
import com.example.webanttrainee.ui.adapters.PictureAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class NewFragment : Fragment() {

    private lateinit var binding: ContentFragmentBinding

    override fun onStart() {
        super.onStart()
        fetchPictureList((activity?.application as App).pictureApi)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ContentFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refreshLayout.setOnRefreshListener {
            fetchPictureList((activity?.application as App).pictureApi)
        }
    }

    private fun fetchPictureList(newApi: PictureApi?) {

        val compositeDisposable = CompositeDisposable()
        newApi?.let {
            newApi.getPicture(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onResponse(it)
                }, {
                    onFailure(it)
                }).let(compositeDisposable::add)
        }
    }

    private fun onResponse(response: PictureList) {
        with(binding.recycler) {
            layoutManager =
                GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL, false)
            adapter = PictureAdapter(response, newClickListener).apply { notifyDataSetChanged() }
        }
    }

    private fun onFailure(_throw: Throwable?){
        Toast.makeText(requireContext(), _throw?.toString(), Toast.LENGTH_LONG).show()
    }

    private val newClickListener = object: ItemClickListener<Data>{
        override fun onClick(value: Data) {
            findNavController().navigate(R.id.action_newFragment_to_descriptionNewFragment,
            bundleOf(ARG_DATA to value))
        }
    }
}