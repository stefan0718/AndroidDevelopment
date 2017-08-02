<?php
$signup_name = $_POST['signup_name'];
$signup_email = $_POST['signup_email'];
$signup_password = $_POST['signup_password'];
$signup_datetime = $_POST['signup_datetime'];
$db_conn = mysqli_connect("localhost", "root", "wangyifan", "bnenjoy");
if (mysqli_connect_errno($db_conn)) {
    die ('Failed to connect: '.mysqli_connect_error());
}
$query = "select * from users where email = '$signup_email'";
$result = mysqli_query($db_conn, $query);
$fail = 'fail';

if (mysqli_num_rows($result) != 0){    //check if new email already existed
    echo $fail;
}
else{    //if new email not existed
    $query_2 = "insert into users(name, email, password, signup_datetime) 
          values ('$signup_name', '$signup_email', '$signup_password', '$signup_datetime')";   
    mysqli_query($db_conn, $query_2);     //insert to database
    $result_2 = mysqli_query($db_conn, $query);    //read new data
    $array = array();
    $i = 0;
    $row = mysqli_fetch_array($result_2);
    $array[$i]['name'] = $row['name'];
    $array[$i]['email'] = $row['email'];
    $array[$i]['password'] = $row['password'];
    $array[$i]['phone'] = $row['phone'];
    $array[$i]['gender'] = $row['gender'];
    $array[$i]['date_of_birth'] = $row['date_of_birth'];
    $array[$i]['location'] = $row['location'];
    $array[$i]['signup_datetime'] = $row['signup_datetime'];
    echo json_encode($array);    //output it as json array format
}
mysqli_close($db_conn); 
?>