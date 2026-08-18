package micronaut.tutorial.producermethods.service;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
@Singleton
@Named("shiftCoder")
public class CoderImpl implements Coder {
    @Override
    public String codeString(String s, int tval) {
        final int SPACE_CHAR = 32, CAPITAL_A = 65, CAPITAL_Z = 90, SMALL_A = 97, SMALL_Z = 122;
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            int cp = sb.codePointAt(i);
            int cplus = cp + tval;
            if (cp == SPACE_CHAR) cplus = SPACE_CHAR;
            if ((cp >= CAPITAL_A) && (cp <= CAPITAL_Z)) { if (cplus > CAPITAL_Z) cplus -= 26; }
            else if ((cp >= SMALL_A) && (cp <= SMALL_Z)) { if (cplus > SMALL_Z) cplus -= 26; }
            else cplus = cp;
            sb.setCharAt(i, (char) cplus);
        }
        return sb.toString();
    }
}
