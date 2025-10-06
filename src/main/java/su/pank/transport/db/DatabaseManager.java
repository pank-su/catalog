package su.pank.transport.db;

import su.pank.transport.model.Route;
import su.pank.transport.model.RouteLinkedList;
import su.pank.transport.model.RoutePoint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:transport_routes.db";

    public void initialize() {
        //createTables();
        //initializeDefaultDepots();
    }

    private void createTables() {
        String dropRoutes = "DROP TABLE IF EXISTS routes";
        String dropRoutePoints = "DROP TABLE IF EXISTS route_points";

        String createRoutePoints = """
            CREATE TABLE route_points (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                locality VARCHAR(100) NOT NULL,
                district VARCHAR(200) NOT NULL,
                description TEXT NOT NULL
            )
        """;

         String createRoutes = """
             CREATE TABLE routes (
                 id INTEGER PRIMARY KEY AUTOINCREMENT,
                 route_number INTEGER NOT NULL CHECK(route_number >= 1 AND route_number <= 999),
                 start_point_id INTEGER NOT NULL,
                 end_point_id INTEGER NOT NULL,
                 special_category VARCHAR(255),
                 FOREIGN KEY(start_point_id) REFERENCES route_points(id),
                 FOREIGN KEY(end_point_id) REFERENCES route_points(id),
                 UNIQUE(route_number)
             )
         """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(dropRoutes);
            stmt.execute(dropRoutePoints);
            stmt.execute(createRoutePoints);
            stmt.execute(createRoutes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeDefaultDepots() {
        String checkSql = "SELECT COUNT(*) FROM route_points";
        try (Connection conn = DriverManager.getConnection(DB_URL);
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
            e.printStackTrace();
        }
    }

    public List<RoutePoint> getAllRoutePoints() {
        List<RoutePoint> points = new ArrayList<>();
        String sql = "SELECT id, locality, district, description FROM route_points";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                points.add(new RoutePoint(
                        rs.getInt("id"),
                        rs.getString("locality"),
                        rs.getString("district"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return points;
    }

    public RouteLinkedList getAllRoutes() {
        RouteLinkedList routes = new RouteLinkedList();
        String sql = """
            SELECT r.id, r.route_number, r.special_category,
                   sp.id as sp_id, sp.locality as sp_locality, sp.district as sp_district, sp.description as sp_desc,
                   ep.id as ep_id, ep.locality as ep_locality, ep.district as ep_district, ep.description as ep_desc
            FROM routes r
            JOIN route_points sp ON r.start_point_id = sp.id
            JOIN route_points ep ON r.end_point_id = ep.id
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RoutePoint startPoint = new RoutePoint(
                        rs.getInt("sp_id"),
                        rs.getString("sp_locality"),
                        rs.getString("sp_district"),
                        rs.getString("sp_desc")
                );
                RoutePoint endPoint = new RoutePoint(
                        rs.getInt("ep_id"),
                        rs.getString("ep_locality"),
                        rs.getString("ep_district"),
                        rs.getString("ep_desc")
                );
                routes.add(new Route(
                        rs.getInt("id"),
                        rs.getInt("route_number"),
                        startPoint,
                        endPoint,
                        rs.getString("special_category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public boolean addRoute(Route route) {
        String sql = "INSERT INTO routes (route_number, start_point_id, end_point_id, special_category) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, route.getRouteNumber());
            pstmt.setInt(2, route.getStartPoint().getId());
            pstmt.setInt(3, route.getEndPoint().getId());
            String cat = route.getSpecialCategoryString();
            if (cat == null || cat.isEmpty()) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, cat);
            }
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRoute(Route route) {
        String sql = "UPDATE routes SET route_number = ?, start_point_id = ?, end_point_id = ?, special_category = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, route.getRouteNumber());
            pstmt.setInt(2, route.getStartPoint().getId());
            pstmt.setInt(3, route.getEndPoint().getId());
            String cat = route.getSpecialCategoryString();
            if (cat == null || cat.isEmpty()) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, cat);
            }
            pstmt.setInt(5, route.getId());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoute(int routeId) {
        String sql = "DELETE FROM routes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, routeId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addRoutePoint(RoutePoint point) {
        String sql = "INSERT INTO route_points (locality, district, description) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, point.getLocality());
            pstmt.setString(2, point.getDistrict());
            pstmt.setString(3, point.getDescription());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoutePoint(int pointId) {
        String sql = "DELETE FROM route_points WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, pointId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        // SQLite doesn't need explicit close for the database
    }
}