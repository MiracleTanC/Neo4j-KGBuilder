package com.warmer.base.util;


public class ColorInfo {
    /**
     * 颜色的alpha值，此值控制了颜色的透明度
     */
    public int A;
    /**
     * 颜色的红分量值，Red
     */
    public int R;
    /**
     * 颜色的绿分量值，Green
     */
    public int G;
    /**
     * 颜色的蓝分量值，Blue
     */
    public int B;

    public int toRGB() {
        return this.R << 16 | this.G << 8 | this.B;
    }

    public java.awt.Color toAWTColor(){
        return new java.awt.Color(this.R,this.G,this.B,this.A);
    }

    public static ColorInfo fromARGB(int red, int green, int blue) {
        return new ColorInfo((int) 0xff, (int) red, (int) green, (int) blue);
    }
    public static ColorInfo fromARGB(int alpha, int red, int green, int blue) {
        return new ColorInfo(alpha, red, green, blue);
    }
    public ColorInfo(int a,int r, int g , int b ) {
        this.A = a;
        this.B = b;
        this.R = r;
        this.G = g;
    }
}
