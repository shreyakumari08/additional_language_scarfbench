package micronaut.tutorial.simplegreeting.dto;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class PrinterForm {
    private String name;
    private String salutation;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSalutation() { return salutation; }
    public void setSalutation(String salutation) { this.salutation = salutation; }
}
