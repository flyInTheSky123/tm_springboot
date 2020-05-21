package com.person.tm.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.security.PrivateKey;

@Entity
@Table(name = "user")
@JsonIgnoreProperties({ "handler","hibernateLazyInitializer"})
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    private String password;

    private String name;

    private String salt;

    @Transient  //匿名名称
    private String anonymousName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    //匿名
    public String getAnonymousName() {
        if (anonymousName!=null)
            return  anonymousName;
        if (name==null)
            anonymousName=null;
        else if (name.length()<=1){
            anonymousName="*";
        }else if (name.length()==2){
            anonymousName=name.substring(0,1)+"*";
        }else {
            char[] chars = name.toCharArray();
            for (int i=1;i<chars.length-1;i++){
                chars[i]='*';
            }

            String anonymousName = new String(chars);
        }




        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", salt='" + salt + '\'' +
                ", anonymousName='" + anonymousName + '\'' +
                '}';
    }
}
