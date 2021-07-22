package in.tts.utils;

public class SpellUtil {

    public static String convertToSpellOnce(String words) {
        StringBuilder sb = new StringBuilder();
        for (char letter : words.toCharArray()) {
            sb.append(letter);
            //sb.append(convertSoundBased(letter)); this is another option
            sb.append(",");
        }
        return sb.toString();
    }

    private static String convertSoundBased(char letter) {
        switch (letter) {
            case 'a':
                return "a";
            case 'b':
                return "bee";
            case 'c':
                return "cee";
            case 'd':
                return "dee";
            case 'e':
                return "e";
            case 'f':
                return "ef";
            case 'g':
                return "gee";
            case 'h':
                return "aitch";
            case 'i':
                return "i";
            case 'j':
                return "jay";
            case 'k':
                return "kay";
            case 'l':
                return "el";
            case 'm':
                return "em";
            case 'n':
                return "en";
            case 'o':
                return "o";
            case 'p':
                return "pee";
            case 'q':
                return "cue";
            case 'r':
                return "ar";
            case 's':
                return "ess";
            case 't':
                return "tee";
            case 'u':
                return "u";
            case 'v':
                return "vee";
            case 'w':
                return "double-u";
            case 'x':
                return "ex";
            case 'y':
                return "wy";
            case 'z':
                return "zed";
        }
        return "";

    }
}
