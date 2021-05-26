<?php

	include 'dbconnect.php';

	$id = $_POST['id'];
	$pw = $_POST['pw'];
	$profile_image = $_POST['profile_image'];

	$id = preg_replace("/[\"\']/i","",$id);
	$pw = preg_replace("/[\"\']/i","",$pw);
	$profile_image = preg_replace("/[\"\']/i","",$profile_image);

	if(!isset($_POST['profile_image']))
  {
    $target_file = "http://10.0.2.2:8080/OpenChat/picture/default_image.png";
  }
	else {
		$target_file = "http://10.0.2.2:8080/OpenChat/".$profile_image;
	}


	$conn = OpenCon();

	$sql = "SELECT * from user where id = '$id'";

	$userCheck = $conn->query($sql);

	if ($userCheck->num_rows > 0) {

		$response = array('id'=>"",
					'profilePath'=>"");

		header('Content-Type: application/json; charset=utf8');
		$json = json_encode($response, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
		echo $json;

	}
	else {

		//기존 가입자가 없다면 회원가입 진행
		$sql = "INSERT INTO user (id,pw,profile_path) values ('$id','$pw','$target_file')";

	  $result = $conn->query($sql);

		if ($result > 0) {

	    $sql = "SELECT * from user where id = '$id' and pw = '$pw'";

	    $newUser = $conn->query($sql);

	  	if ($newUser->num_rows > 0) {

	      $rows = $newUser->fetch_assoc();

	  		$response = array('idx'=>$rows['idx'], 'id'=>$rows['id'],
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

		}
		else {
			// code...
			$response = array('id'=>"",
						'profilePath'=>"");

			header('Content-Type: application/json; charset=utf8');
			$json = json_encode($response, JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
			echo $json;
		}


	}



?>
