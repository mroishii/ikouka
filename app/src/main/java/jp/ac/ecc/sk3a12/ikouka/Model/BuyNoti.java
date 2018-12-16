package jp.ac.ecc.sk3a12.ikouka.Model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class BuyNoti {
    private String userId;
    private String owner;
    private String productId;
    private Timestamp buyDate;

    public BuyNoti() {
    }

    public BuyNoti(String userId, String owner, String productId, Timestamp buyDate) {
        this.userId = userId;
        this.owner = owner;
        this.productId = productId;
        this.buyDate = buyDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Timestamp getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Timestamp buyDate) {
        this.buyDate = buyDate;
    }

    @Override
    public String toString() {
        return "BuyNoti{" +
                "userId='" + userId + '\'' +
                ", owner='" + owner + '\'' +
                ", productId='" + productId + '\'' +
                ", buyDate=" + buyDate +
                '}';
    }
}
