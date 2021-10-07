package com.example.taobao.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
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
import com.example.taobao.model.domain.HomePagerContent;
import com.example.taobao.model.domain.IBaseInfo;
import com.example.taobao.model.domain.ILinearItemInfo;
import com.example.taobao.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LinearItemContentAdapter extends RecyclerView.Adapter<LinearItemContentAdapter.InnerHolder> {
    List<ILinearItemInfo> mData = new ArrayList<>();

//    private int testCount = 1;
    private OnListItemClickListener mItemClickListener=null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LogUtils.d(this,"onCreateViewHolder......"+testCount);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linear_goods_content, parent, false);

//        testCount++;
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
//        LogUtils.d(this,"onBindViewHolder......"+position);
        ILinearItemInfo dataDTO = mData.get(position);

        //设置数据
        holder.setData(dataDTO);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    //HomePagerContent.DataDTO item = mData.get(position);
                    mItemClickListener.onItemClick(dataDTO);
                }
            }
        });
    }

    public void setOnListItemClickListener(OnListItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public interface OnListItemClickListener{
        void onItemClick(IBaseInfo item);
    }
    @Override
    public int getItemCount() {

        return mData.size();
    }

    public void setData(List<? extends ILinearItemInfo> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<? extends ILinearItemInfo> contents) {
        //添加之前拿到原来的size
        int olderSize = mData.size();
        mData.addAll(contents);
        //更新UI
        notifyItemRangeChanged(olderSize, contents.size());

        //从olderSize开始更新contents.size()个数据
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_prise)
        public TextView offPriseTv;

        @BindView(R.id.goods_after_off_prise)
        public TextView finalPriseTv;

        @BindView(R.id.goods_original_prise)
        public TextView originalPriseTv;

        @BindView(R.id.goods_sell_count)
        public TextView sellCountTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(ILinearItemInfo dataDTO) {
            Context context = itemView.getContext();
            //加载标题
            title.setText(dataDTO.getTitle());

            //获得控件的宽高参数
            //ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            //int width = layoutParams.width;
            //int height = layoutParams.height;
            //int coverSize = (width > height ? width : height) / 2;
            //动态载服务器，不是自己的服务器，不好控制
            //加载图片
            //LogUtils.d(this,"url ====> "+dataDTO.getPict_url());
            /*拼接url可以节省空间*/
            String coverOnline = dataDTO.getCover();
            if (!TextUtils.isEmpty(coverOnline)) {
                String coverPath = UrlUtils.getCoverPath(coverOnline);
                //LogUtils.d(this,"coverPath *****> "+coverPath);
                Glide.with(context).load(coverPath).into(this.cover);

            } else {
                cover.setImageResource(R.mipmap.ic_launcher);
            }

            //最终价格
            String finalPrise = dataDTO.getFinalPrise(); //原价
            long couponAmount = dataDTO.getCouponAmount(); //省多少元
            float resultPrise = Float.parseFloat(finalPrise) - couponAmount;
//            LogUtils.d(this, "final prise ------->" + finalPrise);
//            LogUtils.d(this, "result prise ------->" + resultPrise);

            //券后价
            finalPriseTv.setText(String.format("%.2f", resultPrise));
            //省多少元
            offPriseTv.setText(String.format(context.getString(R.string.text_goods_off_prise), couponAmount));
            //原价
            originalPriseTv.setText(String.format(context.getString(R.string.text_goods_original_prise), finalPrise));
            originalPriseTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);//中划线
            //多少人已经购买
            sellCountTv.setText(String.format(context.getString(R.string.text_goods_sell_count),dataDTO.getVolume()));
        }
    }
}
