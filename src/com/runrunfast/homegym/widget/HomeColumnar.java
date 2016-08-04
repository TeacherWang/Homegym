package com.runrunfast.homegym.widget;


/**
 * home-柱状图
 * 
 * @author Administrator
 * 
 */
public class HomeColumnar {

//	private List<Score> score;
//	public List<Map<String, Float>> positio;
//	private float tb;
//	private float interval_left_right;
//	private Paint paint_date, paint_rectf_gray, paint_rectf_blue;
//
//	private int fineLineColor = 0x2faaaaaa; // 灰色
//	private int blueLineColor = 0xff00ffff; // 蓝色
//	private int dateLineColor = 0xbfffffff; // 白色
//
//	public HomeColumnar(Context context, List<Score> score) {
//		super(context);
//		init(score);
//	}
//
//	public void init(List<Score> score) {
//		if (null == score || score.size() == 0)
//			return;
//		this.score = score;
//		Resources res = getResources();
//		tb = res.getDimension(R.dimen.historyscore_tb);
//		interval_left_right = tb * 6.3f;
//
//		paint_date = new Paint();
//		paint_date.setStrokeWidth(tb * 0.1f);
//		paint_date.setTextSize(tb * 1.3f);
//		paint_date.setColor(dateLineColor);
//		paint_date.setTextAlign(Align.CENTER);
//		paint_date.setAntiAlias(true);
//
//		paint_rectf_gray = new Paint();
//		paint_rectf_gray.setStrokeWidth(tb * 0.1f);
//		paint_rectf_gray.setColor(fineLineColor);
//		paint_rectf_gray.setStyle(Style.FILL);
//		paint_rectf_gray.setAntiAlias(true);
//
//		paint_rectf_blue = new Paint();
//		paint_rectf_blue.setStrokeWidth(tb * 0.1f);
//		paint_rectf_blue.setColor(blueLineColor);
//		paint_rectf_blue.setStyle(Style.FILL);
//		paint_rectf_blue.setAntiAlias(true);
//
//		setLayoutParams(new LayoutParams(
//				(int) (this.score.size() * interval_left_right),
//				LayoutParams.MATCH_PARENT));
//	}
//
//	protected void onDraw(Canvas c) {
//		if (null == score || score.size() == 0)
//			return;
//		drawDate(c);
//		drawRectf(c);
//	}
//
//	/**
//	 * 绘制矩形
//	 * 
//	 * @param c
//	 */
//	public void drawRectf(Canvas c) {
//		positio = new ArrayList<Map<String, Float>>();
//		for (int i = 0; i < score.size(); i++) {
//
//			RectF f = new RectF();
//			f.set(interval_left_right * i, getHeight() - tb * 16.5f, tb * 4.8f
//					+ interval_left_right * i, getHeight() - tb * 1.5f);
//			c.drawRoundRect(f, tb * 0.5f, tb * 0.5f, paint_rectf_gray);
//
//			float base = score.get(i).percent * (tb * 15f / 100); 
//			RectF f1 = new RectF();
//			f1.set(interval_left_right * i, getHeight() - (base + tb * 1.5f),
//					tb * 4.8f + interval_left_right * i, getHeight() - tb
//							* 1.5f);
//
//			// 存储监听坐标
//			Map<String, Float> map = new HashMap<String, Float>();
//			map.put("x", interval_left_right * i);
//			map.put("y", getHeight() - tb * 1.5f);
//			positio.add(map);
//
//			c.drawRoundRect(f1, tb * 0.5f, tb * 0.5f, paint_rectf_blue);
//		}
//	}
//
//	public int getPosition(float x, float y) {
//		float height = getHeight() - tb * 1.5f;
//		float weight = tb * 4.8f;
//		for (int i = 0; i < positio.size(); i++) {
//			float pos_x = positio.get(i).get("x");
//			float pos_y = positio.get(i).get("y");
//			if (x >= pos_x && x <= pos_x + weight && y <= pos_y
//					&& y >= pos_y - height) {
//				return i;
//			}
//		}
//		return -1;
//	}
//
//	public Score getTheScore(float x, float y) {
//		int index = getPosition(x, y);
//		if (index != -1) {
//			return score.get(index);
//		}
//		return null;
//	}
//
//	/**
//	 * 绘制日期
//	 * 
//	 * @param c
//	 */
//	public void drawDate(Canvas c) {
//		for (int i = 0; i < score.size(); i++) {
//			String date = score.get(i).date;
//			String date_1 = date
//					.substring(date.indexOf("-") + 1, date.length());
//			c.drawText(date_1, tb * 2.5f + interval_left_right * i,
//					getHeight(), paint_date);
//
//		}
//	}
}
