package com.xiaopo.flying.stickerview;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerIconEvent;
import com.xiaopo.flying.sticker.StickerView;

/**
 * @author wupanjie
 * @see StickerIconEvent
 */

public class HelloIconEvent implements StickerIconEvent, StickerView.OnStickerOperationListener {
  Activity context;
  Sticker stickerr;
  @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionMove(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
//  TextSticker sticker = new TextSticker(context);
//    Toast.makeText(stickerView.getContext(), sticker.getText(), Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onStickerAdded(@NonNull Sticker sticker) {

  }

  @Override
  public void onStickerClicked(@NonNull Sticker sticker) {
    stickerr=sticker;
  }

  @Override
  public void onStickerDeleted(@NonNull Sticker sticker) {

  }

  @Override
  public void onStickerDragFinished(@NonNull Sticker sticker) {

  }

  @Override
  public void onStickerTouchedDown(@NonNull Sticker sticker) {

  }

  @Override
  public void onStickerZoomFinished(@NonNull Sticker sticker) {

  }

  @Override
  public void onStickerFlipped(@NonNull Sticker sticker) {

  }

  @Override
  public void onStickerDoubleTapped(@NonNull Sticker sticker) {

  }
}
