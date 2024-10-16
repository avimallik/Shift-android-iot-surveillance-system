<?php
include("dbconfig.php");
    if($_SERVER['REQUEST_METHOD'] == 'POST'){

        $id = (int)$_POST['id'];
        $imageFileName = $_POST['imageFileName'];

        $sql = "DELETE FROM detection_tbl WHERE id=$id";

        if(mysqli_query($con, $sql)){
            unlink("public/asset/credential/".$imageFileName);
            echo "1";
        }else{
            echo "0";
        }
    }
?>