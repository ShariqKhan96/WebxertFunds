package android.webxert.com.webxertfunds.model;

public class Donation {
    String donation_id;
    String from;
    String value;
    String donated_at;

    public Donation() {
    }

    public Donation(String donation_id, String from, String value, String donated_at) {
        this.donation_id = donation_id;
        this.from = from;
        this.value = value;
        this.donated_at = donated_at;
    }


    public String getDonation_id() {
        return donation_id;
    }

    public void setDonation_id(String donation_id) {
        this.donation_id = donation_id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDonated_at() {
        return donated_at;
    }

    public void setDonated_at(String donated_at) {
        this.donated_at = donated_at;
    }
}
