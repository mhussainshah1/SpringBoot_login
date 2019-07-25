package com.example.demo.test;
/**
 * Password cant be from invalid-password list
 * Password cant be dictionary word
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class Validator implements Serializable {

    private List<String> passwords;

    @Autowired
    public Validator() {
        passwords = new ArrayList<>();
        Collections.addAll(passwords, "a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
    }

    public boolean validatePassword(String password) {
        boolean valid = true;
        for (String p : passwords) {
            if (p.equals(password)) {
                valid = false;
                System.out.println("password found " + p);
                break;
            }
        }
        return valid;
    }

    public void addPassword(String password) {
        passwords.add(password);
    }

    public List<String> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<String> passwords) {
        this.passwords = passwords;
    }
}
