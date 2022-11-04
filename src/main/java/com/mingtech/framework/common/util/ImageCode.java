package com.mingtech.framework.common.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 
 * 随机生成图形验证码
 * 
 * @author 黄磊
 * 
 */
public class ImageCode {

	/**
	 * 随机验证码
	 */
	String sRand = "";

	/**
	 * 验证码图片
	 */
	BufferedImage image;

	public ImageCode(int width, int height, String rand) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取图形上下文
		Graphics g = image.getGraphics();
		// 生成随机类
		Random random = new Random();
		// 设定背景色
		// g.setColor(getRandColor(200,250));
		g.fillRect(0, 0, width, height);
		// 设定字体
		g.setFont(new Font("Georgia", Font.BOLD, 16));
		// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}

		sRand = rand;
		int x = StringUtil.getRandomInt(15, 10, 12);
		int y = StringUtil.getRandomInt(20, 11, 15);
		for (int i = 0; i < sRand.length(); i++) {
			// 将认证码显示到图象中
			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(sRand.charAt(i) + "", x * i, y);
		}
		// 图象生效
		g.dispose();
	}

	/**
	 * 给定范围获得随机颜色
	 * 
	 * @param fc
	 * @param bc
	 * @return
	 */
	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}

	/**
	 * 返回随机数字
	 * 
	 * @return
	 */
	public String getSRand() {
		return sRand;
	}

	/**
	 * 返回随机验证图片
	 * 
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}
}
