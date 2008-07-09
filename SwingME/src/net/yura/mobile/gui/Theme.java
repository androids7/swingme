/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */

package net.yura.mobile.gui;

import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.border.LineBorder;

public class Theme {
	
	public int scrollBarCol=0x00FFFFFF;
	public int scrollTrackCol=0x00000000;
	
	// Font object
	public Font font;

	// Items info
	public int background;
	public int foreground;
	
	public Border normalBorder;
	public Border activeBorder;
	
	public int defaultWidth;
        public int defaultSpace;
	public int barHeight;
        
        public Theme(){
            this(null,0,0);
            // these will have defaults setup anyway
        }
        
	public Theme(Font font,int barHeight,int a) {
		
		this.font = font;

                this.barHeight = barHeight;
		defaultWidth = a;
		
		background = 0x00FFFFFF;
		foreground = 0;
		
		normalBorder = new LineBorder(0x00808080);
		activeBorder = new LineBorder(0);

	}

}

