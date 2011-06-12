package ogakisoft.android.quiz;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Quiz {
    @SuppressWarnings("rawtypes")
    private static Class cls;
    private static Field[] mFlds;
    private Context context;
    private List<String> choice_list;
    private List<Drawable> image_list;
    private String question_answer;
    private String question_text;
    private String question_title;
    private String question_type;
    private String quiz_sequence;

    static {
	try {
	    cls = Class.forName("ogakisoft.android.quiz.R$drawable");
	    mFlds = cls.getDeclaredFields();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (SecurityException e) {
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    e.printStackTrace();
	}
    }
    
    public Quiz(Context context) {
	this.context = context;
	choice_list = new ArrayList<String>(0);
	image_list = new ArrayList<Drawable>(0);
    }

    public void addChoice(int id, String choice) {
	choice_list.add(id, choice);
    }

    public void addDrawable(String name) {
	int id = 0;
	for (Field f : mFlds) {
	    if (f.getName().endsWith(name)) {
		try {
		    id = f.getInt(cls);
		} catch (IllegalArgumentException e) {
		    e.printStackTrace();
		} catch (IllegalAccessException e) {
		    e.printStackTrace();
		}
		break;
	    }
	}
	if (id > 0)
	    image_list.add(context.getResources().getDrawable(id));
    }

    public Quiz copy() {
	Quiz q = new Quiz(null);
	q.choice_list = new ArrayList<String>();
	q.choice_list.addAll(this.choice_list);
	q.image_list.addAll(this.image_list);
	q.question_answer = this.question_answer;
	q.question_text = this.question_text;
	q.question_title = this.question_title;
	q.question_type = this.question_type;
	q.quiz_sequence = this.quiz_sequence;
	return q;
    }

    public String getAnswer() {
	return this.question_answer;
    }

    public List<String> getChoice() {
	return this.choice_list;
    }

    public List<Drawable> getDrawables() {
	return this.image_list;
    }

    public String getSequence() {
	return this.quiz_sequence;
    }

    public String getText() {
	return this.question_text;
    }

    public String getTitle() {
	return this.question_title;
    }

    public String getType() {
	return this.question_type;
    }

    public void setAnswer(String answer) {
	this.question_answer = answer;
    }

    public void setSequence(String sequence) {
	this.quiz_sequence = sequence;
    }

    public void setText(String text) {
	this.question_text = text;
    }

    public void setTitle(String title) {
	this.question_title = title;
    }

    public void setType(String type) {
	this.question_type = type;
    }

    @Override
    public String toString() {
	return "Quiz [choice_list="
		+ java.util.Arrays.toString(choice_list.toArray())
		+ ", image_list="
		+ java.util.Arrays.toString(image_list.toArray())
		+ ", question_answer=" + question_answer + ", question_text="
		+ question_text + ", question_title=" + question_title
		+ ", question_type=" + question_type + ", quiz_seq="
		+ quiz_sequence + "]";
    }

}
