package main.java.management;

import main.java.entities.Review;
import main.java.storage.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewsManager {

    public boolean addReview(String seller, String reviewer, int rating, String comment) {
        if (!DatabaseConnection.userExists(seller)) {
            System.err.println("Error: Seller '" + seller + "' does not exist.");
            return false;
        }
        if (!DatabaseConnection.userExists(reviewer)) {
            System.err.println("Error: Reviewer '" + reviewer + "' does not exist.");
            return false;
        }

        String sql = "INSERT INTO reviews (seller, reviewer, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seller);
            stmt.setString(2, reviewer);
            stmt.setInt(3, rating);
            stmt.setString(4, comment);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;  // Simplified success check

        } catch (SQLException e) {
            System.err.println("Error adding review: " + e.getMessage());
            return false;
        }
    }

    public List<Review> getReviewsForSeller(String seller) {
        List<Review> reviews = new ArrayList<Review>(); // Diamond operator removed
        String sql = "SELECT reviewId, seller, reviewer, rating, comment, reviewDate FROM reviews WHERE seller = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, seller);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Review review = new Review(
                    rs.getInt("reviewId"),
                    rs.getString("seller"),
                    rs.getString("reviewer"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("reviewDate") != null ? new java.util.Date(rs.getTimestamp("reviewDate").getTime()) : null
                );
                reviews.add(review);
            }
            return reviews;  // Return inside try block after successful execution

        } catch (SQLException e) {
            System.err.println("Error getting reviews: " + e.getMessage());
            return reviews;  // Return empty list in case of error
        }
    }
}