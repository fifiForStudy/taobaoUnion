package com.example.taobao.ui.adapter;

import android.text.TextUtils;
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
import com.example.taobao.model.domain.SelectedContent;
import com.example.taobao.utils.Constants;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageContentAdapter extends RecyclerView.Adapter<SelectedPageContentAdapter.InnerHolder> {
    private List<SelectedContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO> mData = new ArrayList<>();
    private OnSelectedPageContentItemClickListener mContentItemClickListener=null;


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_content, parent, false);
        LogUtils.d(this, "onCreateViewHolder.......");
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //TODO：绑定数据
        SelectedContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO itemData = mData.get(position);
        holder.setData(itemData);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContentItemClickListener != null) {
                    mContentItemClickListener.onContentItemClick(itemData);
                }
            }
        });
    }

    public void setOnSelectedPageContentItemClickListener(OnSelectedPageContentItemClickListener listener) {
        this.mContentItemClickListener = listener;
    }

    public interface OnSelectedPageContentItemClickListener{
        void onContentItemClick(IBaseInfo item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(SelectedContent content) {
        if (Constants.SUCCESS_CODE == content.getCode()) {
            List<SelectedContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO> map_data =
                    content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data();
            this.mData.clear();
            this.mData.addAll(map_data);
            notifyDataSetChanged();
        }
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        //找到控件
        @BindView(R.id.selected_cover)
        public ImageView cover;

        @BindView(R.id.selected_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.selected_buy_btn)
        public TextView buyBtn;

        @BindView(R.id.selected_original_prise)
        public TextView originalPriseTv;

        @BindView(R.id.selected_title)
        public TextView title;


        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void setData(SelectedContent.DataDTO.TbkDgOptimusMaterialResponseDTO.ResultListDTO.MapDataDTO itemData) {
            //商品名称
            title.setText(itemData.getTitle());
            //商品图片
            String pict_url = itemData.getPict_url();
//            LogUtils.d(this, "pict_url ---> " + pict_url);
            if (pict_url != null) {

                String coverPath = UrlUtils.getCoverPath(pict_url);
                Glide.with(itemView.getContext()).load(coverPath).into(cover);
            }
            //如果该商品没有优惠券
            if (TextUtils.isEmpty(itemData.getCoupon_click_url())) {
                originalPriseTv.setText("来晚啦，没有优惠券了");
                buyBtn.setVisibility(View.GONE);

            } else {
                originalPriseTv.setText("原价：" + itemData.getZk_final_price());
                buyBtn.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(itemData.getCoupon_info())) {
                offPriseTv.setVisibility(View.GONE);

            } else {
                offPriseTv.setVisibility(View.VISIBLE);
                offPriseTv.setText(itemData.getCoupon_info());
            }
        }
    }
}
