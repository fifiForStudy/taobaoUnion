package com.example.taobao.ui.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobao.R;
import com.example.taobao.model.domain.IBaseInfo;
import com.example.taobao.model.domain.OnSellContent;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnSellContentAdapter extends RecyclerView.Adapter<OnSellContentAdapter.InnerHolder> {
    private List<OnSellContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO> mData = new ArrayList<>();
    private OnSellPageItemClickListener mContentItemClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_sell_content, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //绑定数据
        OnSellContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO mapDataDTO = mData.get(position);
        holder.setData(mapDataDTO);
        //监听点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentItemClickListener != null) {
                    mContentItemClickListener.onSellItemClick(mapDataDTO);
                }
            }
        });
    }

    public void setOnSellPageItemClickListener(OnSellPageItemClickListener listener) {
        this.mContentItemClickListener = listener;
    }
    public interface OnSellPageItemClickListener{
        void onSellItemClick(IBaseInfo data);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(OnSellContent result) {
        this.mData.clear();
        this.mData.addAll(result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        notifyDataSetChanged();
    }

    public void onMoreLoaded(OnSellContent moreResult) {
        //加载更多的内容
        List<OnSellContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO> moreData = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
        //拿到原先的长度
        int oldDataSize = this.mData.size();
        this.mData.addAll(moreData);
        notifyItemChanged(oldDataSize-1,moreData.size());
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.on_sell_cover)
        public ImageView cover;

        @BindView(R.id.on_sell_content_title_tv)
        public TextView title;

        @BindView(R.id.on_sell_origin_prise_tv)
        public TextView originalPriseTv;

        @BindView(R.id.on_sell_off_prise_tv)
        public TextView offPriseTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(OnSellContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO data) {
            title.setText(data.getTitle());
//            LogUtils.d(this, "pic url ---> " + data.getPict_url());
            String coverPath = UrlUtils.getCoverPath(data.getPict_url());
            Glide.with(cover.getContext()).load(coverPath).into(cover);

            String originalPrise = data.getZk_final_price();
            originalPriseTv.setText("￥"+originalPrise+" ");
            originalPriseTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

            int couponAmount = data.getCoupon_amount();
            float originalPriseFloat = Float.parseFloat(originalPrise);
            float finalPrise = originalPriseFloat - couponAmount;
            offPriseTv.setText(String.format("券后价："+"%.2f",finalPrise));
        }
    }
}
