package su.pank.transport.data.repository;

import su.pank.transport.data.models.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryRepository {
    private static final String DB_URL = "jdbc:sqlite:transport_routes.db";
    private static CategoryRepository instance;

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT code, name, bg_color, text_color FROM categories";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(new Category(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("bg_color"),
                         rs.getString("text_color")
                 ));
             }
         } catch (SQLException e) {
             System.err.println("Ошибка получения категорий: " + e.getMessage());
         }
         return categories;
    }

    public Optional<Category> getCategoryByCode(String code) {
        String sql = "SELECT code, name, bg_color, text_color FROM categories WHERE code = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Category(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("bg_color"),
                        rs.getString("text_color")
                 ));
             }
         } catch (SQLException e) {
             System.err.println("Ошибка получения категории по коду: " + e.getMessage());
         }
         return Optional.empty();
    }

    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET name = ?, bg_color = ?, text_color = ? WHERE code = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category.getName());
            pstmt.setString(2, category.getBgColor());
            pstmt.setString(3, category.getTextColor());
            pstmt.setString(4, category.getCode());
             return pstmt.executeUpdate() > 0;
         } catch (SQLException e) {
             System.err.println("Ошибка обновления категории: " + e.getMessage());
             return false;
         }
    }

    public static CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }

    public static String getCategoryBgColor(String code) {
        return getInstance().getCategoryByCode(code)
                .map(Category::getBgColor)
                .orElse("#95A5A6"); // Default grey
    }

    public static String getCategoryTextColor(String code) {
        return getInstance().getCategoryByCode(code)
                .map(Category::getTextColor)
                .orElse("#34495E"); // Default dark grey
    }
}