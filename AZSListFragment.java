package su.moy.chernihov.mapapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import su.moy.chernihov.mapapplication.comparators.DistanceComparator;
import su.moy.chernihov.mapapplication.comparators.NameComparator;
import su.moy.chernihov.mapapplication.comparators.PriceComparator;


public class AZSListFragment extends ListFragment {
    public static final String TAG = "AZS";
    private View mHeaderView;
    AZSListViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG, "AZSListFragment onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new AZSListViewAdapter(getContext(), LabAzs.getCurrentAzsList());
        if (mHeaderView != null) getListView().addHeaderView(mHeaderView);
        // Don't forget to now call setListAdapter()
        setListAdapter(adapter);
        Log.d(TAG, "AZSListFragment onActivityCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AZSListFragment onCreateView");
        mHeaderView = createHeader(inflater);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AzsFragmentsActivity) getActivity()).getBtnShowList().setVisibility(View.INVISIBLE);
        ((AzsFragmentsActivity) getActivity()).getRgFilters().setVisibility(View.INVISIBLE);
        Log.d(TAG, "AZSListFragment onResume"+LabAzs.getCurrentAzsList().size());

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        position -= getListView().getHeaderViewsCount();
        AZS azs = (AZS) getListAdapter().getItem(position);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AZSFragment azsFragment = new AZSFragment(azs);
        fragmentTransaction.replace(R.id.fragment_container, azsFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AzsFragmentsActivity)getActivity()).callBackAddAZSMarkers(LabAzs.getCurrentAzsList());
        Log.d(TAG, "AZSListFragment onPause");
    }

    // создание Header
    View createHeader(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.header_list_azs_fragment, null);
        RadioGroup rgSort = (RadioGroup) v.findViewById(R.id.header_rg_sort);
        rgSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (LabAzs.isInit()) {
                    ArrayList<AZS> azsList = LabAzs.getCurrentAzsList();
                    switch (checkedId) {
                        case R.id.header_rb_sort_name:
                            Collections.sort(azsList, new NameComparator());
                            adapter.notifyDataSetChanged();
                            break;
                        case R.id.header_rb_sort_price:
                            Collections.sort(azsList, new PriceComparator());
                            adapter.notifyDataSetChanged();
                            break;
                        case R.id.header_rb_sort_distance:
                            Collections.sort(azsList, new DistanceComparator((AzsFragmentsActivity) getActivity()));
                            adapter.notifyDataSetChanged();
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        return v;
    }


    private class AZSListViewAdapter extends ArrayAdapter<AZS> {
        private Context mContext;
        private LayoutInflater inflater;
        private CheckBox cbHighlightCurrentAzs, cbHighlightAllBrandAzs;
        private AZS mAzs;
        private LinearLayout checkLayout;

        public AZSListViewAdapter(Context context, ArrayList<AZS> azsList) {
            super(context, 0, azsList);
            mContext = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_azs, null);
            }
            mAzs = getItem(position);
            TextView tv_brandName = (TextView) convertView.findViewById(R.id.list_item_azs_brand_name);
            TextView tv_address = (TextView) convertView.findViewById(R.id.list_item_azs_address);
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.list_item_azs_icon);
            checkLayout = (LinearLayout)convertView.findViewById(R.id.list_item_azs_check_layout);


            cbHighlightAllBrandAzs = (CheckBox) convertView.findViewById(R.id.list_item_azs_cb_check_all_brand_azs);
            cbHighlightAllBrandAzs.setOnCheckedChangeListener(null);

            boolean isBrandHighlight = true;
            for (AZS azs: LabAzs.getAzsList()){
                if (azs.getBrandName().equals(mAzs.getBrandName())) {
                    if (!azs.isHighlight()) {
                        isBrandHighlight = false;
                        break;
                    }
                }
            }
            cbHighlightAllBrandAzs.setChecked(isBrandHighlight);
            cbHighlightAllBrandAzs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getListView().getPositionForView(buttonView);
                    if (pos != ListView.INVALID_POSITION) {
                        pos -= getListView().getHeaderViewsCount();
                        String brandName = LabAzs.getCurrentAzsList().get(pos).getBrandName();
                        for (AZS azs: LabAzs.getAzsList()) {
                            if (azs.getBrandName().equals(brandName)) {
                                azs.setHighlight(isChecked);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });


            cbHighlightCurrentAzs = (CheckBox) convertView.findViewById(R.id.list_item_azs_cb_check_one_azs);
            cbHighlightCurrentAzs.setOnCheckedChangeListener(null);
            cbHighlightCurrentAzs.setChecked(LabAzs.getCurrentAzsList().get(position).isHighlight());
            checkColor(LabAzs.getCurrentAzsList().get(position).isHighlight());
            cbHighlightCurrentAzs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int pos = getListView().getPositionForView(buttonView);
                    if (pos != ListView.INVALID_POSITION) {
                        pos -= getListView().getHeaderViewsCount();
                        LabAzs.getCurrentAzsList().get(pos).setHighlight(isChecked);
                        checkColor(isChecked);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            /*cbHighlightCurrentAzs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAzs.setHighlight(cbHighlightCurrentAzs.isChecked());
                }
            });*/


            tv_brandName.setText(mAzs.getBrandName());

            tv_address.setText(mAzs.getAddress());

            iv_icon.setImageDrawable(mContext.getResources().getDrawable(mAzs.getIconRes()));

            return convertView;
        }

        private void checkColor(boolean isCheck) {
            if (isCheck) {
                checkLayout.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            else {
                checkLayout.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    }


}
