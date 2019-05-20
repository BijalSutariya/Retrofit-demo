package com.example.loginregistration.login;

public class UserModel {

    /**
     * status : 1
     * data : {"name":"harsh","phone":"1234567890","email":"sadsa@asdsa.com","device_token":"123456","device_type":"android","referral":"123456","updated_at":"2018-07-02 16:07:31","created_at":"2018-07-02 16:07:31","id":155}
     * message : Registered successfully
     */

    private String status;
    private DataBean data;
    private String message;

    public UserModel(String status, DataBean data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        /**
         * name : harsh
         * phone : 1234567890
         * email : sadsa@asdsa.com
         * device_token : 123456
         * device_type : android
         * referral : 123456
         * updated_at : 2018-07-02 16:07:31
         * created_at : 2018-07-02 16:07:31
         * id : 155
         */

        private String name;
        private String ph_no;
        private String email;
        private String device_token;
        private String device_type;
        private String referral;
        private String updated_at;
        private String created_at;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return ph_no;
        }

        public void setPhone(String phone) {
            this.ph_no = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDevice_token() {
            return device_token;
        }

        public void setDevice_token(String device_token) {
            this.device_token = device_token;
        }

        public String getDevice_type() {
            return device_type;
        }

        public void setDevice_type(String device_type) {
            this.device_type = device_type;
        }

        public String getReferral() {
            return referral;
        }

        public void setReferral(String referral) {
            this.referral = referral;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
