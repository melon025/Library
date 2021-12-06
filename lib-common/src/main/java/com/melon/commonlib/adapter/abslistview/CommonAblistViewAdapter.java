package info.emm.commonlib.adapter.abslistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import java.util.ArrayList;
import java.util.List;

import info.emm.commonlib.adapter.DataIO;
import info.emm.commonlib.adapter.ViewHolderHelper;

public abstract class CommonAblistViewAdapter<T> extends BaseAdapter implements DataIO<T> {
    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private int layoutId;

    public CommonAblistViewAdapter(Context context, int layoutId, List<T> datas) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = datas;
        this.layoutId = layoutId;
    }

    public CommonAblistViewAdapter(Context context, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = new ArrayList<>();
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderHelper holder = ViewHolderHelper.get(mContext, convertView, parent,
                layoutId, position);
        convert(holder, getItem(position),position);
        return holder.getConvertView();
    }

    public abstract void convert(ViewHolderHelper holder, T t,int position);


    public int getItemCount() {
        return mDatas.size();
    }

    public void add(T elem) {
        mDatas.add(elem);
        notifyDataSetChanged();
    }

    public void addAt(int location, T elem) {
        mDatas.add(location, elem);
        notifyDataSetChanged();
    }

    public void addAll(List<T> elements) {
        mDatas.addAll(elements);
        notifyDataSetChanged();
    }

    public void addAllAt(int location, List<T> elements) {
        mDatas.addAll(location, elements);
        notifyDataSetChanged();
    }


    public void remove(T elem) {
        mDatas.remove(elem);
        notifyDataSetChanged();
    }

    public void removeAt(int index) {
        mDatas.remove(index);
        notifyDataSetChanged();
    }

    public void removeAll(List<T> elements) {
        mDatas.removeAll(elements);
        notifyDataSetChanged();
    }


    public void clear() {
        if (mDatas != null && mDatas.size() > 0) {
            mDatas.clear();
            notifyDataSetChanged();
        }
    }


    public void replace(T oldElem, T newElem) {
        replaceAt(mDatas.indexOf(oldElem), newElem);
    }

    public void replaceAt(int index, T elem) {
        mDatas.set(index, elem);
        notifyDataSetChanged();
    }


    public void replaceAll(List<T> elements) {
        if (mDatas.size() > 0) {
            mDatas.clear();
        }
        mDatas.addAll(elements);
        notifyDataSetChanged();
    }


    public T get(int position) {
        if (position >= mDatas.size())
            return null;
        return mDatas.get(position);
    }


    public List<T> getAll() {
        return mDatas;
    }


    public int getSize() {
        return mDatas.size();
    }


    public boolean contains(T elem) {
        return mDatas.contains(elem);
    }
}
