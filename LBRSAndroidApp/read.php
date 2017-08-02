<html>
    <body>
        <div id='a' style="text-align:center">
            <form action=read.php method=post>
                <p>Please input password: </p>
                <input type=password name=password id='password'></input>
                <input type=submit value='submit' onclick='hideDiv()'></input>
            </form>
        </div>
		<script>
            function hideDiv(){
				document.getElementById('a').style.display='none';
				check();
			}
			function check(){
				var name = document.getElementById('password').value;
				if (name == ''){
                    alert('Password cannot be empty! ');
				}
			}
        </script>
    </body>
</html>

<?php
$password = $_POST['password'];
if($password == 'wangyifan'){  
	echo "<script>
            function hideDiv(){
				document.getElementById('a').style.display='none';
			}
			window.onload = hideDiv;
        </script>";    //hide div
	read();
}
else if($password == ''){}
else{
    echo "<script>alert('Access Denied: Incorrect password!');</script>";
}

function read(){
	$db_conn = mysqli_connect("localhost", "root", "wangyifan", "bnenjoy");
    if (mysqli_connect_errno($db_conn)) {
        die ('Failed to connect: '.mysqli_connect_error());
    }
    $result_1 = mysqli_query($db_conn, "select * from users order by signup_datetime ASC");
	$rows_1 = mysqli_num_rows($result_1);    //get the number of rows
    $columns_1 = mysqli_num_fields($result_1);    //get the number of columns
	$s_1 = '<p>Table User: ';
	$s_1.=$rows_1;
	$s_1.=' rows, ';
	$s_1.=$columns_1;
	$s_1.=' columns</p>';
	$output_1 ='<table border="1" cellspacing="0" cellpadding="0">
	<tr><td>name</td><td>email</td><td>password</td><td>phone</td><td>gender</td>
	<td>date_of_birth</td><td>location</td><td>signup_datetime</td></tr>';
    for ($i = 0; $i < mysqli_num_rows($result_1); $i++){
		$row = mysqli_fetch_array($result_1);
		$output_1.='<tr><td>';
		$output_1.=$row['name'];
		$output_1.='</td>';
		$output_1.='<td>';
		$output_1.=$row['email'];
		$output_1.='</td>';
		$output_1.='<td>';
		$output_1.=$row['password'];
		$output_1.='</td>';
		$output_1.='<td>';
		$output_1.=$row['phone'];
		$output_1.='</td>';
		$output_1.='<td>';
		$output_1.=$row['gender'];
		$output_1.='</td>';
		$output_1.='<td>';
		$output_1.=$row['date_of_birth'];
		$output_1.='</td>';
		$output_1.='<td>';
		$output_1.=$row['location'];
		$output_1.='</td>';
		$output_1.='<td>';
		$output_1.=$row['signup_datetime'];
		$output_1.='</td></tr>';
    }
	$output_1.'</table>';
	$result_2 = mysqli_query($db_conn, "select * from login_record order by login_datetime ASC");
	$rows_2 = mysqli_num_rows($result_2);    //get the number of rows
    $columns_2 = mysqli_num_fields($result_2);    //get the number of columns
	$s_2 = '<p>Table Login Record: ';
	$s_2.=$rows_2;
	$s_2.=' rows, ';
	$s_2.=$columns_2;
	$s_2.=' columns</p>';
	$output_2 ='<table border="1" cellspacing="0" cellpadding="0">
	<tr><td>login_email</td><td>login_password</td><td>login_datetime</td></tr>';
	for ($i = 0; $i < mysqli_num_rows($result_2); $i++){
		$row = mysqli_fetch_array($result_2);
		$output_2.='<tr><td>';
		$output_2.=$row['login_email'];
		$output_2.='</td>';
		$output_2.='<td>';
		$output_2.=$row['login_password'];
		$output_2.='</td>';
		$output_2.='<td>';
		$output_2.=$row['login_datetime'];
		$output_2.='</td></tr>';
    }
	$output_2.'</table>';
	$output_1.=$s_1;
	$output_1.=$output_2;
	$output_1.=$s_2;
	echo $output_1;
	mysqli_close($db_conn); 
}
?>