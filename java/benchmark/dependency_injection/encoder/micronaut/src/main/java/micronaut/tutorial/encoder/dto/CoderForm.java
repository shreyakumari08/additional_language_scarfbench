package micronaut.tutorial.encoder.dto;
import io.micronaut.serde.annotation.Serdeable;
@Serdeable
public class CoderForm {
    private String inputString; private int transVal; private String codedString;
    public String getInputString() { return inputString; } public void setInputString(String s) { this.inputString = s; }
    public int getTransVal() { return transVal; } public void setTransVal(int t) { this.transVal = t; }
    public String getCodedString() { return codedString; } public void setCodedString(String s) { this.codedString = s; }
}
