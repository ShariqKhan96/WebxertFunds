package android.webxert.com.webxertfunds.sqlitemodel;

public class Donor {
    String donor;
    int value;

    public Donor() {
    }

    public Donor(String donor, int value) {
        this.donor = donor;
        this.value = value;
    }

    public String getDonor() {
        return donor;
    }

    public void setDonor(String donor) {
        this.donor = donor;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
