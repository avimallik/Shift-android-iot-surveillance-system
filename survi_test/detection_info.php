<?php
  include("dbconfig.php");
   $sql = "SELECT *from detection_tbl order by id DESC LIMIT 1";
    $result = mysqli_query($con, $sql);
    if (mysqli_num_rows($result) > 0) {
        // output data of each row
      $rows = array();
       while($r = mysqli_fetch_assoc($result)) {
          $rows[] = $r; // with result object
        //  $rows[] = $r; // only array
    
       }
      echo json_encode(reset($rows), JSON_FORCE_OBJECT | JSON_NUMERIC_CHECK);
    } else {
        echo '{"result": "No data found"}';
    }
  ?>