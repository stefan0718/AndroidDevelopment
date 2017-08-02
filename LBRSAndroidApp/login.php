<?php
$login_email = $_POST['login_email'];
$login_password = $_POST['login_password'];
$db_conn = mysqli_connect("localhost", "root", "wangyifan", "bnenjoy");
if (mysqli_connect_errno($db_conn)) {
    die ('Failed to connect: '.mysqli_connect_error());
}
//search the email and password that match posted data
$query = "select * from users where email = '$login_email' and password = '$login_password'";
$result = mysqli_query($db_conn, $query);
$array = array();
$i = 0;
$row = mysqli_fetch_array($result);
$array[$i]['name'] = $row['name'];
$array[$i]['email'] = $row['email'];
$array[$i]['password'] = $row['password'];
$array[$i]['phone'] = $row['phone'];
$array[$i]['gender'] = $row['gender'];
$array[$i]['date_of_birth'] = $row['date_of_birth'];
$array[$i]['location'] = $row['location'];
$array[$i]['signup_datetime'] = $row['signup_datetime'];
$fail = 'fail';
if ($row['name'] == null){    //that means the query has failed run on mysql
    echo $fail;
}
else{
    echo json_encode($array);    //output it as json array format
}
mysqli_close($db_conn); 
?>