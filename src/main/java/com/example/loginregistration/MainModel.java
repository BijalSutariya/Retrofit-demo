package com.example.loginregistration;

import java.util.List;

public class MainModel {


    /**
     * status : 1
     * data : [{"id":170,"name":"Lynn Goh","message":"Loving new Jacket?? ","time":"2018-06-11 15:45:00","created_at":"2018-06-11 21:15:01","updated_at":"2018-06-11 21:15:01","user_id":51,"notifications_id":20},{"id":230,"name":"meeee","message":"hello","time":"2018-06-28 04:45:00","created_at":"2018-06-26 23:15:01","updated_at":"2018-06-26 23:15:01","user_id":51,"notifications_id":26},{"id":244,"name":"meeee","message":"hello","time":"2018-06-28 04:45:00","created_at":"2018-06-28 07:40:02","updated_at":"2018-06-28 07:40:02","user_id":51,"notifications_id":26},{"id":258,"name":"meeee","message":"hello","time":"2018-06-28 04:45:00","created_at":"2018-06-28 08:00:01","updated_at":"2018-06-28 08:00:01","user_id":51,"notifications_id":26},{"id":272,"name":"meeee","message":"hello","time":"2018-06-28 04:45:00","created_at":"2018-06-28 09:50:01","updated_at":"2018-06-28 09:50:01","user_id":51,"notifications_id":26},{"id":286,"name":"meeee","message":"hello","time":"2018-06-28 04:45:00","created_at":"2018-06-28 10:08:01","updated_at":"2018-06-28 10:08:01","user_id":51,"notifications_id":26},{"id":300,"name":"meeee","message":"hello","time":"2018-06-28 04:45:00","created_at":"2018-06-28 10:15:01","updated_at":"2018-06-28 10:15:01","user_id":51,"notifications_id":26}]
     * message : Accident report created successfully.
     */

    private String status;
    private String message;
    private List<DataBean> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 170
         * name : Lynn Goh
         * message : Loving new Jacket??
         * time : 2018-06-11 15:45:00
         * created_at : 2018-06-11 21:15:01
         * updated_at : 2018-06-11 21:15:01
         * user_id : 51
         * notifications_id : 20
         */

        private int id;
        private String name;
        private String message;
        private String time;
        private String created_at;
        private String updated_at;
        private int user_id;
        private int notifications_id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getNotifications_id() {
            return notifications_id;
        }

        public void setNotifications_id(int notifications_id) {
            this.notifications_id = notifications_id;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", message='" + message + '\'' +
                    ", time='" + time + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", user_id=" + user_id +
                    ", notifications_id=" + notifications_id +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MainModel{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
