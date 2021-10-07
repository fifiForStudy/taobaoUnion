package com.example.taobao.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobao.R;
import com.example.taobao.model.domain.SelectedPageCategory;
import com.example.taobao.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectedPageLeftAdapter extends RecyclerView.Adapter<SelectedPageLeftAdapter.InnerHolder> {
    private List<SelectedPageCategory.DataDTO> mData = new ArrayList<>();

    private int mCurrentSelectedPosition = 0;
    private OnLeftItemClickListener mItemClickListener =null;

    @NonNull
    @Override
    public SelectedPageLeftAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_left, parent, false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedPageLeftAdapter.InnerHolder holder, int position) {
//        LogUtils.d(this,"onBindViewHolder..."+position);
        TextView itemTv = holder.itemView.findViewById(R.id.left_category_tv);
        if (mCurrentSelectedPosition == position) {
//            LogUtils.d(this,"colosetEEE..."+position);
            itemTv.setBackgroundColor(itemTv.getResources().getColor(R.color.colorEEEEEE,null));
        } else {
//            LogUtils.d(this,"colosetWWW..."+position);
            itemTv.setBackgroundColor(itemTv.getResources().getColor(R.color.white,null));
        }
        SelectedPageCategory.DataDTO dataDTO = mData.get(position);
        itemTv.setText(dataDTO.getFavorites_title());

        //设置监听，点击切换分类
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LogUtils.d(SelectedPageLeftAdapter.this,"onClick...");
                if (mItemClickListener != null && mCurrentSelectedPosition != position) {
                    mCurrentSelectedPosition = position;
                    mItemClickListener.onLeftItemClick(dataDTO);
                    notifyDataSetChanged();
                }
            }
        });
    }


    /*暴露出去的接口*/
    public void setOnLeftItemClickListener(OnLeftItemClickListener listener) {

        this.mItemClickListener = listener;
    }
    public interface OnLeftItemClickListener{
        void onLeftItemClick(SelectedPageCategory.DataDTO item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //设置数据
    public void setData(SelectedPageCategory categories) {
        List<SelectedPageCategory.DataDTO> data = categories.getData();
        if (data != null) {
            this.mData.clear();
            this.mData.addAll(data);
            notifyDataSetChanged();
        }
        //把当前的位置给出去
        if (mData.size()>0) {
            mItemClickListener.onLeftItemClick(mData.get(mCurrentSelectedPosition));
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
