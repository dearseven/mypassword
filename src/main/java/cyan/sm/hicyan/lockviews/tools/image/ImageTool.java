package cyan.sm.hicyan.lockviews.tools.image;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author CentralPerk
 *
 */
public class ImageTool {


	/** 将字符串数据写成图片Bitmap
	 * @param data 文本数据
	 * @param width bitmap宽
	 * @param height bitmap高
	 * @param bgColor 背景色
	 * @param textColor  文字颜色
	 * @param textSize  文字大小
	 * @param xPosition X轴起始位置
	 * @param textSpace 行间距
	 * @return bitmap ,null写入失败
	 */
	public static Bitmap writeDataToImage(ArrayList<String> data,int width,int height,int bgColor,int textColor,
										  int textSize,int xPosition,int textSpace) {
		try {

			Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawColor(bgColor); // 背景颜色

			Paint p = new Paint();
			p.setColor(textColor); // 字体颜色
			p.setTextSize(textSize); // 字体大小
			for (int i = 0; i < data.size(); i++) {
				canvas.drawText(data.get(i), xPosition, (i + 1) * textSpace + i*textSize, p);
			}
			return bitmap;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
}
