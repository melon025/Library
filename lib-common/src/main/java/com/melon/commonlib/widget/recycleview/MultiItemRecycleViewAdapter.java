package info.emm.commonlib.widget.recycleview;

import android.content.Context;
import android.view.ViewGroup;


import java.util.List;

import info.emm.commonlib.adapter.CommonRecycleViewAdapter;
import info.emm.commonlib.adapter.ViewHolderHelper;
import info.emm.commonlib.adapter.abslistview.MultiItemTypeSupport;

public abstract class MultiItemRecycleViewAdapter<T> extends CommonRecycleViewAdapter<T> {

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public MultiItemRecycleViewAdapter(Context context, List<T> datas,
                                       MultiItemTypeSupport<T> multiItemTypeSupport) {
        super(context, -1, datas);
        mMultiItemTypeSupport = multiItemTypeSupport;

        if (mMultiItemTypeSupport == null)
            throw new IllegalArgumentException("the mMultiItemTypeSupport can not be null.");
    }

    public MultiItemRecycleViewAdapter(Context context, MultiItemTypeSupport<T> multiItemTypeSupport) {
        super(context, -1);
        mMultiItemTypeSupport = multiItemTypeSupport;

        if (mMultiItemTypeSupport == null)
            throw new IllegalArgumentException("the mMultiItemTypeSupport can not be null.");
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemTypeSupport != null)
            return mMultiItemTypeSupport.getItemViewType(position, mDatas.get(position));
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolderHelper onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mMultiItemTypeSupport == null) return super.onCreateViewHolder(parent, viewType);



        int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
        ViewHolderHelper holder = ViewHolderHelper.get(mContext, null, parent, layoutId, -1);
        setListener(parent, holder, viewType);
        return holder;
    }

}
