package com.example.quanlytaichinh.DataBase;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quanlytaichinh.Activity.SignInActivity;
import com.google.common.reflect.TypeToken;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DTBase {

    private final DatabaseReference mDatabase;

    public DTBase() {
        // Khởi tạo tham chiếu đến Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public static DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference(); // Lấy đối tượng tham chiếu đến cơ sở dữ liệu gốc
    }

    // Thêm người dùng mới vào Firebase
    public void addNewUser(int newUserID, String username, String email) {


        // Tạo đối tượng User mới
        User user = new User(newUserID, username, email, "Male", " ", " ", 0);

        // Lưu thông tin người dùng vào Firebase
        mDatabase.child("USERS").child(String.valueOf(newUserID)).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("User added successfully");
                        // Cập nhật ID trong database
                        incrementNewUserID(new IncrementCallback() {
                            @Override
                            public void onSuccess() {
                                System.out.println("NewUserID incremented successfully");
                            }

                            @Override
                            public void onError(String errorMessage) {
                                System.err.println("Error incrementing NewUserID: " + errorMessage);
                            }
                        });
                    } else {
                        System.err.println("Error saving user: " + task.getException().getMessage());
                    }
                });
    }

    //Kiểm tra xem email người dùng và mật khẩu có khớp không
    public void CheckSignIn(String userName, SignInCallback callback) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("USERS");
        ref.orderByChild("userMail").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null ) {
                            // Kiểm tra dữ liệu tài chính liên quan đến userID
                            fetchFinancialData(user.getUserID(), new FinancialCallback() {
                                @Override
                                public void onFinancialDataFetched(List<Financial> financialList) {
                                    // Trả về cả user và danh sách financials
                                    callback.onSignInResult(true, user, financialList);
                                }
                                @Override
                                public void onError(String errorMessage) {

                                }
                            });
                            return;
                        }
                    }
                }
                callback.onSignInResult(false, null, null); // Sai tài khoản hoặc mật khẩu
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onSignInResult(false, null, null); // Lỗi kết nối
            }
        });
    }

    public interface SignInCallback {

        void onSignInResult(boolean success, User user, List<Financial> financialList);
    }

    // Hàm băm mật khẩu để mã hóa
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Kiểm tra sự tồn tại của tên người dùng
    public void isUserNameExists(String username, FirebaseCallback<Boolean> callback) {
        DatabaseReference ref = mDatabase.child("USERS");
        ref.orderByChild("userName").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCallback(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
    // Kiểm tra sự tồn tại của email người dùng
    public void isUserEmailExists(String username, FirebaseCallback<Boolean> callback) {
        DatabaseReference ref = mDatabase.child("USERS");
        ref.orderByChild("userEmail").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCallback(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Lấy NewUserID hiện tại
    public void getNewUserID(FirebaseCallback<Integer> callback) {
        mDatabase.child("NewUserID").child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer newUserId = snapshot.getValue(Integer.class);
                    callback.onCallback(newUserId);  // Trả về ID mới
                } else {
                    // Nếu không tồn tại, khởi tạo giá trị ban đầu cho NewUserID
                    mDatabase.child("NewUserID").child("id").setValue(1)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    callback.onCallback(1);  // Trả về giá trị ban đầu
                                } else {
                                    callback.onError("Failed to initialize NewUserID");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Tăng giá trị NewUserID
    public void incrementNewUserID(IncrementCallback callback) {
        DatabaseReference ref = mDatabase.child("NewUserID").child("id");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        int currentNewUserID = snapshot.getValue(Integer.class);
                        int updatedNewUserID = currentNewUserID + 1;

                        // Cập nhật giá trị NewUserID
                        ref.setValue(updatedNewUserID).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                callback.onSuccess();
                            } else {
                                callback.onError("Failed to update NewUserID");
                            }
                        });
                    } catch (Exception e) {
                        callback.onError("Error parsing NewUserID: " + e.getMessage());
                    }
                } else {
                    callback.onError("NewUserID does not exist in the database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Lấy NewFinancialID hiện tại
    public void getNewFinancialID(FirebaseCallback<Integer> callback) {
        mDatabase.child("NewFinancialID").child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer newFinancialId = snapshot.getValue(Integer.class);
                    callback.onCallback(newFinancialId);  // Trả về ID mới
                } else {
                    // Nếu không tồn tại, khởi tạo giá trị ban đầu cho NewUserID
                    mDatabase.child("NewFinancialID").child("id").setValue(1)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    callback.onCallback(1);  // Trả về giá trị ban đầu
                                } else {
                                    callback.onError("Failed to initialize NewUserID");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    // Tăng giá trị NewFinancialID
    public void incrementNewFinancialID(IncrementCallback callback) {
        DatabaseReference ref = mDatabase.child("NewFinancialID").child("id");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        int currentNewFinancialID = snapshot.getValue(Integer.class);
                        int updatedNewFinancialID = currentNewFinancialID + 1;

                        // Cập nhật giá trị NewUserID
                        ref.setValue(updatedNewFinancialID).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                callback.onSuccess();
                            } else {
                                callback.onError("Failed to update NewUserID");
                            }
                        });
                    } catch (Exception e) {
                        callback.onError("Error parsing NewUserID: " + e.getMessage());
                    }
                } else {
                    callback.onError("NewUserID does not exist in the database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }



    // Interface cho callback bất đồng bộ
    public interface FirebaseCallback<T> {
        void onCallback(T result);
        void onError(String error); // Thêm phương thức onError
    }

    public interface IncrementCallback {
        void onSuccess();

        void onError(String errorMessage);
    }

    public void addFinancialForUser(Financial financial, int userId) {
        // Lấy ID tài chính mới từ Firebase
        getNewFinancialID(new FirebaseCallback<Integer>() {
            @Override
            public void onCallback(Integer financialID) {
                // Thêm tài chính vào Firebase dưới nhánh FINANCIALS -> userId -> financialId
                mDatabase.child("FINANCIALS").child(String.valueOf(userId))
                        .child(String.valueOf(financialID)) // Thêm tài chính vào nhánh của userId và financialId
                        .setValue(financial)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                System.out.println("Financial added successfully for user: " + userId);

                                // Gọi phương thức để tăng giá trị NewFinancialID
                                incrementNewFinancialID(new IncrementCallback() {
                                    @Override
                                    public void onSuccess() {
                                        System.out.println("NewFinancialID incremented successfully");
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        System.out.println("Failed to increment NewFinancialID: " + errorMessage);
                                    }
                                });

                                // Thực hiện truy vấn lại dữ liệu sau khi cập nhật
                                fetchFinancialData(userId, new FinancialCallback() {
                                    @Override
                                    public void onFinancialDataFetched(List<Financial> financialList) {
                                        // Xử lý dữ liệu đã lấy được sau khi cập nhật
                                        System.out.println("Fetched financial data: " + financialList);
                                    }
                                    @Override
                                    public void onError(String errorMessage) {
                                        System.out.println("Failed to fetch financial data: " + errorMessage);
                                    }
                                });

                            } else {
                                System.out.println("Error adding financial: " + task.getException().getMessage());
                            }
                        });
            }

            @Override
            public void onError(String error) {
                System.out.println("Failed to get new financial ID: " + error);
            }
        });
    }


    public void fetchFinancialData(int userID, FinancialCallback callback) {
        // Lấy danh sách financial từ nhánh FINANCIALS -> userId
        DatabaseReference financialRef = mDatabase.child("FINANCIALS").child(String.valueOf(userID));
        financialRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Financial> financialList = new ArrayList<>();
                for (DataSnapshot financialSnapshot : snapshot.getChildren()) {
                    Financial financial = financialSnapshot.getValue(Financial.class);
                    if (financial != null) {
                        financialList.add(financial);
                    }
                }
                callback.onFinancialDataFetched(financialList); // Trả về danh sách financial đã lấy được
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFinancialDataFetched(null); // Xử lý khi có lỗi
            }
        });
    }

    // Interface callback cho việc lấy dữ liệu financial
    public interface FinancialCallback {
        void onFinancialDataFetched(List<Financial> financialList);
        void onError(String errorMessage);
    }



    public void addNewCategory(Category category, int userId, int categoryId) {
        // Lưu category dưới nhánh CATEGORIES -> userId -> categoryId
        mDatabase.child("CATEGORIES").child(String.valueOf(userId))
                .child(String.valueOf(categoryId)) // Thêm category vào nhánh của userId và categoryId
                .setValue(category)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        System.out.println("Category added successfully for user: " + userId);

                        // Sau khi thêm thành công, gọi phương thức fetch để lấy lại danh sách category của người dùng
                        fetchCategoryData(userId, new CategoryCallback() {
                            @Override
                            public void onCategoryDataFetched(List<Category> categoryList) {
                                // Xử lý dữ liệu đã lấy được sau khi cập nhật
                                System.out.println("Fetched category data: " + categoryList);
                            }
                        });
                    } else {
                        System.out.println("Error adding category: " + task.getException().getMessage());
                    }
                });
    }

    public void fetchCategoryData(int userID, CategoryCallback callback) {
        // Lấy danh sách category từ nhánh CATEGORIES -> userId
        DatabaseReference categoryRef = mDatabase.child("CATEGORIES").child(String.valueOf(userID));
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                callback.onCategoryDataFetched(categoryList); // Trả về danh sách category đã lấy được
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCategoryDataFetched(null); // Xử lý khi có lỗi
            }
        });
    }
    public interface CategoryCallback {
        void onCategoryDataFetched(List<Category> categoryList);
    }
    public void addListCategorytoFirebase(List<Category> categoryList, int userId) {
        for (Category category : categoryList) {
            addNewCategory(category, userId, category.getCategoryID());
        }
    }
    public void updateFinancial(Financial financial) {
        // Kiểm tra đối tượng financial có hợp lệ không
        if (financial == null || financial.getUserID() <= 0 || financial.getFinancialID() <= 0) {
            System.out.println("Invalid financial data. Update aborted.");
            return;
        }

        // Cập nhật mục tài chính trong Firebase
        mDatabase.child("FINANCIALS")
                .child(String.valueOf(financial.getUserID()))
                .child(String.valueOf(financial.getFinancialID()))
                .setValue(financial)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thành công
                        System.out.println("Financial updated successfully for UserID: " + financial.getUserID());
                    } else {
                        // Thất bại
                        System.err.println("Error updating financial: " + (task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error"));
                    }
                });
    }

    public void deleteFinancial(int userID, int financialID) {
        // Kiểm tra các tham số đầu vào có hợp lệ hay không
        if (userID <= 0 || financialID <= 0) {
            System.out.println("Invalid userID or financialID. Deletion aborted.");
            return;
        }

        // Xóa mục tài chính khỏi Firebase
        mDatabase.child("FINANCIALS")
                .child(String.valueOf(userID))
                .child(String.valueOf(financialID))
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Thành công
                        System.out.println("Financial deleted successfully for UserID: " + userID + ", FinancialID: " + financialID);
                    } else {
                        // Thất bại
                        System.err.println("Error deleting financial: " + (task.getException() != null
                                ? task.getException().getMessage()
                                : "Unknown error"));
                    }
                });
    }



    //User
    public static class User implements Serializable {
        private int userID;
        private String userMail;
        private String userName;
        private String userGender;
        private String userBirthday; // Sử dụng String thay vì Date
        private String userAddress;
        private int userAvatar;

        public User() {
            // Constructor mặc định cần thiết cho Firebase
        }

        public User(int userID, String userName, String userMail, String userGender, String userBirthday, String userAddress, int userAvatar) {
            this.userID = userID;
            this.userMail = userMail;
            this.userName = userName;
            this.userGender = userGender;
            this.userBirthday = userBirthday;
            this.userAddress = userAddress;
            this.userAvatar = userAvatar;
        }

        // Getter và Setter cho tất cả các thuộc tính
        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public String getUserMail() {
            return userMail;
        }

        public void setUserMail(String userMail) {
            this.userMail = userMail;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserGender() {
            return userGender;
        }

        public void setUserGender(String userGender) {
            this.userGender = userGender;
        }

        public String getUserBirthday() {
            return userBirthday;
        }

        public void setUserBirthday(String userBirthday) {
            this.userBirthday = userBirthday;
        }

        public String getUserAddress() {
            return userAddress;
        }

        public void setUserAddress(String userAddress) {
            this.userAddress = userAddress;
        }

        public int getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(int userAvatar) {
            this.userAvatar = userAvatar;
        }
    }
    //Financial
    public static class Financial {
        private int financialID;
        private int categoryID;
        private String financialName;
        private String financialType;
        private double financialAmount;
        private String financialDate;
        private int userID;

        // Constructor
        public Financial() {
            Date date = new Date();
            // Tạo một đối tượng SimpleDateFormat với định dạng ngày tháng bạn muốn
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Định dạng: năm-tháng-ngày

            // Chuyển đổi Date thành String
            financialDate = sdf.format(date);
        }

        public Financial(int financialID, int categoryID, String financialName, String financialType, double financialAmount, String financialDate, int userID){
            this.financialID = financialID;
            this.categoryID = categoryID;
            this.financialName = financialName;
            this.financialType = financialType;
            this.financialAmount = financialAmount;
            this.financialDate = financialDate;
            this.userID = userID;

        }
        // Getter and Setter for financialID
        public int getFinancialID() {
            return financialID;
        }
        public void setFinancialID(int financialID) {
            this.financialID = financialID;
        }

        // Getter and Setter for categoryID
        public int getCategoryID(){
            return categoryID;
        }
        public void setCategoryID(int categoryID){
            this.categoryID = categoryID;
        }

        public String getFinancialName(){
            return financialName;
        }
        public String getFinancialType(){
            return financialType;
        }
        public double getFinancialAmount(){
            return financialAmount;
        }
        public String getFinancialDate(){
            return financialDate;
        }
        public int getUserID() {
            return userID;
        }
        public void setUserID(int userID) {
            this.userID = userID;
        }
        public void setFinancialName(String financialName){
            this.financialName = financialName;
        }
        public void setFinancialType(String financialType){
            this.financialType = financialType;
        }
        public void setFinancialAmount(double financialAmount) {
            this.financialAmount = financialAmount;
        }
        public void setFinancialDate(String financialDate) {
            this.financialDate = financialDate;
        }


    }
    //Category
    public static class Category {
        private int categoryID;
        private int categoryIcon;
        private String categoryName;
        private String categoryType;
        private int userID;

        public Category(){}

        public Category(int categoryID, int categoryIcon, String categoryName, String categoryType, int userID) {
            this.categoryID = categoryID;
            this.categoryName = categoryName;
            this.categoryType = categoryType;
            this.categoryIcon = categoryIcon;
            this.userID = userID;
        }
        public int getCategoryID(){
            return categoryID;
        }
        public String getCategoryName(){
            return categoryName;
        }
        public String getCategoryType(){
            return categoryType;
        }
        public int getCategoryIcon(){
            return categoryIcon;
        }
        public int getUserID(){
            return userID;
        }

        public void setCategoryID(){
            this.categoryID = categoryID;
        }
        public void setCategoryName(){
            this.categoryName = categoryName;
        }
        public void setCategoryType(){
            this.categoryType = categoryType;
        }
        public void setCategoryIcon() {
            this.categoryIcon = categoryIcon;
        }
        public void setUserID() {
            this.userID = userID;
        }

    }


}
