package paymentmicroservice.Models;

import javax.persistence.Column;
import javax.persistence.Id;

public class CardAvailable {
    @Id
    private int id;
    @Column(unique = true)
    private String name;
    private boolean isActive;

    public  CardAvailable(){}

    public CardAvailable(int id, String name, boolean isActive) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
