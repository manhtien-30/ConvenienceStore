package com.example.checkattendance.Singleton;

public class ImageResult {
        private String notification1="";
        private Boolean check;
        private static  ImageResult instance;

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    private ImageResult()
        {

        }
        public static ImageResult getInstance()
        {
            if(instance == null)
            {
                instance = new ImageResult();
            }
            return instance;
        }
        public void setNotification1(String notification)
        {
            this.notification1 = notification;
        }
        public String getNotification1()
        {
            return instance.notification1;
        }
}
