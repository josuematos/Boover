/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.editorapendragon.boover;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A sample Activity showing how to manage multiple YouTubeThumbnailViews in an adapter for display
 * in a List. When the list items are clicked, the video is played by using a YouTubePlayerFragment.
 * <p>
 * The demo supports custom fullscreen and transitioning between portrait and landscape without
 * rebuffering.
 */
@TargetApi(13)
public final class ActivityVideoList extends Activity implements OnFullscreenListener {

  /** The duration of the animation sliding up the video in portrait. */
  private static final int ANIMATION_DURATION_MILLIS = 300;
  /** The padding between the video list and the video in landscape orientation. */
  private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;

  /** The request code when calling startActivityForResult to recover from an API service error. */
  private static final int RECOVERY_DIALOG_REQUEST = 1;

  private VideoListFragment listFragment;
  private VideoFragment videoFragment;
  static  ArrayList<VideoEntry> list2 =  new ArrayList<VideoEntry>();

  private View videoBox;
  private View closeButton;

  private boolean isFullscreen;
  private static DatabaseReference mDatabase;
  private static String dUid, mUid;
  private ProgressBar mProgressBar;
  private CircleImageView imgUser;
  private TextView txtnomeUser;
  private ImageButton btnClose, btnAddVideo, btnRemoveVideo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.video_list);

      videoFragment =
              (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
      videoBox = findViewById(R.id.video_box);
      closeButton = findViewById(R.id.close_button);
      mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
      txtnomeUser = (TextView) findViewById(R.id.txtnomeUser);
      imgUser = (CircleImageView) findViewById(R.id.imgPhotoPerfil);
      btnAddVideo = (ImageButton) findViewById(R.id.btnAddVideo);
      btnRemoveVideo = (ImageButton) findViewById(R.id.btnRemoveVideo);
      videoBox.setVisibility(View.INVISIBLE);

    Intent it = getIntent();
    dUid = it.getStringExtra("dUid");
    mUid = it.getStringExtra("mUid");
    list2.clear();
    if (dUid!=null) {
      btnAddVideo.setVisibility(View.INVISIBLE);
      btnRemoveVideo.setVisibility(View.INVISIBLE);
    }else if (mUid!=null){
      btnAddVideo.setVisibility(View.VISIBLE);
      btnRemoveVideo.setVisibility(View.VISIBLE);
      dUid = mUid;
    }else{
      mProgressBar.setVisibility(View.INVISIBLE);
      finish();
    }

      mDatabase = FirebaseDatabase.getInstance().getReference();
      mDatabase.child("Users").child(dUid)
              .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot vdataSnapshot) {
                  try {
                    for (DataSnapshot dataSnapshot : vdataSnapshot.getChildren()) {
                      if (dataSnapshot.getKey().equals("nome")) {
                        txtnomeUser.setText(getString(R.string.videos_de)+": \n"+dataSnapshot.getValue().toString());
                      }
                      if (dataSnapshot.getKey().equals("Default")) {
                        Glide.with(getApplicationContext())
                                .load(dataSnapshot.getValue().toString())
                                .into(imgUser);
                      }
                    }
                  } catch (NullPointerException e) {
                    e.printStackTrace();
                  }
                  listFragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
                  mProgressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                  // Getting Post failed, log a message
                  //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                  // ...
                }
              });
      mDatabase.child("Videos").child(dUid)
              .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot vdataSnapshot) {
                  for (DataSnapshot dataSnapshot : vdataSnapshot.getChildren()) {
                    if (dataSnapshot.getValue() != null) {
                      list2.add(new VideoEntry(dataSnapshot.getValue().toString(), dataSnapshot.getKey().toString()));
                    }
                  }
                  listFragment = (VideoListFragment) getFragmentManager().findFragmentById(R.id.list_fragment);
                  mProgressBar.setVisibility(View.INVISIBLE);
                  layout();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                  // Getting Post failed, log a message
                  //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                  // ...
                }
              });
      checkYouTubeApi();

      btnClose = (ImageButton) findViewById(R.id.btnClose);
      btnClose.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              finish();
              Globals.vIntentFoto="0";
          }
      });

    btnRemoveVideo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVideoList.this, R.style.ThemeDialogCustom3);
        builder.setTitle(getResources().getString(R.string.id_remove_video));

        final EditText input = new EditText(getApplicationContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input.setWidth(1280);
        input.setHeight(150);
        input.setPadding(15,0,15,0);
        input.setTextColor(getApplicationContext().getResources().getColor(R.color.blue_main));
        input.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rounded_edittext));
        builder.setView(input);
        builder.setIcon(R.drawable.ic_boover_rounded);

        builder.setPositiveButton(getResources().getString(R.string.remover), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
                if (input.getText().length()>0) {
                  mDatabase.child("Videos").child(dUid).child(input.getText().toString()).removeValue();
                  Toast.makeText(getApplicationContext(), getResources().getString(R.string.id_removido_video), Toast.LENGTH_SHORT).show();
                  recreate();
                }else{
                  Toast.makeText(getApplicationContext(), getResources().getString(R.string.id_invalido_video), Toast.LENGTH_SHORT).show();
                }
          }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });

        builder.show();

      }
    });

    btnAddVideo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        LayoutInflater factory = LayoutInflater.from(ActivityVideoList.this);
        final View textEntryView = factory.inflate(R.layout.layout_add_video, null);

        final EditText input1 = (EditText) textEntryView.findViewById(R.id.edtIdVideo);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.edtNomeVideo);
        input1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input1.setPadding(15,0,15,0);
        input1.setTextColor(getApplicationContext().getResources().getColor(R.color.blue_main));
        input1.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rounded_edittext));
        input2.setPadding(15,0,15,0);
        input2.setTextColor(getApplicationContext().getResources().getColor(R.color.blue_main));
        input2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.rounded_edittext));
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVideoList.this, R.style.ThemeDialogCustom3);
        builder.setTitle(getResources().getString(R.string.digite_id_youtube));
        builder.setView(textEntryView);
        builder.setIcon(R.drawable.ic_boover_rounded);

        builder.setPositiveButton(getResources().getString(R.string.adicionar), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            if (input1.getText().length()>0 && input2.getText().length()>0) {
              AlertDialog.Builder builderYou = new AlertDialog.Builder(ActivityVideoList.this, R.style.ThemeDialogCustom3);
              builderYou.setTitle(getResources().getString(R.string.verifica_youtube));

              final WebView vdChannel = new WebView(ActivityVideoList.this);
              String iframe = "<iframe width=\"340\" height=\"215\" src=\"https://www.youtube.com/embed/" + input1.getText().toString() + "\" frameborder=\"0\" allowfullscreen></iframe>";
              vdChannel.getSettings().setJavaScriptEnabled(true);
              vdChannel.getSettings().setLoadWithOverviewMode(true);
              vdChannel.setWebViewClient(new WebViewClient());
              vdChannel.setBackgroundColor(0x00000000);
              vdChannel.getSettings().setBuiltInZoomControls(true);
              vdChannel.loadData(iframe, "text/html", "UTF-8");
              builderYou.setView(vdChannel);
              builderYou.setIcon(R.drawable.ic_boover_rounded);
              builderYou.setPositiveButton(getResources().getString(R.string.sim), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  mDatabase.child("Videos").child(dUid).child(input1.getText().toString()).setValue(input2.getText().toString());
                  Toast.makeText(getApplicationContext(), getResources().getString(R.string.id_adicionado_video), Toast.LENGTH_SHORT).show();
                  recreate();
                }
              });
              builderYou.setNegativeButton(getResources().getString(R.string.nao), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              });
              builderYou.show();
            }else{
              if (input2.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.id_invalido_nome_video), Toast.LENGTH_SHORT).show();
              }
              if (input1.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.id_invalido_video), Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancelar), new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
          }
        });

        builder.show();

      }
    });



  }

  private void checkYouTubeApi() {
    YouTubeInitializationResult errorReason =
        YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);
    if (errorReason.isUserRecoverableError()) {
      errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
    } else if (errorReason != YouTubeInitializationResult.SUCCESS) {
      String errorMessage =
          String.format("Error P", errorReason.toString());
      Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == RECOVERY_DIALOG_REQUEST) {
      // Recreate the activity if user performed a recovery action
      recreate();
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    layout();
  }

  @Override
  public void onFullscreen(boolean isFullscreen) {
    this.isFullscreen = isFullscreen;
    layout();
  }

  /**
   * Sets up the layout programatically for the three different states. Portrait, landscape or
   * fullscreen+landscape. This has to be done programmatically because we handle the orientation
   * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
   * do not get reloaded.
   */
  private void layout() {
    boolean isPortrait =
        getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

    listFragment.getView().setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
    listFragment.setLabelVisibility(isPortrait);
    closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

    if (isFullscreen) {
      videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
      setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
      setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
    } else if (isPortrait) {
      setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
      setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
      setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
    } else {
      videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
      int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
      setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
      int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
      setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
      setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
          Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    }
  }

  public void onClickClose(@SuppressWarnings("unused") View view) {
    listFragment.getListView().clearChoices();
    listFragment.getListView().requestLayout();
    videoFragment.pause();
    ViewPropertyAnimator animator = videoBox.animate()
        .translationYBy(videoBox.getHeight())
        .setDuration(ANIMATION_DURATION_MILLIS);
    runOnAnimationEnd(animator, new Runnable() {
      @Override
      public void run() {
        videoBox.setVisibility(View.INVISIBLE);
      }
    });
  }

  @TargetApi(16)
  private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
    if (Build.VERSION.SDK_INT >= 16) {
      animator.withEndAction(runnable);
    } else {
      animator.setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          runnable.run();
        }
      });
    }
  }

  /**
   * A fragment that shows a static list of videos.
   */
  public static final class VideoListFragment extends ListFragment {

    private static final List<VideoEntry> VIDEO_LIST;
    static {
     // List<VideoEntry> list = new ArrayList<VideoEntry>();
         VIDEO_LIST = Collections.unmodifiableList(list2);
    }

    private PageAdapter adapter;
    private View videoBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      adapter = new PageAdapter(getActivity(), VIDEO_LIST);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      videoBox = getActivity().findViewById(R.id.video_box);
      getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
      String videoId = VIDEO_LIST.get(position).videoId;

      VideoFragment videoFragment =
          (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
      videoFragment.setVideoId(videoId);

      // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
      if (videoBox.getVisibility() != View.VISIBLE) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
          // Initially translate off the screen so that it can be animated in from below.
          videoBox.setTranslationY(videoBox.getHeight());
        }
        videoBox.setVisibility(View.VISIBLE);
      }

      // If the fragment is off the screen, we animate it in.
      if (videoBox.getTranslationY() > 0) {
        videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
      }
    }

    @Override
    public void onDestroyView() {
      super.onDestroyView();

      adapter.releaseLoaders();
    }

    public void setLabelVisibility(boolean visible) {
      adapter.setLabelVisibility(visible);
    }

  }

  /**
   * Adapter for the video list. Manages a set of YouTubeThumbnailViews, including initializing each
   * of them only once and keeping track of the loader of each one. When the ListFragment gets
   * destroyed it releases all the loaders.
   */
  private static final class PageAdapter extends BaseAdapter {

    private final List<VideoEntry> entries;
    private final List<View> entryViews;
    private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
    private final LayoutInflater inflater;
    private final ThumbnailListener thumbnailListener;

    private boolean labelsVisible;

    public PageAdapter(Context context, List<VideoEntry> entries) {
      this.entries = entries;

      entryViews = new ArrayList<View>();
      thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
      inflater = LayoutInflater.from(context);
      thumbnailListener = new ThumbnailListener();

      labelsVisible = true;
    }

    public void releaseLoaders() {
      for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap.values()) {
        loader.release();
      }
    }

    public void setLabelVisibility(boolean visible) {
      labelsVisible = visible;
      for (View view : entryViews) {
        view.findViewById(R.id.text).setVisibility(visible ? View.VISIBLE : View.GONE);
      }
    }

    @Override
    public int getCount() {
      return entries.size();
    }

    @Override
    public VideoEntry getItem(int position) {
      return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
      return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;
      VideoEntry entry = entries.get(position);

      // There are three cases here
      if (view == null) {
        // 1) The view has not yet been created - we need to initialize the YouTubeThumbnailView.
        view = inflater.inflate(R.layout.video_list_item, parent, false);
        YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
        thumbnail.setTag(entry.videoId);
        thumbnail.initialize(DeveloperKey.DEVELOPER_KEY, thumbnailListener);
      } else {
        YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view.findViewById(R.id.thumbnail);
        YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap.get(thumbnail);
        if (loader == null) {
          // 2) The view is already created, and is currently being initialized. We store the
          //    current videoId in the tag.
          thumbnail.setTag(entry.videoId);
        } else {
          // 3) The view is already created and already initialized. Simply set the right videoId
          //    on the loader.
          thumbnail.setImageResource(R.drawable.loading_thumbnail);
          loader.setVideo(entry.videoId);
        }
      }
      TextView label = ((TextView) view.findViewById(R.id.text));
      label.setText(entry.text);
      label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
      return view;
    }

    private final class ThumbnailListener implements
        YouTubeThumbnailView.OnInitializedListener,
        YouTubeThumbnailLoader.OnThumbnailLoadedListener {

      @Override
      public void onInitializationSuccess(
          YouTubeThumbnailView view, YouTubeThumbnailLoader loader) {
        loader.setOnThumbnailLoadedListener(this);
        thumbnailViewToLoaderMap.put(view, loader);
        view.setImageResource(R.drawable.loading_thumbnail);
        String videoId = (String) view.getTag();
        loader.setVideo(videoId);
      }

      @Override
      public void onInitializationFailure(
          YouTubeThumbnailView view, YouTubeInitializationResult loader) {
        view.setImageResource(R.drawable.no_thumbnail);
      }

      @Override
      public void onThumbnailLoaded(YouTubeThumbnailView view, String videoId) {
      }

      @Override
      public void onThumbnailError(YouTubeThumbnailView view, ErrorReason errorReason) {
        view.setImageResource(R.drawable.no_thumbnail);
      }
    }

  }

  public static final class VideoFragment extends YouTubePlayerFragment
      implements OnInitializedListener {

    private YouTubePlayer player;
    private String videoId;

    public static VideoFragment newInstance() {
      return new VideoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      initialize(DeveloperKey.DEVELOPER_KEY, this);
    }

    @Override
    public void onDestroy() {
      if (player != null) {
        player.release();
      }
      super.onDestroy();
    }

    public void setVideoId(String videoId) {
      if (videoId != null && !videoId.equals(this.videoId)) {
        this.videoId = videoId;
        if (player != null) {
          player.cueVideo(videoId);
        }
      }
    }

    public void pause() {
      if (player != null) {
        player.pause();
      }
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean restored) {
      this.player = player;
      player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
      player.setOnFullscreenListener((ActivityVideoList) getActivity());
      if (!restored && videoId != null) {
        player.cueVideo(videoId);
      }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
      this.player = null;
    }

  }

  private static final class VideoEntry {
    private final String text;
    private final String videoId;

    public VideoEntry(String text, String videoId) {
      this.text = text;
      this.videoId = videoId;
    }
  }

  // Utility methods for layouting.

  private int dpToPx(int dp) {
    return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
  }

  private static void setLayoutSize(View view, int width, int height) {
    LayoutParams params = view.getLayoutParams();
    params.width = width;
    params.height = height;
    view.setLayoutParams(params);
  }

  private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
    params.width = width;
    params.height = height;
    params.gravity = gravity;
    view.setLayoutParams(params);
  }

}
