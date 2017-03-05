package io.memit.android.activity.lecture;

import android.support.design.widget.AppBarLayout;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View;

import io.memit.android.R;

/**
 * Created by peter on 2/18/17.
 */

public class ToggleLectureAppBarIconListener implements  AppBarLayout.OnOffsetChangedListener {

    private boolean isVisible = true;
    private int scrollRange = -2;
    private ActionMenuItemView item;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -2) {
            scrollRange = appBarLayout.getTotalScrollRange() / 2;
        }

        if (isVisible && (scrollRange + verticalOffset) < 0 ) {
            setVisibility(View.GONE, appBarLayout);
        } else if(!isVisible && (scrollRange + verticalOffset) > 0) {
            setVisibility(View.VISIBLE, appBarLayout);
        }

    }
    private void setVisibility(int mode, AppBarLayout layout){
        if(item == null){
            item = (ActionMenuItemView) layout.findViewById(R.id.lectureBookEdit);
        }
        if(item != null){
            item.setVisibility(mode);
            isVisible = mode == View.VISIBLE;
        }
    }
}
