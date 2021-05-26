<?php

function OpenCon()
{
  $servername = "127.0.0.1";
  $username = "db사용자명";
  $password = "db암호";
  $my_db = "db명";

  // Create connection
  $conn = new mysqli($servername, $username, $password, $my_db);

  // Check connection
  if ($conn->connect_error) {
      die("Connection failed: " . $conn->connect_error);
  }

  return $conn;
}

function CloseCon($conn)
{
    echo "gg";
    $conn->close();
}

?>
