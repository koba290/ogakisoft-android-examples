package ogakisoft.android.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends Activity {
    class EditableText extends EditText {
	public EditableText(Context context, boolean editable) {
	    super(context, null, editable ? android.R.attr.editTextStyle
		    : android.R.attr.textViewStyle);
	}
    }

    public class ImageAdapter extends BaseAdapter {
	private final List<Drawable> mList = Collections
		.synchronizedList(new ArrayList<Drawable>());

	public void add(List<Drawable> drawable) {
	    mList.addAll(drawable);
	}

	public void clear() {
	    mList.clear();
	}

	@Override
	public int getCount() {
	    return mList.size();
	}

	@Override
	public Object getItem(int position) {
	    return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	public View getView(final int position, View convertView,
		ViewGroup parent) {
	    ImageView image;
	    if (convertView == null) {
		image = new ImageView(QuizActivity.this);
	    } else {
		image = (ImageView) convertView;
	    }
	    BitmapDrawable drawable = (BitmapDrawable) mList.get(position);
	    if (drawable != null) {
		image.setImageBitmap(drawable.getBitmap());
		image.setAdjustViewBounds(true);
	    }
	    return image;
	}
    }

    private static final String TAG = "QuizActivity";
    private static final String TYPE_CHECK = "check";
    private static final String TYPE_RADIO = "radio";
    private static final String TYPE_TEXT = "text";
    private static final String XML_TAG_ANSWER = "answer";
    private static final String XML_TAG_CHOICE = "choice";
    private static final String XML_TAG_ID = "id";
    private static final String XML_TAG_IMAGE = "image";
    private static final String XML_TAG_QUESTION = "question";
    private static final String XML_TAG_QUIZ = "quiz";
    private static final String XML_TAG_QUIZSET = "quizset";
    private static final String XML_TAG_SEQ = "seq";
    private static final String XML_TAG_SRC = "src";
    private static final String XML_TAG_TITLE = "title";
    private static final String XML_TAG_TYPE = "type";
    private ImageAdapter mAdapter;
    private CheckBox[] mCheck;
    private LinearLayout mCheckboxGroup;
    private EditableText[] mEditableText;
    private GridView mGrid;
    private List<Quiz> mQuizList;
    private int mQuizNo = 0;
    private RadioButton[] mRadio;
    private RadioGroup mRadioGroup;
    private String[] mText;
    private LinearLayout mTextboxGroup;
    private TextView mTextQuestion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	mTextQuestion = (TextView) findViewById(R.id.textView01);
	mGrid = (GridView) findViewById(R.id.gridView01);
	mGrid.setNumColumns(3);
	mAdapter = new ImageAdapter();
	mGrid.setAdapter(mAdapter);
	mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
	mCheckboxGroup = (LinearLayout) findViewById(R.id.checkbox_group);
	mTextboxGroup = (LinearLayout) findViewById(R.id.textbox_group);

	parse(this.getResources().getXml(R.xml.quiz));
	showQuiz();
    }

    private Quiz getQuiz() {
	return mQuizList.get(mQuizNo);
    }

    public void onClickAnswerButton(View v) {
	boolean result = false;
	Quiz quiz = getQuiz();
	String type = quiz.getType();

	if (type.equals(TYPE_RADIO)) {
	    int collect = Integer.parseInt(quiz.getAnswer());
	    result = mRadio[collect - 1].isChecked();
	} else if (type.equals(TYPE_CHECK)) {
	    String[] ans = quiz.getAnswer().split(",");
	    List<String> list = java.util.Arrays.asList(ans);
	    int count = mCheck.length;
	    for (int i = 0; i < count; i++) {
		if (mCheck[i].isChecked()
			&& list.contains(String.valueOf(i + 1))) {
		    result = true;
		} else if (!mCheck[i].isChecked()
			&& !list.contains(String.valueOf(i + 1))) {
		    result = true;
		} else {
		    result = false;
		    break;
		}
	    }
	} else if (type.equals(TYPE_TEXT)) {
	    int count = mText.length;
	    for (int i = 0; i < count; i++) {
		String text = mEditableText[i].getText().toString();
		if (text.equals(mText[i])) {
		    result = true;
		} else {
		    result = false;
		    break;
		}
	    }
	}
	if (result) {
	    Toast.makeText(this, "GOOD!", Toast.LENGTH_SHORT).show();
	} else {
	    Toast.makeText(this, "Oh! No!", Toast.LENGTH_SHORT).show();
	}
    }

    public void onClickBackButton(View v) {
	if (mQuizNo > 0) {
	    --mQuizNo;
	    showQuiz();
	}
    }

    public void onClickNextButton(View v) {
	if (mQuizNo < mQuizList.size() - 1) {
	    ++mQuizNo;
	    showQuiz();
	}
    }

    private void parse(XmlResourceParser parser) {
	mQuizList = new ArrayList<Quiz>();
	Quiz quiz = null;
	boolean done = false;
	try {
	    int eventType = parser.getEventType();
	    while (eventType != XmlPullParser.END_DOCUMENT && !done) {
		String name = null;
		switch (eventType) {
		case XmlPullParser.START_TAG:
		    name = parser.getName();
		    Log.d(TAG, "start_tag:" + name);
		    if (name.equalsIgnoreCase(XML_TAG_QUIZ)) {
			quiz = new Quiz(this);
			quiz.setSequence(parser.getAttributeValue(null,
				XML_TAG_SEQ));
			quiz.setTitle(parser.getAttributeValue(null,
				XML_TAG_TITLE));
			quiz.setType(parser.getAttributeValue(null,
				XML_TAG_TYPE));
			quiz.setAnswer(parser.getAttributeValue(null,
				XML_TAG_ANSWER));
		    } else if (name.equalsIgnoreCase(XML_TAG_QUESTION)) {
			String question = parser.nextText();
			quiz.setText(question);
		    } else if (name.equalsIgnoreCase(XML_TAG_IMAGE)) {
			quiz.addDrawable(parser.getAttributeValue(null,
				XML_TAG_SRC));
		    } else if (name.equalsIgnoreCase(XML_TAG_CHOICE)) {
			String id = parser.getAttributeValue(null, XML_TAG_ID);
			String choice = parser.nextText();
			Log.d(TAG, "id=" + id + ",choice=" + choice);
			int idx = Integer.parseInt(id);
			idx--;
			quiz.addChoice(idx, choice);
		    }
		    break;
		case XmlPullParser.END_TAG:
		    name = parser.getName();
		    Log.d(TAG, "end_tag:" + name);

		    if (name.equalsIgnoreCase(XML_TAG_QUIZ) && quiz != null) {
			mQuizList.add(quiz);
			Log.d(TAG, quiz.toString());

		    } else if (name.equalsIgnoreCase(XML_TAG_QUIZSET)) {
			done = true;
		    }
		    break;
		}
		eventType = parser.next();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void showQuiz() {
	final Quiz quiz = getQuiz();
	String type = quiz.getType();
	int count = quiz.getChoice().size();

	mRadioGroup.removeAllViews();
	mCheckboxGroup.removeAllViews();
	mTextboxGroup.removeAllViews();
	mAdapter.clear();
	mAdapter.notifyDataSetChanged();

	if (quiz.getDrawables().size() > 0) {
	    mAdapter.add(quiz.getDrawables());
	    mAdapter.notifyDataSetChanged();
	}

	if (type.equals(TYPE_RADIO)) {
	    mTextQuestion.setText(quiz.getText());
	    mRadio = new RadioButton[count];
	    for (int i = 0; i < count; i++) {
		mRadio[i] = new RadioButton(QuizActivity.this);
		mRadio[i].setText(quiz.getChoice().get(i));
		mRadio[i].setLayoutParams(new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mRadioGroup.addView(mRadio[i], i);
	    }
	} else if (type.equals(TYPE_CHECK)) {
	    mTextQuestion.setText(quiz.getText());
	    mCheck = new CheckBox[count];
	    for (int i = 0; i < count; i++) {
		mCheck[i] = new CheckBox(QuizActivity.this);
		mCheck[i].setText(quiz.getChoice().get(i));
		mCheck[i].setLayoutParams(new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mCheckboxGroup.addView(mCheck[i], i);
	    }
	} else if (type.equals(TYPE_TEXT)) {
	    mTextQuestion.setText("");
	    String text = quiz.getText();
	    mText = text.split("[{}]");
	    String[] question = text.split("\\{[^{}]*\\}");
	    int all_count = mText.length;
	    int j = 0;
	    mEditableText = new EditableText[all_count];
	    for (int i = 0; i < all_count; i++) {
		if (mText[i].equals(question[j])) {
		    mEditableText[i] = new EditableText(QuizActivity.this,
			    false);
		    mEditableText[i].setText(question[j++]);
		    mEditableText[i].setLayoutParams(new LayoutParams(
			    LayoutParams.WRAP_CONTENT,
			    LayoutParams.WRAP_CONTENT));
		    mTextboxGroup.addView(mEditableText[i], i);
		} else {
		    mEditableText[i] = new EditableText(QuizActivity.this, true);
		    mEditableText[i].setText("???");
		    mEditableText[i].setLayoutParams(new LayoutParams(
			    LayoutParams.WRAP_CONTENT,
			    LayoutParams.WRAP_CONTENT));
		    mTextboxGroup.addView(mEditableText[i], i);
		}
	    }
	}
    }
}