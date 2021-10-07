package com.example.taobao.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.taobao.R;
import com.example.taobao.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TextFlowLayout extends ViewGroup {
    /*各个item之间的上下左右的间距*/
    public static final int DEFAULT_SPACE = 10;
    private float mItemHorizontalSpace = DEFAULT_SPACE;
    private float mItemVerticalSpace = DEFAULT_SPACE;
    private int mSelfWidth;
    private int mItemHeight;
    private OnFlowTextItemClickListener mItemClickListener=null;

    /*暴露接口：设置各个item之间的间距*/
    public float getItemHorizontalSpace() {
        return mItemHorizontalSpace;
    }

    /*暴露接口：获得数据个数*/
    public int getContentSize() {
        return mTextList.size();
    }
    public void setItemHorizontalSpace(float itemHorizontalSpace) {
        mItemHorizontalSpace = itemHorizontalSpace;
    }

    public float getItemVerticalSpace() {
        return mItemVerticalSpace;
    }

    public void setItemVerticalSpace(float itemVerticalSpace) {
        mItemVerticalSpace = itemVerticalSpace;
    }

    private List<String> mTextList = new ArrayList<>();

    /*构造方法*/
    public TextFlowLayout(Context context) {
        this(context, null);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //去拿到相关属性
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //拿到相关属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FlowTextStyle);
        mItemHorizontalSpace = ta.getDimension(R.styleable.FlowTextStyle_horizontalSpace, DEFAULT_SPACE);
        mItemVerticalSpace = ta.getDimension(R.styleable.FlowTextStyle_verticalSpace, DEFAULT_SPACE);
        ta.recycle();
    }

    //暴露方法出去，把东西添加进来
    public void setTextList(List<String> textList) {
        removeAllViews();
        this.mTextList.clear();
        this.mTextList.addAll(textList);
        //让最近搜索的历史词出现在最前面
        Collections.reverse(mTextList);
        //遍历内容
        for (String text : mTextList) {
            //添加子view
            //1.使用代码添加子view
            //TextView textView = new TextView(getContext());//......
            //2.使用xml添加子view
//            LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, true);

            //等价于(使用xml添加子view)
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            item.setText(text);
            item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onFlowItemClick(text);

                    }
                }
            });
            addView(item);
        }
    }


    //描述所有·的行
    private List<List<View>> lines = new ArrayList<>();

    //测量孩子
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount()==0) {
            return;
        }
        lines.clear();
        //描述单行
        List<View> line = null;
        //获取控件宽度,再减去左右的padding值
        mSelfWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        LogUtils.d(this, "onMeasure ----> " + getChildCount());
        //测量孩子
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View itemView = getChildAt(i);
            if (itemView.getVisibility() != VISIBLE) {
                //不需要进行测量
                continue;
            }
            //测量前
            LogUtils.d(this, "before height --> " + itemView.getMeasuredHeight());
            measureChild(itemView, widthMeasureSpec, heightMeasureSpec);
            //测量后
            LogUtils.d(this, "after height --> " + itemView.getMeasuredHeight());

            if (line == null) {
                //当前行为空行，可以直接添加进来
                line=createNewLine(itemView);

            } else {
                //判断当前行是否可以继续再添加进去
                if (canBeAdd(itemView, line)) {
                    //可以添加，添加进去
                    line.add(itemView);
                } else {
                    //新创建一行
                    line=createNewLine(itemView);
                }
            }
        }
        //控件高度
        mItemHeight = getChildAt(0).getMeasuredHeight();
        int selfHeight = (int) (lines.size() * mItemHeight + mItemVerticalSpace * (lines.size() + 1) + 0.5f);
        //测量自己
        setMeasuredDimension(mSelfWidth, selfHeight);
    }

    private List<View> createNewLine(View itemView) {
        List<View>line = new ArrayList<>();
        line.add(itemView);
        lines.add(line);
        return line;
    }

    /*判断当前行是否可以再继续添加新的数据*/
    private boolean canBeAdd(View itemView, List<View> line) {
        //所有的已经添加到line的子view宽度加起来+(line.size()+1)x水平间距+itemView.getMeasuredWidth()
        int totalWidth = itemView.getMeasuredWidth();
        for (View view : line) {
            //叠加所有已经添加的控件的宽度
            totalWidth += view.getMeasuredWidth();

        }
        //水平间距的宽度
        totalWidth += (line.size() + 1) * mItemHorizontalSpace;
        LogUtils.d(this, "totalWidth --->" + totalWidth);
        LogUtils.d(this, "mSelfWidth ----> " + mSelfWidth);
        //条件：如果<=当前控件的宽度，则可以添加,否则不能添加
        return totalWidth <= mSelfWidth;

    }

    //摆放孩子
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        LogUtils.d(this, "onLayout ......");
        int topOffSet = (int) mItemHorizontalSpace;
        for (List<View> views : lines) {
            //views是每一行
            int leftOffSet = (int) mItemVerticalSpace;
            for (View view : views) {
                //view是一行里面的每一个item
                view.layout(leftOffSet, topOffSet, leftOffSet + view.getMeasuredWidth(), topOffSet + view.getMeasuredHeight());
                leftOffSet += view.getMeasuredWidth() + mItemHorizontalSpace;
            }
            //TODO:9.29.????????????????????????????/
            topOffSet += mItemHeight + mItemVerticalSpace;
        }
    }

    public void setOnFlowTextItemClickListener(OnFlowTextItemClickListener listener) {
        this.mItemClickListener = listener;
    }
    public interface OnFlowTextItemClickListener{
        void onFlowItemClick(String text);
    }
}
