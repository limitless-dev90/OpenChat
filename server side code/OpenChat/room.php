<?php

	include 'dbconnect.php';

	$conn = OpenCon();

  $sql = "SELECT r.idx, r.name, COUNT(DISTINCT cc.user_idx) AS count, r.room_img_path  FROM room AS r
						INNER JOIN chat_content AS cc ON r.idx = cc.room_idx
						GROUP	BY	r.idx";

  $result = $conn->query($sql);

	if ($result->num_rows > 0) {

    $response = array();

    while($roomRows = mysqli_fetch_assoc($result))
    {
      array_push($response,
               array('roomIdx'=>(int)$roomRows['idx'],
                     'roomName'=>$roomRows['name'],
                     'roomCount'=>(int)$roomRows['count'],
                     'roomImgPath'=>$roomRows['room_img_path']
               ));
    }

		header('Content-Type: application/json; charset=utf8');
		$json = json_encode(array("total_room"=>$result->num_rows,"room_list"=>$response), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
		echo $json;

	}
	else {
		// code...
    echo "채팅방이 없습니다.";
	}

  $conn->close();

?>
