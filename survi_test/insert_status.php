<?php
    include("dbconfig.php");
    
    if($_SERVER['REQUEST_METHOD']=='POST'){
        
        $status = (int) $_POST['status'];

        /*image upload*/
        $image = $_POST['image'];
        $type = $_POST['type'];
        $path = $_POST['path'];
        $mainpath = $_POST['mainpath'];
        $time = $_POST['time'];
        $date = $_POST['date'];

        $sql = "INSERT INTO detection_tbl (status, image, type, time, date) 
        VALUES 
        ($status, '$mainpath', '$type', '$time', '$date')";
        
        if(mysqli_query($con,$sql)){
            echo "1";
            file_put_contents($path,base64_decode($image));
         }else{
            echo "0";
         }
    }
		
?>