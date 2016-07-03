package com.runrunfast.homegym.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.Globle;

public class PopupWindows extends PopupWindow {

	private View view;
	private View parent;
	private Context mContext;
	private View layout;

	private int START_YEAR, END_YEAR;// 起始年费 结束年费
	private String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	private String[] months_little = { "4", "6", "9", "11" };
	private final List<String> list_big = Arrays.asList(months_big);
	private final List<String> list_little = Arrays.asList(months_little);

	private int cp_front;// 身高/体重-- 整数
	private String cp_after = "";// 身高/体重-- 小数
	private int cp_year = 0; // 年
	private String cp_month = "";// 月
	private String cp_day = "";// 日
	private String sceneStr = "";
	private String physicalStr = "";

//	private EndListener endListener = null;

	private int START_HEIGHT, END_HEIGHT;// 起始身高/体重 结束身高/体重

	public View getLayout() {
		return layout;
	}

	public void setLayout(View layout) {
		this.layout = layout;
	}

	private Context context = Globle.gApplicationContext;
//	private String[] phisicals = context.getResources().getStringArray(
//			R.array.physical);
//	private String[] scenes = context.getResources().getStringArray(
//			R.array.locatioin);

	public PopupWindows(Context mContext, View parent) {
		this.mContext = mContext;
		this.parent = parent;
	}

	public void show() {
		view = layout;
		view.startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.fade_in));

		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		showAtLocation(parent, Gravity.CENTER, 0, 0);
		update();
	}

	/**
	 * 选择日期
	 */
//	public void chooseDate(String date) {
//		final Button popupwindow_menu_date_cancel = (Button) view
//				.findViewById(R.id.popupwindow_menu_date_cancel);
//		final Button popupwindow_menu_date_confirm = (Button) view
//				.findViewById(R.id.popupwindow_menu_date_confirm);
//
//		popupwindow_menu_date_cancel.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
//		popupwindow_menu_date_confirm.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				endListener.onEnd(Const.DATE, cp_year + "-" + cp_month + "-"
//						+ cp_day);
//				dismiss();
//			}
//		});
//
//		START_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 70;
//		END_YEAR = Calendar.getInstance().get(Calendar.YEAR);
//
//		int year = 0;
//		int month = 0;
//		int day = 0;
//		if (null == date || date.equals("")) {
//			year = START_YEAR;
//			month = 1;
//			day = 1;
//		} else {
//			year = Integer.parseInt(date.substring(0, date.indexOf("-")));
//			month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
//					date.lastIndexOf("-")));
//			day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1,
//					date.length()));
//		}
//
//		String a = "";
//		String b = "";
//		if (month < 10) {
//			a = "0";
//		}
//		if (day < 10) {
//			b = "0";
//		}
//
//		final WheelViewNew wv_year = (WheelViewNew) view.findViewById(R.id.years);
//		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
//		wv_year.setCyclic(true);// 可循环滚动
//		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
//
//		cp_year = year;
//		// 月
//		final WheelViewNew wv_month = (WheelViewNew) view.findViewById(R.id.month);
//		wv_month.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
//		wv_month.setCyclic(true);
//		wv_month.setCurrentItem(month - 1);
//
//		cp_month = a + month;
//		// 日
//		final WheelViewNew wv_day = (WheelViewNew) view.findViewById(R.id.day);
//		if (list_big.contains(String.valueOf(month + 1))) {
//			wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d"));
//		} else if (list_little.contains(String.valueOf(month + 1))) {
//			wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d"));
//		} else {
//			// 闰年
//			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
//				wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d"));
//			else
//				wv_day.setAdapter(new NumericWheelAdapter(1, 28, "%02d"));
//		}
//
//		wv_day.setCyclic(true);
//		wv_day.setCurrentItem(day - 1);
//
//		cp_day = b + day;
//		// 添加"年"监听
//		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				int year_num = newValue + START_YEAR;
//				// 判断大小月及是否闰年,用来确定"日"的数据
//				if (list_big
//						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
//					wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d"));
//				} else if (list_little.contains(String.valueOf(wv_month
//						.getCurrentItem() + 1))) {
//					wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d"));
//				} else {
//					if ((year_num % 4 == 0 && year_num % 100 != 0)
//							|| year_num % 400 == 0)
//						wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d"));
//					else
//						wv_day.setAdapter(new NumericWheelAdapter(1, 28, "%02d"));
//				}
//				cp_year = year_num;
//				System.out.println("年----->" + year_num);
//			}
//		};
//		// 添加"月"监听
//		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				int month_num = newValue + 1;
//				// 判断大小月及是否闰年,用来确定"日"的数据
//				if (list_big.contains(String.valueOf(month_num))) {
//					wv_day.setAdapter(new NumericWheelAdapter(1, 31, "%02d"));
//				} else if (list_little.contains(String.valueOf(month_num))) {
//					wv_day.setAdapter(new NumericWheelAdapter(1, 30, "%02d"));
//				} else {
//					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
//							.getCurrentItem() + START_YEAR) % 100 != 0)
//							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
//						wv_day.setAdapter(new NumericWheelAdapter(1, 29, "%02d"));
//					else
//						wv_day.setAdapter(new NumericWheelAdapter(1, 28, "%02d"));
//				}
//
//				String a = "";
//				if (month_num < 10) {
//					a = "0";
//				}
//				cp_month = a + month_num;
//			}
//		};
//
//		// 添加"日"监听
//		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				int day_num = newValue + 1;
//
//				String a = "";
//				if (day_num < 10) {
//					a = "0";
//				}
//				cp_day = a + day_num;
//			}
//		};
//		wv_year.addChangingListener(wheelListener_year);
//		wv_month.addChangingListener(wheelListener_month);
//		wv_day.addChangingListener(wheelListener_day);
//
//	}

	/**
	 * 选择身体 / 体重
	 * 
	 * @param unit
	 *            cm / kg
	 */

