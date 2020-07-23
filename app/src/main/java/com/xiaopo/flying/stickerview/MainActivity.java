package com.xiaopo.flying.stickerview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.sticker.ZoomIconEvent;
import com.xiaopo.flying.stickerview.util.ColorUtils;
import com.xiaopo.flying.stickerview.util.FileUtil;
import com.xiaopo.flying.stickerview.util.FontProvider;
import com.xiaopo.flying.stickerview.util.FontsAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    public static final int PERM_RQST_CODE = 110;
    private StickerView stickerView;
    private TextSticker sticker;
    PhotoView photoView;
    Button btn_bg_color, btn_add_text, btn_image_bg, btn_gradient_bg_color, btn_pattern_bg;
    View v;
    View dialogView;

    FontProvider fontProvider;
    Typeface typeface;

    Spinner spinner;

    Bitmap bitmap;
    Uri selectedImageUri, tempUri;
    File file;
    String fileStringimage;

    String text;

    private final String[] option = {"Take from Camera", "Select from Gallery"};

    int i = 0, ACTION_CODE = 0;

    final String[] bulltes = {"Select Bullets", "1", "\u25CF", "⋄", "\u25CB", "➥", "\u25A0", "\u25A1", "\u25BA", "\u2605", "✓", "⍟", "✰", "➮", "➤"};
    int cursorPosition = 1;
    int cursorline, start, end;
    int aa, bb, cc = 0;
    Layout layout;
    int bulletNo = 1, counter = 0;
    int lastLine;
    boolean isDelete = false;
    int mPreviousCount = 0;

    private int currentColor;
    int color1, color2, color3;
    int[] gradient_colors;
    int image_text_color, color_pattern, color_gradient, pattern_code = 0;
    GradientDrawable gd;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.fontProvider = new FontProvider(getResources());
        i = getIntent().getIntExtra("text constrast", 1);

        stickerView = (StickerView) findViewById(R.id.sticker_view);
        photoView = (PhotoView) findViewById(R.id.photoView);
        btn_bg_color = (Button) findViewById(R.id.btn_bg_color);
        btn_add_text = (Button) findViewById(R.id.btn_add_txt);
        btn_image_bg = (Button) findViewById(R.id.btn_bg_image);
        btn_gradient_bg_color = (Button) findViewById(R.id.btn_gradient_bg_color);
        btn_pattern_bg = (Button) findViewById(R.id.btn_pattern_bg);

        currentColor = ContextCompat.getColor(this, R.color.colorAccent);
        btn_bg_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorDialog();
            }
        });

        btn_add_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAdd(v, text);
            }
        });

        btn_image_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialogg(v);

            }
        });

        btn_gradient_bg_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gradientColor(v);

            }
        });

        btn_pattern_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionDialogg(v);
                pattern_code = 1;

            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_flip_white_18dp),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        BitmapStickerIcon heartIcon =
                new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp),
                        BitmapStickerIcon.LEFT_BOTTOM);
        heartIcon.setIconEvent(new HelloIconEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon, heartIcon));


        stickerView.setBackgroundColor(Color.WHITE);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);

        sticker = new TextSticker(this);


        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull final Sticker sticker) {
                //stickerView.removeAllSticker();

            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerTouchedDown");
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull final Sticker sticker) {
                Log.d(TAG, "onDoubleTapped: double tap will be with two click");

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, currentColor,
                        true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {

                        if (sticker instanceof TextSticker) {

                            ((TextSticker) sticker).setTextColor(color);

                        }
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();

            }
        });

        if (toolbar != null) {
            if (i == 1) {
                toolbar.setTitle("Contrast Text Color");
            } else toolbar.setTitle("Black And White Text Color");

            toolbar.inflateMenu(R.menu.menu_save);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.item_save) {
                        File file = FileUtil.getNewFile(MainActivity.this, "Sticker");
                        if (file != null) {
                            stickerView.save(file);
                            Toast.makeText(MainActivity.this, "saved in " + file.getAbsolutePath(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //                    stickerView.replace(new DrawableSticker(
                    //                            ContextCompat.getDrawable(MainActivity.this, R.drawable.haizewang_90)
                    //                    ));
                    return false;
                }
            });
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, PERM_RQST_CODE);
        } else {
//      loadSticker();
        }
    }


    private void loadSticker() {
        Drawable drawable =
                ContextCompat.getDrawable(this, R.drawable.haizewang_215);
        Drawable drawable1 =
                ContextCompat.getDrawable(this, R.drawable.haizewang_23);
        stickerView.addSticker(new DrawableSticker(drawable));
        stickerView.addSticker(new DrawableSticker(drawable1), Sticker.Position.BOTTOM | Sticker.Position.RIGHT);

        Drawable bubble = ContextCompat.getDrawable(this, R.drawable.bubble);
        stickerView.addSticker(
                new TextSticker(getApplicationContext())
                        .setDrawable(bubble)
                        .setText("Sticker\n")
                        .setMaxTextSize(14)
                        .resizeText()
                , Sticker.Position.TOP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERM_RQST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//      loadSticker();
        }
    }

    public void testReplace(View view) {
        if (stickerView.replace(sticker)) {
            Toast.makeText(MainActivity.this, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Replace Sticker failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void testLock(View view) {
        stickerView.setLocked(!stickerView.isLocked());
    }

    public void testRemove(View view) {
        if (stickerView.removeCurrentSticker()) {
            Toast.makeText(MainActivity.this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(MainActivity.this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
                    .show();

        }
    }

    public void testRemoveAll(View view) {
        stickerView.removeAllStickers();

    }

    public void reset(View view) {
        stickerView.removeAllStickers();
//    loadSticker();
    }

    /* set color BackGround */
    private void openColorDialog() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor,
                true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                currentColor = color;
                photoView.setBackgroundDrawable(null);
                photoView.setImageBitmap(null);
                photoView.setBackgroundColor(color);
                ACTION_CODE = 0;
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    /* GRADIENT COLOR SET BG */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void gradientColor(View view) {
        /* FIRST COLOR DIALOG */
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentColor,
                true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                color1 = color;
                /* SECOND COLOR DIALOG */
                AmbilWarnaDialog dialog1 = new AmbilWarnaDialog(MainActivity.this, currentColor,
                        true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        color2 = color;
                        /* THIRD COLOR DIALOG */
                        AmbilWarnaDialog dialog3 = new AmbilWarnaDialog(MainActivity.this, currentColor,
                                true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                color3 = color;
                                String hexColor1 = String.format("#%06X", (0xFFFFFF & color1));
                                String hexColor2 = String.format("#%06X", (0xFFFFFF & color2));
                                String hexColor3 = String.format("#%06X", (0xFFFFFF & color3));
                                gradient_colors = new int[]{Color.parseColor(hexColor1),
                                        Color.parseColor(hexColor2),
                                        Color.parseColor(hexColor3)};

                                //create a new gradient color
                                gd = new GradientDrawable(
                                        GradientDrawable.Orientation.TOP_BOTTOM, gradient_colors);
                                gd.setCornerRadius(0f);
                                photoView.setImageBitmap(null);
                                photoView.setBackgroundDrawable(null);
                                photoView.setBackground(gd);
                                color_gradient = ColorUtils.getDominantColor1(convertToBitmap(gd,
                                        photoView.getWidth(), photoView.getHeight()), i);
                                ACTION_CODE = 2;
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog3.show();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog1.show();

            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }


    /*Add text in Editor*/
    public void testAdd(View view, final String text) {
        v = view;
        final TextSticker sticker = new TextSticker(MainActivity.this);

        /*Text ADD Dialog*/
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.text_add_dialog, viewGroup, false);
        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        bulletNo = 1;
        alertDialog.show();

        final EditText dialog_editText = dialogView.findViewById(R.id.edittext_dialog);
        Button dialog_btn_save = dialogView.findViewById(R.id.btn_text_add_dialiof);
        final Button dialog_btn_change_font = dialogView.findViewById(R.id.btn_font_change_dialiog);

        /* Font Change */
        final List<String> fonts = fontProvider.getFontNames();
        dialog_btn_change_font.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FontsAdapter fontsAdapter = new FontsAdapter(MainActivity.this, fonts, fontProvider);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Select Font")
                        .setAdapter(fontsAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                typeface = fontProvider.getTypeface(fonts.get(which));

                            }

                        }).show();
            }
        });

        /* Set Bullets in Spinner */
        spinner = dialogView.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bulltes);
        spinner.setAdapter(adapter);

        /* Bullets */
        dialog_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                /* cursor position and CurrentCursorLine */
                layout = dialog_editText.getLayout();
                cursorline = getCurrentCursorLine(dialog_editText);
                start = layout.getLineStart(cursorline);
                end = layout.getLineEnd(cursorline);
                lastLine = getLastLine(dialog_editText);
                cursorPosition = dialog_editText.getSelectionEnd();

                /*  delete character action have done */
                if (spinner.getSelectedItemId() != 0) {
                    int length = s.length();
                    counter = end - start;

                    if (mPreviousCount > length) {
                        if (spinner.getSelectedItemId() == 1) {
                            if ((end - start) == 4
                                    && lastLine == cursorline
                                    && spinner.getSelectedItemId() == 1) {

                                Editable currentText1 = dialog_editText.getText();
                                if (cursorline == 0)
                                    currentText1.delete(length - 4, length);
                                else currentText1.delete(length - 5, length);
                                dialog_editText.setText(currentText1);
                                bulletNo--;

                                dialog_editText.setSelection(dialog_editText.getText().length());
                            }
                            if (lastLine != cursorline && cursorline != 0) {
                                int lc = 0, Ecounter = 0;
                                int x = 0;
                                int Estart;
                                int Eend;
                                x = cursorPosition;
                                if ((end - start) == 5) {

                                    Editable currentText1 = dialog_editText.getText();
                                    currentText1.delete(cursorPosition - 4, cursorPosition);
                                    dialog_editText.setText(currentText1);
                                    s = s.toString().replace("..", ".");
                                    dialog_editText.setText(s);

                                    for (int j = 0; j <= lastLine; j++) {

                                        Estart = layout.getLineStart(j);
                                        Eend = layout.getLineEnd(j);
                                        lc++;
                                        Editable currentText = dialog_editText.getText();

                                        if (Eend - Estart > 1) {

                                            Ecounter++;
                                            if (Ecounter >= 10) {
                                                currentText.replace(Estart, Estart + 3, Ecounter + ".");
                                                dialog_editText.setText(currentText);
                                                dialog_editText.setSelection(x - 4);
                                            } else {
                                                currentText.replace(Estart, Estart + 2, Ecounter + ".");
                                                dialog_editText.setText(currentText);
                                                dialog_editText.setSelection(x - 4);
                                            }

                                            Log.i(TAG, "onTextChanged: ssss:=" + lc + "--" + (Eend - Estart - 1));
                                        }

                                        bulletNo = Ecounter;
                                    }

                                }
                            }
                        } else {
                            if ((end - start) == 4 && lastLine == cursorline && spinner.getSelectedItemId() != 1 && cursorline != 0) {
                                int x = cursorPosition;
                                Editable currentText1 = dialog_editText.getText();
                                if (cursorline == 0) {
                                    currentText1.delete(cursorPosition - 4, cursorPosition);
                                    dialog_editText.setText(currentText1);
                                    dialog_editText.setSelection(x - 4);
                                } else {
                                    currentText1.delete(cursorPosition - 5, cursorPosition);
                                    dialog_editText.setText(currentText1);
                                    dialog_editText.setSelection(x - 5);
                                }
                            }
                            if (lastLine != cursorline) {
                                int x = cursorPosition;
                                if ((end - start) == 5 && cursorline != 0) {

                                    Editable currentText1 = dialog_editText.getText();
                                    currentText1.delete(cursorPosition - 4, cursorPosition);
                                    dialog_editText.setText(currentText1);
                                    dialog_editText.setText(s);
                                    dialog_editText.setSelection(x - 4);
                                }
                            }
                        }
                        isDelete = true;
                        Log.i("MainActivityTag", "---------------Character deleted------------");
                    }
                    mPreviousCount = length;

                    /* Line start With ZERO ...... ||  FIRST LINE */
                    if (s.toString().length() == 1) {
                        if (spinner.getSelectedItemId() == 1) {
                            s = spinner.getSelectedItem().toString() + ".   " + s;
                            s = s.toString().replace(spinner.getSelectedItem().toString() + "   " + spinner.getSelectedItem().toString(),
                                    "");
                            dialog_editText.setText(s);
                            dialog_editText.setSelection(dialog_editText.getText().length());
                        } else {
                            s = spinner.getSelectedItem().toString() + "    " + s;
                            s = s.toString().replace(spinner.getSelectedItem().toString() + "    " + spinner.getSelectedItem().toString(),
                                    "");
                            dialog_editText.setText(s);
                            dialog_editText.setSelection(dialog_editText.getText().length());
                        }
                    }

                    /* Last Line Enter */
                    if (s.toString().endsWith("\n")) {
                        bb = 1;
                        isDelete = false;
                    }
                    cursorPosition = dialog_editText.getSelectionEnd();

                    if (dialog_editText.getText().length() == cursorPosition) {
                        if (bb == 1 && !isDelete) {
                            if ((end - start) == 1) {
                                if (spinner.getSelectedItemId() == 1) {
                                    bulletNo++;
                                    String no = String.valueOf(bulletNo);
                                    Editable currentText1 = dialog_editText.getText();
                                    currentText1.insert(dialog_editText.getText().length() - 1, no + ".   ");
                                    dialog_editText.setText(currentText1);
                                    dialog_editText.setSelection(dialog_editText.getText().length());
                                    bb = 0;
                                    isDelete = false;
                                } else {
                                    Editable currentText1 = dialog_editText.getText();
                                    currentText1.insert(dialog_editText.getText().length() - 1, spinner.getSelectedItem().toString() + "    ");
                                    dialog_editText.setText(currentText1);
                                    s = s.toString().replace(spinner.getSelectedItem().toString()
                                                    + "    " + spinner.getSelectedItem().toString(),
                                            "");
                                    dialog_editText.setText(s);
                                    dialog_editText.setSelection(dialog_editText.getText().length());
                                    bb = 0;
                                    isDelete = false;
                                }

                            }
                        }
                    }

                    /* Line Inside Enter */
                    int y = 0;
                    if (start > before && spinner.getSelectedItemId() != 1) {
                        for (int i = 0; i < cursorPosition; i++) {
                            if (s.charAt(cursorPosition - 1) == '\n') {

                                Layout layout1 = dialog_editText.getLayout();
                                int cursorline1 = getCurrentCursorLine(dialog_editText);
                                int start1 = layout1.getLineStart(cursorline1 - 1);
                                int end1 = layout1.getLineEnd(cursorline1 - 1);
                                if ((end - start) >= 2) {
                                    y = cursorPosition;
                                    Editable currentText1 = dialog_editText.getText();
                                    currentText1.insert(cursorPosition, spinner.getSelectedItem().toString() + "    ");
                                    dialog_editText.setText(currentText1);
                                    s = s.toString().replace(spinner.getSelectedItem().toString() + "    " + spinner.getSelectedItem().toString(),
                                            spinner.getSelectedItem().toString());
                                    dialog_editText.setText(s);
                                    dialog_editText.setSelection(y + 5);
                                }
                                if ((end1 - start1 - 1) == 5) {
                                    Editable currentText1 = dialog_editText.getText();

                                    if ((end1 - start1 - 1) == 5) {
                                        currentText1.delete(y - 6, y);
                                        currentText1.insert(y - 6, "\n");
                                    }

                                    dialog_editText.setText(currentText1);

                                    dialog_editText.setSelection(y);

                                }
                            }
                        }
                    }

                    /* Random  Line Enter */
                    int x = 0;
                    int Estart;
                    int Eend;
                    if (dialog_editText.getText().length() != cursorPosition) {
                        if (before == 0) {
                            for (int i = 0; i < cursorPosition; i++) {
                                if (s.charAt(cursorPosition) == '\n') {
                                    if ((end - start) == 2) {
                                        x = cursorPosition;
                                        if (spinner.getSelectedItemId() == 1) {

                                            x = cursorPosition;
                                            Editable currentText = dialog_editText.getText();
                                            currentText.insert(cursorPosition - 1, bulletNo + "    " /*+ "    "*/);
                                            dialog_editText.setText(currentText);

                                            if (spinner.getSelectedItemId() == 1) {
                                                int lc = 0, Ecounter = 0;

                                                for (int j = 0; j <= lastLine; j++) {

                                                    Estart = layout.getLineStart(j);
                                                    Eend = layout.getLineEnd(j);
                                                    lc++;
                                                    Editable currentText1 = dialog_editText.getText();

                                                    if (Eend - Estart > 1) {
                                                        Ecounter++;
                                                        if (Ecounter >= 10) {
                                                            currentText1.replace(Estart, Estart + 3, Ecounter + ".");
                                                            dialog_editText.setText(currentText1);
                                                            dialog_editText.setSelection(x + 6);
                                                        } else {
                                                            currentText1.replace(Estart, Estart + 2, Ecounter + ".");
                                                            dialog_editText.setText(currentText1);
                                                            dialog_editText.setSelection(x + 5);
                                                        }
                                                    }
                                                    bulletNo = Ecounter;
                                                }
                                            }
                                        }
                                        if (spinner.getSelectedItemId() != 1) {
                                            x = cursorPosition;
                                            Editable currentText1 = dialog_editText.getText();
                                            currentText1.insert(cursorPosition - 1, spinner.getSelectedItem().toString() + "    ");
                                            dialog_editText.setText(currentText1);
                                            s = s.toString().replace(spinner.getSelectedItem().toString() + "    " + spinner.getSelectedItem().toString(),
                                                    "");
                                            dialog_editText.setText(s);
                                            dialog_editText.setSelection(x + 5);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    cursorPosition = dialog_editText.getSelectionEnd();
                    sticker.setText(dialog_editText.getText().toString());
                    String textBoxText = sticker.getText();
                    String returnedString = textBoxText.replace("..", ".");
                    sticker.setText(returnedString);
                } else {
                    sticker.setText(dialog_editText.getText().toString());
                }
                if (dialog_editText.getText().length() == 0) {
                    sticker.setText("");
                    cursorPosition = 0;
                    bulletNo = 1;
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        dialog_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bulletNo = 1;
                if (!dialog_editText.getText().toString().isEmpty()) {

                    /* TEXT COLOR CONTRAST*/
                    if (i == 1) {
                        /* NORMAL SET BG COLOR*/
                        if (ACTION_CODE == 0) {
                            sticker.setTextColor(ColorUtils.getTextColor(currentColor));
                        }
                        /*IMAGE USING SET TEXT COLOR*/
                        if (ACTION_CODE == 1) {
                            sticker.setTextColor(image_text_color);
                        }
                        /*GRADIENT BG SET TEXT COLOR*/
                        if (ACTION_CODE == 2) {
                            sticker.setTextColor(color_gradient);
                        }
                        /*PATTERN BG SET TEXT COLOR*/
                        if (ACTION_CODE == 3) {
                            sticker.setTextColor(image_text_color);
                        }
                    }
                    /* TEXT COLOR BLACK AND WHITE*/
                    else {
                        /* NORMAL SET BG COLOR*/
                        if (ACTION_CODE == 0) {
                            sticker.setTextColor(ColorUtils.getBlackAndWhiteColor(currentColor));
                        }
                        /*IMAGE USING SET TEXT COLOR*/
                        if (ACTION_CODE == 1) {
                            sticker.setTextColor(image_text_color);
                        }
                        /*GRADIENT BG SET TEXT COLOR*/
                        if (ACTION_CODE == 2) {
                            sticker.setTextColor(color_gradient);
                        }
                        /*PATTERN BG SET TEXT COLOR*/
                        if (ACTION_CODE == 3) {
                            sticker.setTextColor(image_text_color);
                        }
                    }

                    sticker.setTypeface(getResources().getAssets(), typeface);
                    sticker.setTextAlign(Layout.Alignment.ALIGN_NORMAL);
                    sticker.resizeText();
                    stickerView.addSticker(sticker);
                    alertDialog.dismiss();
                } else {
                    alertDialog.dismiss();
                    bulletNo = 1;
                }
            }
        });
    }

    public int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1) {

            return layout.getLineForOffset(selectionStart);
        }

        return -1;
    }

    public int getLastLine(EditText display) {
        String[] lines = display.getText().toString().split("\\r?\\n");
        return lines.length - 1;
    }

    public void optionDialogg(final View view) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Pick Image ");
        dialogBuilder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("Take from Camera".equals(option[which])) {
                    callCamera();
                }
                if ("Select from Gallery".equals(option[which])) {
                    callGallery();
                }
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

    }

    private void callCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    private void callGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    bitmap = (Bitmap) data.getExtras().get("data");

                    /* PATTERN BG SET*/
                    if (pattern_code == 1) {

                        tempUri = getImageUri(MainActivity.this, bitmap);
                        file = new File(getRealPathFromURI(tempUri));
                        fileStringimage = file.toString();

                        photoView.setImageBitmap(null);
                        photoView.setBackgroundDrawable(null);
                        Bitmap bitmap2 = BitmapFactory.decodeFile(fileStringimage);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap2);
                        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                        photoView.setBackgroundDrawable(bitmapDrawable);

                        pattern_code = 0;
                        ACTION_CODE = 3;
                    } else {
                        photoView.setBackgroundDrawable(null);
                        photoView.setImageBitmap(bitmap);
                        ACTION_CODE = 1;
                    }
                    if (i == 1) {
                        image_text_color = ColorUtils.getDominantColor1(bitmap, 1);
                    } else image_text_color = ColorUtils.getDominantColor1(bitmap, 0);

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImageUri = data.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

                            /* PATTERN BG SET*/
                            if (pattern_code == 1) {
                                File file1 = new File(getRealPathFromURI(selectedImageUri));
                                fileStringimage = file1.toString();
                                photoView.setImageBitmap(null);
                                photoView.setBackgroundDrawable(null);
                                Bitmap bitmap2 = BitmapFactory.decodeFile(fileStringimage);
                                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap2);
                                bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
                                photoView.setBackgroundDrawable(bitmapDrawable);

                                pattern_code = 0;
                                ACTION_CODE = 3;
                            } else {
                                photoView.setBackgroundDrawable(null);
                                photoView.setImageURI(selectedImageUri);
                                ACTION_CODE = 1;
                            }
                            if (i == 1) {
                                image_text_color = ColorUtils.getDominantColor1(bitmap, 1);
                            } else {
                                image_text_color = ColorUtils.getDominantColor1(bitmap, 0);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }
}
