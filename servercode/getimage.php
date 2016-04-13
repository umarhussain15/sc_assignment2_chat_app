<?php
$response=array();
$response["success"]=-1;
if (isset($_GET['id'])){
 require_once __DIR__ . './nosql_connect.php';
    
    $db= new NOSQLDB_CONNECT();
    $db_c= $db->connect();
   
   
//     $cursor= $db_c->user_messages->find(['_id' => $_GET['id']]);
//    echo $_GET['id']."";   
    $cc= new MongoDB\BSON\ObjectId($_GET['id']);
     $cursor= $db_c->user_messages->find(array('_id'=>$cc));
//    var_dump($cursor);
    $it= iterator_to_array($cursor);
    
   header('content-type: image/jpeg');
    foreach ($it as $doc) {
        echo base64_decode($doc["imagestring"]);
        
    }
// 
 }else{
 echo json_encode($response);
 }
?>