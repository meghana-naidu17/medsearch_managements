import java.sql.*;

public class AdminManagement {
    public void register(Admin a) {
        if (a == null) return;
        if (DBConnection.isAvailable()) {
            String q = "INSERT INTO admins(admin_id,username,password) VALUES(?,?,?)";
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
                p.setInt(1, a.getId());
                p.setString(2, a.getUsername());
                p.setString(3, a.getPassword());
                p.executeUpdate();
                System.out.println("Admin Registered Successfully!");
                return;
            } catch (Exception e) {
                System.out.println("Note: DB write failed: " + e.getMessage());
            }
        }
        System.out.println("Admin registered in local store.");
    }

    public Admin login(String u, String pw) {
        if (u == null || pw == null) return null;
        if (DBConnection.isAvailable()) {
            try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM admins WHERE username=? AND password=?")) {
                p.setString(1, u);
                p.setString(2, pw);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) return new Admin(r.getInt("admin_id"), r.getString("username"), r.getString("password"));
                }
            } catch (Exception ignored) {}
        }
        return DatasetStore.getInstance().loginAdmin(u, pw);
    }
}
