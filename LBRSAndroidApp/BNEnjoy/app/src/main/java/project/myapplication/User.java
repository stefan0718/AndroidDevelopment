package project.myapplication;

import java.util.Date;

public class User {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String gender;
    private String location;
    private Date date_of_birth;
    private Date signup_datetime;
    private Date login_date_time;

    //construct a new user with given fields
    public User(String name, String email, String password, String phone, String gender,
                String location, Date date_of_birth, Date signup_datetime){
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.location = location;
        this.date_of_birth = date_of_birth;
        this.signup_datetime = signup_datetime;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){ this.name = name; }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getGender(){
        return gender;
    }

    public void setGender(String gender){ this.gender = gender; }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public Date getDateOfBirth(){
        return date_of_birth;
    }

    public void setDateOfBirth(Date dob){
        this.date_of_birth = dob;
    }

    public Date getSignupDatetime(){
        return signup_datetime;
    }

    public void setSignupDatetime(Date signup_datetime){
        this.signup_datetime = signup_datetime;
    }

    public Date getLoginDateTime() { return login_date_time; }

    public void setLoginDateTime(Date ldt) { this.login_date_time = ldt; }
}
