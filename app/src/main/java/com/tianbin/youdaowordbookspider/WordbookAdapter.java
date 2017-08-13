package com.tianbin.youdaowordbookspider;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * WordbookAdapter
 * Created by tianbin on 2017/8/13.
 */
public class WordbookAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public WordbookAdapter() {
        super(R.layout.holder_word, null);
    }

    @Override
    protected void convert(BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_word, s);
    }
}
