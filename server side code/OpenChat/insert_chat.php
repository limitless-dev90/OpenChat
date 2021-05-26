<?php

	include 'dbconnect.php';

	$user_idx = $_POST['user_idx'];
	$room_idx = $_POST['room_idx'];
  $content = $_POST['content'];

  $content = preg_replace("/[\"\']/i","",$content);

	$conn = OpenCon();

  $sql = "INSERT INTO chat_content (user_idx,room_idx,content) values ($user_idx,$room_idx,'$content')";

  $result = $conn->query($sql);

	if ($result > 0) {

    //채팅 등록 성공
    echo true;

	}
	else {
		// 채팅 등록 실패
		echo false;
	}

?>
