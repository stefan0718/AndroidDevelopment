<?php
$place_id = $_POST['place_id'];
$comment = $_POST['comment'];
$db_conn = mysqli_connect("localhost", "root", "wangyifan", "bnenjoy");
if (mysqli_connect_errno($db_conn)) {
    die ('Failed to connect: '.mysqli_connect_error());
}
$query = "select comment from comments where place_id = '$place_id'";
$result = mysqli_query($db_conn, $query);
//post comments to database
$query_1 = "insert into comments(place_id, comment) values ('$place_id', '$comment')";
$query_2 = "update comments set comment = concat(comment, '$comment') where place_id = '$place_id'";
if (mysqli_num_rows($result) != 0){    //check if id already existed
    mysqli_query($db_conn, $query_2);     //update database
}
else{  //if id doe not exist
    mysqli_query($db_conn, $query_1);     //insert to database
}
mysqli_close($db_conn); 
?>