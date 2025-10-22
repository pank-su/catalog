package su.pank.transport.data.models;

import su.pank.transport.domain.LinkedList;
import su.pank.transport.domain.SimpleLinkedList;

public class Route {
    private int id;
    private int routeNumber;
    private int startPointId;
    private String startLocality;
    private String startDistrict;
    private String startDescription;
    private int endPointId;
    private String endLocality;
    private String endDistrict;
    private String endDescription;
    private String[] specialCategories;
    private String routeType;

    public Route(int id, int routeNumber, int startPointId, String startLocality, String startDistrict, String startDescription,
                 int endPointId, String endLocality, String endDistrict, String endDescription, String specialCategory) {
        this.id = id;
        this.routeNumber = routeNumber;
        this.startPointId = startPointId;
        this.startLocality = startLocality;
        this.startDistrict = startDistrict;
        this.startDescription = startDescription;
        this.endPointId = endPointId;
        this.endLocality = endLocality;
        this.endDistrict = endDistrict;
        this.endDescription = endDescription;
        this.specialCategories = specialCategory != null && !specialCategory.isEmpty() ?
                specialCategory.split(",") : new String[0];
        this.routeType = determineRouteType(routeNumber);
    }

    private String determineRouteType(int number) {
        if (number >= 1 && number <= 199) return "Городской";
        else if (number >= 200 && number <= 299) return "Смешанный";
        else if (number >= 300 && number <= 399) return "Пригородный";
        else if (number >= 400 && number <= 999) return "Региональный";
        return "Неизвестный";
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRouteNumber() { return routeNumber; }
    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
        this.routeType = determineRouteType(routeNumber);
    }

    public int getStartPointId() { return startPointId; }
    public void setStartPointId(int startPointId) { this.startPointId = startPointId; }

    public String getStartLocality() { return startLocality; }
    public void setStartLocality(String startLocality) { this.startLocality = startLocality; }

    public String getStartDistrict() { return startDistrict; }
    public void setStartDistrict(String startDistrict) { this.startDistrict = startDistrict; }

    public String getStartDescription() { return startDescription; }
    public void setStartDescription(String startDescription) { this.startDescription = startDescription; }

    public int getEndPointId() { return endPointId; }
    public void setEndPointId(int endPointId) { this.endPointId = endPointId; }

    public String getEndLocality() { return endLocality; }
    public void setEndLocality(String endLocality) { this.endLocality = endLocality; }

    public String getEndDistrict() { return endDistrict; }
    public void setEndDistrict(String endDistrict) { this.endDistrict = endDistrict; }

    public String getEndDescription() { return endDescription; }
    public void setEndDescription(String endDescription) { this.endDescription = endDescription; }

    public String[] getSpecialCategories() { return specialCategories; }
    public void setSpecialCategories(String[] specialCategories) { this.specialCategories = specialCategories; }

    public String getSpecialCategoryString() {
        return String.join(",", specialCategories);
    }

    public String getRouteType() { return routeType; }

    // Методы, связанные с UI (для совместимости, но идеально переместить в слой презентации)
    public String getBadgeColor() {
        int num = getRouteNumber();
        if (num >= 1 && num <= 199) return "#B8F1B9"; // City - Green
        else if (num >= 200 && num <= 299) return "#DFE0FF"; // Mixed - Purple
        else if (num >= 300 && num <= 399) return "#F9E287"; // Suburban - Yellow
        return "#E3E9EA"; // Regional - Grey
    }

    public String getTextColor() {
        int num = getRouteNumber();
        if (num >= 1 && num <= 199) return "#1E5127";
        else if (num >= 200 && num <= 299) return "#3B4279";
        else if (num >= 300 && num <= 399) return "#534600";
        return "#161D1D";
    }

    public String getCategoryColor(String category) {
        return su.pank.transport.data.repository.CategoryRepository.getCategoryBgColor(category);
    }

    public String getCategoryTextColor(String category) {
        return su.pank.transport.data.repository.CategoryRepository.getCategoryTextColor(category);
    }
}