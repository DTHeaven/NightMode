package im.quar.nightmode.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import im.quar.nightmode.MultiBackground;
import im.quar.nightmode.MultiTextColor;
import im.quar.nightmode.NightModeManager;

/**
 * Created by DTHeaven on 16/1/27.
 */
public class ListFragment extends Fragment {

    private RecyclerView mListRcv;
    private ListAdapter mAdapter;

//    @MultiTextColor(R.color.textColor)
//    @MultiBackground(R.color.buttonBackgroundColor)
//    @MultiTextColor({R.color.textColor, R.color.textColorNight})
//    @MultiBackground({R.color.buttonBackgroundColor, R.color.buttonBackgroundColorNight})
    @MultiTextColor(R.attr.textColor)
    @MultiBackground(R.attr.buttonBackgroundColor)
    Button mChangeThemeBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        NightModeManager.bind(this);

        View view = inflater.inflate(R.layout.fragment_list, container, false);
        mChangeThemeBtn = (Button) view.findViewById(R.id.btn);
        mListRcv = (RecyclerView) view.findViewById(R.id.list_rcv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mListRcv.setLayoutManager(layoutManager);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            data.add("Title:" + i);
        }
        mAdapter = new ListAdapter(getContext(), data);
        mAdapter.setParentFragment(this);
        mListRcv.setAdapter(mAdapter);

        mChangeThemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NightModeManager.toggleModeWithAnimation();
//                startActivity(new Intent(getContext(), ThirdActivity.class));
            }
        });

        NightModeManager.updateCurrent(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        NightModeManager.unbind(this);
    }
}
