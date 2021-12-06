package info.emm.commonlib.adapter.abslistview;

public interface MultiItemTypeSupport<T> {
    int getLayoutId(int position);

    int getViewTypeCount();

    int getItemViewType(int position, T t);
}