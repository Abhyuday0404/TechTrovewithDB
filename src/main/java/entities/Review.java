package main.java.entities;

import java.util.Date;

public class Review {
    private int reviewId;
    private String seller;
    private String reviewer;
    private int rating;
    private String comment;
    private Date reviewDate;

    public Review(int reviewId, String seller, String reviewer, int rating, String comment, Date reviewDate) {
        this.reviewId = reviewId;
        this.seller = seller;
        this.reviewer = reviewer;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    // Getters
    public int getReviewId() {
        return reviewId;
    }

    public String getSeller() {
        return seller;
    }

    public String getReviewer() {
        return reviewer;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

   //toString method for console output
   @Override
   public String toString(){
       return String.format("Review ID: %d, Seller: %s, Reviewer: %s, Rating: %d, Comment: %s, Date: %s",
       reviewId, seller, reviewer, rating, comment, reviewDate);
   }
}