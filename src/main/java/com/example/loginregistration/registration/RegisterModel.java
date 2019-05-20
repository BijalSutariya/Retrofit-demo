package com.example.loginregistration.registration;

public class RegisterModel {

    private String name;
    private String email;
    private String password;
    private String ph_no;
    private String device_token;
    private String device_type;

    public RegisterModel(String name, String email, String password, String contact, String device_token, String device_type) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.ph_no = contact;
        this.device_token = device_token;
        this.device_type = device_type;
    }
  /*  public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContact() {
        return ph_no;
    }

    public void setContact(String contact) {
        this.ph_no = contact;
    }

    public String getDevice_token() {
        return "123456";
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getDevice_type() {
        return "Android";
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    @Override
    public String toString() {
        return "RegisterModel{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", contact='" + ph_no + '\'' +
                ", device_token='" + device_token + '\'' +
                ", device_type='" + device_type + '\'' +
                '}';
    }*/

    public static class Builder{
        private String name;
        private String email;
        private String password;
        private String ph_no;
        private String device_token;
        private String device_type;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setPh_no(String ph_no) {
            this.ph_no = ph_no;
            return this;
        }

        public Builder setDevice_token(String device_token) {
            this.device_token = device_token;
            return this;
        }

        public Builder setDevice_type(String device_type) {
            this.device_type = device_type;
            return this;
        }
        public RegisterModel build(){
            return new RegisterModel(name,email,password,ph_no,device_token,device_type);
        }
    }
}
