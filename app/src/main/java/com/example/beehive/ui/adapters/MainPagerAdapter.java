// Файл: MainPagerAdapter.java
// Назначение: Адаптер для ViewPager, управляющий вкладками.
package com.example.beehive.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.beehive.ui.EntriesFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    private final String userId;
    private final String userRole;

    public MainPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle, String userId, String userRole) {
        super(fragmentManager, lifecycle);
        this.userId = userId;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return EntriesFragment.newInstance(userId, userRole, "all");
            case 1:
                return EntriesFragment.newInstance(userId, userRole, "my");
            case 2:
                return EntriesFragment.newInstance(userId, userRole, "deleted");
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
