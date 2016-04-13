<?php
$response=array();
$response["success"]=-1;
if (isset($_POST["username"]) && isset($_POST["image"])  && isset($_POST["receiver"]) && isset($_POST["type"])){
 $image = $_POST['image'];
 require_once __DIR__ . './nosql_connect.php';
    $name= $_POST["username"];
    $exp= "Image Shared. Click to open";
    $receiver= $_POST["receiver"];
    $db= new NOSQLDB_CONNECT();
    $db_c= $db->connect();
    $str=$name.":".$receiver;
    $content=array("username"=>$name,"message"=>$exp,"receiver"=>$receiver,"type"=>$_POST["type"],"str"=>$str,
                  "imagestring"=>$image);
     $qry= $db_c->user_messages->insertOne($content,array('fsync' => 1));
            $response["insertid"]=$qry->getInsertedId()."";
    $response["success"]=1;
    echo json_encode($response);
 }else{
 echo json_encode($response);
 }
?>