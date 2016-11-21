package com.simair.android.androidutils.ui;

import android.view.View;
import android.widget.RadioButton;

/**
 * Created by simair on 16. 11. 21.
 */

public class ARadioGroup implements View.OnClickListener {
    private RadioButton[] buttons;
    private CheckedChangeListener listener = null;

    public ARadioGroup(RadioButton... radioButtons) {
        this.buttons = radioButtons;
        if (buttons != null) {
            for (RadioButton btn : buttons) {
                btn.setOnClickListener(this);
            }
        }
    }

    public void check(int index) {
        for(int i = 0; i < buttons.length; i++) {
            if(index == i) {
                buttons[i].setChecked(true);
            } else {
                buttons[i].setChecked(false);
            }
        }
    }

    public void check(RadioButton button) {
        RadioButton btn = findButton(button);
        if (btn != null) {
            for (RadioButton bt : buttons) {
                if (bt.equals(btn)) {
                    bt.setChecked(true);
                } else {
                    bt.setChecked(false);
                }
            }
        }
    }

    public void clearCheck() {
        if (buttons != null) {
            for (RadioButton btn : buttons) {
                btn.setChecked(false);
            }
        }
        if (listener != null) {
            listener.onCheckedChanged(null);
        }
    }

    public RadioButton getCheckedRadioButton() {
        if (buttons == null) {
            return null;
        }
        for (RadioButton btn : buttons) {
            if (btn.isChecked()) {
                return btn;
            }
        }
        return null;
    }

    public void setOnCheckedChangeListener(CheckedChangeListener l) {
        this.listener = l;
    }

    private RadioButton findButton(RadioButton button) {
        if (buttons == null) {
            return null;
        }

        for (RadioButton btn : buttons) {
            if (btn.equals(button)) {
                return btn;
            }
        }
        return null;
    }

    public interface CheckedChangeListener {
        /**
         * Called when the checked radio button has changed. When the selection is cleared, radioButton is null.
         * @param radioButton the unique RadioButton instance of the newly checked
         *            radio button
         */
        void onCheckedChanged(RadioButton radioButton);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        for (RadioButton btn : buttons) {
            if (btn.equals(arg0)) {
                btn.setChecked(true);
            } else {
                btn.setChecked(false);
            }
        }
        if (listener != null) {
            listener.onCheckedChanged((RadioButton) arg0);
        }
    }
}
