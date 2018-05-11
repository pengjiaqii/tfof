package com.jade.surface;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Jade on 2018/5/10.
 */
public class Util {

	/**
	 * dp2px
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, float dp) {
		int px = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
		return px;
	}

}
