<?php
$login_email = $_POST['login_email'];
$login_password = $_POST['login_password'];
$login_datetime = $_POST['login_datetime'];
$db_conn = mysqli_connect("localhost", "root", "wangyifan", "bnenjoy");
if (mysqli_connect_errno($db_conn)) {
    die ('Failed to connect: '.mysqli_connect_error());
}
$query = "insert into login_record(login_email, login_password, login_datetime) 
          values ('$login_email', '$login_password', '$login_datetime')";
mysqli_query($db_conn, $query);
mysqli_close($db_conn); 
?>