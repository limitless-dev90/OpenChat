<?php
  include 'dbconnect.php';

  $target_dir = "picture/";

  // echo isset($_FILES['file']['name']);
  //echo var_dump($_FILES);
  $response = array();

  //var_dump($_FILES["file"]["name"]);
  $exploded_file = explode(".", $_FILES["file"]["name"]);
  $file_extension = array_pop($exploded_file);  //확장자 추출
  $file_name = implode($exploded_file);         //파일명 추출

  $target_file_name = $target_dir.time().".".$file_extension;  //서버에 저장할 파일명

  $response = array();

  // Check if image file is an actual image or fake image
  if (isset($_FILES["file"]))
  {
     if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file_name))
     {
        //새로 생긴 프로필이미지 파일권한 설정 777
        //파일 권한 설정 실패 시 400 에러 응답
        if(!chmod($target_file_name, 0777))
        {
          echo 0;
          $message = "file permissions error";
          exit();
        }

        echo $target_file_name;
        $message = $target_file_name;
     }
     else
     {
        echo 0;
        $message = "uploading 실패...";
     }
  }
  else
  {
        echo 0;
        $message = "Required Field Missing";
  }

?>
