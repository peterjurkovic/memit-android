package memit.io.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import memit.io.android.dao.DatabaseOpenHelper;

public class BaseActivity extends AppCompatActivity {

    private static final int PROFILE_SETTING = 100000;
    private AccountHeader headerResult = null;
    private Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DatabaseOpenHelper.getInstance(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        final IProfile profile = new ProfileDrawerItem()
                                    .withName("Mike Penz")
                                    .withEmail("mikepenz@gmail.com")
                                    .withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460")
                                    .withIdentifier(100);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withOnlyMainProfileImageVisible(true)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withAlternativeProfileHeaderSwitching(false)
                .addProfiles( profile )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
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
                .withTranslucentStatusBar(true)
                .withHasStableIds(true)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.menu_item_drill).withIcon(FontAwesome.Icon.faw_play),
                        new PrimaryDrawerItem().withName(R.string.menu_item_books).withIcon(FontAwesome.Icon.faw_book),
                        new PrimaryDrawerItem().withName(R.string.menu_item_repository).withIcon(FontAwesome.Icon.faw_cloud_download),
                        new PrimaryDrawerItem().withName(R.string.menu_item_setting).withIcon(FontAwesome.Icon.faw_cog),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.menu_item_help)
                )
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // if (id == R.id.action_settings) {
        //    return true;
        // }

        return super.onOptionsItemSelected(item);
    }
}
