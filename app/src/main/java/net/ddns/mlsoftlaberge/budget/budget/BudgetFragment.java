package net.ddns.mlsoftlaberge.budget.budget;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.ddns.mlsoftlaberge.budget.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BudgetFragment extends Fragment {

    public BudgetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.budget_fragment, container, false);
    }
}
