import java.sql.*;

public class ManagerManagement {
    public void register(Manager m) {
        if (m == null) return;
        if (DBConnection.isAvailable()) {
            String q = "INSERT INTO managers(manager_id,username,password) VALUES(?,?,?)";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
                p.setInt(1, m.getId());
                p.setString(2, m.getUsername());
                p.setString(3, m.getPassword());
                p.executeUpdate();
                System.out.println("Manager Registered Successfully!");
                return;
            } catch (Exception e) {
                System.out.println("Note: DB write failed: " + e.getMessage());
            }
        }
        System.out.println("Manager registered in local store.");
    }

    public Manager login(String u, String pw) {
        if (u == null || pw == null) return null;
        if (DBConnection.isAvailable()) {
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM managers WHERE username=? AND password=?")) {
                p.setString(1, u);
                p.setString(2, pw);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) return new Manager(r.getInt("manager_id"), r.getString("username"), r.getString("password"));
                }
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().loginManager(u, pw);
    }
}
