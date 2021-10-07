package com.example.taobao.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobao.R;
import com.example.taobao.model.domain.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.InnerHolder> {

    List<SearchResult.DataDTO.TbkDgMaterialOptionalResponseDTO.ResultListDTO.MapDataDTO> mData = new ArrayList<>();
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linear_goods_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /*设置数据*/
    public void setData(SearchResult result) {
        List<SearchResult.DataDTO.TbkDgMaterialOptionalResponseDTO.ResultListDTO.MapDataDTO> resultData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        this.mData.clear();
        mData.addAll(resultData);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
