package com.runrunfast.homegym.course;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.utils.Const;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class ActionDemoActivity extends Activity {
	private VideoView mVideoView;
	private TextView tvName;
	private MediaController mMediaController;
	private Action mAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_action_demo);
		
		initView();
		
		initData();
	}

	private void initData() {
		mAction = (Action) getIntent().getSerializableExtra(Const.KEY_ACTION);
		
		tvName.setText(mAction.action_name + "教学");
		
		mMediaController = new MediaController(this);
		startVideo();
	}

	private void startVideo() {
		String path = mAction.action_local_file;
		
		/*
		 * Alternatively,for streaming media you can use
		 * mVideoView.setVideoURI(Uri.parse(URLstring));
		 */
		mVideoView.setVideoPath(path);
		mVideoView.setMediaController(mMediaController);
		mVideoView.requestFocus();
		
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				// optional need Vitamio 4.0
				mediaPlayer.setPlaybackSpeed(1.0f);
				mediaPlayer.setLooping(true);
			}
		});
	}

	private void initView() {
		mVideoView = (VideoView)findViewById(R.id.demo_surface_view);
		tvName = (TextView)findViewById(R.id.action_name_text);
	}
}
