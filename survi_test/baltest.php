<?php
$server_key = "AAAA79A8_8c:APA91bG4uOMK3h9bx3QeB2-9RJcRPoKJu_fXWra3povebs5gip3WfJtBPrBIy0GRXNUUV6lcJVtndZemx5dVFMSPI7Gkx0i-ecDSLc0d1fF_d2WGHuRmLZ_BkMcK6CMlLy4JbUfNGnl-";
    $topic_adress = "/topics/detect";
    $fcm_server_url = "https://fcm.googleapis.com/fcm/send";

    $title = utf8_encode("Application Notice !");

    $message = "85RTXM@";
    $content_text = utf8_encode($message);

    $httpheader = array('Content-Type:application/json', 'Authorization:key='.$server_key);
    $post_content = array('to' => $topic_adress, 'data' => array('title' => $title, 'content-text' => $content_text));
    $curl_connection = curl_init();
    curl_setopt($curl_connection, CURLOPT_URL, $fcm_server_url);
    curl_setopt($curl_connection, CURLOPT_POST, true);
    curl_setopt($curl_connection, CURLOPT_HTTPHEADER, $httpheader);
    curl_setopt($curl_connection, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($curl_connection, CURLOPT_POSTFIELDS, json_encode($post_content));
    $answerFromServer = curl_exec($curl_connection);
    curl_close($curl_connection);
    echo "Arm_Avi".$answerFromServer;
?>