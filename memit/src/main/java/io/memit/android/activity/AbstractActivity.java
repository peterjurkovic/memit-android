package io.memit.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import io.memit.android.R;
import io.memit.android.activity.drill.DrillActivity;

/**
 * Created by peter on 1/31/17.
 */

public abstract class AbstractActivity extends AppCompatActivity{

    public static final String SHOW_SAVED_EXTRA = "savedExtra";
    protected static final int PROFILE_SETTING = 100000;
    protected AccountHeader headerResult = null;
    protected Drawer result = null;


    protected void initDrawer(Toolbar toolbar, Bundle savedInstanceState) {
        final IProfile profile = new ProfileDrawerItem()
                .withName("Peter Jurkovič")
                .withEmail("mikepenz@gmail.com")
                .withIcon(FontAwesome.Icon.faw_user_circle)
                .withTextColorRes(R.color.colorPrimaryLight)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Snackbar.make(view, "onDrawerItemClickListener", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return false;
                    }
                })

                .withIdentifier(100);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withOnlyMainProfileImageVisible(true)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.color.colorPrimaryDark)
                // .withHeaderBackground(R.drawable.header)
                .addProfiles( profile )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, com.mikepenz.materialdrawer.model.interfaces.IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        Snackbar.make(view, "onProfileChanged", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        // if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                        //     int count = 100 + headerResult.getProfiles().size() + 1;
                        //     IProfile newProfile = new ProfileDrawerItem().withNameShown(true)
                        //             .withName("Batman" + count).withEmail("batman" + count + "@gmail.com");
                        //     if (headerResult.getProfiles() != null) {
                        //         //we know that there are 2 setting elements. set the new profile above them ;)
                        //         headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                        //     } else {
                        //         headerResult.addProfiles(newProfile);
                        //     }
                        // }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("Home");

        new DrawerBuilder()
                .withActivity(this)
                .withDrawerWidthDp(250)
                .withTranslucentStatusBar(true)
                .withHasStableIds(true)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(0L).withName("Home").withIcon(FontAwesome.Icon.faw_home),
                        new PrimaryDrawerItem().withIdentifier(1L).withName(R.string.menu_item_drill).withIcon(FontAwesome.Icon.faw_play),
                        new PrimaryDrawerItem().withIdentifier(2L).withName(R.string.menu_item_books).withIcon(FontAwesome.Icon.faw_book),
                        new PrimaryDrawerItem().withIdentifier(3L).withName(R.string.menu_item_repository).withIcon(FontAwesome.Icon.faw_cloud_download),
                        new PrimaryDrawerItem().withIdentifier(4L).withName(R.string.menu_item_setting).withIcon(FontAwesome.Icon.faw_cog),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.menu_item_help)
                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(getApplicationContext(), DrillActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(getApplicationContext(), BookListActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                // intent = new Intent(DrawerActivity.this, MultiDrawerActivity.class);
                            } else if (drawerItem.getIdentifier() == 4) {

                            } else if (drawerItem.getIdentifier() == 5) {

                            }
                            if(intent != null){
                                startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .build();
    }


    public void showSuccessfulySavedMessage(View view){
        if(view != null){
            boolean wasSaved = getIntent().getBooleanExtra(SHOW_SAVED_EXTRA, false);
            if(wasSaved){
                Snackbar.make(view, getString(R.string.book_saved), Snackbar.LENGTH_LONG).show();
            }
        }
    }


    protected void useBackButtonIn(Toolbar toolbar){
        useBackButtonIn(toolbar, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    protected void useBackButtonIn(Toolbar toolbar, View.OnClickListener listener){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24px);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(listener);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }



}
