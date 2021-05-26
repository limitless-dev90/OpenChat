<?php

	include 'dbconnect.php';

	$id = $_POST['id'];
	$pw = $_POST['pw'];


	//안드로이드 retrofit2 요청 시 id, pw 값이 "(쌍따옴표) 특수문자와 함께 전달되서 제거가 필요함.
	$id = preg_replace("/[\"\']/i","",$id);
	$pw = preg_replace("/[\"\']/i","",$pw);

	$conn = OpenCon();

  $sql = "SELECT * from user where id = '$id' and pw ='$pw'";

  $result = $conn->query($sql);

	if ($result->num_rows > 0) {

    $rows = $result->fetch_assoc();



		$response = array('idx'=>$rows['idx'],'id'=>$rows['id'],
					'profilePath'=>$rows['profile_path']);

		header('Content-Type: application/json; charset=utf8');
		$json = json_encode($response, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
		echo $json;

	}
	else {
		// code...
		$response = array('id'=>"",
					'profilePath'=>"");

		header('Content-Type: application/json; charset=utf8');
		$json = json_encode($response, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
		echo $json;
	}

	$conn->close();

?>