//	public void chooseHeight(final String unit, String value) {
//		int whole = 0;// 整数
//		int small = 0;// 小数
//
//		final Button popupwindow_menu_height_cancel = (Button) view
//				.findViewById(R.id.popupwindow_menu_height_cancel);
//		final Button popupwindow_menu_height_confirm = (Button) view
//				.findViewById(R.id.popupwindow_menu_height_confirm);
//		final TextView popupwindow_menu_height_text = (TextView) view
//				.findViewById(R.id.popupwindow_menu_height_text);
//
//		popupwindow_menu_height_cancel
//				.setOnClickListener(new OnClickListener() {
//					public void onClick(View v) {
//						dismiss();
//					}
//				});
//		popupwindow_menu_height_confirm
//				.setOnClickListener(new OnClickListener() {
//					public void onClick(View v) {
//						String str = cp_front + cp_after;
//						if (unit.equals(Const.CM)) {
//							endListener.onEnd(Const.CM, str);
//						} else {
//							endListener.onEnd(Const.KG, str);
//						}
//						dismiss();
//					}
//				});
//
//		if (unit.equals(Const.CM)) {
//			popupwindow_menu_height_text.setText(mContext
//					.getString(R.string.Personal_text_height));
//			START_HEIGHT = 130;
//			END_HEIGHT = 200;
//		} else {
//			popupwindow_menu_height_text.setText(mContext
//					.getString(R.string.Personal_text_weight));
//			START_HEIGHT = 40;
//			END_HEIGHT = 120;
//		}
//
//		if (null == value || value.equals("")) {
//			whole = START_HEIGHT;
//			small = 0;
//		} else {
//			whole = Integer.parseInt(value.substring(0, value.indexOf(".")));
//			small = Integer.parseInt(value.substring(value.indexOf(".") + 1,
//					value.length()));
//		}
//
//		cp_front = whole;
//		cp_after = "." + small;
//
//		final WheelViewNew wv_height = (WheelViewNew) view.findViewById(R.id.height);
//		wv_height.setAdapter(new NumericWheelAdapter(START_HEIGHT, END_HEIGHT));
//		wv_height.setCurrentItem(whole - START_HEIGHT);// 初始化时显示的数据
//		wv_height.setCyclic(true);
//
//		final WheelViewNew wv_cm = (WheelViewNew) view.findViewById(R.id.cm);
//		wv_cm.setLabel(unit);
//		final String[] countries = { ".0", ".5" };
//		wv_cm.setAdapter(new ArrayWheelAdapter<String>(countries));
//		if (small == 0) {
//			wv_cm.setCurrentItem(0);// 初始化时显示的数据
//		}
//		if (small == 5) {
//			wv_cm.setCurrentItem(1);// 初始化时显示的数据
//		}
//
//		OnWheelChangedListener wheelListener_height = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				int height_num = newValue + START_HEIGHT;
//				cp_front = height_num;
//			}
//		};
//		OnWheelChangedListener wheelListener_cm = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				cp_after = countries[newValue];
//			}
//		};
//		wv_height.addChangingListener(wheelListener_height);
//		wv_cm.addChangingListener(wheelListener_cm);
//	}

	/**
	 * 选择时间
	 */
