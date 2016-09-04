<?php

$servername = "localhost";
$username = "root";
$password = "";

// Create connection
$link = @mysql_connect($servername, $username, $password);
if (!$link) {
    die('Not connected : ' . mysql_error());
}

$db_selected = @mysql_select_db('test', $link);
if (!$db_selected) {
    die ('Can\'t use test : ' . mysql_error());
}
$user = $_GET['userid'];
$res1 = @mysql_query("SELECT * FROM users where userid = '".$user."'", $link);
$num = @mysql_num_rows($res1);
$ctr = -1;
if( $num == 0){
	$res = @mysql_query("SELECT * FROM videos", $link);
	$num_rows = @mysql_num_rows($res);
	$num = $num_rows;
	$str = "INSERT into users values ";
	for($i = 1; $i <= $num_rows ; $i ++ ){
		if($i == 1){
			$str = $str."( '".$user."' , '".$i."')";
		}else{
			$str = $str.",( '".$user."' , '".$i."')";
		}
		
	}
	$res2 = @mysql_query($str, $link);
	//echo "Connected successfully";
	$val = @mt_rand(1,$num);
	$ctr = $val;
}else{
	$val = @mt_rand(0,$num-1);
	$ind = 0;
	$ctr = $val;
	while ($row = @mysql_fetch_array($res1)) {
		if($ind == $val)
			$ctr = $row[1];
		$ind++;
	}
}

$res = @mysql_query("SELECT * FROM videos where id = ".$ctr, $link);
if($row = @mysql_fetch_array($res))
	echo $row[1].",".$row[2].",".$row[3];

$res = @mysql_query("DELETE FROM users WHERE userid = '".$user."' and videoid = '".$ctr."'", $link);

?>