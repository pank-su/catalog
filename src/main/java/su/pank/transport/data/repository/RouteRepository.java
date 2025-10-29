package su.pank.transport.data.repository;

import su.pank.transport.data.DatabaseConfig;
import su.pank.transport.data.models.Category;
import su.pank.transport.data.models.Route;
import su.pank.transport.domain.RouteLinkedList;

import java.sql.*;

public class RouteRepository {

    public void initialize() {
        createTables();
        initializeDefaultCategories();
    }

    private void createTables() {
        String createCategories = """
                    CREATE TABLE IF NOT EXISTS categories (
                        code VARCHAR(1) PRIMARY KEY,
                        name VARCHAR(50) NOT NULL,
                        bg_color VARCHAR(7) NOT NULL CHECK(LENGTH(bg_color) = 7 AND bg_color LIKE '#%'),
                        text_color VARCHAR(7) NOT NULL CHECK(LENGTH(text_color) = 7 AND text_color LIKE '#%')
                    )
                """;

        String createRouteCategories = """
                    CREATE TABLE IF NOT EXISTS route_categories (
                        route_id INTEGER NOT NULL,
                        category_code VARCHAR(1) NOT NULL,
                        PRIMARY KEY(route_id, category_code),
                        FOREIGN KEY(route_id) REFERENCES routes(id),
                        FOREIGN KEY(category_code) REFERENCES categories(code)
                    )
                """;

        String createRoutes = """
                    CREATE TABLE IF NOT EXISTS routes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        route_number INTEGER NOT NULL CHECK(route_number >= 1 AND route_number <= 999),
                        start_point_id INTEGER NOT NULL,
                        end_point_id INTEGER NOT NULL,
                        FOREIGN KEY(start_point_id) REFERENCES route_points(id),
                        FOREIGN KEY(end_point_id) REFERENCES route_points(id),
                        UNIQUE(route_number)
                    )
                """;

        String dropFullRouteInfoView = "DROP VIEW IF EXISTS full_route_info";

        String createFullRouteInfoView = """
                    CREATE VIEW full_route_info AS
                    SELECT
                        r.id AS route_id,
                        r.route_number,
                        sp.id AS start_point_id,
                        sp.locality AS start_locality,
                        sp.district AS start_district,
                        sp.description AS start_description,
                        ep.id AS end_point_id,
                        ep.locality AS end_locality,
                        ep.district AS end_district,
                        ep.description AS end_description,
                        GROUP_CONCAT(rc.category_code, ',') AS category_codes
                    FROM routes r
                    JOIN route_points sp ON r.start_point_id = sp.id
                    JOIN route_points ep ON r.end_point_id = ep.id
                    LEFT JOIN route_categories rc ON r.id = rc.route_id
                    GROUP BY r.id, r.route_number, sp.id, sp.locality, sp.district, sp.description, ep.id, ep.locality, ep.district, ep.description
                """;

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createCategories);
            stmt.execute(createRoutes);
            stmt.execute(createRouteCategories);
            stmt.execute(dropFullRouteInfoView);
            stmt.execute(createFullRouteInfoView);
         } catch (SQLException e) {
             System.err.println("Ошибка создания таблиц базы данных: " + e.getMessage());
         }
    }

    private void initializeDefaultCategories() {
        String checkSql = "SELECT COUNT(*) FROM categories";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String[][] categories = {
                        {"K", "Коммерческий", "#FF6B6B", "#721C24"},
                        {"С", "Экспресс", "#4ECDC4", "#0E6251"},
                        {"M", "Ночной", "#45B7D1", "#1B4F72"}
                };

                String insertSql = "INSERT INTO categories (code, name, bg_color, text_color) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (String[] cat : categories) {
                        pstmt.setString(1, cat[0]);
                        pstmt.setString(2, cat[1]);
                        pstmt.setString(3, cat[2]);
                        pstmt.setString(4, cat[3]);
                        pstmt.executeUpdate();
                    }
                 }
             }
         } catch (SQLException e) {
             System.err.println("Ошибка инициализации категорий: " + e.getMessage());
         }
    }

    public RouteLinkedList getAllRoutes() {
        RouteLinkedList routes = new RouteLinkedList();
        String sql = "SELECT * FROM full_route_info";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String categoryCodes = rs.getString("category_codes");
                if (categoryCodes == null) categoryCodes = "";
                Route route = new Route(
                        rs.getInt("route_id"),
                        rs.getInt("route_number"),
                        rs.getInt("start_point_id"),
                        rs.getString("start_locality"),
                        rs.getString("start_district"),
                        rs.getString("start_description"),
                        rs.getInt("end_point_id"),
                        rs.getString("end_locality"),
                        rs.getString("end_district"),
                        rs.getString("end_description"),
                        categoryCodes
                );
                 routes.add(route);
             }
         } catch (SQLException e) {
             System.err.println("Ошибка получения маршрутов: " + e.getMessage());
         }
        return routes;
    }

    public boolean addRoute(Route route) {
        String sql = "INSERT INTO routes (route_number, start_point_id, end_point_id) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, route.getRouteNumber());
            pstmt.setInt(2, route.getStartPointId());
            pstmt.setInt(3, route.getEndPointId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {


                    // Добавление специальных категорий к опр. маршруту
                    if (rs.next()) {
                        int routeId = rs.getInt(1);
                        if (route.getSpecialCategories().length > 0) {
                            addRouteCategories(routeId, route.getSpecialCategories());
                        }
                        return true;
                     }
                 }
             }
             return false;
         } catch (SQLException e) {
             System.err.println("Ошибка добавления маршрута: " + e.getMessage());
             return false;
         }
    }

    public boolean updateRoute(Route route) {
        String sql = "UPDATE routes SET route_number = ?, start_point_id = ?, end_point_id = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, route.getRouteNumber());
            pstmt.setInt(2, route.getStartPointId());
            pstmt.setInt(3, route.getEndPointId());
            pstmt.setInt(4, route.getId());
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Update categories
                updateRouteCategories(route.getId(), route.getSpecialCategories());
                return true;
            }
             return false;
         } catch (SQLException e) {
             System.err.println("Ошибка обновления маршрута: " + e.getMessage());
             return false;
         }
    }

    public boolean deleteRoute(int routeId) {
        String sql = "DELETE FROM routes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, routeId);
             pstmt.executeUpdate();
             return true;
         } catch (SQLException e) {
             System.err.println("Ошибка удаления маршрута: " + e.getMessage());
             return false;
         }
    }

    public Category[] getAllCategories() {
        String countSql = "SELECT COUNT(*) FROM categories";
        int count = 0;
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
         } catch (SQLException e) {
             System.err.println("Ошибка подсчета категорий: " + e.getMessage());
             return new Category[0];
         }

        Category[] categories = new Category[count];
        String sql = "SELECT code, name, bg_color, text_color FROM categories";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int index = 0;
            while (rs.next() && index < count) {
                categories[index++] = new Category(
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("bg_color"),
                        rs.getString("text_color")
                 );
             }
         } catch (SQLException e) {
             System.err.println("Ошибка получения категорий: " + e.getMessage());
         }
        return categories;
    }

    public boolean addRouteCategories(int routeId, String[] categoryCodes) {
        String sql = "INSERT INTO route_categories (route_id, category_code) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String code : categoryCodes) {
                pstmt.setInt(1, routeId);
                pstmt.setString(2, code);
                pstmt.executeUpdate();
            }
             return true;
         } catch (SQLException e) {
             System.err.println("Ошибка добавления категорий маршрута: " + e.getMessage());
             return false;
         }
    }

    public boolean updateRouteCategories(int routeId, String[] categoryCodes) {
        // Удаление из смежной
        String deleteSql = "DELETE FROM route_categories WHERE route_id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, routeId);
            deleteStmt.executeUpdate();
         } catch (SQLException e) {
             System.err.println("Ошибка обновления категорий маршрута: " + e.getMessage());
             return false;
         }

        // Добавление в смежную
        return addRouteCategories(routeId, categoryCodes);
    }


}