//	public void chooseTime() {
//		final Button popupwindow_menu_time_bt1 = (Button) view
//				.findViewById(R.id.popupwindow_menu_time_bt1);
//		final Button popupwindow_menu_time_bt2 = (Button) view
//				.findViewById(R.id.popupwindow_menu_time_bt2);
//		final TextView popupwindow_menu_time_text1 = (TextView) view
//				.findViewById(R.id.popupwindow_menu_time_text1);
//		final TextView popupwindow_menu_time_text2 = (TextView) view
//				.findViewById(R.id.popupwindow_menu_time_text2);
//		final TextView popupwindow_menu_time_text3 = (TextView) view
//				.findViewById(R.id.popupwindow_menu_time_text3);
//		final TextView popupwindow_menu_time_text4 = (TextView) view
//				.findViewById(R.id.popupwindow_menu_time_text4);
//		popupwindow_menu_time_bt1.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
//		popupwindow_menu_time_bt2.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
//
//		final WheelViewNew wv_hour_1 = (WheelViewNew) view.findViewById(R.id.hour_1);
//		wv_hour_1.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
//		wv_hour_1.setCurrentItem(0);// 初始化时显示的数据
//		wv_hour_1.setCyclic(true);
//
//		final WheelViewNew wv_minute_1 = (WheelViewNew) view
//				.findViewById(R.id.minute_1);
//		wv_minute_1.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
//		wv_minute_1.setCurrentItem(0);// 初始化时显示的数据
//		wv_minute_1.setCyclic(true);
//
//		final WheelViewNew wv_hour_2 = (WheelViewNew) view.findViewById(R.id.hour_2);
//		wv_hour_2.setAdapter(new NumericWheelAdapter(0, 23, "%02d"));
//		wv_hour_2.setCurrentItem(0);// 初始化时显示的数据
//		wv_hour_2.setCyclic(true);
//
//		final WheelViewNew wv_minute_2 = (WheelViewNew) view
//				.findViewById(R.id.minute_2);
//		wv_minute_2.setAdapter(new NumericWheelAdapter(0, 59, "%02d"));
//		wv_minute_2.setCurrentItem(0);// 初始化时显示的数据
//		wv_minute_2.setCyclic(true);
//
//		OnWheelChangedListener wheelListener_hour_1 = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				popupwindow_menu_time_text1.setText("" + newValue);
//			}
//		};
//		OnWheelChangedListener wheelListener_minute_1 = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				popupwindow_menu_time_text2.setText("" + newValue);
//			}
//		};
//		OnWheelChangedListener wheelListener_hour_2 = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				popupwindow_menu_time_text3.setText("" + newValue);
//			}
//		};
//		OnWheelChangedListener wheelListener_minute_2 = new OnWheelChangedListener() {
//			public void onChanged(WheelViewNew wheel, int oldValue, int newValue) {
//				popupwindow_menu_time_text4.setText("" + newValue);
//			}
//		};
//		wv_hour_1.addChangingListener(wheelListener_hour_1);
//		wv_minute_1.addChangingListener(wheelListener_minute_1);
//		wv_hour_2.addChangingListener(wheelListener_hour_2);
//		wv_minute_2.addChangingListener(wheelListener_minute_2);
//	}

	/**
	 * 选择场景和身体值
	 */
