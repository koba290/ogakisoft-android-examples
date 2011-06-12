package ogakisoft.android.speech;

import java.util.HashMap;

public class KanaToRomaji {
    private static final String[][] array = { { "あ", "ア", "a" },
	    { "い", "イ", "i" }, { "う", "ウ", "u" }, { "え", "エ", "e" },
	    { "お", "オ", "o" }, { "か", "カ", "ka" }, { "き", "キ", "ki" },
	    { "く", "ク", "ku" }, { "け", "ケ", "ke" }, { "こ", "コ", "ko" },
	    { "さ", "サ", "sa" }, { "し", "シ", "si" }, { "す", "ス", "su" },
	    { "せ", "セ", "se" }, { "そ", "ソ", "so" }, { "た", "タ", "ta" },
	    { "ち", "チ", "ti" }, { "つ", "ツ", "tu" }, { "て", "テ", "te" },
	    { "と", "ト", "to" }, { "な", "ナ", "na" }, { "に", "ニ", "ni" },
	    { "ぬ", "ヌ", "nu" }, { "ね", "ネ", "ne" }, { "の", "ノ", "no" },
	    { "は", "ハ", "ha" }, { "ひ", "ヒ", "hi" }, { "ふ", "フ", "fu" },
	    { "へ", "ヘ", "he" }, { "ほ", "ホ", "ho" }, { "ま", "マ", "ma" },
	    { "み", "ミ", "mi" }, { "む", "ム", "mu" }, { "め", "メ", "me" },
	    { "も", "モ", "mo" }, { "や", "ヤ", "ya" }, { "ゆ", "ユ", "yu" },
	    { "よ", "ヨ", "yo" }, { "ら", "ラ", "ra" }, { "り", "リ", "ri" },
	    { "る", "ル", "ru" }, { "れ", "レ", "re" }, { "ろ", "ロ", "ro" },
	    { "わ", "ワ", "wa" }, { "を", "ヲ", "wo" }, { "ん", "ン", "n" },
	    { "ぁ", "ァ", "a" }, { "ぃ", "ィ", "i" }, { "ぅ", "ゥ", "u" },
	    { "ぇ", "ェ", "e" }, { "ぉ", "ォ", "o" }, { "っ", "ッ", "tu" },
	    { "ゃ", "ャ", "ya" }, { "ゅ", "ュ", "yu" }, { "ょ", "ョ", "yo" },
	    { "ゔ", "ヴ", "vu" }, { "が", "ガ", "ga" }, { "ぎ", "ギ", "gi" },
	    { "ぐ", "グ", "gu" }, { "げ", "ゲ", "ge" }, { "ご", "ゴ", "go" },
	    { "ざ", "ザ", "za" }, { "じ", "ジ", "zi" }, { "ず", "ズ", "zu" },
	    { "ぜ", "ゼ", "ze" }, { "ぞ", "ゾ", "zou" }, { "だ", "ダ", "da" },
	    { "ぢ", "ヂ", "ji" }, { "づ", "ヅ", "zoo" }, { "で", "デ", "de" },
	    { "ど", "ド", "dou" }, { "ば", "バ", "ba" }, { "び", "ビ", "bi" },
	    { "ぶ", "ブ", "bu" }, { "べ", "ベ", "be" }, { "ぼ", "ボ", "bo" },
	    { "ぱ", "パ", "pa" }, { "ぴ", "ピ", "pi" }, { "ぷ", "プ", "pu" },
	    { "ぺ", "ペ", "pe" }, { "ぽ", "ポ", "po" } };
    private static HashMap<String, String> map;

    public static String toRomaji(String kana) {
	if (map == null) {
	    map = new HashMap<String, String>();
	    for (int i = 0; i < array.length; i++) {
		map.put(array[i][0], array[i][2]);
		map.put(array[i][1], array[i][2]);
	    }
	}
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < kana.length(); i++) {
	    String key = String.valueOf(kana.charAt(i));
	    if (map.containsKey(key)) {
		sb.append(map.get(key));
	    } else {
		sb.append(key);
	    }
	}
	return sb.toString();
    }
}
