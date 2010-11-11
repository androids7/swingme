/**
 *  MicroEmulator
 *  Copyright (C) 2008 Bartek Teodorczyk <barteo@barteo.net>
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id: AndroidFont.java 1822 2008-11-13 11:15:10Z barteo $
 */

package net.yura.android.lcdui;


import java.util.HashMap;

import javax.microedition.lcdui.Font;

import net.yura.android.AndroidMeActivity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

public class FontManager {

	private static int SIZE_SMALL = 14;
	private static int SIZE_MEDIUM = 16;
	private static int SIZE_LARGE = 20;

	private static HashMap<Font, FontManager> fonts = new HashMap<Font, FontManager>();

	Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public FontManager(Typeface typeface, int size, boolean underlined) {
		paint.setTypeface(typeface);
		paint.setTextSize(size);
		paint.setUnderlineText(underlined);
	}

	public static FontManager getFont(Font meFont) {
		FontManager result = fonts.get(meFont);

		if (result == null) {
			Typeface family = Typeface.SANS_SERIF;
			if (meFont.getFace() == Font.FACE_SYSTEM) {
				family = Typeface.SANS_SERIF;
			} else if (meFont.getFace() == Font.FACE_MONOSPACE) {
				family = Typeface.MONOSPACE;
			} else if (meFont.getFace() == Font.FACE_PROPORTIONAL) {
				family = Typeface.SANS_SERIF;
			}
			int style = 0;
			if ((meFont.getStyle() & Font.STYLE_PLAIN) != 0) {
				style |= Typeface.NORMAL;
			}
			if ((meFont.getStyle() & Font.STYLE_BOLD) != 0) {
				style |= Typeface.BOLD;
			}
			if ((meFont.getStyle() & Font.STYLE_ITALIC) != 0) {
				style |= Typeface.ITALIC;
			}
			boolean underlined = false;
			if ((meFont.getStyle() & Font.STYLE_UNDERLINED) != 0) {
				underlined = true;
			}

			int textAppearance;
			int size;
 			if (meFont.getSize() == Font.SIZE_SMALL) {
				size = SIZE_SMALL;
				textAppearance = android.R.style.TextAppearance_Small;
			} else if (meFont.getSize() == Font.SIZE_MEDIUM) {
				size = SIZE_MEDIUM;
				textAppearance = android.R.style.TextAppearance_Medium;
			} else {
				size = SIZE_LARGE;
				textAppearance = android.R.style.TextAppearance_Large;
			}

			Context ctx = AndroidMeActivity.DEFAULT_ACTIVITY;

			// The font size is adjusted to the phone "scaled density"
			DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
			size = (int) (size * metrics.scaledDensity);

//            // Use the values from Android Theme. If it fails, we will still
//            // have some reasonable values
//			try {
//    			int attrs[] = {android.R.attr.textSize};
//    	        TypedArray a = ctx.obtainStyledAttributes(textAppearance, attrs);
//    	        size = Math.round(a.getDimension(0, size));
//    	        a.recycle();
//			}
//			catch (Throwable e) {
//                //#debug debug
//			    e.printStackTrace();
//            }

			result = new FontManager(Typeface.create(family, style), size,
					underlined);
			fonts.put(meFont, result);
		}

		return result;
	}

	public int charWidth(Font f, char ch) {
		return getFont(f).charWidth(ch);
	}

	public int charsWidth(Font f, char[] ch, int offset, int length) {
		return getFont(f).charsWidth(ch, offset, length);
	}

	public int getBaselinePosition(Font f) {
		return getFont(f).getBaselinePosition();
	}

	public int getHeight(Font f) {
		return getFont(f).getHeight();
	}

	public int stringWidth(Font f, String str) {
		return getFont(f).stringWidth(str);
	}

	public Paint getPaint() {
		return paint;
	}

	public int charWidth(char ch) {
		return (int) paint.measureText(new char[] { ch }, 0, 1);
	}

	public int charsWidth(char[] ch, int offset, int length) {
		return (int) paint.measureText(ch, offset, length);
	}

	public int getBaselinePosition() {
		return -paint.getFontMetricsInt().ascent;
	}

	public int getHeight() {
		return paint.getFontMetricsInt(paint.getFontMetricsInt());
	}

	public int stringWidth(String str) {
		return (int) paint.measureText(str);
	}
}
