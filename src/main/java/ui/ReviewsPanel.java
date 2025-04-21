package main.java.ui;

import main.java.entities.Review;
import main.java.entities.User;
import main.java.management.ReviewsManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ReviewsPanel extends JPanel {

    private JTextField sellerField;
    private JSpinner ratingSpinner;
    private JTextArea commentArea;
    private JButton addReviewButton, listReviewsButton;
    private JTextArea displayArea;
    private ReviewsManager reviewsManager = new ReviewsManager();

    private User loggedInUser;

    public ReviewsPanel(User loggedInUser) {
        this.loggedInUser = loggedInUser;

        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        inputPanel.add(new JLabel("Seller:"));
        sellerField = new JTextField();
        inputPanel.add(sellerField);

        inputPanel.add(new JLabel("Rating (1-5):"));
        ratingSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        inputPanel.add(ratingSpinner);

        inputPanel.add(new JLabel("Comment:"));
        commentArea = new JTextArea(5, 20);
        JScrollPane commentScrollPane = new JScrollPane(commentArea);
        inputPanel.add(commentScrollPane);

        addReviewButton = new JButton("Add Review");
        inputPanel.add(addReviewButton);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        listReviewsButton = new JButton("List Reviews");
        buttonPanel.add(listReviewsButton);

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane displayScrollPane = new JScrollPane(displayArea);
        add(displayScrollPane, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);


        addReviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seller = sellerField.getText();
                int rating = (Integer) ratingSpinner.getValue();
                String comment = commentArea.getText();

                boolean added = reviewsManager.addReview(seller, loggedInUser.getUsername(), rating, comment);
                if (added) {
                    JOptionPane.showMessageDialog(ReviewsPanel.this, "Review added successfully!");
                    clearInputs(); // Clear the inputs after successful addition
                } else {
                    JOptionPane.showMessageDialog(ReviewsPanel.this, "Failed to add review. Check console for errors.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        listReviewsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seller = sellerField.getText();
                displayReviews(seller);
            }
        });
    }

    private void displayReviews(String seller) {
        List<Review> reviews = reviewsManager.getReviewsForSeller(seller);
        StringBuilder sb = new StringBuilder();

        if (reviews.isEmpty()) {
            sb.append("No reviews found for ").append(seller);
        } else {
            sb.append("--- Reviews for ").append(seller).append(" ---\n");
            for (Review review : reviews) {
                sb.append(review.toString()).append("\n"); // Use toString method of Review class
            }
            sb.append("--- End of Reviews ---");
        }

        displayArea.setText(sb.toString());
    }

    // Method to clear the input fields after adding a review
    private void clearInputs() {
        sellerField.setText("");
        ratingSpinner.setValue(1);
        commentArea.setText("");
    }
}