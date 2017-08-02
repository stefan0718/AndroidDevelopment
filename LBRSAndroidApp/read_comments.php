<?php
$place_id = $_POST['place_id'];
$db_conn = mysqli_connect("localhost", "root", "wangyifan", "bnenjoy");
if (mysqli_connect_errno($db_conn)) {
    die ('Failed to connect: '.mysqli_connect_error());
}
$query = "select comment from comments where place_id = '$place_id'";
$result = mysqli_query($db_conn, $query);
$array = array();
    $i = 0;
    $row = mysqli_fetch_array($result);
    $array[$i]['comment'] = $row['comment'];
    echo json_encode($array);    //output it as json array format
?>