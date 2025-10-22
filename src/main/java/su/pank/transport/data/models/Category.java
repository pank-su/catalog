package su.pank.transport.data.models;

public class Category {
    private String code;
    private String name;
    private String bgColor;
    private String textColor;

    public Category(String code, String name, String bgColor, String textColor) {
        if (!isValidHexColor(bgColor)) {
            throw new IllegalArgumentException("Invalid background color format: " + bgColor);
        }
        if (!isValidHexColor(textColor)) {
            throw new IllegalArgumentException("Invalid text color format: " + textColor);
        }
        this.code = code;
        this.name = name;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    private boolean isValidHexColor(String color) {
        return color != null && color.matches("^#[0-9A-Fa-f]{6}$");
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBgColor() { return bgColor; }
    public void setBgColor(String bgColor) { this.bgColor = bgColor; }

    public String getTextColor() { return textColor; }
    public void setTextColor(String textColor) { this.textColor = textColor; }
}