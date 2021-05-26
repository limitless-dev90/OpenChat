<?php

	include 'dbconnect.php';

	$room_idx = $_GET['room_idx'];

	$conn = OpenCon();

  $sql = "SELECT u.idx AS user_idx,u.id, u.profile_path, cc.content, cc.create_date AS time FROM chat_content AS cc
            INNER JOIN user AS u ON u.idx = cc.user_idx
            WHERE cc.room_idx = $room_idx AND cc.delete_date IS NULL";

  $result = $conn->query($sql);

	if ($result->num_rows > 0) {

    $response = array();

		$today = date("YMD",  time());

    while($chatRows = mysqli_fetch_assoc($result))
    {
        $diff = time() - strtotime($chatRows['time']);  //현재시간에서 작성시간을 빼서 얼마전에 작성한 것인지 차이 값을 $diff 변수에 저장.

				$Yesterday = date("YMD",  strtotime($chatRows['time']));
        $day = 3600 * 24; //1일 = 24시간
        $month = $day * 30; //1개월 = 30일
        $year = $day * 365; //1년 = 365일

        if ($Yesterday == $today) {
            $trans_date = date("A h시 i분",  strtotime($chatRows['time']));
						$trans_date = str_replace("AM", "오전", $trans_date);
						$trans_date = str_replace("PM", "오후", $trans_date);
        } else if ($month > $diff && $diff >= $day) {
            $trans_date = floor($diff/$day) . '일전';
        } else if($year > $diff && $diff >= $month){
            $trans_date = floor($diff/$month) . '개월전';
        }else if($diff >= $year)
        {
            $trans_date = floor($diff/$year) . '년전';
        }

      array_push($response,
               array('userIdx'=>(int)$chatRows['user_idx'],
                     'id'=>$chatRows['id'],
                     'message'=>$chatRows['content'],
                     'time'=>$trans_date,
                     'profilePath'=>$chatRows['profile_path']
               ));
    }

		header('Content-Type: application/json; charset=utf8');
		$json = json_encode(array("chat_list"=>$response), JSON_PRETTY_PRINT+JSON_UNESCAPED_UNICODE);
		echo $json;

	}
	else {
		// code...
    echo "채팅내용이 없습니다.";
	}

  $conn->close();

?>