//	public void choosePhisical(final String unit, String value) {
//		final ListView listview = (ListView) view.findViewById(R.id.listview);
//		final Button popupwindow_menu_city_cancel = (Button) view
//				.findViewById(R.id.popupwindow_menu_city_cancel);
//		final Button popupwindow_menu_city_confirm = (Button) view
//				.findViewById(R.id.popupwindow_menu_city_confirm);
//		final TextView popupwindow_menu_city_text = (TextView) view
//				.findViewById(R.id.popupwindow_menu_city_text);
//		final List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
//		final ListAdapter adapter = new ListAdapter(mContext, listItem);
//
//		if (unit.equals(Const.SCENE)) {// 场景
//			for (int i = 0; i < scenes.length; i++) {
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				map.put("Text", "" + scenes[i]);
//				listItem.add(map);
//			}
//		} else {// 体质
//			for (int i = 0; i < phisicals.length; i++) {
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				map.put("Text", "" + phisicals[i]);
//				listItem.add(map);
//			}
//		}
//
//		// 初始化显示
//		if (unit.equals(Const.SCENE)) {
//			popupwindow_menu_city_text.setText(mContext
//					.getString(R.string.Personal_text_city));
//		} else {
//			popupwindow_menu_city_text.setText(mContext
//					.getString(R.string.Personal_text_physical));
//		}
//
//		if (null == value || value.equals("")) {
//			if (unit.equals(Const.SCENE)) {
//				sceneStr = scenes[0];
//				physicalStr = "";
//			} else {
//				sceneStr = "";
//				physicalStr = phisicals[0];
//			}
//		} else {
//			sceneStr = value;
//			physicalStr = value;
//		}
//
//		listview.setAdapter(adapter);
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				if (unit.equals(Const.SCENE)) {
//					popupwindow_menu_city_text.setText(scenes[arg2]);
//					sceneStr = scenes[arg2];
//				} else {
//					popupwindow_menu_city_text.setText(phisicals[arg2]);
//					physicalStr = phisicals[arg2];
//				}
//			}
//		});
//		popupwindow_menu_city_cancel.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
//		popupwindow_menu_city_confirm.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				if (unit.equals(Const.SCENE)) {
//					endListener.onEnd(Const.SCENE, sceneStr);
//				} else {
//					endListener.onEnd(Const.PHISICAL, physicalStr);
//				}
//				dismiss();
//			}
//		});
//	}

//	public void setEndListener(EndListener endListener) {
//		this.endListener = endListener;
//	}
//
//	public static interface EndListener {
//		public void onEnd(String state, String str);
//	}

//	public class ListAdapter extends BaseAdapter {
//		private List<Map<String, Object>> listItem;
//		private int selectedPosition = -1;
//
//		public int getSelectedPosition() {
//			return selectedPosition;
//		}
//
//		public void setSelectedPosition(int selectedPosition) {
//			this.selectedPosition = selectedPosition;
//		}
//
//		public List<Map<String, Object>> getListItems() {
//			return listItem;
//		}
//
//		public void setListItems(List<Map<String, Object>> listItems) {
//			this.listItem = listItems;
//		}
//
//		private LayoutInflater listContainer;
//		Context activity;
//
//		public final class ListItemView {
//			public TextView text;
//		}
//
//		public ListAdapter(Context mContext, List<Map<String, Object>> listItem) {
//			listContainer = LayoutInflater.from(mContext);
//			this.listItem = listItem;
//			activity = mContext;
//		}
//
//		public int getCount() {
//
//			return listItem.size();
//		}
//
//		public Object getItem(int arg0) {
//
//			return null;
//		}
//
//		public long getItemId(int arg0) {
//
//			return 0;
//		}
//
//		/**
//		 * ListView Item设置
//		 */
//		public View getView(final int position, View convertView,
//				ViewGroup parent) {
//
//			// 自定义视图
//			ListItemView listItemView = null;
//			if (convertView == null) {
//				listItemView = new ListItemView();
//
//				convertView = listContainer.inflate(
//						R.layout.list_item_popuwindow_city, null);
//
//				listItemView.text = (TextView) convertView
//						.findViewById(R.id.popuwindow_item_city_text);
//
//				convertView.setTag(listItemView);
//			} else {
//				listItemView = (ListItemView) convertView.getTag();
//			}
//
//			listItemView.text.setText((String) listItem.get(position).get(
//					"Text"));
//
//			return convertView;
//		}
//	}
}
