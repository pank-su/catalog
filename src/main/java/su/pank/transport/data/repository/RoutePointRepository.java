package su.pank.transport.data.repository;

import su.pank.transport.data.DatabaseConfig;
import su.pank.transport.data.models.RoutePoint;
import su.pank.transport.domain.SimpleLinkedList;
import su.pank.transport.domain.LinkedList;

import java.sql.*;

public class RoutePointRepository {

    public void initialize() {
        createTables();
        initializeDefaultDepots();
    }

    private void createTables() {
        String createRoutePoints = """
            CREATE TABLE IF NOT EXISTS route_points (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                locality VARCHAR(100) NOT NULL,
                district VARCHAR(200) NOT NULL,
                description TEXT NOT NULL
            )
        """;

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
              Statement stmt = conn.createStatement()) {
             stmt.execute(createRoutePoints);
         } catch (SQLException e) {
             System.err.println("Ошибка создания таблицы точек маршрута: " + e.getMessage());
         }
    }

    private void initializeDefaultDepots() {
        String checkSql = "SELECT COUNT(*) FROM route_points";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                String[][] depots = {
                        {"Санкт-Петербург", "Фрунзенский район", "Автобусный парк № 1"},
                        {"Санкт-Петербург", "Приморский район", "Автобусный парк № 2"},
                        {"Санкт-Петербург", "Невский район", "Автобусный парк № 3"},
                        {"Санкт-Петербург", "Кировский район", "Автобусный парк № 5"},
                        {"Санкт-Петербург", "Красногвардейский район", "Автобусный парк № 6"},
                        {"Санкт-Петербург", "Московский район", "Автобусный парк № 7"},
                        {"Колпино", "Колпинский район", "Колпинский автобусный парк"}
                };

                String insertSql = "INSERT INTO route_points (locality, district, description) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (String[] depot : depots) {
                        pstmt.setString(1, depot[0]);
                        pstmt.setString(2, depot[1]);
                        pstmt.setString(3, depot[2]);
                        pstmt.executeUpdate();
                    }
                 }
             }
         } catch (SQLException e) {
             System.err.println("Ошибка инициализации депо: " + e.getMessage());
         }
    }

    public RoutePoint[] getAllRoutePoints() {
        String countSql = "SELECT COUNT(*) FROM route_points";
        int count = 0;
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
         } catch (SQLException e) {
             System.err.println("Ошибка подсчета точек маршрута: " + e.getMessage());
             return new RoutePoint[0];
         }

        RoutePoint[] points = new RoutePoint[count];
        String sql = "SELECT id, locality, district, description FROM route_points";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int index = 0;
            while (rs.next() && index < count) {
                points[index++] = new RoutePoint(
                        rs.getInt("id"),
                        rs.getString("locality"),
                        rs.getString("district"),
                        rs.getString("description")
                 );
             }
         } catch (SQLException e) {
             System.err.println("Ошибка получения точек маршрута: " + e.getMessage());
         }
        return points;
    }

    public boolean addRoutePoint(RoutePoint point) {
        String sql = "INSERT INTO route_points (locality, district, description) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, point.getLocality());
             pstmt.setString(2, point.getDistrict());
             pstmt.setString(3, point.getDescription());
             pstmt.executeUpdate();
             return true;
         } catch (SQLException e) {
             System.err.println("Ошибка добавления точки маршрута: " + e.getMessage());
             return false;
         }
    }

    public boolean deleteRoutePoint(int pointId) {
        String sql = "DELETE FROM route_points WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL);
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setInt(1, pointId);
             pstmt.executeUpdate();
             return true;
         } catch (SQLException e) {
             System.err.println("Ошибка удаления точки маршрута: " + e.getMessage());
             return false;
         }
    }

    public void close() {
        // SQLite не требует явного закрытия базы данных
    }
}