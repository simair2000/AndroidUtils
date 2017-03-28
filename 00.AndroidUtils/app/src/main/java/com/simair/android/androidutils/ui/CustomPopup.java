package com.simair.android.androidutils.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by simair on 16. 11. 17.
 */

public class CustomPopup implements View.OnClickListener {
    private Context context;
    private Dialog dialog;
    private CustomPopupListener listener;
    private boolean useSound = false;
    private ArrayList<Integer> popupHideButtons;

    public CustomPopup(Context context, boolean backgroundTranslucent) {
        this.context = context;
        if(backgroundTranslucent) {
            dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        } else {
            dialog = new Dialog(context);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
    }

    public Context getCurrentContext() {
        return context;
    }

    public CustomPopup setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public CustomPopup setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    public CustomPopup setFullScreen() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(dialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
        return this;
    }

    public interface CustomPopupListener {
        void onButtonClicked(View button);
    }

    public CustomPopup(Context context){
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
    }

    public CustomPopup build(View popupLayout, CustomPopupListener l, int ... buttons) {
        ViewParent parent = popupLayout.getParent();
        if(parent != null) {
            ((ViewGroup)parent).removeView(popupLayout);
        }
        dialog.setContentView(popupLayout);
        if(buttons != null) {
            for(int button : buttons) {
                dialog.findViewById(button).setClickable(true);
                dialog.findViewById(button).setOnClickListener(this);
            }
            this.listener = l;
        }
        return this;
    }

    public CustomPopup build(int popupLayout, CustomPopupListener l, int ... buttons) {
        dialog.setContentView(popupLayout);
        if(buttons != null) {
            for(int button : buttons) {
                dialog.findViewById(button).setClickable(true);
                dialog.findViewById(button).setOnClickListener(this);
            }
            this.listener = l;
        }
        return this;
    }

    public CustomPopup useSound(boolean useSound) {
        this.useSound = useSound;
        return this;
    }

    public CustomPopup show() {
        if(useSound) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, notification);
            if(ringtone != null) {
                ringtone.play();
            }
        }
        dialog.show();
        return this;
    }

    public Dialog getDialogView() {
        return dialog;
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void hideDialog(CustomPopup popup) {
        if(popup != null) {
            popup.hideDialog();
        }
    }

    public boolean isShowDialog(){
        if(dialog != null && dialog.isShowing()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 여기 등록된 버튼이 눌러지면 그냥 팝업이 닫힌다
     * @param buttons
     */
    public CustomPopup setPopupCloseButtons(int ... buttons) {
        popupHideButtons = new ArrayList<>();
        if(buttons != null && buttons.length > 0) {
            for(Integer buttonResId : buttons) {
                popupHideButtons.add(buttonResId);
                dialog.findViewById(buttonResId).setClickable(true);
                dialog.findViewById(buttonResId).setOnClickListener(this);
            }
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        if(popupHideButtons != null && popupHideButtons.size() > 0) {
            for(Integer buttonResId : popupHideButtons) {
                if(v.getId() == buttonResId) {
                    hideDialog();
                    return;
                }
            }
        }
        if(listener != null) {
            listener.onButtonClicked(v);
        }
    }

    public void setOnPopupDismissListener(DialogInterface.OnDismissListener l) {
        dialog.setOnDismissListener(l);
    }
}
