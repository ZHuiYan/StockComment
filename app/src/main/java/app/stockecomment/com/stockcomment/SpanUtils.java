package app.stockecomment.com.stockcomment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.DynamicDrawableSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Wode9 on 2016/10/13.
 */

public class SpanUtils {
    /**
     * $$   $深圳成指(399001)$
     */
    public static final String STOCK_PATTERN = "\\$+[a-zA-Z0-9\\u4e00-\\u9fa5]+（\\d{6}）\\$";
    /**
     * at人的正则
     */
    public static final String AT_PARTERN ="@[^,，：:\\s@]+";

    public static SpannableString getSpan(TextView mEditText,int color) throws Exception {
        SpannableString spannableString = new SpannableString(mEditText.getText().toString());
        Pattern patten = Pattern.compile(STOCK_PATTERN, Pattern.CASE_INSENSITIVE);
        Pattern patten1 = Pattern.compile(AT_PARTERN, Pattern.CASE_INSENSITIVE);
        dealPattern(mEditText,color, spannableString,patten, 0);
        dealPattern(mEditText,color,spannableString,patten1,0);
        return spannableString;
    }
    /**
     * 自动识别股票并做颜色处理,可点击
     * @param color
     */
    public static SpannableString getStockSpan(TextView mEditText,int color, SpannableString spannableString, boolean clickable,SpanClickListener spanClickListener, String stock) throws Exception {
        Pattern patten = Pattern.compile(STOCK_PATTERN, Pattern.CASE_INSENSITIVE);
        if(clickable){
            dealClick(spannableString, patten, 0, spanClickListener, stock);
        }
        dealPattern(mEditText,color, spannableString,patten, 0);
        return spannableString;
    }
    /**
     * @用户 颜色处理、点击处理
     * @param color 前景色
     * @param clickable 是否可点击
     * @param spanClickListener
     * @return
     * @throws Exception
     */
    public static SpannableString getAtUserSpan(TextView mEditText,int color, SpannableString spannableString, boolean clickable, SpanClickListener spanClickListener, String name) throws Exception {
        Pattern patten;
        patten = Pattern.compile(AT_PARTERN, Pattern.CASE_INSENSITIVE);
        if(clickable){
            dealClick(spannableString, patten, 0, spanClickListener, name);
        }
        dealPattern(mEditText,color, spannableString, patten, 0);
        return spannableString;
    }

    /**
     * 对spanableString进行正则判断，如果符合要求，则将内容变色
     * @param color
     * @param spannableString 整个字符串
     * @param patten @张三0
     * @param start
     * @throws Exception
     */
    private static SpannableString dealPattern(TextView mEditText,int color,SpannableString spannableString, Pattern patten, int start) throws Exception {
        final Context context = mEditText.getContext();
        Matcher matcher = patten.matcher(spannableString);//整个字符串
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            // 计算该内容的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            //设置前景色span
            String match_str = spannableString.subSequence(matcher.start(),end).toString();
            final Bitmap bmp = getBitmap(match_str,mEditText,color);
            spannableString.setSpan(new DynamicDrawableSpan(
                                            DynamicDrawableSpan.ALIGN_BASELINE) {

                                        @Override
                                        public Drawable getDrawable() {
                                            // TODO Auto-generated method stub
                                            BitmapDrawable drawable = new BitmapDrawable(
                                                    context.getResources(), bmp);
                                            drawable.setBounds(0, 0,
                                                    bmp.getWidth(),
                                                    bmp.getHeight());
                                            return drawable;
                                        }
                                    }, matcher.start(),end,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spannableString.setSpan(new ForegroundColorSpan(color), matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealPattern(mEditText,color, spannableString, patten, end);
            }
            break;
        }
        return spannableString;
    }
    /**
     * 把返回的人名，转换成bitmap
     * @param name
     * @return
     */
    public static Bitmap getBitmap(String name, TextView textView, int color) {
		/* 把@相关的字符串转换成bitmap 然后使用DynamicDrawableSpan加入输入框中 */
        name = "" + name + " ";
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setTextSize(textView.getTextSize() - dpToPx(textView.getContext(),1));
        Rect rect = new Rect();
        paint.getTextBounds(name, 0, name.length(), rect);
        // 获取字符串在屏幕上的长度
        int width = (int) (paint.measureText(name));

        final Bitmap bmp = Bitmap.createBitmap(width,rect.height(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        canvas.drawText(name, rect.left, (rect.height()- rect.bottom)/2 + rect.height() - dpToPx(textView.getContext(),10), paint);

        return bmp;
    }
    /**
     * 得到textView的高度
     * @param textView
     * @return
     */
    public static int getFontHeight(TextView textView) {
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }
    /**
     * 对spanableString进行正则判断，如果符合要求，将内容设置可点击
     * @param spannableString
     * @param patten
     * @param start
     * @param spanClickListener
     * @param bean
     */
    private static void dealClick(SpannableString spannableString, Pattern patten, int start, final SpanClickListener spanClickListener, final Object bean) {
        Matcher matcher = patten.matcher(spannableString);
        while (matcher.find()) {
            String key = matcher.group();
            // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
            if (matcher.start() < start) {
                continue;
            }
            // 计算该内容的长度，也就是要替换的字符串的长度
            int end = matcher.start() + key.length();
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    spanClickListener.onSpanClick(bean);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    //设置画笔属性
                    ds.setUnderlineText(false);//默认有下划线
                }
            }, matcher.start(), end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (end < spannableString.length()) {
                // 如果整个字符串还未验证完，则继续。。
                dealClick(spannableString, patten, end, spanClickListener, bean);
            }
            break;
        }
    }
    public static int dpToPx(Context context,int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
