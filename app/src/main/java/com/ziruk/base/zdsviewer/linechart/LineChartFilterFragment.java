package com.ziruk.base.zdsviewer.linechart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ziruk.base.R;
import com.ziruk.viewitems.datepicker.FilterDatePicker;
import com.ziruk.viewitems.spinner.CodeValueCls;
import com.ziruk.viewitems.spinner.MySpinner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 列表检索条件：设施信息 / 车库信息
 */
public class LineChartFilterFragment extends Fragment {

    private DrawerLayout mDrawerLayout;
    private LineChartActivity activity;
    private LinearLayout mFilterDatePicker;

    //region 变量定义

    /**
     * 设施ID
     */
    private MySpinner spinnerFacilityType;

    private FilterDatePicker dpLastCheckInTimeFrom;
    private FilterDatePicker dpLastCheckInTimeTo;


    /**
     * 确定按钮
     */
    private Button btnSubmit;
    //endregion

    public void setValues(DrawerLayout mDrawerLayout, LineChartActivity activity) {
        this.mDrawerLayout = mDrawerLayout;
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.activity_facilityinfo_garageinfo_list_filter, container, false);

        activity = ( LineChartActivity ) getActivity();



        mFilterDatePicker = view.findViewById(R.id.ll_filter_date_piker);

        //设施ID
        spinnerFacilityType = view.findViewById(R.id.spinnerFacilityType);

        dpLastCheckInTimeFrom = view.findViewById(R.id.dpLastCheckInTimeFrom);
        dpLastCheckInTimeTo = view.findViewById(R.id.dpLastCheckInTimeTo);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        spinnerFacilityType.setDataBindListener( new MySpinner.OnDataBindingBeginListener( ) {
            @Override
            public List <CodeValueCls> getData() {

                List<CodeValueCls> clsList = new ArrayList <>(  );

                clsList.add( new CodeValueCls( "1" ,"日线") ) ;
                clsList.add( new CodeValueCls( "2" ,"时线") ) ;
                clsList.add( new CodeValueCls( "3" ,"实时") ) ;

                return clsList;
            }
        } );

        spinnerFacilityType.fresh();

        spinnerFacilityType.setOnItemSelectedListener( new MySpinner.OnItemSelectedListener( ) {
            @Override
            public void onItemSelected(int i, String s) {
                if ( i == 2 ){
                    mFilterDatePicker.setVisibility( View.GONE );
                }else {
                    mFilterDatePicker.setVisibility( View.VISIBLE );
                }
            }
            @Override
            public void onNothingSelected() {

            }
        } );

        dpLastCheckInTimeFrom.setDate( activity.filterValues.LastCheckInTimeFrom );
        dpLastCheckInTimeTo.setDate( activity.filterValues.LastCheckInTimeTo );


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设施ID
                activity.filterValues.FacilityType = spinnerFacilityType.getValue();

                activity.filterValues.LastCheckInTimeFrom = dpLastCheckInTimeFrom.getDate();
                activity.filterValues.LastCheckInTimeTo = dpLastCheckInTimeTo.getDate();

                activity.needRefresh = true;
                mDrawerLayout.closeDrawer( Gravity.LEFT);
            }
        });



        return view;
    }


}
