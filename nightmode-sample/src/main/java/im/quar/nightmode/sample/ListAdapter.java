package im.quar.nightmode.sample;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiTextColor;
import im.quar.nightmode.NightModeManager;

/**
 * Created by DTHeaven on 16/1/27.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private Context mContext;
    private List<String> mData;
    private Fragment mParentFragment;

    public ListAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }

    public void setParentFragment(Fragment fragment) {
        mParentFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(itemView, mParentFragment);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.titleTxt.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @MultiTextColor(R.attr.textColor)
        @MultiBackground(R.attr.backgroundColor)
        TextView titleTxt;

        public ViewHolder(View itemView, Fragment parentFragment) {
            super(itemView);
            NightModeManager.bind(this, parentFragment);
            titleTxt = (TextView) itemView.findViewById(R.id.txt_title);
            NightModeManager.updateCurrent(this);
        }

//        @Override
//        protected void finalize() throws Throwable {
//            super.finalize();
//            NightModeManager.unbind(this);
//        }
    }

}
