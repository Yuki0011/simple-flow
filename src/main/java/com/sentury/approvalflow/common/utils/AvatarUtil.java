package com.sentury.approvalflow.common.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Random;

/**
 * @author zhj
 * @version 1.0
 * @description: 头像工具类
 * @date 2024/3/16 15:22
 */
public class AvatarUtil {

    //图片宽度
    private static final int WIDTH=100;
    //图片高度
    private static final int HEIGHT=100;


    //得到该字体字符串的长度
    public static int getWordWidth(Font font, String content) {
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        Rectangle rec = font.getStringBounds(content, frc).getBounds();
        return (int) rec.getWidth();
    }

    /**
     * @param name 用户名称
     * @return 访问图片的路径 localhost:8082/img/
     * @author: zhengzhonghua
     * @date: 2023/3/16 13:24
     * @description: 头像生成
     */
    @SneakyThrows
    public static File generateImg(String name) {
        int nameLen = name.length();
        //定义最后在图片上显示的姓名
        String nameWritten;
        if (nameLen <= 2) {
            nameWritten = name;
        } else {
            //如果用户姓名大于三位,截取后两位
            nameWritten = StrUtil.subWithLength(name, name.length() - 2, 2);
        }
        String uuid = IdUtil.fastSimpleUUID();
        //文件名(路径+uuid+.jpg)
        String fileName = "/tmp/" + uuid + ".jpg";
        File file = new File(fileName);
        //生成图片
        BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setBackground(getRandomColor());
        g2.clearRect(0, 0, WIDTH, HEIGHT);
        g2.setPaint(Color.WHITE);


        //引入字体
        InputStream inputStream = ResourceUtil.getStream("fonts/syht.otf");
        BufferedInputStream fb = new BufferedInputStream(inputStream);

        // 两个字及以上
        if (nameWritten.length() >= 2) {


            Font font = null;
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, fb);
                font=font.deriveFont(Font.BOLD,30);
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            }

            g2.setFont(font);

            //计算该字体文本的长度
            int wordWidth = getWordWidth(font, nameWritten);


            g2.drawString(nameWritten, (WIDTH - wordWidth) / 2, 60);
        }
        // 一个字
        if (nameWritten.length() == 1) {
            // 中文
            Font font = null;
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, fb);
                font=font.deriveFont(Font.BOLD,50);
            } catch (FontFormatException e) {
                throw new RuntimeException(e);
            }

            g2.setFont(font);


            //计算该字体文本的长度
            int wordWidth = getWordWidth(font, nameWritten);


            g2.drawString(nameWritten, (WIDTH - wordWidth) / 2, 70);

        }
        //图片做圆角处理
        BufferedImage rounded = makeRoundedCorner(bi, 20);
        ImageIO.write(rounded, "png", file);

        fb.close();
        inputStream.close();
        return file;
    }


    /**
     * @author: zhengzhonghua
     * @date: 2023/3/16 13:32
     * @description: 生成随机颜色
     */
    private static Color getRandomColor() {
        String[] beautifulColors = new String[]{"2,168,250","51,153,204","51,102,255","102,0,204","102,102,51","99," +
                "00,99","204,51,204","255,153,102"};
        int len = beautifulColors.length;
        Random random = new Random();
        String[] color = beautifulColors[random.nextInt(len)].split(",");
        return new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2]));
    }

    /**
     * @author: zhengzhonghua
     * @date: 2023/3/16 13:39
     * @description: 图片做圆角处理
     */
    public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return output;
    }
}
