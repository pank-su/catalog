package su.pank.transport.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RoutePoint {
    private final IntegerProperty id;
    private final StringProperty locality;
    private final StringProperty district;
    private final StringProperty description;

    public RoutePoint(int id, String locality, String district, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.locality = new SimpleStringProperty(locality != null ? locality : "");
        this.district = new SimpleStringProperty(district != null ? district : "");
        this.description = new SimpleStringProperty(description != null ? description : "");
    }

    public int getId() { return id.get(); }
    public String getLocality() { return locality.get(); }
    public void setLocality(String value) { locality.set(value); }
    public String getDistrict() { return district.get(); }
    public void setDistrict(String value) { district.set(value); }
    public String getDescription() { return description.get(); }
    public void setDescription(String value) { description.set(value); }

    @Override
    public String toString() {
        return description.get() + " (" + locality.get() + ", " + district.get() + ")";
    }
}