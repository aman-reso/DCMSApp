package com.management.org.dcms.codes.dcmsclient.viewitem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.codes.dcmsclient.data.models.HouseHold
import com.management.org.dcms.databinding.DcmsClientHouseHoldItemBinding

class ViewItemAdapter:ListAdapter<HouseHold, ViewItemAdapter.vh>(diffUtil) {

    class vh(val item: DcmsClientHouseHoldItemBinding):RecyclerView.ViewHolder(item.root){
        fun bind(data: HouseHold){
            item.name.text = data.name
            item.mobileNumber.text = data.mobileNumber
            item.villageName.text = data.villageName
            item.wardNo.text = data.wardNumber
        }

    }
    companion object{
        val diffUtil = object :DiffUtil.ItemCallback<HouseHold>(){
            override fun areItemsTheSame(oldItem: HouseHold, newItem: HouseHold): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: HouseHold, newItem: HouseHold): Boolean {
                return oldItem.id == newItem.id
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vh {
        val binding = DcmsClientHouseHoldItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return vh(binding)
    }

    override fun onBindViewHolder(holder: vh, position: Int) {
        holder.bind(getItem(position))
    }

}