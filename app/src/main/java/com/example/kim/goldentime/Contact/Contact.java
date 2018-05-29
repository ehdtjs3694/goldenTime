package com.example.kim.goldentime.Contact;

/**
 * Created by kim on 2017-03-23.
 */

public class Contact {
    String name;
    String tel;

    public Contact(String name, String tel) {
        this.name = name;
        this.tel = tel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

}
