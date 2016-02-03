package cz.mumi.fi.pv168.backend;

/**
 * Created by MANEOR on 7.3.14.
 */
public class Guest {
    private Long id;
    private String fullName;
    private String passportNumber;
    private String creditCardNumber;
    private boolean vipCustomer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Guest guest = (Guest) o;

        if (id != null ? !id.equals(guest.id) : guest.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


    public Guest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public boolean getVipCustomer() {
        return vipCustomer;
    }

    public void setVipCustomer(boolean vipCustomer) {
        this.vipCustomer = vipCustomer;
    }

    @Override
    public String toString() {
        return

                fullName + " - " + passportNumber;
    }
}
