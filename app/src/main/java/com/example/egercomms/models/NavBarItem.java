package com.example.egercomms.models;
import com.example.egercomms.R;

public enum NavBarItem {
    FACULTY_REPS(R.id.faculty_reps),
    DEPARTMENT_REPS(R.id.department_reps),
    SUEU_GOVERNORS(R.id.sueu_governors),
    FACULTY_CONGRESS(R.id.faculty_congresses),
    RESIDENCE_CONGRESS(R.id.residence_congresses);

    private int itemId;

    NavBarItem(int itemId) {
        this.itemId = itemId;
    }

    public int getItemId() {
        return itemId;
    }

    public static NavBarItem fromViewId(int viewId) {
        for (NavBarItem navBarItem : NavBarItem.values()) {
            if (navBarItem.getItemId() == viewId) {
                return navBarItem;
            }
        }
        throw new IllegalStateException("Cannot find view type");
    }

}
