package app.stockecomment.com.stockcomment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stockimageloader.app.PhotosThumbnailActivity;

import app.stockecomment.com.stockcomment.atabout.PersonActivity;

public class CommentActivity extends Activity implements View.OnClickListener {

    private EditText mEditText;
    private Button button;
    private Button btn_send;

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mEditText = (EditText) findViewById(R.id.edit_text);
        button = (Button) findViewById(R.id.bt);
        btn_send = (Button) findViewById(R.id.bt_send);
        textView = (TextView) findViewById(R.id.text_description);
        button.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        findViewById(R.id.img).setOnClickListener(this);
        mEditText.setText("我很好呀$深圳（399001）$ ");
        mEditText.setSelection(mEditText.getText().length());
        textView.setHighlightColor(Color.parseColor("#00000000"));
    }

    private void setText() {
        SpannableString text = null;
        try {
            text = SpanUtils.getSpan(mEditText,Color.BLUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEditText.setTextKeepState(text);
    }
    @Override
    protected void onResume() {
        super.onResume();
        setText();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img:
                PhotosThumbnailActivity.start(CommentActivity.this);
                break;
            case R.id.bt:
                goAt();
                break;
            case R.id.bt_send:
                String str = mEditText.getText().toString();
                textView.setText(str);
                SpannableString spannablString = new SpannableString(str);
                try {
                    spannablString = SpanUtils.getStockSpan(textView, Color.BLUE, spannablString, true, new SpanClickListener() {
                        @Override
                        public void onSpanClick(Object o) {
                            Toast.makeText(CommentActivity.this,o.toString(),Toast.LENGTH_SHORT).show();
                        }
                    },"点击了股票");
                    spannablString = SpanUtils.getAtUserSpan(textView, Color.BLUE, spannablString, true, new SpanClickListener() {
                        @Override
                        public void onSpanClick(Object o) {
                            Toast.makeText(CommentActivity.this,o.toString(),Toast.LENGTH_SHORT).show();
                        }
                    },"点击了人名");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView.setText(spannablString);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                break;
        }
    }

    private static final int CODE_PERSON = 1;

    private void goAt() {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivityForResult(intent, CODE_PERSON);
    }

    /**
     * 返回的所有的用户名,用于识别输入框中的所有要@的人
     * 如果用户删除过，会出现不匹配的情况，需要在for循环中做处理
     */
    private String nameStr;

    /**
     * 上一次返回的用户名，用于把要@的用户名拼接到输入框中
     */
    private String lastNameStr;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != RESULT_OK) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CODE_PERSON:
                String tmpNameStr = data.getStringExtra(PersonActivity.KEY_NAME);
                if (nameStr == null) {
                    nameStr = tmpNameStr;
                } else if (!nameStr.contains(tmpNameStr)) {
                    nameStr = nameStr + tmpNameStr;
                }
                lastNameStr = tmpNameStr;
                int curIndex = mEditText.getSelectionStart();
                mEditText.getText().insert(curIndex, lastNameStr);
                break;

        }
    }


}
