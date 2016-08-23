package com.runrunfast.homegym.course;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.utils.Const;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class ActionDemoActivity extends Activity implements OnClickListener{
	private VideoView mVideoView;
	private TextView tvName;
	private RelativeLayout rlEndLayout;
	private ImageView ivReplay, ivExit;
	private Button btnEnd;
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
		
		initVideo();
		startVideo();
	}

	private void initVideo() {
		mMediaController = new MediaController(this);
		mVideoView.requestFocus();
		mVideoView.setMediaController(mMediaController);
		
		mVideoView.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				handleVideoEnd();
			}
		});
		
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				// optional need Vitamio 4.0
				mediaPlayer.setPlaybackSpeed(1.0f);
			}
		});
	}

	private void handleVideoEnd() {
		rlEndLayout.setVisibility(View.VISIBLE);
	}

	private void startVideo() {
		String path = mAction.action_video_local.get(0);
		
		/*
		 * Alternatively,for streaming media you can use
		 * mVideoView.setVideoURI(Uri.parse(URLstring));
		 */
		mVideoView.setVideoPath(path);
		mVideoView.start();
	}

	private void initView() {
		mVideoView = (VideoView)findViewById(R.id.demo_surface_view);
		tvName = (TextView)findViewById(R.id.action_name_text);
		ivReplay = (ImageView)findViewById(R.id.demo_end_img);
		ivReplay.setOnClickListener(this);
		
		btnEnd = (Button)findViewById(R.id.demo_end_btn);
		btnEnd.setOnClickListener(this);
		
		rlEndLayout = (RelativeLayout)findViewById(R.id.demo_end_layout);
		rlEndLayout.setVisibility(View.GONE);
		
		ivExit = (ImageView)findViewById(R.id.video_exit_img);
		ivExit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.demo_end_img:
			rlEndLayout.setVisibility(View.GONE);
			startVideo();
			break;
			
		case R.id.demo_end_btn:
			finish();
			break;
			
		case R.id.video_exit_img:
			mVideoView.suspend();
			finish();
			break;

		default:
			break;
		}
	}
}
