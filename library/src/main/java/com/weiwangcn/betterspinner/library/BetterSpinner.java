package com.weiwangcn.betterspinner.library;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import java.util.Calendar;

public class BetterSpinner extends AutoCompleteTextView implements AdapterView.OnItemClickListener {

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    private boolean isPopup;
    private AdapterView.OnItemClickListener itemClickListenerFromOutside = null;

    public BetterSpinner(Context context) {
        super(context);
        super.setOnItemClickListener(this);
    }

    public BetterSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        super.setOnItemClickListener(this);
    }

    public BetterSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        super.setOnItemClickListener(this);
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.itemClickListenerFromOutside = listener;
    }

    /**
     * Selects the item of the given index, without calling any listeners.
     * It ignores values which are out of range of the adapter's items list
     * @param selectedItemIndex
     */
    public void setSelectedItem(int selectedItemIndex) {
        if (selectedItemIndex >= 0 && selectedItemIndex < this.getAdapter().getCount()) {
            Object item = this.getAdapter().getItem(selectedItemIndex);
            this.setText(item.toString());

            // Reset the filter list to show all elements
            this.performFiltering("", 0);
        }
    }

    /**
     * Clears the selection by clearing the EditText used by this Spinner.
     */
    public void clearSelectedItem() {
        this.setText("");

        // Reset the filter list to show all elements
        this.performFiltering("", 0);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            performFiltering("", 0);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
            setKeyListener(null);
            dismissDropDown();
        } else {
            isPopup = false;
            dismissDropDown();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            }
            case MotionEvent.ACTION_UP: {
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION) {
                    if (isPopup) {
                        dismissDropDown();
                        isPopup = false;
                    } else {
                        requestFocus();
                        showDropDown();
                        isPopup = true;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        // Do whatever this class wants to do
        isPopup = false;

        // Call click listener of other class
        if(this.itemClickListenerFromOutside != null){
            this.itemClickListenerFromOutside.onItemClick(adapterView, view, i, l);
        }
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_expand_more_black_18dp);
        if (dropdownIcon != null) {
            right = dropdownIcon;
            right.mutate().setAlpha(128);
        }
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

}
